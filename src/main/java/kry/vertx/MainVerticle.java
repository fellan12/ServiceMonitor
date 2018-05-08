package kry.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.DeploymentOptions;

/**
* Main class that deploys other verticles
* @author Felix De Silva
*/
public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> fut) {

    //Set deployment options of config file
    DeploymentOptions options = new DeploymentOptions().setConfig(config());

    ServiceStorage store = new ServiceStorage(config().getString("storage.path", "database/storage.json"));
    vertx.deployVerticle(new MonitorVerticle(store), options);
    vertx.deployVerticle(new StatusUpdateVerticle(store), options);
  }

}
