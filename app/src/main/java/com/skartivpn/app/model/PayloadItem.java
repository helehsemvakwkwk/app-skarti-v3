package com.skartivpn.app.model;

/**
 * Model class for Payload Item
 */
public class PayloadItem {
    private String id;
    private String name;
    private String description;
    private String payloadContent;
    private String type; // DIRECT, SNI, SLOWDNS, SSL/TLS, WEBSOCKET
    private boolean isActive;
    private boolean isFavorite;

    public PayloadItem(String id, String name, String description, String type, boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.payloadContent = "";
        this.type = type;
        this.isActive = isActive;
        this.isFavorite = false;
    }

    public PayloadItem(String id, String name, String description, String payloadContent, String type, boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.payloadContent = payloadContent;
        this.type = type;
        this.isActive = isActive;
        this.isFavorite = false;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPayloadContent() {
        return payloadContent;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPayloadContent(String payloadContent) {
        this.payloadContent = payloadContent;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    /**
     * Get payload type icon resource
     */
    public int getTypeIcon() {
        switch (type.toUpperCase()) {
            case "DIRECT":
                return android.R.drawable.ic_menu_directions;
            case "SNI":
                return android.R.drawable.ic_menu_share;
            case "SLOWDNS":
                return android.R.drawable.ic_menu_compass;
            case "SSL/TLS":
                return android.R.drawable.ic_lock_lock;
            case "WEBSOCKET":
                return android.R.drawable.ic_menu_rotate;
            default:
                return android.R.drawable.ic_menu_help;
        }
    }

    /**
     * Get payload type color
     */
    public String getTypeColor() {
        switch (type.toUpperCase()) {
            case "DIRECT":
                return "#04D000"; // Green
            case "SNI":
                return "#1E88E5"; // Blue
            case "SLOWDNS":
                return "#FFA500"; // Orange
            case "SSL/TLS":
                return "#9C27B0"; // Purple
            case "WEBSOCKET":
                return "#00BCD4"; // Cyan
            default:
                return "#9cb3b4"; // Gray
        }
    }
}
