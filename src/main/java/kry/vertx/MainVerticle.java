package kry.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> fut) {

    //Set deployment options
    DeploymentOptions options = new DeploymentOptions().setConfig(config());

    ServiceStorage store = new ServiceStorage();
    vertx.deployVerticle(new MonitorVerticle(store), options);
    vertx.deployVerticle(new StatusUpdate(store), options);
  }

}
