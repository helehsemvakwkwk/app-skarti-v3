package com.skartivpn.app.custom;

import com.skartivpn.inject.config.Settings;

import org.json.JSONObject;

/**
 * One user-defined custom tunnel profile, in the style of HTTP Custom /
 * DarkTunnel: a host, a port, an SNI and a payload, plus the mode that
 * decides how the existing tunnel engine uses them.
 *
 * The profile itself never talks to the tunnel engine. When it is applied,
 * its values are written into the same private preferences the old
 * server/payload selection used to fill from the encrypted config
 * (sshServer, sshPort, customSNI, proxyPayload, tunnelType, ...), so the
 * engine, the START button and LaunchVpn keep working unchanged.
 */
public class CustomProfile {

    public String id;
    public String name;
    public String host;
    public String port;
    public int mode;
    public String sni;
    public String payload;
    public String proxyHost;
    public String proxyPort;
    public String slowDnsKey;
    public String slowDnsHost;
    public String dnsKey;

    public CustomProfile() {
        this.id = "";
        this.name = "";
        this.host = "";
        this.port = "22";
        this.mode = Settings.bTUNNEL_TYPE_SSH_DIRECT;
        this.sni = "";
        this.payload = "";
        this.proxyHost = "";
        this.proxyPort = "";
        this.slowDnsKey = "";
        this.slowDnsHost = "";
        this.dnsKey = "";
    }

    public String modeLabel() {
        if (mode == Settings.bTUNNEL_TYPE_SSH_PROXY) {
            return "SSH Proxy";
        } else if (mode == Settings.bTUNNEL_TYPE_SSL_TLS) {
            return "SSL/TLS";
        } else if (mode == Settings.bTUNNEL_TYPE_PAY_SSL) {
            return "SSL + Payload";
        } else if (mode == Settings.bTUNNEL_TYPE_SLOWDNS) {
            return "SlowDNS";
        }
        return "SSH Direct";
    }

    public boolean needsProxy() {
        return mode == Settings.bTUNNEL_TYPE_SSH_PROXY;
    }

    public boolean needsSni() {
        return mode == Settings.bTUNNEL_TYPE_SSL_TLS
                || mode == Settings.bTUNNEL_TYPE_PAY_SSL;
    }

    public boolean needsPayload() {
        return mode == Settings.bTUNNEL_TYPE_SSH_PROXY
                || mode == Settings.bTUNNEL_TYPE_PAY_SSL;
    }

    public boolean needsSlowDns() {
        return mode == Settings.bTUNNEL_TYPE_SLOWDNS;
    }

    /** Port the engine will actually use (SlowDNS is fixed to 2222). */
    public String effectivePort() {
        if (needsSlowDns()) {
            return "2222";
        }
        if (port == null || port.isEmpty()) {
            return "22";
        }
        return port;
    }

    public String summary() {
        String h = host == null ? "" : host;
        return h + ":" + effectivePort();
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id == null ? "" : id);
            obj.put("name", name == null ? "" : name);
            obj.put("host", host == null ? "" : host);
            obj.put("port", port == null ? "" : port);
            obj.put("mode", mode);
            obj.put("sni", sni == null ? "" : sni);
            obj.put("payload", payload == null ? "" : payload);
            obj.put("proxyHost", proxyHost == null ? "" : proxyHost);
            obj.put("proxyPort", proxyPort == null ? "" : proxyPort);
            obj.put("slowDnsKey", slowDnsKey == null ? "" : slowDnsKey);
            obj.put("slowDnsHost", slowDnsHost == null ? "" : slowDnsHost);
            obj.put("dnsKey", dnsKey == null ? "" : dnsKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static CustomProfile fromJson(JSONObject obj) {
        CustomProfile p = new CustomProfile();
        if (obj == null) {
            return p;
        }
        p.id = obj.optString("id", "");
        p.name = obj.optString("name", "");
        p.host = obj.optString("host", "");
        p.port = obj.optString("port", "22");
        p.mode = obj.optInt("mode", Settings.bTUNNEL_TYPE_SSH_DIRECT);
        p.sni = obj.optString("sni", "");
        p.payload = obj.optString("payload", "");
        p.proxyHost = obj.optString("proxyHost", "");
        p.proxyPort = obj.optString("proxyPort", "");
        p.slowDnsKey = obj.optString("slowDnsKey", "");
        p.slowDnsHost = obj.optString("slowDnsHost", "");
        p.dnsKey = obj.optString("dnsKey", "");
        return p;
    }
}
