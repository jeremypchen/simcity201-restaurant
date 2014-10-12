package restaurant.JPC.gui;

import restaurant.JPC.roles.CustomerRoleJPC;
import restaurant.JPC.roles.WaiterRoleJPC;

import java.awt.*;

public class WaiterGui implements Gui {

	private WaiterRoleJPC agent = null;

	private int xPos = -20, yPos = -20;//default waiter position
	private int xDestination = -20, yDestination = -20;//default start position

	static final int WAITER_WIDTH = 20;
	static final int WAITER_LENGTH = 20;
	public Table currentTable = null;
	public CustomerRoleJPC c;

	private enum Command{noCommand, GoToCook, GoToTable, GoToCashier, GoToCustomerLobby}
	private Command command = Command.noCommand;

	public WaiterGui(WaiterRoleJPC agent) {
		this.agent = agent;
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

		if(xPos == xDestination && yPos == yDestination){
			if (command == Command.GoToCook) 
				agent.msgAtCook();
			else if (command == Command.GoToTable){
				agent.msgAtTable(currentTable.tableNumber);}
			else if (command == Command.GoToCashier)
				agent.msgAtCashier();
			else if (command == Command.GoToCustomerLobby)
				agent.msgAtCustomerLobby();
			command = Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.MAGENTA);
		g.fillRect(xPos, yPos, WAITER_WIDTH, WAITER_LENGTH);
	}

	public boolean isPresent() {
		return true;
	}

	public void DoGoToTable(Table table) {
		command = Command.GoToTable;
		currentTable = table;
		xDestination = currentTable.getXPos() + 20;
		yDestination = currentTable.getYPos() - 20;
	}

	public void DoGoToCook(){
		command = Command.GoToCook;
		xDestination = 425;
		yDestination = 175;
	}

	public void DoLeaveCustomer() {
		command = Command.GoToCustomerLobby;
		xDestination = 40;
		yDestination = 40;
	}

	public void DoGoToCashier(){
		command = Command.GoToCashier;
		xDestination = 200;
		yDestination = -20;
	}

	public void DoGoHomePosition(int waiterNumber){
		xDestination = 150 + (waiterNumber*50);
		yDestination = 75;
	}

	public void DoGoBreakPosition(){
		xDestination = 100;
		yDestination = 200;
	}

	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}
}
