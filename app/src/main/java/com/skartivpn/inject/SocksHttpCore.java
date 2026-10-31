package com.skartivpn.inject;

import android.content.Context;
import android.annotation.TargetApi;
import android.os.Build;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.graphics.Color;
import com.skarti.premium.v2.R;

/**
 * @author SlipkHunter
 * Fixed: Proper notification channel importance and configuration for all Android devices
 */
public class SocksHttpCore {
    private static SocksHttpCore mInstance = null;
    private Context mContext;

    // Metode init untuk inisialisasi instance (Singleton)
    public static synchronized void init(Context context) {
        if (mInstance == null) {
            if (context == null) {
                throw new IllegalArgumentException("Context cannot be null for initialization");
            }
            mInstance = new SocksHttpCore(context.getApplicationContext());
        }
    }

    // Metode untuk mendapatkan instance (Singleton)
    public static SocksHttpCore getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("SocksHttpCore has not been initialized. Call init() first.");
        }
        return mInstance;
    }

    // Konstruktor privat untuk mencegah instansiasi langsung (Singleton)
    private SocksHttpCore(Context context) {
        mContext = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannels() {
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager == null) {
            return;
        }

        // FIXED: Background service channel - changed from IMPORTANCE_MIN to IMPORTANCE_LOW
        // IMPORTANCE_MIN can be killed by aggressive OEMs, IMPORTANCE_LOW is more stable
        CharSequence nameBg = mContext.getString(R.string.channel_name_background);
        NotificationChannel mChannelBg = new NotificationChannel(
                SocksHttpService.NOTIFICATION_CHANNEL_BG_ID,
                nameBg, 
                NotificationManager.IMPORTANCE_LOW  // Changed from IMPORTANCE_MIN
        );
        mChannelBg.setDescription(mContext.getString(R.string.channel_description_background));
        mChannelBg.enableLights(false);
        mChannelBg.enableVibration(false);
        mChannelBg.setSound(null, null);  // Silent notification
        mChannelBg.setShowBadge(false);
        // CRITICAL: This prevents the notification from being hidden
        mChannelBg.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
        mNotificationManager.createNotificationChannel(mChannelBg);

        // Connection status change messages
        CharSequence nameStatus = mContext.getString(R.string.channel_name_status);
        NotificationChannel mChannelStatus = new NotificationChannel(
                SocksHttpService.NOTIFICATION_CHANNEL_NEWSTATUS_ID,
                nameStatus, 
                NotificationManager.IMPORTANCE_DEFAULT  // Changed from IMPORTANCE_LOW for better visibility
        );
        mChannelStatus.setDescription(mContext.getString(R.string.channel_description_status));
        mChannelStatus.enableLights(true);
        mChannelStatus.setLightColor(Color.BLUE);
        mChannelStatus.enableVibration(false);  // Disable vibration to avoid annoyance
        mChannelStatus.setSound(null, null);  // Silent notification
        mChannelStatus.setShowBadge(true);
        mChannelStatus.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
        mNotificationManager.createNotificationChannel(mChannelStatus);

        // Urgent requests, e.g. two factor auth
        CharSequence nameUserReq = mContext.getString(R.string.channel_name_userreq);
        NotificationChannel mChannelUserReq = new NotificationChannel(
                SocksHttpService.NOTIFICATION_CHANNEL_USERREQ_ID,
                nameUserReq, 
                NotificationManager.IMPORTANCE_HIGH
        );
        mChannelUserReq.setDescription(mContext.getString(R.string.channel_description_userreq));
        mChannelUserReq.enableLights(true);
        mChannelUserReq.setLightColor(Color.CYAN);
        mChannelUserReq.enableVibration(true);
        mChannelUserReq.setShowBadge(true);
        mChannelUserReq.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
        mNotificationManager.createNotificationChannel(mChannelUserReq);
    }
}
