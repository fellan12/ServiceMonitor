package kry.vertx;

import java.util.UUID;
import io.vertx.core.json.JsonObject;

/**
 * Class that represent a service
 * 
 * @author Felix De Silva
 */
public class Service {
    private String id;
    private String name;
    private String URL;
    private String status;
    private long lastChecked;
    
    public Service () {
	this.id = UUID.randomUUID().toString();
    }

    public Service(String name, String url) {
	this.id = UUID.randomUUID().toString();
	this.name = name;
	this.URL = url;
	this.status = "INCOMING";
    }
    
    /**
     * Converts a JsonObject to a Service
     * 
     * @param json - Json representation of a service
     * @return service as a Service
     */
    public static Service fromJson(JsonObject json) {
	Service service = new Service(json.getString("name"), json.getString("url"));
	service.id = json.getString("id");
	service.status = json.getString("status");
	service.lastChecked = json.getLong("lastChecked");
	return service;
    }
    
    /**
     * Converts a Service object to a JsonObject
     * @return service as a JsonObject
     */
    public JsonObject toJson(){
	JsonObject json = new JsonObject();
	json.put("id", id);
	json.put("name", name);
	json.put("url", URL);
	json.put("status", status);
	json.put("lastChecked", lastChecked);
	return json;
    }
    
    /**
     * Get host from URL
     * @return host from URL parameter
     */
    public String getHost(){
	String url = URL;
	if(url.contains("://")) url = url.split("://")[1];
	if(URL.contains("/")) url = url.split("/")[0];
	return url;
    }
    
    /**
     * get URI from URL
     * @return URI from URL parameter
     */
    public String getURI(){
	String uri = "/";
	String url = URL;
	if (url.contains("://")) url = url.split("://")[1];
	if (url.contains("/")) {
	    String[] uris = url.split("/");
	    for(int i = 1; i < uris.length; i++) {
		uri += uris[i] + "/";
	    }
	}
	return uri;
    }

    public String getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public String getURL(){
	return URL;
    }

    public String getStatus(){
	return status;
    }

    public long getLastChecked(){
	return lastChecked;
    }

    public void setId(String id){
	this.id = id;
    }

    public void setStatus(String status){
	this.status = status;
    }

    public void setLastChecked(long lastChecked){
	this.lastChecked = lastChecked;
    }

    @Override
    public String toString() {
	return toJson().toString();
    }

}
