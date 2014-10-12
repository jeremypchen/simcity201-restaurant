package restaurant.JPC.test.mock;

import restaurant.JPC.interfaces.Cashier;
import restaurant.JPC.interfaces.Cook;
import restaurant.JPC.interfaces.Waiter;

public class MockCook extends Mock implements Cook {
	public MockCook(String name) {
		super(name);
		log = new EventLog();
	}
	
	public Cashier cashier;
	public EventLog log;
	
	public void msgHereIsAnOrder(Waiter w, String choice, int tableNumber) {
		log.add(new LoggedEvent("Received HereIsOrder from waiter. Choice: " + choice + ", Table Number: " + tableNumber));
	}
	
	public void msgHereIsYourFood(String food, int amount) {
		log.add(new LoggedEvent("Received HereIsYourFood from market. Food: " + food + ", Amount: " + amount));
	}

	public void msgCannotFulfillOrder(String food) {
		log.add(new LoggedEvent("Received CannotFulfillOrder from market. Food: " + food));
	}
}
