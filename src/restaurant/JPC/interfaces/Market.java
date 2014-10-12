package restaurant.JPC.interfaces;

public interface Market {
	public abstract void msgHereIsPaymentForMarketBill(int bill); // from cashier
	public abstract void msgCantPayForMarketBill(int bill); // from cashier
	public abstract String getName();
	public abstract void startThread();
	public abstract void msgINeedFood(String name, int amount,
			Cook cookAgent, Cashier cashier);
}
