package restaurant.JPC.roles;

import agent.Agent;
import restaurant.JPC.gui.Table;

import java.util.*;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class HostRoleJPC extends Agent {
	public List<CustomerRoleJPC> waitingCustomers = Collections.synchronizedList(new ArrayList<CustomerRoleJPC>());
	public List<WaiterRoleJPC> allWaiters 		= Collections.synchronizedList(new ArrayList<WaiterRoleJPC>());
	public List<WaiterRoleJPC> availableWaiters 	= Collections.synchronizedList(new ArrayList<WaiterRoleJPC>());
	public List<WaiterRoleJPC> wantToBreakWaiters = Collections.synchronizedList(new ArrayList<WaiterRoleJPC>());
	static final int NTABLES = 4;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	private String name;

	public HostRoleJPC(String name) {
		super();

		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}

		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<CustomerRoleJPC> getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection<Table> getTables() {
		return tables;
	}

	// Messages

	public void msgIWantToEat(CustomerRoleJPC cust) {
		waitingCustomers.add(cust);
		stateChanged();
	}

	public void msgIAmFree(WaiterRoleJPC waiter){
		addWaiterToAvailable(waiter);
		stateChanged();
	}

	public void msgTableIsFree(Table table){
		setTableUnoccupied(table);
		stateChanged();
	}

	public void msgIdLikeToGoOnBreak(WaiterRoleJPC waiter){
		wantToBreakWaiters.add(waiter);
		stateChanged();
	}

	public void msgBackFromBreak(WaiterRoleJPC waiter){
		availableWaiters.add(waiter);
		allWaiters.add(waiter);
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */

		for (Table table : tables) {
			if (!table.isOccupied() && !waitingCustomers.isEmpty() && !availableWaiters.isEmpty()){
				print("Serving next customer: " + waitingCustomers.get(0).getName());
				assignWaiter(waitingCustomers.get(0), table); // the action
				waitingCustomers.remove(0);
				return true;//return true to the abstract agent to reinvoke the scheduler.
			}
		}

		if (!waitingCustomers.isEmpty() && !availableWaiters.isEmpty()){ // means all tables full
			if(!waitingCustomers.get(0).isPatient()) {// if not patient
				print("Sorry we are full right now, would you like to wait?");
				waitingCustomers.get(0).msgWeAreFull();
				waitingCustomers.remove(0);
				return true;
			}
		}

		try { 
			for (WaiterRoleJPC w : wantToBreakWaiters){
				if (allWaiters.size() == 1){
					sayCannotGoOnBreak(w);
				} else {
					grantBreak(w);
				}
				wantToBreakWaiters.remove(w);
			}
		} catch (ConcurrentModificationException e) {
		}

		return false;
	}

	// Actions
	public void addWaiterToAvailable(WaiterRoleJPC waiter){
		boolean waiterAlreadyInAvailable = false;
		synchronized(availableWaiters){
			for (WaiterRoleJPC w : availableWaiters){
				if (waiter == w)
					waiterAlreadyInAvailable = true;
			}
		}
		if (!waiterAlreadyInAvailable)
			availableWaiters.add(waiter);
	}


	private void assignWaiter(CustomerRoleJPC customer, Table table){
		table.setOccupant(customer);
		print("The next available waiter is: " + availableWaiters.get(0).getName());
		availableWaiters.get(0).msgSitAtTable(customer, table);
		availableWaiters.remove(0);
	}

	private void setTableUnoccupied(Table table){
		for (Table t: tables){
			if(t == table)
				t.setUnoccupied();
		}
	}

	private void sayCannotGoOnBreak(WaiterRoleJPC w){
		print("You cannot go on break - we don't have enough waiters");
	}

	private void grantBreak(WaiterRoleJPC w){
		w.msgCanGoOnBreak();
		allWaiters.remove(w);
		availableWaiters.remove(w);
		print("You can go on break after you finish your current tasks");
	}


}

