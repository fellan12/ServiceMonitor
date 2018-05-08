package kry.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.*;

import org.apache.commons.validator.routines.UrlValidator;

/**
* Monitor verticle handles the routing of requests
*
* @author Felix De Silva
*/
public class MonitorVerticle extends AbstractVerticle {

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
    router.get("/test").handler (body -> body.response ().end ("FOR TESTING"));

    // Create the HTTP server and pass the "accept" method to the request handler.
    vertx
    .createHttpServer()
    .requestHandler(router::accept)
    .listen(
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

  /**
  * Get all services
  *
  * @param routingContext - Context of the request
  */
  private void getAll(RoutingContext routingContext) {
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

  /**
  * Add a new service
  *
  * @param routingContext - Context of the request
  */
  private void add(RoutingContext routingContext) {
    System.out.println("Add new service");
    String name = routingContext.getBodyAsJson().getString("name");
    String url = routingContext.getBodyAsJson().getString("url");

    if(verifyURL(url)){
      final Service serv = new Service(name, url);
      store.addService(serv);

      routingContext.response()
      .setStatusCode(201) //Created
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(Json.encodePrettily(serv));
    } else {
      routingContext.response().setStatusCode(400).end(); //Bad Request
    }

  }

  /**
  * Delete a service
  *
  * @param routingContext - Context of the request
  */
  private void delete(RoutingContext routingContext) {
    System.out.println("Remove service");
    String id = routingContext.request().getParam("id");

    if (id == null) {
      routingContext.response().setStatusCode(400).end(); //Bad Request
    } else {
      store.removeService(id);
      routingContext.response().setStatusCode(204).end(); //No content
    }
  }

  /**
  * Verifies is a URL string is of proper format
  *
  * @param url - URL string
  * @return true if it is of proper format, false otherwise
  */
  public boolean verifyURL(String url){
    String[] schemes = {"http","https"};
    UrlValidator urlValidator = new UrlValidator(schemes);
    if (urlValidator.isValid(url)) {
      return true;
    }
    return false;
  }

}
