package kry.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpClient;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientResponse;


/**
 * Verticle that updates the service accordingly.
 */
public class StatusUpdate extends AbstractVerticle {

    ServiceStorage store;

    public StatusUpdate(ServiceStorage store) {
	this.store = store;
    }

    @Override
    public void start() throws Exception {
	vertx.setPeriodic(10000, handler -> {
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
	HttpClient httpClient = vertx.createHttpClient();
	httpClient.getNow(80, service.getHost(), service.getURI(), new Handler<HttpClientResponse>() {
	    @Override
	    public void handle(HttpClientResponse httpClientResponse) {
		System.out.println(service.getURL());
		System.out.println("STATUSCODE: " +httpClientResponse.statusCode());
		service.setStatus(httpClientResponse.statusCode() == 200 ? "OK" : "FAIL");
		service.setLastChecked(System.currentTimeMillis());
		store.updateService(service);
	    }
	});
    }
}
