package hao.sakahao.com.hao;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Noel on 7/16/2016.
 */
public class Hao {
    public String name;
    public String location;
    public String description;
    public double lat;
    public double longitude;

    public Hao(String name, String location, String description, double lat, double lonitude){
        this.name = name;
        this.location = location;
        this.description = description;
        this.lat = lat;
        this.longitude = lonitude;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", "test");
        result.put("name", name);
        result.put("location", location);
        result.put("description", description);
        result.put("latitude", lat);
        result.put("longitude", longitude);
        return result;
    }


}
