package kry.vertx;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServiceStorageTest {

    private ServiceStorage store;

    @Before
    public void init() {
	store = new ServiceStorage("testStorage.json");
	store.addService(new Service("Google", "https://www.google.se"));
	store.addService(new Service("KRY", "https://www.kry.se"));
    }

    @After
    public void down() {
	     store.deleteStorage ();
    }

    @Test
    public void testAddService() {
	int before = store.getAllServices().size();
	Service s = new Service("test", "http://www.test.se");
	store.addService(s);
	assert(store.getAllServices().size() == before+1);
    }

    @Test
    public void testRemoveService() {
	store.addService(new Service("test", "http://test.se"));
	Service s = store.getAllServices().get(0);
	int amount = store.getAllServices().size();
	store.removeService(s.getId());
	assert(amount-1 == store.getAllServices().size());
    }
}
