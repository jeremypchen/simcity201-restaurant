package restaurant.JPC.test.mock;

import restaurant.JPC.interfaces.Cashier;
import restaurant.JPC.interfaces.Cook;
import restaurant.JPC.interfaces.Market;

public class MockMarket extends Mock implements Market {
	public Cashier cashier;
	public EventLog log;
	
	public MockMarket(String name){
		super(name);
		log = new EventLog();
	}

	public void msgHereIsPaymentForMarketBill(int bill) {
		log.add(new LoggedEvent("Received bill of $" + bill));
	}

	public void msgCantPayForMarketBill(int bill) {
		log.add(new LoggedEvent("Cashier could not pay bill of: " + bill));
	}

	
	public void startThread() {
	}

	public void msgINeedFood(String name, int amount, Cook cookAgent,
			Cashier cashier) {
		log.add(new LoggedEvent("Received INeedFood from: " + cookAgent.toString()));
	}
	
	
}
