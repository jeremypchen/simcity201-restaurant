package restaurant.JPC.roles;

import restaurant.JPC.gui.CustomerGui;
import restaurant.JPC.gui.Menu;
import restaurant.JPC.gui.Table;
import restaurant.JPC.interfaces.Customer;
import agent.Agent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/**
 * Restaurant customer agent.
 */
public class CustomerRoleJPC extends Agent implements Customer{
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;
	private Table table;
	// Booleans to test non-normatives
	private boolean rude = false;
	private boolean mistake = false;
	private boolean presetChoice = false;
	private boolean patient = true;
	private boolean outOfSaladAndPizza = false;
	private int debt;
	private Menu menu;
	private int bill;
	private int cash;
	private String choice;
	
	private Semaphore atWaitingArea = new Semaphore(0, true);

	// agent correspondents
	private HostRoleJPC host;
	private WaiterRoleJPC myWaiter;

	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, AskedToOrder, Ordered, 
		Eating, DoneEating, Leaving, WaitingForCheck, PaidCashier, GivenMenu};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followWaiter, seated, waitingForFood, receivedFood, doneEating, 
		doneLeaving, AskedToOrder, ReceivedCheck, Paid, readyToOrder, GivenMenu, GiveOrder};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerRoleJPC(String name){
		super();
		this.name = name;
		
		if (name.equals("5"))
			cash = 5;
		else if (name.equals("7"))
			cash = 7;
		else if (name.equals("7Out")){
			cash = 7;
			outOfSaladAndPizza = true;
		}
		else if (name.equals("Rude")){
			cash = 5;
			rude = true;
		}
		else if (name.equals("Mistake")){
			cash = 5;
			rude = true;
			mistake = true;
		}
		else if (name.equals("Impatient")){
			cash = 5;
			patient = false;
		}
		else if (name.equals("Pizza")){
			cash = 10;
			presetChoice = true;
			choice = "Pizza";
		}
		else if (name.equals("Steak")){
			cash = 10;
			presetChoice = true;
			choice = "Steak";
		}
		else 
			cash = 10;
	}
	
	public String getCustomerName() {
		return name;
	}
	// Messages

	public void gotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	public void msgWeAreFull(){ // from host
		print("No thanks. Leaving now.");
		leaveEarly();
		stateChanged();
	}
	
	public void msgFollowMeToTable(WaiterRoleJPC w, Menu menu, Table table){ // from waiter
		event = AgentEvent.followWaiter;
		myWaiter = w;
		this.table = table;
		this.menu = menu;
		// hack to test a non-norm
		if (outOfSaladAndPizza){
			menu.delete("Pizza");
			menu.delete("Salad");
		}
		stateChanged();
	}
	
	public void msgHereIsTheMenu(){ // waiter
		event = AgentEvent.GivenMenu;
		stateChanged();
	}
	
	public void msgWhatWouldYouLike(){ // from waiter
		event = AgentEvent.AskedToOrder;
		stateChanged();
	}
	
	public void msgWhatWouldYouLikeToReorder(Menu updatedMenu){
		state = AgentState.BeingSeated;
		event = AgentEvent.GivenMenu;
		menu = updatedMenu;
		stateChanged();
	}
	
	public void msgHereIsYourFood(){ // from waiter
		print("Received food");
		event = AgentEvent.receivedFood;
		stateChanged();
	}
	
	public void msgAnimationAtWaitingArea(){ // from animation
		atWaitingArea.release();
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
		
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	
	public void msgAnimationFinishedPayCashier(){
		//from animation
		event = AgentEvent.Paid;
		stateChanged();
	}
	
	public void msgHereIsCheck(int bill){ // from waiter
		this.bill = bill;
		event = AgentEvent.ReceivedCheck;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter){
			state = AgentState.BeingSeated;
			followWaiter(myWaiter);
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.GivenMenu){
			state = AgentState.GivenMenu;
			thinkOfOrder();
			return true;
		}
		if (state == AgentState.GivenMenu && event == AgentEvent.AskedToOrder){
			state = AgentState.Ordered;
			giveOrder();
			return true;
		}
		if (state == AgentState.Ordered && event == AgentEvent.receivedFood){
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.WaitingForCheck;
			askForCheck();
			return true;
		}
		
		if (state == AgentState.WaitingForCheck && event == AgentEvent.ReceivedCheck){
			state = AgentState.PaidCashier;
			payCashier();
			return true;
		}
		
		if (state == AgentState.PaidCashier && event == AgentEvent.Paid){
			state = AgentState.Leaving;
			leave();
			return true;
		}
		
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		Do("Going to restaurant");
		customerGui.DoGoToWaitingArea();
		try {
			atWaitingArea.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		host.msgIWantToEat(this);//send our instance, so he can respond to us
	}
	
	private void followWaiter(WaiterRoleJPC waiter){
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(1, table);
	}

	private void thinkOfOrder(){
		if(!presetChoice)
			choice = menu.pickItem(cash, rude);
		
			timer.schedule(new CustomerTimerTask(myWaiter, this){
				public void run(){
					if(choice == "None"){
						print("I cannot afford anything. Leaving.");
						leave();
					} else {
					print("I am ready to order.");
					myWaiter.msgImReadyToOrder(this.c, choice);
					stateChanged();
					}
				}
			}, 5000);
		stateChanged();
	}
	
	private void giveOrder(){
		print("I would like " + choice + ", please");
	}

	private void EatFood() {
		Do("Eating Food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			public void run() {
				print("Done eating.");
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
	}
	
	private void askForCheck(){
		myWaiter.msgCheckPlease(this);
		print("Can I have the check please?");
	}
	
	private void payCashier(){
		customerGui.DoPayCashier();
		if (cash >= bill){
			if (!mistake){
			cash -= bill;
			print("Paying cashier bill of " + bill + " dollars");
			print("I now have " + cash + " dollars. Leaving now.");
			} else {
				cash -= (bill + debt);
				print("Paying cashier bill of " + bill + " dollars and also " 
				+ debt + " dollars for not paying last time I was here.");
				debt = 0;
				print("I now have " + cash + " dollars. Leaving now.");
			}
		} else {
			if (mistake){
				print("I don't have enough cash to pay this bill... I will pay you back next time.");
				debt += bill;
				cash = 20;
			} else if (rude)
				print("I don't have enough cash to pay this bill... Leaving quietly.");
		}
	}
	
	private void leave() {
		myWaiter.msgLeaving(this);
		customerGui.DoExitRestaurant();
		state = AgentState.Leaving;
	}
	
	private void leaveEarly(){
		customerGui.DoExitRestaurant();
		state = AgentState.Leaving;
	}

	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}
	
	public boolean isPatient(){
		return patient;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}
	
	public void setHost(HostRoleJPC h){
		host = h;
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	
	public Table getTable(){
		return table;
	}
	
	class CustomerTimerTask extends TimerTask  {
		WaiterRoleJPC myWaiter;
		CustomerRoleJPC c;

	     public CustomerTimerTask(WaiterRoleJPC w, CustomerRoleJPC c) {
	         this.myWaiter = w;
	         this.c = c;
	     }

	     public void run() {
	     }
	}
}

