package restaurant.JPC.roles;

import agent.Agent;
import restaurant.JPC.gui.FoodGui;
import restaurant.JPC.gui.Menu;
import restaurant.JPC.gui.Table;
import restaurant.JPC.gui.WaiterGui;
import restaurant.JPC.interfaces.Customer;
import restaurant.JPC.interfaces.Waiter;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Waiter Agent
 */
public class WaiterRoleJPC extends Agent implements Waiter{
	private List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	private List<Order> readyOrders = Collections.synchronizedList(new ArrayList<Order>());
	private List<Check> checksToPickUp = Collections.synchronizedList(new ArrayList<Check>());

	private String name;
	private int waiterNumber;
	private boolean onBreak = false;
	private Menu menu;

	private CookRoleJPC cook;
	private HostRoleJPC host;
	private CashierRoleJPC cashier;
	private List<FoodGui> foodGuis = new ArrayList<FoodGui>();

	public enum CustomerState 
	{Waiting, Seated, ReadyToOrder, ReadyToReorder, AskedToOrder, Ordered, 
		WaitingForFood, Leaving, AskedForCheck, WaitingForCheck, NeedToReorder, GivenMenu};

		private Semaphore atTable1 			= new Semaphore(0, true);
		private Semaphore atTable2 			= new Semaphore(0, true);
		private Semaphore atTable3 			= new Semaphore(0, true);
		private Semaphore atTable4 			= new Semaphore(0, true);

		private Semaphore atCook 			= new Semaphore(0, true);
		private Semaphore atCustomerLobby 	= new Semaphore(0, true);
		private Semaphore atCashier 		= new Semaphore(0, true);

		public WaiterGui WaiterGui = null;

		public WaiterRoleJPC(String name, int waiterNumber) {
			super();
			this.name = name;
			menu = new Menu();
			this.waiterNumber = waiterNumber;
		}

		public String getMaitreDName() {
			return name;
		}

		public String getName() {
			return name;
		}

		public void setCook(CookRoleJPC cook){
			this.cook = cook;
		}

		public void setHost(HostRoleJPC host){
			this.host = host;
		}

		public void setCashier(CashierRoleJPC cashier){
			this.cashier = cashier;
		}

		public void setFoodGuis(ArrayList<FoodGui> foodGuis){
			this.foodGuis = foodGuis;
		}

		public boolean isOnBreak(){
			return onBreak;
		}

		// Messages
		public void msgSitAtTable(CustomerRoleJPC c, Table table){ // from host
			customers.add(new MyCustomer(c, table, CustomerState.Waiting));
			stateChanged();
		}

		public void msgImReadyToOrder(CustomerRoleJPC c, String choice){ // from customer
			synchronized(customers){
				for (MyCustomer cust : customers){
					if (cust.c == c){
						cust.state = CustomerState.ReadyToOrder;
						cust.choice = choice;
					}
				}
				stateChanged();
			}
		}

		public void msgHereIsMyChoice(CustomerRoleJPC c, String choice){ // from customer
			synchronized(customers){
				for (MyCustomer cust : customers){
					if (cust.c == c){
						cust.choice = choice;
						cust.state = CustomerState.Ordered;
					}
				}
				stateChanged();
			}
		}

		public void msgWeAreOutOf(String food, int tableNumber){ // from cook
			menu.delete(food);
			synchronized(customers){
				for (MyCustomer cust : customers){
					if (cust.table.tableNumber == tableNumber){
						cust.state = CustomerState.NeedToReorder;
					}
				}
				stateChanged();
			}
		}

		public void msgWeRestocked(String food){ // from cook
			if(menu.doesNotContain(food))
				menu.add(food);
			stateChanged();
		}

		public void msgOrderIsReady(String choice, int tableNumber){ // from cook
			synchronized(customers){
				for (MyCustomer cust : customers){
					if (cust.table.tableNumber == tableNumber){
						readyOrders.add(new Order(choice, cust.c, cust.table));
					}
				}
				stateChanged();
			}
		}

		public void msgLeaving(CustomerRoleJPC c) { // from customer
			synchronized(customers){
				for (MyCustomer cust : customers){
					if (cust.c == c){
						cust.state = CustomerState.Leaving;
					}
				}
				stateChanged();
			}
		}

		public void msgCheckPlease(CustomerRoleJPC c){ // from customer
			synchronized(customers){
				for (MyCustomer cust : customers){
					if (cust.c == c){
						cust.state = CustomerState.AskedForCheck;
					}
				}
				stateChanged();
			}
		}

