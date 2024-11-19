package ticket.luckyticket.saler.model;

public class SalerMap {
    private String userId;
    private double latitude;
    private double longitude;
    private boolean isOnline;

    public SalerMap() {
        // Constructor mặc định cho Firebase
    }

    public SalerMap(String userId, double latitude, double longitude, boolean isOnline) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isOnline = isOnline;
    }

    // Getter và Setter cho các thuộc tính
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
