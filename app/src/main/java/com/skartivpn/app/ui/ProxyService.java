package com.skartivpn.app.ui;

import com.skartivpn.app.core.ProxyServer; // Asumsi class ini ada
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class ProxyService extends Service {
    public static final String TAG = "ProxyService";

    // Tidak ada perubahan kode di sini berdasarkan analisis sebelumnya,
    // karena fungsionalitasnya sangat bergantung pada ProxyServer.getInstance()

    @Override
    public IBinder onBind(Intent binder) {
        return new IProxyControl.Stub() {
            @Override
            public boolean start() throws RemoteException {
                return doStart();
            }

            @Override
            public boolean stop() throws RemoteException {
                return doStop();
            }

            @Override
            public boolean isRunning() throws RemoteException {
                // Pastikan ProxyServer.getInstance() tidak null dan aman dipanggil
                ProxyServer server = ProxyServer.getInstance();
                return server != null && server.isRunning();
            }

            @Override
            public int getPort() throws RemoteException {
                // Pastikan ProxyServer.getInstance() tidak null dan aman dipanggil
                ProxyServer server = ProxyServer.getInstance();
                if (server != null) {
                    return server.getPort();
                }
                return -1; // Atau nilai default jika server null
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // Jika ProxyServer perlu di-cleanup secara eksplisit saat service dihancurkan
        // ProxyServer.getInstance().shutdown(); // Contoh, sesuaikan dengan implementasi ProxyServer
        super.onDestroy();
    }

    private boolean doStart() {
        ProxyServer proxyServer = ProxyServer.getInstance();
        if (proxyServer == null) return false; // Tambahan null check

        if (proxyServer.isRunning()) {
            return true; // Anggap sudah berhasil jika sudah berjalan
        }
        return proxyServer.start();
    }

    private boolean doStop() {
        ProxyServer proxyServer = ProxyServer.getInstance();
        if (proxyServer == null) return false; // Tambahan null check

        if (!proxyServer.isRunning()) {
            return true; // Anggap sudah berhasil jika memang tidak berjalan
        }
        return proxyServer.stop();
    }
}