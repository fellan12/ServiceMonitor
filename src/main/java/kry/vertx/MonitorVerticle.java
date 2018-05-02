package kry.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.*;

import org.apache.commons.validator.routines.UrlValidator;


public class MonitorVerticle extends AbstractVerticle {

  private Map<String, Service> services = new HashMap<>();
  private ServiceStorage store;

  public MonitorVerticle(ServiceStorage store){
    this.store = store;
  }

  @Override
  public void start(Future<Void> fut) {

    // Create a router object.
    Router router = Router.router(vertx);

    router.route("/*").handler(StaticHandler.create("assets"));
    router.get("/service").handler(this::getAll);
    router.route("/service*").handler(BodyHandler.create());
    router.post("/service").handler(this::add);
    router.delete("/service/:id").handler(this::delete);

    // Create the HTTP server and pass the "accept" method to the request handler.
    vertx
        .createHttpServer()
        .requestHandler(router::accept)
        .listen(
            // Retrieve the port from the configuration,
            // default to 8080.
            config().getInteger("http.port", 8080),
            result -> {
              if (result.succeeded()) {
                fut.complete();
              } else {
                fut.fail(result.cause());
              }
            }
        );
  }

  private void add(RoutingContext routingContext) {
    System.out.println("ADDING");
    // Read the request's content and create an instance of Whisky.
    String name = routingContext.getBodyAsJson().getString("name");
    String url = routingContext.getBodyAsJson().getString("url");

    if(verifyURL(url)){
      final Service serv = new Service(name, url);

      // Add it to the backend storage
      store.addService(serv);

      // Return the created service as JSON
      routingContext.response()
          .setStatusCode(201) //Created
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(serv));
    } else {
      routingContext.response().setStatusCode(400).end(); //Bad Request
    }

  }

  private void delete(RoutingContext routingContext) {
    System.out.println("DELETE");
    String id = routingContext.request().getParam("id");

    if (id == null) {
      routingContext.response().setStatusCode(400).end(); //Bad Request
    } else {
      store.removeService(id);
      routingContext.response().setStatusCode(204).end(); //No content
    }
  }

  private void getAll(RoutingContext routingContext) {
    System.out.println("GET ALL");
    List<Service> servs = store.getAllServices();

    JsonArray array = new JsonArray();
		for (Service s : servs) {
			array.add(s.toJson());
		}

    JsonObject json = new JsonObject();
    json.put("services", array);

    routingContext.response()
        .setStatusCode(200) // Ok
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(json));
  }

  public boolean verifyURL(String url){
    String[] schemes = {"http","https"};
    UrlValidator urlValidator = new UrlValidator(schemes);
    if (urlValidator.isValid(url)) {
      System.out.println("URL IS VALID");
       return true;
    }
    System.out.println("URL IS INVALID");
    return false;
  }

}
