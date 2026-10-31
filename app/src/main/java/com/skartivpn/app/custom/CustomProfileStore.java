package com.skartivpn.app.custom;

import android.content.Context;
import android.content.SharedPreferences;

import com.skartivpn.inject.config.Settings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stores custom profiles as a JSON array in a plain SharedPreferences file.
 * This is deliberately separate from the tunnel engine's private
 * (SecurePreferences) store: the list is user data, while the engine keys
 * are only written when a profile is applied, in PhreakersMainActivity.
 */
public class CustomProfileStore {

    private static final String PREFS_NAME = "SkartiCustomProfiles";
    private static final String KEY_PROFILES = "profiles";
    private static final String KEY_ACTIVE_ID = "active_id";
    private static final String KEY_MIGRATED = "migrated";

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static List<CustomProfile> load(Context context) {
        List<CustomProfile> result = new ArrayList<CustomProfile>();
        try {
            String raw = prefs(context).getString(KEY_PROFILES, "");
            if (raw == null || raw.isEmpty()) {
                return result;
            }
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.optJSONObject(i);
                if (obj == null) {
                    continue;
                }
                CustomProfile profile = CustomProfile.fromJson(obj);
                if (profile.id == null || profile.id.isEmpty()) {
                    continue;
                }
                result.add(profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void save(Context context, List<CustomProfile> profiles) {
        try {
            JSONArray array = new JSONArray();
            for (CustomProfile profile : profiles) {
                array.put(profile.toJson());
            }
            prefs(context).edit().putString(KEY_PROFILES, array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CustomProfile getActive(Context context) {
        List<CustomProfile> profiles = load(context);
        if (profiles.isEmpty()) {
            return null;
        }
        String activeId = prefs(context).getString(KEY_ACTIVE_ID, "");
        for (CustomProfile profile : profiles) {
            if (profile.id.equals(activeId)) {
                return profile;
            }
        }
        return profiles.get(0);
    }

    public static void setActive(Context context, String id) {
        prefs(context).edit().putString(KEY_ACTIVE_ID, id == null ? "" : id).apply();
    }

    public static void upsert(Context context, CustomProfile profile) {
        if (profile.id == null || profile.id.isEmpty()) {
            profile.id = UUID.randomUUID().toString();
        }
        List<CustomProfile> profiles = load(context);
        boolean replaced = false;
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).id.equals(profile.id)) {
                profiles.set(i, profile);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            profiles.add(profile);
        }
        save(context, profiles);
    }

    public static void delete(Context context, String id) {
        List<CustomProfile> profiles = load(context);
        List<CustomProfile> kept = new ArrayList<CustomProfile>();
        for (CustomProfile profile : profiles) {
            if (!profile.id.equals(id)) {
                kept.add(profile);
            }
        }
        save(context, kept);
        String activeId = prefs(context).getString(KEY_ACTIVE_ID, "");
        if (activeId.equals(id)) {
            if (kept.isEmpty()) {
                setActive(context, "");
            } else {
                setActive(context, kept.get(0).id);
            }
        }
    }

    /**
     * One-time migration from the old server/payload world: if no custom
     * profile exists yet, build one from the tunnel values the old
     * _pos_() call last wrote into the private preferences, so a user
     * updating the app does not lose the connection that last worked.
     * Runs at most once, even when there is nothing to migrate.
     */
    public static void migrateIfNeeded(Context context, SharedPreferences privatePrefs) {
        SharedPreferences store = prefs(context);
        if (store.getBoolean(KEY_MIGRATED, false)) {
            return;
        }
        try {
            String server = "";
            String port = "";
            int tunnelType = Settings.bTUNNEL_TYPE_SSH_DIRECT;
            String sni = "";
            String payload = "";
            String proxyHost = "";
            String proxyPort = "";
            String slowKey = "";
            String slowHost = "";
            String dnsKey = "";
            String serverName = "";
            if (privatePrefs != null) {
                server = privatePrefs.getString(Settings.SERVIDOR_KEY, "");
                port = privatePrefs.getString(Settings.SERVIDOR_PORTA_KEY, "");
                tunnelType = privatePrefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);
                sni = privatePrefs.getString(Settings.CUSTOM_SNI, "");
                payload = privatePrefs.getString(Settings.CUSTOM_PAYLOAD_KEY, "");
                proxyHost = privatePrefs.getString(Settings.PROXY_IP_KEY, "");
                proxyPort = privatePrefs.getString(Settings.PROXY_PORTA_KEY, "");
                slowKey = privatePrefs.getString(Settings.CHAVE_KEY, "");
                slowHost = privatePrefs.getString(Settings.NAMESERVER_KEY, "");
                dnsKey = privatePrefs.getString(Settings.DNS_KEY, "");
                serverName = privatePrefs.getString("ServerName", "");
            }
            if (server != null && !server.isEmpty()) {
                CustomProfile profile = new CustomProfile();
                profile.id = UUID.randomUUID().toString();
                profile.name = (serverName == null || serverName.isEmpty()) ? "Profil Lama" : serverName;
                if (tunnelType == Settings.bTUNNEL_TYPE_SLOWDNS) {
                    profile.host = (slowHost == null || slowHost.isEmpty()) ? server : slowHost;
                    profile.port = "2222";
                } else {
                    profile.host = server;
                    profile.port = (port == null || port.isEmpty()) ? "22" : port;
                }
                profile.mode = tunnelType;
                profile.sni = sni == null ? "" : sni;
                profile.payload = payload == null ? "" : payload;
                profile.proxyHost = proxyHost == null ? "" : proxyHost;
                profile.proxyPort = proxyPort == null ? "" : proxyPort;
                profile.slowDnsKey = slowKey == null ? "" : slowKey;
                profile.slowDnsHost = slowHost == null ? "" : slowHost;
                profile.dnsKey = dnsKey == null ? "" : dnsKey;
                List<CustomProfile> profiles = new ArrayList<CustomProfile>();
                profiles.add(profile);
                save(context, profiles);
                setActive(context, profile.id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        store.edit().putBoolean(KEY_MIGRATED, true).apply();
    }
}
