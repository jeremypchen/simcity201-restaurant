package restaurant.JPC.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FoodGui implements Gui{
	private int xPos, yPos;
	private int xDestination, yDestination;
	private String food_string;
	boolean visible;

	public FoodGui(){
		xPos = 0;
		yPos = 0;
		xDestination = 0;
		yDestination = 0;
		//img = null;
		visible = false;
	}

	public void setVisible(String food, String destination, Table table, WaiterGui waiterGui){	
		visible = true;
		if (destination.equals("Cook")){ // if waiter is bringing to kitchen
			xPos = table.getXPos();
			yPos = table.getYPos();
			xDestination = 425;
			yDestination = 175;
			if (food.equals("Steak"))
				food_string = "St?";
			else if (food.equals("Chicken"))
				food_string = "Ch?";
			else if (food.equals("Pizza"))
				food_string = "P?";
			else if (food.equals("Salad"))
				food_string = "S?";
		} else if (destination.equals("Table")){ // if waiter is bringing to table
			xPos = 425;
			yPos = 175;
			xDestination = table.getXPos();
			yDestination = table.getYPos()+20;
			if (food.equals("Steak"))
				food_string = "St";
			else if (food.equals("Chicken"))
				food_string = "Ch";
			else if (food.equals("Pizza"))
				food_string = "P";
			else if (food.equals("Salad"))
				food_string = "S";
		}
	}

	public void setVisible(String food, String destination){
		visible = true;
		if (destination.equals("Grill")){ // if cook is bringing to grill
			xPos = 685;
			yPos = 265;
			xDestination = 515;
			yDestination = 175;
			if (food.equals("Steak"))
				food_string = "St";
			else if (food.equals("Chicken"))
				food_string = "Ch";
			else if (food.equals("Pizza"))
				food_string = "P";
			else if (food.equals("Salad"))
				food_string = "S";
		} else if (destination.equals("Plating Area")){
			xPos = 515;
			yPos = 175;
			xDestination = 450;
			yDestination = 175;
			if (food.equals("Steak"))
				food_string = "St";
			else if (food.equals("Chicken"))
				food_string = "Ch";
			else if (food.equals("Pizza"))
				food_string = "P";
			else if (food.equals("Salad"))
				food_string = "S";
		}
	}

	public void updatePosition(){
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;
		if (xPos == xDestination && yPos == yDestination){
			setNotVisible();
		}
	}

	public void setNotVisible(){
		visible = false;
	}

	public void draw(Graphics2D g){
		if (visible){
			g.setColor(Color.RED);
			g.drawString(food_string, xPos, yPos);
		}
	}

	public boolean isPresent() {
		return true;
	}
}
