package restaurant.JPC.gui;

import restaurant.JPC.roles.CookRoleJPC;

import java.awt.*;

public class CookGui implements Gui {
	private CookRoleJPC agent = null;

	private int xPos = 600, yPos = 225;	//default start position
	private int xDestination = 600, yDestination = 225;//default start position

	static final int COOK_WIDTH = 20;
	static final int COOK_LENGTH = 20;

	private enum Command{noCommand, GoToGrill, GoToPlatingArea, GoToFridge}
	private Command command = Command.noCommand;

	public CookGui(CookRoleJPC agent) {
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
			if (command == Command.GoToPlatingArea) 
				agent.msgAtPlatingArea();
			else if (command == Command.GoToGrill){
				agent.msgAtGrill();}
			else if (command == Command.GoToFridge)
				agent.msgAtFridge();
			command = Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(xPos, yPos, COOK_WIDTH, COOK_LENGTH);
	}

	public boolean isPresent() {
		return true;
	}

	public void DoGoToPlatingArea() {
		command = Command.GoToPlatingArea;
		xDestination = 500;
		yDestination = 175;
	}

	public void DoGoToGrill(int grill){
		command = Command.GoToGrill;
		xDestination = 465 + (grill*50);
		yDestination = 190;
	}

	public void DoGoToFridge(){
		command = Command.GoToFridge;
		xDestination = 670;
		yDestination = 265;
	}

	public void DoGoHomePosition(){
		xDestination = 600;
		yDestination = 225;
	}

	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}
}
