package restaurant.JPC.gui;

import restaurant.JPC.roles.CustomerRoleJPC;

public class Table {
	private CustomerRoleJPC occupiedBy;
	public int tableNumber;
	private int table_xpos;
	private int table_ypos;
    static final int START_TABLE_XPOS = 200;
    static final int START_TABLE_YPOS = 150;

	public Table(int tableNumber) {
		this.tableNumber = tableNumber;
		if (tableNumber == 1){
			table_xpos = START_TABLE_XPOS;
			table_ypos = START_TABLE_YPOS;
		} else if (tableNumber == 2){
			table_xpos = START_TABLE_XPOS;
			table_ypos = START_TABLE_YPOS + 100;
		} else if (tableNumber == 3){
			table_xpos = START_TABLE_XPOS + 100;
			table_ypos = START_TABLE_YPOS;
		} else if (tableNumber == 4){
			table_xpos = START_TABLE_XPOS + 100;
			table_ypos = START_TABLE_YPOS + 100;
		}
	}

	public void setOccupant(CustomerRoleJPC cust) {
		occupiedBy = cust;
	}

	public void setUnoccupied() {
		occupiedBy = null;
	}

	public CustomerRoleJPC getOccupant() {
		return occupiedBy;
	}

	public boolean isOccupied() {
		return occupiedBy != null;
	}

	public String toString() {
		return "table " + tableNumber;
	}
	
	public int getXPos(){
		return table_xpos;
	}
	
	public int getYPos(){
		return table_ypos;
	}
}

