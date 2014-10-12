package restaurant.JPC.interfaces;

import restaurant.JPC.roles.CustomerRoleJPC;

public interface Waiter {
	public abstract void msgHereIsCheckFor(int bill, Customer c); // from cashier
	public abstract void msgWeAreOutOf(String choice, int tableNumber);
	public abstract void msgWeRestocked(String name);
	public abstract void msgOrderIsReady(String choice, int tableNumber);

}
