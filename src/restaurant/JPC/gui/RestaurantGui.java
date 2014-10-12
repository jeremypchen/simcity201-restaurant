package restaurant.JPC.gui;

import restaurant.JPC.roles.CustomerRoleJPC;
import restaurant.JPC.roles.WaiterRoleJPC;

import javax.swing.*;

import agent.Agent;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	/* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	JFrame animationFrame = new JFrame("Restaurant Animation");
	AnimationPanel animationPanel = new AnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    private JButton pauseButton;
    private JButton restartButton;
    private JButton breakButton;
    private JButton returnButton;

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        int WINDOWX = 900;
        int WINDOWY = 700;//350;
    	
    	setBounds(50, 50, WINDOWX, WINDOWY);

    	setLayout(new GridBagLayout());
    	GridBagConstraints restC = new GridBagConstraints();
    	GridBagConstraints infoC = new GridBagConstraints();
    	GridBagConstraints animC = new GridBagConstraints();

    	restC.gridx = 0;
    	restC.gridy = 0;
    	restC.insets = new Insets(0, 0, 5, 50);
        add(restPanel, restC);
        
        // Info Panel
        infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        
        pauseButton = new JButton("Pause");
        restartButton = new JButton("Restart");
        breakButton = new JButton("Break");
        returnButton = new JButton("Return");
        pauseButton.addActionListener(this);
        restartButton.addActionListener(this);
        breakButton.addActionListener(this);
        returnButton.addActionListener(this);
        
        restartButton.setVisible(false);

        infoPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        c.gridx = 0; c.gridy = 0;
        infoPanel.add(infoLabel, c);
        c.gridx = 1; c.gridy = 0;
        infoPanel.add(stateCB, c);
        c.gridx = 2; c.gridy = 0;
        infoPanel.add(pauseButton, c);
        infoPanel.add(restartButton, c);
        
        infoC.gridx = 0;
        infoC.gridy = 1; // 2; 
        add(infoPanel, infoC);
        
        // Animation panel
        animationPanel.setBorder(BorderFactory.createTitledBorder("Animation"));
        animC.gridx = 0;
        animC.gridy = 2;
        animC.fill = GridBagConstraints.HORIZONTAL;
        animC.ipady = 350;
        add(animationPanel, animC);
        
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        stateCB.setVisible(true);
        currentPerson = person;

        if (person instanceof CustomerRoleJPC) {
            CustomerRoleJPC customer = (CustomerRoleJPC) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        if (person instanceof WaiterRoleJPC) {
            WaiterRoleJPC waiter = (WaiterRoleJPC) person;
            stateCB.setText("On break?");
            stateCB.setSelected(waiter.isOnBreak());
            stateCB.setEnabled(true);
            infoLabel.setText(
               "<html><pre>     Name: " + waiter.getName() + " </pre></html>");
        }
        infoPanel.validate();
    }
    
    
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerRoleJPC) {
                CustomerRoleJPC c = (CustomerRoleJPC) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
            if (currentPerson instanceof WaiterRoleJPC){
            	WaiterRoleJPC w = (WaiterRoleJPC) currentPerson;
            	if (stateCB.isSelected()){
            		w.tryGoOnBreak();
            		System.out.println("Amount of waiters: " + restPanel.getAllWaiters().size());
            		if (restPanel.getAllWaiters().size() == 1)
            			stateCB.setSelected(false);
            	} else {
            		w.returnFromBreak();
            	}
            }
        }
        if (e.getSource() == pauseButton){
        	for (Agent a : restPanel.getAgents()){
        		a.pause();
        	}
        	pauseButton.setVisible(false);
        	restartButton.setVisible(true);
        }
        if (e.getSource() == restartButton){
        	for (Agent a : restPanel.getAgents()){
        		a.restart();
        	}
        	restartButton.setVisible(false);
        	pauseButton.setVisible(true);
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(CustomerRoleJPC c) {
        if (currentPerson instanceof CustomerRoleJPC) {
            CustomerRoleJPC cust = (CustomerRoleJPC) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(true);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
