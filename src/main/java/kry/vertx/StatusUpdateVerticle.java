package kry.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.ext.web.client.WebClient;



/**
 * Verticle that updates the service accordingly.
 */
public class StatusUpdateVerticle extends AbstractVerticle {

    ServiceStorage store;

    public StatusUpdateVerticle(ServiceStorage store) {
	this.store = store;
    }

    @Override
    public void start() throws Exception {
    	vertx.setPeriodic(60000, handler -> {
    	    for (Service service : store.getAllServices()) {
    		      updateStatus(service);
    	    }
    	});
    }

    /**
     * Update the status of a service
     *
     * @param service - service to be updated
     */
    public void updateStatus(Service service) {
      WebClient.create(vertx)
        .get(service.getHost(), service.getURI())
        .send(ar -> {
          String updatedStatus = "FAIL";
          if (ar.succeeded()) {
            int code = ar.result().statusCode();
            if (code == 200) {
              updatedStatus = "OK";
            }
          } else {
            System.err.println("[ERROR] "+ar.cause().getMessage());
          }
          service.setStatus(updatedStatus);
          service.setLastChecked(System.currentTimeMillis());
          store.updateService(service);
        });
    }
}
