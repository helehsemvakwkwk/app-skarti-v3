package com.skartivpn.inject.tunnel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel; // <-- PERBAIKAN: Typo sudah diperbaiki di sini
import java.util.Random;

// Mengasumsikan com.trilead.ssh2.* dan com.skartivpn.inject.logger.* tersedia
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.LocalPortForwarder;
import com.skartivpn.inject.logger.SkStatus;

public class Pinger extends Thread {

    // [REALTIME PING] Callback interface for realtime updates
    public interface PingCallback {
        void onPingResult(long tunnelTimeMs, long directTimeMs, String status, String color);
        void onPingError(String errorMessage);
    }

    private final Connection connection;
    private LocalPortForwarder localPortForwarder;
    private volatile boolean isRunning = false;
    private PingCallback callback; // Callback for realtime updates

    private final String targetHost;
    private final int targetPort = 80;
    private final int localForwardPort = 9395;

    // [REALTIME PING] Configurable ping interval
    private int pingIntervalMs = 2000; // Default 2 seconds for realtime response
    private static final int MIN_PING_INTERVAL = 1000; // Minimum 1 second
    private static final int MAX_PING_INTERVAL = 10000; // Maximum 10 seconds

    public Pinger(Connection connection, String targetHostOverride) {
        this.connection = connection;
        if (targetHostOverride != null && !targetHostOverride.trim().isEmpty()) {
            this.targetHost = targetHostOverride.trim();
        } else {
            this.targetHost = "clients3.google.com";
            SkStatus.logWarning("Pinger: Target host tidak diberikan atau kosong, menggunakan default: " + this.targetHost);
        }
        setName("PingerThread-" + this.targetHost);
    }

    /**
     * Set callback for realtime ping updates
     * @param callback Callback instance
     */
    public void setPingCallback(PingCallback callback) {
        this.callback = callback;
        SkStatus.logInfo("Pinger: Callback diset untuk realtime updates pada " + this.targetHost);
    }

    /**
     * Set custom ping interval in milliseconds (1000-10000ms)
     * @param intervalMs Interval in milliseconds
     */
    public void setPingInterval(int intervalMs) {
        if (intervalMs < MIN_PING_INTERVAL) {
            this.pingIntervalMs = MIN_PING_INTERVAL;
            SkStatus.logWarning("Pinger: Interval terlalu kecil, diset ke minimum: " + MIN_PING_INTERVAL + "ms");
        } else if (intervalMs > MAX_PING_INTERVAL) {
            this.pingIntervalMs = MAX_PING_INTERVAL;
            SkStatus.logWarning("Pinger: Interval terlalu besar, diset ke maximum: " + MAX_PING_INTERVAL + "ms");
        } else {
            this.pingIntervalMs = intervalMs;
        }
        SkStatus.logInfo("Pinger: Ping interval diset ke: " + this.pingIntervalMs + "ms untuk " + this.targetHost);
    }

    /**
     * Get current ping interval
     * @return Current interval in milliseconds
     */
    public int getPingInterval() {
        return this.pingIntervalMs;
    }

    private int getRandomDelay() {
        // [FIXED] Removed random delay, using fixed interval for realtime response
        return this.pingIntervalMs;
    }

    public void close() {
        synchronized (this) {
            if (!isRunning) {
                SkStatus.logInfo("Pinger: close() dipanggil untuk " + this.targetHost + ", tapi sudah berhenti atau sedang berhenti.");
                return;
            }
            SkStatus.logInfo("Pinger: close() dipanggil untuk " + this.targetHost + ". Memberi sinyal thread untuk berhenti.");
            this.isRunning = false;
            interrupt();
        }
    }

