package restaurant.JPC.roles;

import agent.Agent;

import java.util.*;

import restaurant.JPC.Bill;
import restaurant.JPC.interfaces.Cashier;
import restaurant.JPC.interfaces.Customer;
import restaurant.JPC.interfaces.Market;
import restaurant.JPC.interfaces.Waiter;
import restaurant.JPC.test.mock.EventLog;

public class CashierRoleJPC extends Agent implements Cashier {
	public List<Check> checkRequests = Collections.synchronizedList(new ArrayList<Check>());
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	public List<MarketBill> marketBills = Collections.synchronizedList(new ArrayList<MarketBill>());
	public List<MarketBill> indebtedMarketBills = Collections.synchronizedList(new ArrayList<MarketBill>());

	private Map<String, Integer> foodPrices = new HashMap<String, Integer>();

	public int register;
	private String name;

	public EventLog log;

	public CashierRoleJPC(String name){
		super();
		this.name = name;
		register = 50;
		foodPrices.put("Steak", 10);
		foodPrices.put("Chicken", 10);
		foodPrices.put("Pizza", 6);
		foodPrices.put("Salad", 6);
		log = new EventLog();
	}

	public String getName(){
		return name;
	}

	// Messages
	public void msgNeedCheckFor(String choice, Customer c, Waiter w){ // from waiter
		checkRequests.add(new Check(choice, c, w));
		stateChanged();
	}

	public void msgHereIsBill(int dollars){ // from customer
		register += dollars;
		print("There is " + register + " dollars in the register");
		stateChanged();
	}

	public void msgHereIsBillForMarketOrder(Market m, int bill){ // from market
		marketBills.add(new MarketBill(m, bill));
		stateChanged();
	}

	// Scheduler
	public boolean pickAndExecuteAnAction(){
		synchronized(checkRequests){
			for (Check check : checkRequests){
				getCheck(check);
				checkRequests.remove(check);
				return true;
			}
		}

		synchronized(marketBills){
			for (MarketBill mb : marketBills){
				payMarketBill(mb);
				marketBills.remove(mb);
				return true;
			}
		}
		
		synchronized(indebtedMarketBills){
			for (MarketBill mb : indebtedMarketBills){
				tryToPayForIndebtedMarketBill(mb);
				return true;
			}
		}

		return false;
	}

	// Actions
	private void getCheck(Check c){
		int cost = foodPrices.get(c.choice);
		c.bill = cost;
		c.waiter.msgHereIsCheckFor(c.bill, c.customer);
		print("Check for " + c.customer.getName() + " is ready");
	}

	private void payMarketBill(MarketBill mb){
		if (register >= mb.bill){
			register -= mb.bill;
			print("Paying " + mb.market.getName() + " bill of " + mb.bill);
			print("There is $" + register + " left in the register");
			mb.market.msgHereIsPaymentForMarketBill(mb.bill);
		} else {
			print("Not enough money to pay for market bill. I will pay you back when I can.");
			mb.market.msgCantPayForMarketBill(mb.bill);
			indebtedMarketBills.add(mb);
		}
	}
	
	private void tryToPayForIndebtedMarketBill(MarketBill mb){
		if (register >= mb.bill){
			register -= mb.bill;
			print("Have enough money to now pay " + mb.market.getName() + " bill of " + mb.bill);
			print("There is $" + register + " left in the register");
			mb.market.msgHereIsPaymentForMarketBill(mb.bill);
			indebtedMarketBills.remove(mb);
		}
		
	}

	private class Check {
		String choice;
		int bill;
		Customer customer;
		Waiter waiter;

		Check(String choice, Customer c, Waiter w){
			this.choice = choice;
			customer = c;
			waiter = w;
		}
	}

	private class MarketBill{
		Market market;
		int bill;

		MarketBill(Market m, int bill){
			market = m;
			this.bill = bill;
		}

	}
}