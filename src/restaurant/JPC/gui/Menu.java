package restaurant.JPC.gui;

import java.util.*;

public class Menu {
	private List<String> choices = new ArrayList<String>();
	private int numberOfItems = 4;
	
	public Menu(){
		choices.add("Steak"); 	// $10
		choices.add("Chicken"); // $10
		choices.add("Salad"); 	// $6
		choices.add("Pizza"); 	// $6
	}
	
	public String pickItem(int cash, boolean rude){
		// If customer does not have enough cash for anything and is not rude
		if (cash < 6 && !rude){ 
			return "None";
		}
		// If customer only has enough cash for Salad and Pizza but they are out of stock
		// And the customer is not rude
		if (cash < 10 && !rude && !choices.contains("Salad") && !choices.contains("Pizza"))
			return "None";
		// If customer only has enough cash for Salad and Pizza, which is in stock
		if (cash < 10 && !rude){
			boolean canAfford = false;
			int random = 0;
			while (!canAfford){
				random = (int)(Math.random() * ((numberOfItems-1) +1));
				if (choices.get(random) == "Salad" || choices.get(random) == "Pizza")
					canAfford = true;
			}
			return choices.get(random);
		}
		// If customer has enough cash or is rude
		int random = (int)(Math.random() * ((numberOfItems-1) +1));	
		return choices.get(random);
	}
	
	public void delete(String food){
		numberOfItems--;
		choices.remove(food);
	}
	
	public void add(String food){
		numberOfItems++;
		choices.add(food);
	}
	
	public boolean doesNotContain(String food){
		for (String item : choices)
			if (item.equals(food))
				return false;
		return true;
	}
	
	public List<String> getChoices(){
		return choices;
	}
}
