package models;

public class LocationSignalPackage {

    public Long id;
    public String SSID;
    public String Lat;
    public String Long;
    public String Rssi;

    public LocationSignalPackage(java.lang.Long id, String SSID, String lat, String aLong, String rssi) {
        this.id = id;
        this.SSID = SSID;
        Lat = lat;
        Long = aLong;
        Rssi = rssi;
    }
}
