package com.skartivpn.inject.tunnel;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import androidx.collection.ArrayMap;

import com.skartivpn.inject.logger.SkStatus;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Enumeration;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.Inet4Address;
import java.util.regex.Matcher;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;

public class TunnelUtils {
    public static Map<String, CharSequence> BBCODES_LIST;

    public static String formatCustomPayload(String hostname, int port, String payload) {
        BBCODES_LIST = new ArrayMap<>();

        BBCODES_LIST.put("[method]", "CONNECT");
        BBCODES_LIST.put("[host]", hostname);
        BBCODES_LIST.put("[port]", Integer.toString(port));
        BBCODES_LIST.put("[host_port]", String.format("%s:%d", hostname, port));
        BBCODES_LIST.put("[protocol]", "HTTP/1.0");
        BBCODES_LIST.put("[ssh]", String.format("%s:%d", hostname, port));

        BBCODES_LIST.put("[crlf]", "\r\n");
        BBCODES_LIST.put("[cr]", "\r");
        BBCODES_LIST.put("[lf]", "\n");
        BBCODES_LIST.put("[lfcr]", "\n\r");

        BBCODES_LIST.put("\\n", "\n");
        BBCODES_LIST.put("\\r", "\r");

        String ua = System.getProperty("http.agent");
        BBCODES_LIST.put("[ua]", ua == null ? "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36" : ua);

        if (!payload.isEmpty()) {
            for (String key : BBCODES_LIST.keySet()) {
                payload = payload.replace(key.toLowerCase(), BBCODES_LIST.get(key));
            }

            Matcher matcher = Pattern.compile("\\[(.*?)\\]").matcher(payload);
            Set<String> listKeys = getAvailableTokens();

            while (matcher.find()) {
                String group = matcher.group(1).toLowerCase();
                if (!listKeys.contains("[" + group + "]") && !group.startsWith("rotate=") && !group.startsWith("random=")) {
                    SkStatus.logWarning(String.format("<strong>Warning:</strong> [%s] tidak didukung", group));
                }
            }

            payload = parseRandom(parseRotate(payload));
            SkStatus.logDebug("Payload: " + payload.replace("\n", "\\n").replace("\r", "\\r"));
        }

        return payload;
    }

    public static boolean injectSplitPayload(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("[delay_split]")) {
            String[] split = requestPayload.split(Pattern.quote("[delay_split]"));

            for (int n = 0; n < split.length; n++) {
                String str = split[n];

                if (!injectSimpleSplit(str, out)) {
                    try {
                        out.write(str.getBytes("ISO-8859-1"));
                    } catch (UnsupportedEncodingException e2) {
                        out.write(str.getBytes());
                    }
                    out.flush();
                }

                try {
                    if (n != (split.length - 1))
                        Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

            return true;
        } else if (injectSimpleSplit(requestPayload, out)) {
            return true;
        }

        return false;
    }

    private static boolean injectSimpleSplit(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("[split]")) {
            String[] split2 = requestPayload.split(Pattern.quote("[split]"));

            for (String part : split2) {
                try {
                    out.write(part.getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e2) {
                    out.write(part.getBytes());
                }
                out.flush();
            }

            return true;
        }

        return false;
    }

    private static Map<Integer, Integer> lastRotateList = new ArrayMap<>();
    private static String lastPayload = "";

    public static String parseRotate(String payload) {
        Matcher match = Pattern.compile("\\[rotate=(.*?)\\]").matcher(payload);

        if (!lastPayload.equals(payload)) {
            restartRotateAndRandom();
            lastPayload = payload;
        }

        int i = 0;
        while (match.find()) {
            String group = match.group(1);
            String[] split = group.split(";");
            if (split.length == 0) continue;

            int split_key = lastRotateList.getOrDefault(i, -1) + 1;
            if (split_key >= split.length) split_key = 0;

            String host = split[split_key];
            payload = payload.replace(match.group(0), host);

            lastRotateList.put(i, split_key);
            i++;
        }

        return payload;
    }

    public static String parseRandom(String payload) {
        Matcher match = Pattern.compile("\\[random=(.*?)\\]").matcher(payload);

        while (match.find()) {
            String group = match.group(1);
            String[] split = group.split(";");
            if (split.length == 0) continue;

            Random r = new Random();
            int split_key = r.nextInt(split.length);
            if (split_key < 0 || split_key >= split.length) split_key = 0;

            String host = split[split_key];
            payload = payload.replace(match.group(0), host);
        }

        return payload;
    }

    public static void restartRotateAndRandom() {
        lastRotateList.clear();
    }

    public static boolean isNetworkOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            return "ERROR Obtaining IP";
        }
        return "No IP Available";
    }

    public static boolean isActiveVpn(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
        } else {
            NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_VPN);
            return (info != null && info.isConnectedOrConnecting());
        }
    }

    public static Set<String> getAvailableTokens() {
        Set<String> tokens = new HashSet<>();

        tokens.add("[method]");
        tokens.add("[host]");
        tokens.add("[port]");
        tokens.add("[host_port]");
        tokens.add("[protocol]");
        tokens.add("[ssh]");
        tokens.add("[crlf]");
        tokens.add("[cr]");
        tokens.add("[lf]");
        tokens.add("[lfcr]");
        tokens.add("\\n");
        tokens.add("\\r");
        tokens.add("[ua]");

        tokens.add("[rotate]");
        tokens.add("[delay_split]");
        tokens.add("[split]");
        tokens.add("[random]");

        return tokens;
    }
}
