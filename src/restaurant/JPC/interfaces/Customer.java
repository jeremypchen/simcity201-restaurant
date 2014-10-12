package restaurant.JPC.interfaces;

import restaurant.JPC.gui.Table;

public interface Customer {
	public abstract void msgHereIsCheck(int bill); // from waiter
	public abstract String getName();
	public abstract Table getTable();
}
