package restaurant.JPC.gui;

import restaurant.JPC.roles.CashierRoleJPC;
import restaurant.JPC.roles.CookRoleJPC;
import restaurant.JPC.roles.CustomerRoleJPC;
import restaurant.JPC.roles.HostRoleJPC;
import restaurant.JPC.roles.WaiterRoleJPC;

import javax.swing.*;

import agent.Agent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel {
    // Agent Instantiation
    private HostRoleJPC host 		= new HostRoleJPC("Host");
    private CookRoleJPC cook 		= new CookRoleJPC("Cook");
    private CashierRoleJPC cashier= new CashierRoleJPC("Cashier");
    private CookGui cookGui;

    private Vector<CustomerRoleJPC> customers = new Vector<CustomerRoleJPC>();
    private Vector<WaiterRoleJPC> waiters = new Vector<WaiterRoleJPC>();
    private List<Agent> allAgents = new ArrayList<Agent>();
    private ArrayList<FoodGui> foodGuis = new ArrayList<FoodGui>();
    private int waiterNumber;

    private JPanel restLabel = new JPanel();
    private JPanel group = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");
        
    private RestaurantGui gui; //reference to main gui

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        allAgents.add(host);
        allAgents.add(cook);
        allAgents.add(cashier);
        cook.setCashier(cashier);
        cookGui = new CookGui(cook);
        cook.setGui(cookGui);
		gui.animationPanel.addGui(cookGui);
        
        FoodGui foodTable1 = new FoodGui();
        FoodGui foodTable2 = new FoodGui();
        FoodGui foodTable3 = new FoodGui();
        FoodGui foodTable4 = new FoodGui();
        foodGuis.add(foodTable1);
        foodGuis.add(foodTable2);
        foodGuis.add(foodTable3);
        foodGuis.add(foodTable4);
		gui.animationPanel.addGui(foodTable1);
		gui.animationPanel.addGui(foodTable2);
		gui.animationPanel.addGui(foodTable3);
		gui.animationPanel.addGui(foodTable4);

        host.startThread();
        cook.startThread();
        cashier.startThread();
        waiterNumber = 1;
        cook.setFoodGuis(foodGuis);

        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));
       
        group.add(customerPanel);
        group.add(waiterPanel);

        initRestLabel();
        add(restLabel);
        add(group);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3>"
                + "<table><tr><td>host:</td><td>" + host.getName() + "</td></tr>"
                + "<tr><td>cook:</td><td>" + cook.getName() + "</td></tr></table>"
                + "<h3><u> Menu</u></h3><table>"
                + "<tr><td>Steak</td><td>$10</td></tr>"
                + "<tr><td>Chicken</td><td>$10</td></tr>"
                + "<tr><td>Salad</td><td>$6</td></tr>"
                + "<tr><td>Pizza</td><td>$6</td></tr>"
                + "</table><br></html>");
        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {
            for (int i = 0; i < customers.size(); i++) {
                CustomerRoleJPC temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        if (type.equals("Waiters")){
        	for (int i = 0; i < waiters.size(); i++) {
                WaiterRoleJPC temp = waiters.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
    }
    
    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name, boolean isHungry) {

    	if (type.equals("Customers")) {
    		CustomerRoleJPC c = new CustomerRoleJPC(name);	
    		CustomerGui g = new CustomerGui(c, gui);

    		if (isHungry)
    			g.setHungry();
    		gui.animationPanel.addGui(g);// dw
    		c.setGui(g);
    		c.setHost(host);
    		customers.add(c);
    		allAgents.add(c);
    		c.startThread();
    	}
    	if (type.equals("Waiters")){
    		WaiterRoleJPC w = new WaiterRoleJPC(name, waiterNumber);
    		waiterNumber++;
    		WaiterGui g = new WaiterGui(w);
    		gui.animationPanel.addGui(g);
    		w.setGui(g);
    		w.setCook(cook);
    		cook.addWaiter(w);
    		w.setCashier(cashier);
    		w.setFoodGuis(foodGuis);
    		w.setHost(host);
    		waiters.add(w);
    		host.allWaiters.add(w);
    		allAgents.add(w);
            host.addWaiterToAvailable(w);
    		w.startThread();
    	}	
    }
        
    public List<Agent> getAgents(){
    	return allAgents;
    }
    
    public List<WaiterRoleJPC> getAllWaiters(){
    	return host.allWaiters;
    }

}
