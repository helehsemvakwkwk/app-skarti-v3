package com.skartivpn.app.util;

import android.net.TrafficStats;
import android.os.Process;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
public class NethPogi {


    static long totalUpload = 0;
    static long totalDownload = 0;
    static long totalUpload_n = 0;
    static long totalDownload_n = 0; //for notification


    //  static long totalDownload = TrafficStats.getTotalRxBytes();

    /**
     *
     * @return downloaded and uploaded data per second
     */
    public static List<Long>  findData() {
        List<Long> allData = new ArrayList<>();

        long newTotalDownload, incDownload, newTotalUpload, incUpload;


        // Use this app's UID traffic (the tunnel runs in this app), not
        // device-wide traffic. Fall back to total traffic if UID stats
        // are unsupported (TrafficStats.UNSUPPORTED == -1).
        long uidRx = TrafficStats.getUidRxBytes(Process.myUid());
        long uidTx = TrafficStats.getUidTxBytes(Process.myUid());
        if (uidRx == TrafficStats.UNSUPPORTED) { uidRx = TrafficStats.getTotalRxBytes(); }
        if (uidTx == TrafficStats.UNSUPPORTED) { uidTx = TrafficStats.getTotalTxBytes(); }
        if (totalDownload == 0)
            totalDownload = uidRx;
        if (totalUpload == 0)
            totalUpload = uidTx;
        newTotalDownload = uidRx;
        incDownload = newTotalDownload - totalDownload;
        newTotalUpload = uidTx;
        incUpload = newTotalUpload - totalUpload;


        //total downloaded and uploaded data after started new session
        totalDownload = newTotalDownload;
        totalUpload = newTotalUpload;

        allData.add(incDownload);
        allData.add(incUpload);


        return allData;


        //return incDownload + incUpload;
    }

    public static long getNotificationData() {

        long newTotalDownload, incDownload, newTotalUpload, incUpload;

        if (totalDownload_n == 0)
            totalDownload_n = TrafficStats.getTotalRxBytes();

        if (totalUpload_n == 0)
            totalUpload_n = TrafficStats.getTotalTxBytes();

        newTotalDownload = TrafficStats.getTotalRxBytes();
        incDownload = newTotalDownload - totalDownload_n;

        newTotalUpload = TrafficStats.getTotalTxBytes();
        incUpload = newTotalUpload - totalUpload_n;


        totalDownload_n = newTotalDownload;
        totalUpload_n = newTotalUpload;


        return incDownload + incUpload;
    }
    public static List<Long> downloadList = new ArrayList<>();
    public static List<Long> uploadList = new ArrayList<>();
    public static long downloadSpeed = 0;
    public static long uploadSpeed = 0;
    public static boolean isSetData = false;
//    public static int Tres = ClientServerHello.Bleeh;
    public static void setZero() {
        isSetData = true;
        // return if listed is full
        for (int i = 0; i < 300; i++) {
            downloadList.add(0L);
            uploadList.add(0L);

        }

    }
    public static void damn(Long mDownload, Long mUpload) {

        downloadSpeed = mDownload;
        uploadSpeed = mUpload;

        if(isSetData) {
            downloadList.remove(0);
            uploadList.remove(0);

            downloadList.add(mDownload);
            uploadList.add(mUpload);
        }



    }
}
