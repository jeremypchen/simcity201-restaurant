package restaurant.JPC.roles;

import agent.Agent;

import java.util.*;

import restaurant.JPC.interfaces.Cashier;
import restaurant.JPC.interfaces.Cook;
import restaurant.JPC.interfaces.Market;
import restaurant.JPC.roles.CustomerRoleJPC.AgentEvent;

public class MarketAgent extends Agent implements Market{
	private Map<String, Integer> foodInventory = new HashMap<String, Integer>();
	private List<FoodOrder> foodToDeliver = new ArrayList<FoodOrder>();
	private String name;
	Timer timer = new Timer();


	public MarketAgent(String name){
		super();
		this.name = name;
		if(name.equals("Pizza Market"))
			foodInventory.put("Pizza", 10);
		else if (name.equals("Salad Market"))
			foodInventory.put("Salad", 10);
		else if (name.equals("Meat Market")) {
			foodInventory.put("Steak", 10);
			foodInventory.put("Chicken", 10);
		}
	}

	public String getName(){
		return name;
	}

	// Messages
	public void msgINeedFood(String food, int amount, Cook cook, Cashier cashier) { // from cook
		foodToDeliver.add(new FoodOrder(food, amount, cook, cashier));
		stateChanged();
	}

	public void msgHereIsPaymentForMarketBill(int bill){ // from cashier
		stateChanged();
	}

	public void msgCantPayForMarketBill(int bill){ // from cashier
		stateChanged();
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction(){
		for (FoodOrder f : foodToDeliver){
			if(foodInventory.get(f.food) > 0){
				print("Preparing " + f.food + " for delivery to " + f.cook.getName());
				deliverFood(f);
			} else {
				print("We are out of " + f.food);
				tellCookOutOfFood(f);
			}
			foodToDeliver.remove(f);
			return true;
		}
		return false;
	}

	// Actions
	private void deliverFood(FoodOrder f){
		timer.schedule(new MarketTimerTask(f, this) {
			public void run() {
				print("Food delivery ready.");
				f.cook.msgHereIsYourFood(f.food, f.amount);
				foodInventory.put(f.food, foodInventory.get(f.food) - f.amount);
				print("There is " + foodInventory.get(f.food) + " " + f.food + " remaining in this market.");
				f.cashier.msgHereIsBillForMarketOrder(m, f.price);
				stateChanged();
			}
		},
		3000);
	}

	private void tellCookOutOfFood(FoodOrder f){
		f.cook.msgCannotFulfillOrder(f.food);
	}

	private class FoodOrder {
		String food;
		int amount; 
		int price;
		Cook cook;
		Cashier cashier;
		HashMap<String, Integer> foodPrices;

		FoodOrder(String food, int amount, Cook cook, Cashier cashier){
			this.food = food;
			this.amount = amount;
			this.cook = cook;
			this.cashier = cashier;
			foodPrices = new HashMap<String, Integer>();
			foodPrices.put("Steak", 4);
			foodPrices.put("Chicken", 3);
			foodPrices.put("Pizza", 2);
			foodPrices.put("Salad", 1);

			price = foodPrices.get(food)*amount;
		}
	}

	class MarketTimerTask extends TimerTask  {
		FoodOrder f;
		MarketAgent m;

		public MarketTimerTask(FoodOrder f, MarketAgent m) {
			this.f = f;
			this.m = m;
		}

		public void run() {
		}
	}

}
