package kry.vertx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

class ServiceStorage {
  private Path storagePath;

  public ServiceStorage(String path){
    this.storagePath = Paths.get(path);
  }

  public JsonObject getStorage(){
    String json = "";
		try {
			json = new String(Files.readAllBytes(storagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JsonObject(json);
  }

  public List<Service> getAllServices() {
    List<Service> services = new ArrayList<>();
  		JsonArray a = getStorage().getJsonArray("services");
  		for (int i = 0; i < a.size(); i++) {
  			JsonObject json = a.getJsonObject(i);
  			services.add(Service.fromJson(json));
  		}
  	return services;
}

  public void updateStatus(String id, String status){

  }
}
