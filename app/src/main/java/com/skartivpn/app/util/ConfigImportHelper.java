package com.skartivpn.app.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helper class for importing VPN configuration files
 */
public class ConfigImportHelper {

    private static final String TAG = "ConfigImportHelper";

    /**
     * Read config content from URI
     * @param context Application context
     * @param uri File URI
     * @return Config content as string, or null if error
     */
    public static String readConfigFromUri(Context context, Uri uri) {
        if (context == null || uri == null) {
            Log.e(TAG, "Context or URI is null");
            return null;
        }

        InputStream inputStream = null;
        BufferedReader reader = null;

        try {
            // Open input stream from URI
            inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e(TAG, "Cannot open input stream from URI: " + uri);
                return null;
            }

            // Read file contents
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder configContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                configContent.append(line).append("\n");
            }

            return configContent.toString().trim();

        } catch (IOException e) {
            Log.e(TAG, "Error reading config from URI", e);
            return null;
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception reading URI", e);
            return null;
        } finally {
            // Clean up resources
            try {
                if (reader != null) reader.close();
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing streams", e);
            }
        }
    }

    /**
     * Validate VPN config content
     * @param config Config string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidVpnConfig(String config) {
        if (config == null || config.trim().isEmpty()) {
            return false;
        }

        // Trim config
        config = config.trim();

        // Check for minimum length
        if (config.length() < 20) {
            return false;
        }

        // Check for essential OpenVPN config elements
        boolean hasEssentialElement =
            config.contains("remote") ||
            config.contains("client") ||
            config.contains("dev tun") ||
            config.contains("dev tap") ||
            config.contains("proto tcp") ||
            config.contains("proto udp") ||
            config.contains("port");

        if (!hasEssentialElement) {
            Log.w(TAG, "Config doesn't contain expected VPN elements");
            return false;
        }

        // Additional validation: check for suspicious content
        if (config.contains("<script>") || config.contains("javascript:")) {
            Log.w(TAG, "Config contains suspicious script content");
            return false;
        }

        return true;
    }

    /**
     * Extract filename from URI
     * @param uri File URI
     * @return Filename or "imported_config" if not found
     */
    public static String getFileNameFromUri(Uri uri) {
        if (uri == null) {
            return "imported_config";
        }

        String fileName = uri.getLastPathSegment();
        if (fileName == null || fileName.isEmpty()) {
            fileName = "imported_config";
        }

        return fileName;
    }

    /**
     * Check if URI represents a VPN config file based on extension
     * @param uri File URI
     * @return true if likely a VPN config file
     */
    public static boolean isVpnConfigFile(Uri uri) {
        if (uri == null) {
            return false;
        }

        String path = uri.toString().toLowerCase();
        return path.endsWith(".skarti") ||
               path.endsWith(".conf") ||
               path.endsWith(".json") ||
               path.contains("skarti") ||
               path.contains("json");
    }

    /**
     * Sanitize config content (remove potentially harmful content)
     * @param config Raw config content
     * @return Sanitized config
     */
    public static String sanitizeConfig(String config) {
        if (config == null) {
            return "";
        }

        // Remove any HTML/script tags that might have been accidentally included
        config = config.replaceAll("<[^>]*>", "");

        // Remove excessive whitespace but preserve structure
        config = config.replaceAll("\\r\\n", "\n");
        config = config.replaceAll("\\r", "\n");

        return config.trim();
    }
}
