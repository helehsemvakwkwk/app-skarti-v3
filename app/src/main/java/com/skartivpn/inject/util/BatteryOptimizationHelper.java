package com.skartivpn.inject.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

/**
 * Utility class to handle vendor-specific battery optimization
 * This helps prevent VPN service from being killed by aggressive OEM battery managers
 */
public class BatteryOptimizationHelper {
    
    private static final String TAG = "BatteryOptimization";
    
    /**
     * Check if battery optimization is ignored for this app
     */
    public static boolean isIgnoringBatteryOptimizations(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                return pm.isIgnoringBatteryOptimizations(context.getPackageName());
            }
        }
        return true; // For older versions, assume it's fine
    }
    
    /**
     * Request to ignore battery optimizations
     * Call this from an Activity, not from Service
     */
    public static Intent getBatteryOptimizationIntent(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            return intent;
        }
        return null;
    }
    
    /**
     * Open battery optimization settings page
     */
    public static Intent getBatteryOptimizationSettingsIntent(Context context) {
        Intent intent = new Intent();
        
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        
        try {
            // Xiaomi/MIUI/HyperOS
            if (manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco")) {
                intent.setAction("miui.intent.action.POWER_HIDE_MODE_APP_LIST");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.putExtra("package_name", context.getPackageName());
                intent.putExtra("package_label", getAppName(context));
                return intent;
            }
            
            // OPPO/ColorOS
            else if (manufacturer.contains("oppo")) {
                intent.setAction("com.coloros.safecenter");
                intent.putExtra("packageName", context.getPackageName());
                return intent;
            }
            
            // Vivo/FunTouchOS
            else if (manufacturer.contains("vivo")) {
                intent.setAction("com.iqoo.secure");
                intent.putExtra("packageName", context.getPackageName());
                return intent;
            }
            
            // OnePlus/OxygenOS
            else if (manufacturer.contains("oneplus")) {
                intent.setAction("com.oneplus.security");
                intent.putExtra("packageName", context.getPackageName());
                return intent;
            }
            
            // Huawei/EMUI
            else if (manufacturer.contains("huawei") || manufacturer.contains("honor")) {
                intent.setAction("huawei.intent.action.HSM_PROTECTED_APPS");
                intent.putExtra("packageName", context.getPackageName());
                return intent;
            }
            
            // Samsung/OneUI
            else if (manufacturer.contains("samsung")) {
                intent.setAction("com.samsung.android.sm.ACTION_BATTERY_OPTIMIZATION");
                intent.putExtra("package_name", context.getPackageName());
                return intent;
            }
            
            // Asus
            else if (manufacturer.contains("asus")) {
                intent.setAction("com.asus.mobilemanager.MainActivity");
                return intent;
            }
            
            // Default: Open standard battery optimization settings
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                return intent;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to create vendor-specific intent: " + e.getMessage());
        }
        
        // Fallback to app settings
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        return intent;
    }
    
    /**
     * Get user-friendly instructions for specific OEM
     */
    public static String getBatteryOptimizationInstructions(Context context) {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        
        if (manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco")) {
            return "MIUI/HyperOS:\n" +
                   "1. Settings → Apps → Manage apps → " + getAppName(context) + "\n" +
                   "2. Battery saver → No restrictions\n" +
                   "3. Autostart → Enable\n" +
                   "4. Display pop-up windows while in background → Enable";
        }
        else if (manufacturer.contains("oppo")) {
            return "ColorOS:\n" +
                   "1. Settings → Battery → App Battery Management\n" +
                   "2. Find " + getAppName(context) + " → Allow background activity\n" +
                   "3. Settings → Privacy → Permission Manager → Autostart → Enable for " + getAppName(context);
        }
        else if (manufacturer.contains("vivo")) {
            return "FunTouchOS:\n" +
                   "1. Settings → Battery → Background battery consumption\n" +
                   "2. Find " + getAppName(context) + " → Allow high background power consumption\n" +
                   "3. Settings → More settings → Permissions → Autostart → Enable for " + getAppName(context);
        }
        else if (manufacturer.contains("oneplus")) {
            return "OxygenOS:\n" +
                   "1. Settings → Apps → " + getAppName(context) + " → Battery\n" +
                   "2. Battery optimization → Don't optimize\n" +
                   "3. Settings → Apps → Special access → Battery optimization → " + getAppName(context) + " → Don't optimize";
        }
        else if (manufacturer.contains("huawei") || manufacturer.contains("honor")) {
            return "EMUI/Magic UI:\n" +
                   "1. Settings → Battery → App launch\n" +
                   "2. Find " + getAppName(context) + " → Manage manually\n" +
                   "3. Enable: Auto-launch, Secondary launch, Run in background";
        }
        else if (manufacturer.contains("samsung")) {
            return "Samsung OneUI:\n" +
                   "1. Settings → Apps → " + getAppName(context) + " → Battery\n" +
                   "2. Battery optimization → All apps → " + getAppName(context) + " → Don't optimize\n" +
                   "3. Put app to sleep → Never";
        }
        else if (manufacturer.contains("asus")) {
            return "Asus:\n" +
                   "1. Mobile Manager → Auto-start Manager\n" +
                   "2. Enable " + getAppName(context) + "\n" +
                   "3. Power Master → Auto-start Manager → Enable " + getAppName(context);
        }
        
        return "Standard Android:\n" +
               "Settings → Battery → Battery optimization → All apps → " + getAppName(context) + " → Don't optimize";
    }
    
    /**
     * Check if this device is from a vendor known for aggressive battery optimization
     */
    public static boolean isAggressiveBatteryOptimizationDevice() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        return manufacturer.contains("xiaomi") 
            || manufacturer.contains("redmi")
            || manufacturer.contains("poco")
            || manufacturer.contains("oppo")
            || manufacturer.contains("vivo")
            || manufacturer.contains("oneplus")
            || manufacturer.contains("huawei")
            || manufacturer.contains("honor")
            || manufacturer.contains("asus")
            || manufacturer.contains("realme")
            || manufacturer.contains("tecno")
            || manufacturer.contains("infinix");
    }
    
    /**
     * Get device manufacturer name
     */
    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }
    
    /**
     * Get app name
     */
    private static String getAppName(Context context) {
        try {
            return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        } catch (Exception e) {
            return context.getPackageName();
        }
    }
    
    /**
     * Log device info for debugging
     */
    public static void logDeviceInfo() {
        Log.i(TAG, "Device Info:");
        Log.i(TAG, "  Manufacturer: " + Build.MANUFACTURER);
        Log.i(TAG, "  Brand: " + Build.BRAND);
        Log.i(TAG, "  Model: " + Build.MODEL);
        Log.i(TAG, "  Android Version: " + Build.VERSION.RELEASE);
        Log.i(TAG, "  SDK: " + Build.VERSION.SDK_INT);
        Log.i(TAG, "  Aggressive Battery Optimization: " + isAggressiveBatteryOptimizationDevice());
    }
}