		public void msgHereIsCheckFor(int bill, Customer c){ // from cashier
			checksToPickUp.add(new Check(bill, c));
			stateChanged();
		}

		public void msgAtTable(int tableNumber) { //from animation
			switch(tableNumber){
			case(1):
				atTable1.release();
			break;
			case(2):
				atTable2.release();
			break;
			case(3):
				atTable3.release();
			break;
			case(4):
				atTable4.release();
			break;
			}
			stateChanged();
		}

		public void msgAtCook(){
			atCook.release();				
			stateChanged();
		}

		public void msgAtCashier(){ // from animation
			atCashier.release();
			stateChanged();
		}

		public void msgAtCustomerLobby(){
			atCustomerLobby.release();
			stateChanged();
		}

		public void msgCanGoOnBreak(){
			onBreak = true;
			stateChanged();
		}

		/**
		 * Scheduler.  Determine what action is called for, and do it.
		 */
		protected boolean pickAndExecuteAnAction() { 
			/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
			 */
			synchronized(customers){
				for (MyCustomer c : customers){
					if (c.state == CustomerState.Waiting){
						print("Serving next customer");
						goGetCustomer();
						seatCustomer(c, c.table);
						return true;
					}
				}
			}

			synchronized(customers){
				for (MyCustomer c : customers){
					if (c.state == CustomerState.ReadyToOrder){
						getOrder(c);
						sendOrderToCook(c);
						return true;
					}
				}
			}

			synchronized(customers){
				for (MyCustomer c : customers){
					if (c.state == CustomerState.NeedToReorder){
						print("Taking new order from " + c.c.getName());
						askToReorder(c);
						return true;
					}
				}
			}

			synchronized(readyOrders){
				for(Order o : readyOrders){
					pickUpOrderFromCook();
					print("Serving " + o.customer + " " + o.choice);
					serveCustomer(o);
					readyOrders.remove(o);
					return true;
				}
			}

			synchronized(customers){
				for (MyCustomer c : customers){
					if (c.state == CustomerState.AskedForCheck){
						getCheckFromCashier(c.choice, c.c);
						c.state = CustomerState.WaitingForCheck;
						return true;
					}
				}
			}

			synchronized(checksToPickUp){
				for (Check c : checksToPickUp){
					getCheck(c);
					return true;
				}
			}

			synchronized(customers){
				for (MyCustomer c : customers){
					if (c.state == CustomerState.Leaving){
						tellHostTableIsFree(c);
						return true;
					}
				}
			}

			if (customers.isEmpty() && onBreak) {
				WaiterGui.DoGoBreakPosition();
				return true;
			} else {
				tellHostWaiterIsFree();
				WaiterGui.DoGoHomePosition(waiterNumber);
			}

			return false;
			//we have tried all our rules and found
			//nothing to do. So return false to main loop of abstract agent
			//and wait.
		}

		// Actions

