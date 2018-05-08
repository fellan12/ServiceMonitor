package kry.vertx;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class MonitorVerticleTest {

  private Vertx vertx;
  private ServiceStorage store;

  @Before
  public void setUp(TestContext context) {
    vertx = Vertx.vertx();
    store = new ServiceStorage("testStorage.json");
    store.addService(new Service("Google", "https://www.google.se"));
    store.addService(new Service("KRY", "https://www.kry.se"));
    vertx.deployVerticle(new MonitorVerticle(store), context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
    store.deleteStorage ();
  }

  @Test
  public void testConnection(TestContext context) {
    final Async async = context.async();
    vertx.createHttpClient().getNow(8080,"localhost", "/test",response -> {
      response.handler(body -> {
        context.assertTrue(body.toString().contains("FOR TESTING"));
        async.complete();
      });
    });
  }

  @Test
  public void testGetAllServices(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/service", response -> {
      response.handler(body -> {
        JsonObject json = new JsonObject(body);
        JsonArray array = json.getJsonArray("services");
        context.assertTrue(array.getJsonObject(0).getString("name").equals("Google"));
        context.assertTrue(array.getJsonObject(1).getString("name").equals("KRY"));
        async.complete();
      });
    });

  }

  @Test
  public void testAddService(TestContext context) {
    Async async = context.async();
    final String json = Json.encode(new Service("test", "https://www.test.se"));
    vertx.createHttpClient().post(8080, "localhost", "/service")
    .putHeader("content-type", "application/json")
    .putHeader("content-length", Integer.toString(json.length()))
    .handler(response -> {
      context.assertEquals(response.statusCode(), 201);
      context.assertTrue(response.headers().get("content-type").contains("application/json"));

      response.bodyHandler(body -> {
        final JsonObject serv = body.toJsonObject ();
        context.assertEquals(serv.getString ("name"), "test");
        context.assertEquals(serv.getString ("status"), "INCOMING");
        context.assertEquals(serv.getString ("url"), "https://www.test.se");
        context.assertNotNull(serv.getString ("id"));

        async.complete();
      });
    }).write(json).end();
  }

  @Test
  public void testDeleteService(TestContext context) {
    Async async = context.async();
    vertx.createHttpClient().delete(8080, "localhost", "/service/:"+store.getAllServices ().get (0).getId ())
    .handler(response -> {
      context.assertEquals(response.statusCode(), 204);
      context.assertTrue(response.headers().entries ().size () == 1);   //Should only contain Content-Length=0
      response.bodyHandler(body -> {
        context.assertEquals(body.toString (), "");
      });
      async.complete();
    }).end();
  }
}
