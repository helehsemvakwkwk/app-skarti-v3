package com.skartivpn.app.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Helper class to fetch public IP address
 * Uses api.ipify.org for reliable IP detection
 */
public class IpAddressHelper {

    private static final String TAG = "IpAddressHelper";
    private static final String IP_API_URL = "https://api.ipify.org";
    private static final int TIMEOUT_MS = 5000; // 5 seconds timeout

    private ExecutorService executorService;

    public interface IpAddressCallback {
        void onIpAddressFetched(String ipAddress);
        void onError(String error);
    }

    public IpAddressHelper() {
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Fetches the public IP address asynchronously
     * @param callback Callback to receive the IP address or error
     */
    public void fetchIpAddress(final IpAddressCallback callback) {
        executorService.execute(() -> {
            String ipAddress = getPublicIpAddress();

            // Run callback on main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                if (ipAddress != null && !ipAddress.isEmpty()) {
                    callback.onIpAddressFetched(ipAddress);
                } else {
                    callback.onError("Unable to fetch IP");
                }
            });
        });
    }

    /**
     * Fetches the public IP address synchronously (blocking)
     * Should be called from a background thread
     * @return IP address or null if failed
     */
    private String getPublicIpAddress() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(IP_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("User-Agent", "SkartiVPN/1.0");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String ipAddress = reader.readLine();

                if (ipAddress != null && !ipAddress.isEmpty()) {
                    // Validate IP format (basic validation)
                    if (isValidIpFormat(ipAddress)) {
                        Log.d(TAG, "IP Address fetched: " + ipAddress);
                        return ipAddress.trim();
                    } else {
                        Log.e(TAG, "Invalid IP format: " + ipAddress);
                    }
                }
            } else {
                Log.e(TAG, "HTTP Error: " + responseCode);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error fetching IP address: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (connection != null) connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Basic IP address format validation
     * Supports both IPv4 and IPv6
     */
    private boolean isValidIpFormat(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        // IPv4 pattern (simple check)
        if (ip.matches("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
            return true;
        }

        // IPv6 pattern (simple check - contains colons)
        if (ip.contains(":") && ip.length() >= 3) {
            return true;
        }

        return false;
    }

    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