		private void seatCustomer(MyCustomer myCustomer, Table table) {
			table.setOccupant(myCustomer.c);
			myCustomer.c.msgFollowMeToTable(this, menu, table);
			DoSeatCustomer(myCustomer.c, table);
			try {
				switch(table.tableNumber){
				case(1):
					atTable1.acquire();
				break;
				case(2):
					atTable2.acquire();
				break;
				case(3):
					atTable3.acquire();
				break;
				case(4):
					atTable4.acquire();
				break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			myCustomer.c.msgHereIsTheMenu();
			myCustomer.state = CustomerState.GivenMenu;
		}

		private void goGetCustomer(){
			WaiterGui.DoLeaveCustomer();
			try {
				atCustomerLobby.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// The animation DoXYZ() routines
		private void DoSeatCustomer(CustomerRoleJPC customer, Table table) {
			print("Seating " + customer + " at " + table);
			WaiterGui.DoGoToTable(table); 
		}

		private void askToReorder(MyCustomer c){
			WaiterGui.DoGoToTable(c.table);
			try {
				switch(c.table.tableNumber){
				case(1):
					atTable1.acquire();
				break;
				case(2):
					atTable2.acquire();
				break;
				case(3):
					atTable3.acquire();
				break;
				case(4):
					atTable4.acquire();
				break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			print("Sorry, we are out of " + c.choice + ". You must reorder.");
			c.c.msgWhatWouldYouLikeToReorder(menu);
			c.state = CustomerState.AskedToOrder;
		}

		private void getOrder(MyCustomer c){
			WaiterGui.DoGoToTable(c.table);
			try {
				switch(c.table.tableNumber){
				case(1):
					atTable1.acquire();
				break;
				case(2):
					atTable2.acquire();
				break;
				case(3):
					atTable3.acquire();
				break;
				case(4):
					atTable4.acquire();
				break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			print("What would you like to order, " + c.c.getName() + "?");
			c.c.msgWhatWouldYouLike();
			c.state = CustomerState.AskedToOrder;
		}

		private void sendOrderToCook(MyCustomer c){
			switch(c.table.tableNumber){
			case (1):
				foodGuis.get(0).setVisible(c.choice, "Cook", c.table, this.getGui());
			break;
			case (2):
				foodGuis.get(1).setVisible(c.choice, "Cook", c.table, this.getGui());
			break;
			case (3):
				foodGuis.get(2).setVisible(c.choice, "Cook", c.table, this.getGui());
			break;
			case (4):
				foodGuis.get(3).setVisible(c.choice, "Cook", c.table, this.getGui());
			break;
			}		
			WaiterGui.DoGoToCook();
			print("Bringing order of " + c.choice + " for " + c.c + " to the cook.");
			try {
				atCook.acquire();
			} catch (InterruptedException e) { e.printStackTrace(); }
			cook.msgHereIsAnOrder(this, c.choice, c.table.tableNumber);
			c.state = CustomerState.WaitingForFood;
		}

		private void pickUpOrderFromCook(){
			WaiterGui.DoGoToCook();
			try {
				atCook.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private void serveCustomer(Order o){
			WaiterGui.DoGoToTable(o.table);
			//print("Serving " + o.customer + " " + o.choice);
			switch(o.table.tableNumber){
			case (1):
				foodGuis.get(0).setVisible(o.choice, "Table", o.table, this.getGui());
			break;
			case (2):
				foodGuis.get(1).setVisible(o.choice, "Table", o.table, this.getGui());
			break;
			case (3):
				foodGuis.get(2).setVisible(o.choice, "Table", o.table, this.getGui());
			break;
			case (4):
				foodGuis.get(3).setVisible(o.choice, "Table", o.table, this.getGui());
			break;
			}	

			try {
				switch(o.table.tableNumber){
				case(1):
					atTable1.acquire();
				break;
				case(2):
					atTable2.acquire();
				break;
				case(3):
					atTable3.acquire();
				break;
				case(4):
					atTable4.acquire();
				break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			o.customer.msgHereIsYourFood();
		}

		private void tellHostWaiterIsFree(){
			host.msgIAmFree(this);
		}

		private void tellHostTableIsFree(MyCustomer c){
			host.msgTableIsFree(c.table);
			customers.remove(c);
		}

		public void tryGoOnBreak(){
			print("I would like to go on break.");
			host.msgIdLikeToGoOnBreak(this);
		}

		public void returnFromBreak(){
			onBreak = false;
			print("I am back from break.");
			host.msgBackFromBreak(this);
		}

		public void getCheckFromCashier(String choice, CustomerRoleJPC c){
			WaiterGui.DoGoToCashier();
			print("Going to cashier to get check for " + c.getName());
			try {
				atCashier.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cashier.msgNeedCheckFor(choice, c, this);
			print("I need a check for " + c.getName() + ", who ordered " + choice);
		}

		public void getCheck(Check c){
			print("Going to cashier to get check for " + c.customer.getName());
			WaiterGui.DoGoToCashier();
			try {
				atCashier.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			checksToPickUp.remove(c);
			print("Check for " + c.customer.getName() + " picked up");
			WaiterGui.DoGoToTable(c.customer.getTable());
			print("Bringing check to " + c.customer.getName());
			try {
				switch(c.customer.getTable().tableNumber){
				case(1):
					atTable1.acquire();
				break;
				case(2):
					atTable2.acquire();
				break;
				case(3):
					atTable3.acquire();
				break;
				case(4):
					atTable4.acquire();
				break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			c.customer.msgHereIsCheck(c.bill);
			print("Here is your check");
		}

		//utilities

		public void setGui(WaiterGui gui) {
			WaiterGui = gui;
		}

		public WaiterGui getGui() {
			return WaiterGui;
		}

		private class Check {
			int bill;
			Customer customer;

			Check(int bill, Customer c){
				this.bill = bill;
				customer = c;
			}
		}

		private class MyCustomer {
			CustomerRoleJPC c;
			Table table;
			String choice;
			CustomerState state;

			MyCustomer(CustomerRoleJPC c, Table t, CustomerState s){
				this.c = c;
				table = t;
				state = s;
			}
		}

		private class Order {
			String choice;
			CustomerRoleJPC customer;
			Table table;

			Order(String c, CustomerRoleJPC cu, Table t){
				choice = c;
				customer = cu;
				table = t;
			}
		}
}


