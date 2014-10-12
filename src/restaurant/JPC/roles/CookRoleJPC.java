package restaurant.JPC.roles;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant.JPC.gui.CookGui;
import restaurant.JPC.gui.FoodGui;
import restaurant.JPC.interfaces.Cashier;
import restaurant.JPC.interfaces.Cook;
import restaurant.JPC.interfaces.Market;
import restaurant.JPC.interfaces.Waiter;

public class CookRoleJPC extends Agent implements Cook{
	private List<Order> ordersDone = Collections.synchronizedList(new ArrayList<Order>());
	private Market pizza_market;
	private Market salad_market;
	private Market meat_market;
	static final int STEAK_COOKTIME = 7000;
	static final int CHICKEN_COOKTIME = 7000;
	static final int PIZZA_COOKTIME = 5000;
	static final int SALAD_COOKTIME = 3000;
	private Map<String, Integer> foodAmounts = new HashMap<String, Integer>();
	private Cashier cashier;
	private List<FoodGui> foodGuis = new ArrayList<FoodGui>();
	private List<Order> ordersToMake = new ArrayList<Order>();
	private List<Waiter> allWaiters = new ArrayList<Waiter>();
	private boolean haveCheckedInventory = false;

	private Semaphore atPlatingArea		= new Semaphore(0, true);
	private Semaphore atGrill 			= new Semaphore(0, true);
	private Semaphore atFridge 			= new Semaphore(0, true);
	private Semaphore cooking 			= new Semaphore(0, true);


	public CookGui CookGui = null;

	Timer timer = new Timer();

	private String name;

	public CookRoleJPC(String name){
		super();
		this.name = name;
		this.pizza_market = new MarketAgent("Pizza Market");
		this.pizza_market.startThread();
		this.salad_market = new MarketAgent("Salad Market");
		this.salad_market.startThread();
		this.meat_market = new MarketAgent("Meat Market");
		this.meat_market.startThread();
		foodAmounts.put("Steak", 6);
		foodAmounts.put("Chicken", 6);
		foodAmounts.put("Pizza", 6);
		foodAmounts.put("Salad", 6);
	}

	public String getName(){
		return name;
	}

	public void setCashier(Cashier c){
		cashier = c;
	}

	public void setFoodGuis(ArrayList<FoodGui> foodGuis){
		this.foodGuis = foodGuis;
	}

	public void setGui(CookGui gui){
		CookGui = gui;
	}
	
	public void addWaiter(Waiter w){
		allWaiters.add(w);
	}

	// Messages
	public void msgHereIsAnOrder(Waiter w, String choice, int tableNumber){ // from waiter
		ordersToMake.add(new Order(choice, w, tableNumber));
		stateChanged();
	}

	public void msgHereIsYourFood(String food, int amount) { // from market
		foodAmounts.put(food, amount);
		haveCheckedInventory = false;
		stateChanged();
	}

	public void msgCannotFulfillOrder(String food){
		stateChanged();
	}

	// Messages from Animation
	public void msgAtPlatingArea(){
		atPlatingArea.release();
		stateChanged();
	}

	public void msgAtGrill(){
		atGrill.release();
		stateChanged();
	}

