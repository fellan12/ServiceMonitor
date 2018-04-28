package kry.vertx;

import java.util.UUID;

public class Service {
  private final String id;
  private String name;
  private String URL;
  private String status = "";
  private String lastCheck = "";

  public Service(String name, String url) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.URL = url;
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

  public String getLastCheck(){
    return lastCheck;
  }

  public void setStatus(String status){
    this.status = status;
  }

  public void setLastCheck(String lastCheck){
    this.lastCheck = lastCheck;
  }

}
