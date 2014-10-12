package restaurant.JPC.test.mock;

import restaurant.JPC.interfaces.Cashier;
import restaurant.JPC.interfaces.Customer;
import restaurant.JPC.interfaces.Waiter;

public class MockWaiter extends Mock implements Waiter {
	public Cashier cashier;
	public EventLog log;
	
	public MockWaiter(String name) {
		super(name);
		log = new EventLog();
	}

	public void msgHereIsCheckFor(int bill, Customer c) {
		log.add(new LoggedEvent("Received check for customer " + c.toString() + " for $" + bill));
	}

	public void msgWeAreOutOf(String choice, int tableNumber) {
	}

	public void msgWeRestocked(String name) {
	}

	public void msgOrderIsReady(String choice, int tableNumber) {
	}

}