    @Override
    public void run() {
        try {
            SkStatus.logInfo("Pinger: Thread Dimulai untuk " + this.targetHost + ":" + this.targetPort + " melalui port lokal " + this.localForwardPort);

            if (this.connection == null) {
                 SkStatus.logError("Pinger: Koneksi SSH null. Thread Pinger untuk " + this.targetHost + " keluar.");
                 return;
            }
            if (!this.connection.isAuthenticationComplete()) {
                 SkStatus.logError("Pinger: Koneksi SSH belum terautentikasi. Thread Pinger untuk " + this.targetHost + " keluar.");
                 return;
            }
            
            // Inisialisasi localPortForwarder
            this.localPortForwarder = this.connection.createLocalPortForwarder(this.localForwardPort, this.targetHost, this.targetPort);
            SkStatus.logInfo("Pinger: LocalPortForwarder dibuat untuk " + this.targetHost + ". Mendengarkan di 127.0.0.1:" + this.localForwardPort);
            
            this.isRunning = true;

            while (this.isRunning) {
                long directConnectionTimeMs = -1;
                long tunnelResponseTimeMs = -1;
                String tunnelResponseLine = null;
                Socket tunnelSocket = null;

                try {
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName(this.targetHost), this.targetPort);
                    try (SocketChannel socketChannel = SocketChannel.open()) { // SocketChannel sekarang seharusnya dikenali
                        socketChannel.configureBlocking(true);
                        long startTime = System.currentTimeMillis();
                        socketChannel.socket().connect(inetSocketAddress, 5000);
                        if (socketChannel.isConnected()) {
                             directConnectionTimeMs = System.currentTimeMillis() - startTime;
                        }
                    }
                } catch (SocketTimeoutException e) {
                    SkStatus.logWarning("Pinger: Percobaan koneksi langsung ke " + this.targetHost + " timeout.");
                    directConnectionTimeMs = -2; 
                } catch (IOException e) {
                    SkStatus.logWarning("Pinger: Percobaan koneksi langsung ke " + this.targetHost + " gagal: " + e.getMessage());
                    directConnectionTimeMs = -2;
                } catch (Exception e) {
                    SkStatus.logWarning("Pinger: Error tak terduga saat pemeriksaan koneksi langsung ke " + this.targetHost + ": " + e.getMessage());
                    directConnectionTimeMs = -2;
                }

                if (this.isRunning) {
                    try {
                        long tunnelRequestStartTime = System.currentTimeMillis();
                        tunnelSocket = new Socket("127.0.0.1", this.localForwardPort);
                        // [REALTIME PING] Reduced timeout from 10s to 5s for faster response
                        tunnelSocket.setSoTimeout(5000);

                        OutputStream outputStream = tunnelSocket.getOutputStream();
                        String request = String.format("GET http://%s/ HTTP/1.1\r\nHost: %s\r\nConnection: close\r\nUser-Agent: Pinger/1.0\r\n\r\n", this.targetHost, this.targetHost);
                        outputStream.write(request.getBytes("UTF-8"));
                        outputStream.flush();

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(tunnelSocket.getInputStream(), "UTF-8"));
                        tunnelResponseLine = bufferedReader.readLine();
                        tunnelResponseTimeMs = System.currentTimeMillis() - tunnelRequestStartTime;
                        
                    } catch (SocketTimeoutException e) {
                        SkStatus.logError("Pinger: Request tunnel ke " + this.targetHost + " (via 127.0.0.1:" + this.localForwardPort + ") timeout: " + e.getMessage());
                        tunnelResponseTimeMs = -2;
                    } catch (IOException e) {
                        SkStatus.logError("Pinger: IO Error saat ping tunnel ke " + this.targetHost + " (via 127.0.0.1:" + this.localForwardPort + "): " + e.getMessage());
                        tunnelResponseTimeMs = -3;
                    } catch (Exception e) {
                         SkStatus.logError("Pinger: Error tak terduga saat ping tunnel ke " + this.targetHost + ": " + e.getMessage());
                         tunnelResponseTimeMs = -3;
                    } finally {
                        if (tunnelSocket != null) {
                            try {
                                tunnelSocket.close();
                            } catch (IOException e) {
                                SkStatus.logWarning("Pinger: Error menutup socket tunnel untuk " + this.targetHost + ": " + e.getMessage());
                            }
                        }
                    }
                }
                
                String logMsg;
                long timeToLogForColor;

                if (tunnelResponseLine != null) {
                    logMsg = tunnelResponseLine;
                    timeToLogForColor = tunnelResponseTimeMs;
                } else if (tunnelResponseTimeMs == -2) {
                    logMsg = "Request tunnel timeout";
                    timeToLogForColor = tunnelResponseTimeMs; 
                } else if (tunnelResponseTimeMs == -3) {
                    logMsg = "Error pada request tunnel";
                    timeToLogForColor = tunnelResponseTimeMs;
                } else if (tunnelResponseTimeMs >= 0) {
                    logMsg = "Tidak ada baris respons diterima (Tunnel terhubung)";
                    timeToLogForColor = tunnelResponseTimeMs;
                } else { 
                    logMsg = null; 
                    timeToLogForColor = -1;
                }
                
                if (logMsg != null) { 
                     logPingerStatus(logMsg, timeToLogForColor, tunnelResponseTimeMs, directConnectionTimeMs);
                }

                if (this.isRunning) {
                    try {
                        Thread.sleep(getRandomDelay());
                    } catch (InterruptedException e) {
                        SkStatus.logInfo("Pinger: Sleep diinterupsi untuk " + this.targetHost + ". Loop akan berhenti jika isRunning false.");
                        Thread.currentThread().interrupt(); 
                    }
                }
            } 
        } catch (IOException e) { 
            SkStatus.logError("Pinger: IO Error saat setup Pinger untuk " + this.targetHost + ": " + e.toString() + ". Thread Pinger keluar.");
        } catch (Exception e) { 
            SkStatus.logError("Pinger: Error fatal tak terduga di thread Pinger untuk " + this.targetHost + ": " + e.toString() + ". Thread Pinger keluar.");
        } finally {
            SkStatus.logInfo("Pinger: Memasuki blok finally untuk " + this.targetHost + ".");
            // Menggunakan this.localPortForwarder
            if (this.localPortForwarder != null) {
                try {
                    SkStatus.logInfo("Pinger: Menutup LocalPortForwarder untuk " + this.targetHost + " di blok finally.");
                    this.localPortForwarder.close();
                } catch (Exception e) { 
                    SkStatus.logError("Pinger: Error menutup localPortForwarder di blok finally untuk " + this.targetHost + ": " + e.getMessage());
                }
                this.localPortForwarder = null;
            }
            this.isRunning = false; 
            SkStatus.logInfo("Pinger: Thread Ping selesai untuk " + this.targetHost + ".");
        }
    }

    private void logPingerStatus(String statusInfo, long timeForColoring, long actualTunnelTimeMs, long directConnectionTimeMs) {
        String color;
        if (timeForColoring < 0) {
            color = "red";
        } else if (timeForColoring < 250) {
            color = "green";
        } else if (timeForColoring < 500) {
            color = "#FFA500";
        } else {
            color = "red";
        }

        String truncatedStatus = statusInfo;
        if (statusInfo != null && statusInfo.length() > 100) {
            truncatedStatus = statusInfo.substring(0, 97) + "...";
        } else if (statusInfo == null) {
            truncatedStatus = "N/A";
        }

        String directTimeLog = "";
        if (directConnectionTimeMs > 0) {
            directTimeLog = String.format(" (Direct: %dms)", directConnectionTimeMs);
        } else if (directConnectionTimeMs == -2) {
             directTimeLog = " (Direct: Gagal)";
        }

        String tunnelTimeStr;
        if (actualTunnelTimeMs == -2) tunnelTimeStr = "Timeout";
        else if (actualTunnelTimeMs == -3) tunnelTimeStr = "Error";
        else if (actualTunnelTimeMs < 0) tunnelTimeStr = "N/A";
        else tunnelTimeStr = String.format("%dms", actualTunnelTimeMs);

        // Log to console
        SkStatus.logInfo(String.format("Pinger Status [%s]: %s | Tunnel: (<font color=\"%s\">%s</font>)%s",
                this.targetHost, truncatedStatus, color, tunnelTimeStr, directTimeLog));

        // [REALTIME PING] Trigger callback for realtime UI update
        if (callback != null) {
            try {
                callback.onPingResult(actualTunnelTimeMs, directConnectionTimeMs, truncatedStatus, color);
            } catch (Exception e) {
                SkStatus.logError("Pinger: Error saat memanggil callback: " + e.getMessage());
            }
        }
    }
}
