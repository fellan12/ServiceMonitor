package kry.vertx;

import java.util.UUID;
import io.vertx.core.json.JsonObject;


public class Service {
  private String id;
  private String name;
  private String URL;
  private int status;
  private long lastChecked;

  public Service(String name, String url) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.URL = url;
    if(url.contains("://")) this.URL = url.split("://")[1];
    if(URL.contains("/")) this.URL = URL.split("/")[0];
  }

  public static Service fromJson(JsonObject json) {
    System.out.println("CREATING SERVICE FROM JSON");
		Service service = new Service(json.getString("name"), json.getString("url"));
		service.id = json.getString("id");
		service.status = json.getInteger("status");
		service.lastChecked = json.getLong("lastChecked");
		return service;
	}

  public JsonObject toJson(){
    JsonObject json = new JsonObject();
		json.put("id", id);
		json.put("name", name);
		json.put("url", URL);
		json.put("status", status);
		json.put("lastChecked", lastChecked);
    return json;
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

  public int getStatus(){
    return status;
  }

  public long getLastChecked(){
    return lastChecked;
  }

  public void setId(String id){
    this.id = id;
  }

  public void setStatus(int status){
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
