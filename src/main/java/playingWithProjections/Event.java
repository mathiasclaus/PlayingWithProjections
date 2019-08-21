package playingWithProjections;

import java.util.Map;

public class Event {
    String type;
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    String timestamp;
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    Map<String, String> payload;
    public Map<String, String> getPayload() { return payload; }
    public void setPayload(Map<String, String> payload) { this.payload = payload; }
}
