/*
 * Copyright (c) 2016, Psiphon Inc.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.skartivpn.inject.tunnel.vpn;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import com.skartivpn.inject.logger.SkStatus;
import com.skarti.premium.v2.R;
import com.skartivpn.app.PhreakersMainActivity;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TunnelVpnService extends VpnService {

  private static final String LOG_TAG = "TunnelVpnService";
  public static final String TUNNEL_VPN_DISCONNECT_BROADCAST =
      "tunnelVpnDisconnectBroadcast";
  public static final String TUNNEL_VPN_START_BROADCAST =
      "tunnelVpnStartBroadcast";
  public static final String TUNNEL_VPN_START_SUCCESS_EXTRA =
      "tunnelVpnStartSuccessExtra";

  // Notification constants - MUST be non-zero and consistent
  private static final int VPN_NOTIFICATION_ID = 1001;
  private static final String VPN_NOTIFICATION_CHANNEL_ID = "vpn_service_channel";
  private static final String VPN_NOTIFICATION_CHANNEL_NAME = "VPN Service";

  private TunnelVpnManager m_tunnelManager = new TunnelVpnManager(this);
  private NotificationManager notificationManager;

  public class LocalBinder extends Binder {
    public TunnelVpnService getService() {
      return TunnelVpnService.this;
    }
  }

  private final IBinder m_binder = new LocalBinder();

  @Override
  public IBinder onBind(Intent intent) {
    String action = intent.getAction();
    if (action != null && action.equals(SERVICE_INTERFACE)) {
      return super.onBind(intent);
    }
    return m_binder;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(LOG_TAG, "on create");
    
    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    
    // Create notification channel for Android O+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createVpnNotificationChannel();
    }
    
    TunnelState.getTunnelState().setTunnelManager(m_tunnelManager);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(LOG_TAG, "on start");
    
    // CRITICAL: Start foreground IMMEDIATELY to prevent service from being killed
    startForeground(VPN_NOTIFICATION_ID, createVpnNotification("VPN Initializing..."));
    
    return m_tunnelManager.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    Log.d(LOG_TAG, "on destroy");
    TunnelState.getTunnelState().setTunnelManager(null);
    m_tunnelManager.onDestroy();
    
    // Proper cleanup: Use STOP_FOREGROUND_DETACH for Android S+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      stopForeground(STOP_FOREGROUND_REMOVE);
    } else {
      stopForeground(true);
    }
  }

  @Override
  public void onRevoke() {
    SkStatus.logInfo("<strong>VPN service revoked</strong>");
    broadcastVpnDisconnect();
    // stopSelf will trigger onDestroy in the main thread.
    stopSelf();
  }

  public VpnService.Builder newBuilder() {
    return new VpnService.Builder();
  }

  // Broadcast non-user-initiated VPN disconnect.
  public void broadcastVpnDisconnect() {
    dispatchBroadcast(new Intent(TUNNEL_VPN_DISCONNECT_BROADCAST));
  }

  // Broadcast VPN start. |success| is true if the VPN and tunnel were started
  // successfully, and false otherwise.
  public void broadcastVpnStart(boolean success) {
    Intent vpnStart = new Intent(TUNNEL_VPN_START_BROADCAST);
    vpnStart.putExtra(TUNNEL_VPN_START_SUCCESS_EXTRA, success);
    dispatchBroadcast(vpnStart);
    
    // Update notification when VPN starts successfully
    if (success) {
      updateVpnNotification("VPN Connected");
    }
  }

  private void dispatchBroadcast(final Intent broadcast) {
    LocalBroadcastManager.getInstance(TunnelVpnService.this)
        .sendBroadcast(broadcast);
  }

  /**
   * Create notification channel for Android O+ with proper importance
   */
  @TargetApi(Build.VERSION_CODES.O)
  private void createVpnNotificationChannel() {
    NotificationChannel channel = new NotificationChannel(
        VPN_NOTIFICATION_CHANNEL_ID,
        VPN_NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW  // LOW prevents intrusive notifications but keeps it visible
    );
    channel.setDescription("Keeps VPN connection active");
    channel.setShowBadge(false);
    channel.enableLights(false);
    channel.enableVibration(false);
    channel.setSound(null, null);  // Silent notification
    
    if (notificationManager != null) {
      notificationManager.createNotificationChannel(channel);
    }
  }

  /**
   * Create persistent VPN notification
   */
  private Notification createVpnNotification(String contentText) {
    // FIX: Proper intent configuration to prevent crashes when notification is clicked
    Intent notificationIntent = new Intent(this, PhreakersMainActivity.class);
    // Clear any existing tasks and create a new one
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP);

    // Use proper PendingIntent flags for all Android versions
    int flags;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      // Android 12+ requires IMMUTABLE or MUTABLE flag
      flags = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      // Android 6.0+
      flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT;
    } else {
      flags = PendingIntent.FLAG_UPDATE_CURRENT;
    }

    PendingIntent pendingIntent = null;
    try {
      pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags);
    } catch (Exception e) {
      Log.e(LOG_TAG, "Error creating PendingIntent", e);
      // Fallback without PendingIntent if it fails
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // Use Notification.Builder for Android O+
      Notification.Builder builder = new Notification.Builder(this, VPN_NOTIFICATION_CHANNEL_ID)
          .setContentTitle(getString(R.string.app_name))
          .setContentText(contentText)
          .setSmallIcon(R.drawable.ic_shield_connected)
          .setOngoing(true)  // Cannot be dismissed by user
          .setOnlyAlertOnce(true)  // Don't alert for updates
          .setShowWhen(false)
          .setCategory(Notification.CATEGORY_SERVICE)
          .setAutoCancel(false);

      // Only set content intent if it was created successfully
      if (pendingIntent != null) {
        builder.setContentIntent(pendingIntent);
      }

      return builder.build();
    } else {
      // Use NotificationCompat for older versions
      NotificationCompat.Builder builder = new NotificationCompat.Builder(this, VPN_NOTIFICATION_CHANNEL_ID)
          .setContentTitle(getString(R.string.app_name))
          .setContentText(contentText)
          .setSmallIcon(R.drawable.ic_shield_connected)
          .setOngoing(true)
          .setOnlyAlertOnce(true)
          .setShowWhen(false)
          .setPriority(NotificationCompat.PRIORITY_LOW)
          .setAutoCancel(false);

      // Only set content intent if it was created successfully
      if (pendingIntent != null) {
        builder.setContentIntent(pendingIntent);
      }

      return builder.build();
    }
  }

  /**
   * Update notification content without recreating
   */
  public void updateVpnNotification(String contentText) {
    if (notificationManager != null) {
      Notification notification = createVpnNotification(contentText);
      notificationManager.notify(VPN_NOTIFICATION_ID, notification);
    }
  }
}
