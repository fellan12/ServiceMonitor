package kry.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a verticle. A verticle is a _Vert.x component_. This verticle is implemented in Java
 */
public class MainVerticle extends AbstractVerticle {

  private Map<String, Service> services = new HashMap<>();
  private ServiceStorage store;

  /**
   * This method is called when the verticle is deployed. It creates a HTTP server and registers a simple request
   * handler.
   * <p/>
   * Notice the `listen` method. It passes a lambda checking the port binding result. When the HTTP server has been
   * bound on the port, it call the `complete` method to inform that the starting has completed. Else it reports the
   * error.
   *
   * @param fut the future
   */
  @Override
  public void start(Future<Void> fut) {

    // createExampleData();

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

    store = new ServiceStorage();
    vertx.deployVerticle(new StatusUpdate(store));
  }

  private void add(RoutingContext routingContext) {
    System.out.println("ADDING");
    // Read the request's content and create an instance of Whisky.

    final Service serv = new Service(routingContext.getBodyAsJson().getString("name"),
                                      routingContext.getBodyAsJson().getString("url"));

    // Add it to the backend map
    services.put(serv.getId(), serv);

    // Return the created service as JSON
    routingContext.response()
        .setStatusCode(201) //Created
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(serv));
  }

  private void delete(RoutingContext routingContext) {
    System.out.println("DELETE ONE");
    String id = routingContext.request().getParam("id");
    if (id == null) {
      routingContext.response().setStatusCode(400).end(); //Bad Request
    } else {
      services.remove(id);
      routingContext.response().setStatusCode(204).end(); //No content
    }
  }

  private void getAll(RoutingContext routingContext) {
    System.out.println("GET ALL");

    // Write the HTTP response
    // The response is in JSON using the utf-8 encoding
    // We returns the list of bottles
    routingContext.response()
        .setStatusCode(200) // Ok
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(services.values()));
  }

  private void createExampleData() {
    Service kry = new Service("KRY", "www.kry.se");
    services.put(kry.getId(), kry);
    Service google = new Service("Google", "www.google.se");
    services.put(google.getId(), google);
  }

}
