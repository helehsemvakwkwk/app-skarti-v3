package com.skartivpn.app.util;

public class PayloadModel {
    String sSname,sSinfo;
    public String getServerName() {
        return sSname;
    }
    public void setServerName(String name) {
        this.sSname = name;
    }

    public String getServerInfo() {
        return sSinfo;
    }
    public void setServerInfo(String info) {
        this.sSinfo = info;
    }
}
