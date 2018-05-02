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

  private JsonObject getStorage(){
    String json = "";
		try {
			json = new String(Files.readAllBytes(storagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JsonObject(json);
  }

  private void writeToStorage(JsonObject json) {
		byte[] bytes = json.encodePrettily().getBytes();
		try {
			Files.write(storagePath, bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

  public List<Service> getAllServices() {
    System.out.println("GETTING SERVICES FROM STORAGE");
    List<Service> services = new ArrayList<>();
		JsonArray array = getStorage().getJsonArray("services");
		for (int i = 0; i < array.size(); i++) {
			services.add(Service.fromJson(array.getJsonObject(i)));
		}
    System.out.println("GOT ALL SERVICES FROM STORAGE");
  	return services;
  }

  public void removeService(String id){
    System.out.println("REMOVE SERVICE: " + id);
    JsonArray array = getStorage().getJsonArray("services");
		for (int i = 0; i < array.size(); i++) {
			Service service = Service.fromJson(array.getJsonObject(i));
      System.out.println(service.getId());
			if (service.getId().equals(id)) {
				array.remove(i);
        writeToStorage(new JsonObject().put("services", array));
				break;
			}
		}
  }

  public void addService(Service serv){
    System.out.println("ADD SERVICE: " + serv.getURL());
    JsonArray array = getStorage().getJsonArray("services");
    array.add(serv.toJson());
    writeToStorage(new JsonObject().put("services", array));
  }

  public void updateService(Service service){
    removeService(service.getId());
    JsonArray array = getStorage().getJsonArray("services");
    array.add(service.toJson());
    writeToStorage(new JsonObject().put("services", array));
  }
}
