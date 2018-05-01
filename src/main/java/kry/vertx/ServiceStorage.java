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

  public List<Service> getAllServices() {
  List<Service> services = new ArrayList<>();
  System.out.println("Poll");
  return services;
}

  public void updateStatus(String id, String status){

  }
}
