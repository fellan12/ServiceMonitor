package kry.vertx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Class that handles storage
 *
 * @author Felix De Silva
 */
class ServiceStorage {
    private Path storagePath;

    public ServiceStorage(String path){
	this.storagePath = Paths.get(path);
	if (!Files.exists(storagePath)) {
	    writeToStorage(new JsonObject().put("services", new JsonArray()));
	}

    }

    /**
     * Get json storage as JsonObject
     *
     * @return JsonObject of json storage
     */
    private JsonObject getStorage(){
	String json = "";
	try {
	    json = new String(Files.readAllBytes(storagePath));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return new JsonObject(json);
    }

    /**
     * write JsonObject to storage
     *
     * @param json - Object to be stored
     */
    private void writeToStorage(JsonObject json) {
    	byte[] bytes = json.encodePrettily().getBytes();
    	try {
    	    Files.write(storagePath, bytes);
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    }

    /**
     * delete storage
     */
    public void deleteStorage() {
      try {
          Files.deleteIfExists (storagePath);
      } catch (IOException e) {
          e.printStackTrace();
      }
    }

    /**
     * Get all services from storage
     *
     * @return list of services from storage as Service objects
     */
    public List<Service> getAllServices() {
	List<Service> services = new ArrayList<>();
	JsonArray array = getStorage().getJsonArray("services");
	for (int i = 0; i < array.size(); i++) {
	    services.add(Service.fromJson(array.getJsonObject(i)));
	}
	return services;
    }

    /**
     * Add a service from storage
     *
     * @param service - service to be added
     */
    public void addService(Service service){
	JsonArray array = getStorage().getJsonArray("services");
	array.add(service.toJson());
	writeToStorage(new JsonObject().put("services", array));
    }


    /**
     * Remove a service from storage
     *
     * @param id - id of the storage to be removed
     */
    public void removeService(String id){
	JsonArray array = getStorage().getJsonArray("services");
	for (int i = 0; i < array.size(); i++) {
	    Service service = Service.fromJson(array.getJsonObject(i));
	    if (service.getId().equals(id)) {
		array.remove(i);
		writeToStorage(new JsonObject().put("services", array));
		break;
	    }
	}
    }

    /**
     * Update a service from storage
     *
     * @param service - service to be updated
     */
    public void updateService(Service service){
	removeService(service.getId());
	JsonArray array = getStorage().getJsonArray("services");
	array.add(service.toJson());
	writeToStorage(new JsonObject().put("services", array));
    }
}
