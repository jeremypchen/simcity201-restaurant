package restaurant.JPC;

import restaurant.JPC.interfaces.Cashier;
import restaurant.JPC.interfaces.Customer;

public class Bill {
	public Cashier cashier;
	public Customer customer;
	public int tableNumber;
	public double price;
	public int netCost;
	public cashierBillState state;
	public enum cashierBillState{customerApproached, done};
	public int changeDue;
	
	public Bill(Cashier c, Customer cust, int tableNum, double price){
		cashier = c;
		customer = cust;
		tableNumber = tableNum;
		this.price = price;
	}
}
