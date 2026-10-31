package com.skartivpn.app.model;

/**
 * Model class for Server Item
 */
public class ServerItem {
    private String id;
    private String name;
    private String location;
    private String region;
    private String countryCode;
    private int ping;
    private int load; // Server load percentage (0-100)
    private boolean isPremium;
    private boolean isOnline;

    public ServerItem(String id, String name, String location, String region, String countryCode, int ping, boolean isOnline) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.region = region;
        this.countryCode = countryCode;
        this.ping = ping;
        this.load = 0; // Default load
        this.isPremium = false;
        this.isOnline = isOnline;
    }

    public ServerItem(String id, String name, String location, String region, String countryCode, int ping, boolean isPremium, boolean isOnline) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.region = region;
        this.countryCode = countryCode;
        this.ping = ping;
        this.load = 0; // Default load
        this.isPremium = isPremium;
        this.isOnline = isOnline;
    }
    
    public ServerItem(String id, String name, String location, String region, String countryCode, int ping, int load, boolean isPremium, boolean isOnline) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.region = region;
        this.countryCode = countryCode;
        this.ping = ping;
        this.load = load;
        this.isPremium = isPremium;
        this.isOnline = isOnline;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getRegion() {
        return region;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public int getPing() {
        return ping;
    }
    
    public int getLoad() {
        return load;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public boolean isOnline() {
        return isOnline;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }
    
    public void setLoad(int load) {
        this.load = load;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    /**
     * Get ping quality indicator
     * @return "Excellent", "Good", "Fair", or "Poor"
     */
    public String getPingQuality() {
        if (ping < 50) return "Excellent";
        else if (ping < 100) return "Good";
        else if (ping < 150) return "Fair";
        else return "Poor";
    }

    /**
     * Get ping color resource based on quality
     * @return color code for ping indicator
     */
    public String getPingColor() {
        if (ping < 50) return "#04D000"; // Green
        else if (ping < 100) return "#EEFD00"; // Yellow
        else if (ping < 150) return "#FFA500"; // Orange
        else return "#FD1C0D"; // Red
    }
    
    /**
     * Get load color based on percentage
     * @return color code for load indicator
     */
    public String getLoadColor() {
        if (load < 50) return "#04D000"; // Green - Low load
        else if (load < 75) return "#EEFD00"; // Yellow - Medium load
        else if (load < 90) return "#FFA500"; // Orange - High load
        else return "#FD1C0D"; // Red - Very high load
    }
    
    /**
     * Get load status text
     * @return "Low", "Medium", "High", or "Critical"
     */
    public String getLoadStatus() {
        if (load < 50) return "Low";
        else if (load < 75) return "Medium";
        else if (load < 90) return "High";
        else return "Critical";
    }
}
