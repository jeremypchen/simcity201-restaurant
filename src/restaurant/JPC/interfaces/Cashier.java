package restaurant.JPC.interfaces;

import restaurant.JPC.Bill;
import restaurant.JPC.roles.MarketAgent;

public interface Cashier {
	public abstract void msgNeedCheckFor(String choice, Customer c, Waiter w); // from waiter
	public abstract void msgHereIsBill(int dollars); // from customer
	public abstract void msgHereIsBillForMarketOrder(Market m, int bill); //from market
}
