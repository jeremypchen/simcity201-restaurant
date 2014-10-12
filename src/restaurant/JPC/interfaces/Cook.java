package restaurant.JPC.interfaces;

public interface Cook {
	public abstract void msgHereIsAnOrder(Waiter w, String choice, int tableNumber);
	public abstract void msgHereIsYourFood(String food, int amount);
	public abstract void msgCannotFulfillOrder(String food);
	public abstract String getName();
	
}
