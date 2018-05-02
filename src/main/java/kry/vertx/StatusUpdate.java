package kry.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpClient;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientResponse;


/**
 * Verticle that updates the url accordingly.
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

	public void updateStatus(Service service) {
    HttpClient httpClient = vertx.createHttpClient();
    httpClient.getNow(80, service.getHost(), service.getURI(), new Handler<HttpClientResponse>() {
      @Override
      public void handle(HttpClientResponse httpClientResponse) {
					service.setStatus(httpClientResponse.statusCode());
					service.setLastChecked(System.currentTimeMillis());
					store.updateService(service);
      }
    });
	}
}
