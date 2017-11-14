package aroundme.mcproject.com.safetyapp;

/**
 * Created by papa on 14-11-2017.
 */

public class SOSMessage {
    public String message;
    public String username;
    public String distance;
    public String latitude;
    public String longitude;

    public SOSMessage() {

    }
    public SOSMessage(String username, String message, String lat, String lon) {
        this.username = username;
        this.message = message;
        this.latitude =lat;
        this.longitude=lon;
    }
}
