package restaurant.JPC.gui;

import restaurant.JPC.roles.CustomerRoleJPC;
import restaurant.JPC.roles.HostRoleJPC;

import java.awt.*;

public class CustomerGui implements Gui{

	private CustomerRoleJPC agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	RestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant, GoToCashier, GoToWaitingArea};
	private Command command=Command.noCommand;

	public static final int xTable = 200;
	public static final int yTable = 250;	
	
	public static final int CASHIER_X = 200;
	public static final int CASHIER_Y = -20;
	
	public static final int waiting_area_xpos = 20;
	public static final int waiting_area_ypos = 20;

	static final int CUSTOMER_WIDTH = 20;
	static final int CUSTOMER_LENGTH = 20;
	static final int CUSTOMER_STARTX = -40;
	static final int CUSTOMER_STARTY = -40;

	public CustomerGui(CustomerRoleJPC c, RestaurantGui gui){ //HostAgent m) {
		agent = c;
		xPos = CUSTOMER_STARTX;
		yPos = CUSTOMER_STARTY;
		xDestination = CUSTOMER_STARTX;
		yDestination = CUSTOMER_STARTY;
		//maitreD = m;
		this.gui = gui;
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToWaitingArea) agent.msgAnimationAtWaitingArea();
			else if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.GoToCashier) agent.msgAnimationFinishedPayCashier();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				//System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, CUSTOMER_WIDTH, CUSTOMER_LENGTH);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToWaitingArea(){
		xDestination = waiting_area_xpos;
		yDestination = waiting_area_ypos;
		command = Command.GoToWaitingArea;
	}

	public void DoGoToSeat(int seatnumber, Table table) {//later you will map seatnumber to table coordinates.
		xDestination = table.getXPos();
		yDestination = table.getYPos();
		command = Command.GoToSeat;
	}
	
	public void DoPayCashier(){
		xDestination = CASHIER_X;
		yDestination = CASHIER_Y;
		command = Command.GoToCashier;
	}

	public void DoExitRestaurant() {
		xDestination = CUSTOMER_STARTX;
		yDestination = CUSTOMER_STARTY;
		command = Command.LeaveRestaurant;
	}
}