	public void msgAtFridge(){
		atFridge.release();
		stateChanged();
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction(){
		synchronized(ordersDone){
			for (Order o : ordersDone){
				print("Order of " + o.choice + " is ready for table " + o.tableNumber);
				tellWaiterOrderIsReady(o);	
				ordersDone.remove(o);
				return true;
			}
		}

		for (Order o : ordersToMake){
			getOrderAndFoodFromFridge(o);
			if(foodAmounts.get(o.choice) > 0){
				print("Cooking " + o.choice + " for table " + o.tableNumber);
				makeOrder(o);
				foodAmounts.put(o.choice, foodAmounts.get(o.choice) -1);
				print("There is " + foodAmounts.get(o.choice) + " " + o.choice + " remaining.");
			} else {
				print("We are out of " + o.choice);
				tellWaiterToReorder(o);
			}
			ordersToMake.remove(o);
			return true;
		}

		if(!haveCheckedInventory)
			checkInventory(); // when there's nothing to do, check inventory
		
		CookGui.DoGoHomePosition();

		return false;
	}

	// Actions
	private void tellWaiterToReorder(Order o){
		o.waiter.msgWeAreOutOf(o.choice, o.tableNumber);
	}

	private void getOrderAndFoodFromFridge(Order o){
		CookGui.DoGoToPlatingArea();
		try {
			atPlatingArea.acquire(); 
		} catch (InterruptedException e) { e.printStackTrace(); }	
		CookGui.DoGoToFridge();
		try {
			atFridge.acquire();
		} catch (InterruptedException e) { e.printStackTrace(); }

		switch(o.tableNumber){
		case (1):
			foodGuis.get(0).setVisible(o.choice, "Grill");
		break;
		case (2):
			foodGuis.get(1).setVisible(o.choice, "Grill");
		break;
		case (3):
			foodGuis.get(2).setVisible(o.choice, "Grill");
		break;
		case (4):
			foodGuis.get(3).setVisible(o.choice, "Grill");
		break;
		}
	}

	private void makeOrder(Order o){
		CookGui.DoGoToGrill(1);
		try {
			atGrill.acquire();
		} catch (InterruptedException e) {e.printStackTrace(); }

		String foodChoice = o.choice;
		int timeToCook;
		if (foodChoice == "Steak")
			timeToCook = STEAK_COOKTIME;
		else if (foodChoice == "Chicken")
			timeToCook = CHICKEN_COOKTIME;
		else if (foodChoice == "Salad")
			timeToCook = SALAD_COOKTIME;
		else if (foodChoice == "Pizza")
			timeToCook = PIZZA_COOKTIME;
		else 
			timeToCook = PIZZA_COOKTIME;

		timer.schedule(new CookTimerTask(o){
			public void run(){
				cooking.release();
				print("Order of " + o.choice + " is cooked");
				ordersDone.add(o);

				switch(o.tableNumber){
				case (1):
					foodGuis.get(0).setVisible(o.choice, "Plating Area");
				break;
				case (2):
					foodGuis.get(1).setVisible(o.choice, "Plating Area");
				break;
				case (3):
					foodGuis.get(2).setVisible(o.choice, "Plating Area");
				break;
				case (4):
					foodGuis.get(3).setVisible(o.choice, "Plating Area");
				break;
				}

				stateChanged();
			}
		}, timeToCook);

		try { 
			cooking.acquire();
		} catch (InterruptedException e) { e.printStackTrace(); }
	}

	private void tellWaiterOrderIsReady(Order o){
		CookGui.DoGoToPlatingArea();
		try {
			atPlatingArea.acquire(); 
		} catch (InterruptedException e) { e.printStackTrace(); }

		o.waiter.msgOrderIsReady(o.choice, o.tableNumber);
	}
	
	private void checkInventory(){
		List<String> foodToReorder = new ArrayList<String>();
		if (foodAmounts.get("Steak") <= 3)
			foodToReorder.add("Steak");
		if (foodAmounts.get("Chicken") <= 3)
			foodToReorder.add("Chicken");
		if (foodAmounts.get("Pizza") <= 3)
			foodToReorder.add("Pizza");
		if (foodAmounts.get("Salad") <= 3)
			foodToReorder.add("Salad");
		reorderFood(foodToReorder);
		if (foodToReorder.size() != 0)
			haveCheckedInventory = true;
	}

	private void reorderFood(List<String> itemsToReorder){
		for(String item : itemsToReorder){
			Food food = new Food(item);
			if (food.name == "Pizza") {
				print("Ordering " + food.name + " from pizza market.");
				pizza_market.msgINeedFood(food.name, food.amount, this, cashier);
			} else if (food.name == "Chicken" || food.name == "Steak"){
				print("Ordering " + food.name + " from meat market.");
				meat_market.msgINeedFood(food.name, food.amount, this, cashier);
			}else if (food.name == "Salad"){
				print("Ordering " + food.name + " from salad market.");
				salad_market.msgINeedFood(food.name, food.amount, this, cashier);
			}
			for(Waiter w : allWaiters){
				w.msgWeRestocked(food.name);
			}
		}
	}

	class CookTimerTask extends TimerTask  {
		Order o;

		public CookTimerTask(Order o) {
			this.o = o;
		}

		public void run() {
		}
	}

	private class Order{
		String choice;
		Waiter waiter;
		int tableNumber;

		Order(String c, Waiter w, int t){
			choice = c;
			waiter = w;
			tableNumber = t;
		}

	}

	private class Food{
		String name;
		int amount;

		Food(String name){
			this.name = name;
			if (name == "Steak")
				amount = 5;
			else if (name == "Chicken")
				amount = 5;
			else if (name == "Pizza")
				amount = 7;
			else if (name == "Salad")
				amount = 7;
		}
	}


}