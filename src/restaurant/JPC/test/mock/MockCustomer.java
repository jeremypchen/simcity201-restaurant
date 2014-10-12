package restaurant.JPC.test.mock;

import restaurant.JPC.gui.Table;
import restaurant.JPC.interfaces.Cashier;
import restaurant.JPC.interfaces.Customer;

public class MockCustomer extends Mock implements Customer {
	public Cashier cashier;
	public EventLog log;
	
	public MockCustomer(String name){
		super(name);
		log = new EventLog();
	}

	public void msgHereIsCheck(int bill) {
		log.add(new LoggedEvent("Received HereIsCheck from waiter. Bill: " + bill));
	}

	public Table getTable() {
		return null;
	}
}
