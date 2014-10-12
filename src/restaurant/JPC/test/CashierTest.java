package restaurant.JPC.test;

import junit.framework.TestCase;
import restaurant.JPC.Bill;
import restaurant.JPC.Bill.cashierBillState;
import restaurant.JPC.roles.CashierRoleJPC;
import restaurant.JPC.test.mock.MockCook;
import restaurant.JPC.test.mock.MockCustomer;
import restaurant.JPC.test.mock.MockMarket;
import restaurant.JPC.test.mock.MockWaiter;

public class CashierTest extends TestCase {
	CashierRoleJPC cashier;
	MockWaiter waiter;
	MockCook cook;
	MockCustomer customer;
	MockMarket pizza_market;
	MockMarket salad_market;
	MockMarket meat_market;


	public void setUp() throws Exception{
		super.setUp();
		cashier = new CashierRoleJPC("cashier");
		customer = new MockCustomer("mockcustomer");
		waiter = new MockWaiter("mockwaiter");
		cook = new MockCook("mockcook");
		pizza_market = new MockMarket("Pizza Market");
		salad_market = new MockMarket("Salad Market");
		meat_market = new MockMarket("Meat Market");
	}
	
	// TEST ONE (Test 1) NORMAL MARKET - One market fulfills one order, one bill paid in full
	public void testOneNormalMarketScenario(){
		// Preconditions
		assertEquals("Cashier should have $50 in it. It doesn't.", cashier.register, 50);
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgHereIsBill is called. " +
				"Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());

		// Step 1 - Cook messages Pizza Market for 10 Pizzas, Market delivers 
		pizza_market.msgINeedFood("Pizza", 10, cook, cashier); // Pizza Market gets order of 10 pizzas from cook

		assertTrue("Pizza Market should have logged \"Received INeedFood from mockcook\", but didn't. His log reads instead:" 
				+ pizza_market.log.getLastLoggedEvent().toString(), pizza_market.log.containsString("Received INeedFood from: " + cook.toString()));
		
		cook.msgHereIsYourFood("Pizza", 10); // Cook gets delivery of 10 pizzas
		
		assertTrue("Cook should have logged \"Received HereIsYourFood from market. Food: Pizza, Amount: 10\", but didn't. His log reads instead:" 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received HereIsYourFood from market. Food: Pizza, Amount: 10"));
		
		// Step 2 - Cashier is billed for $30 for pizza order
		cashier.msgHereIsBillForMarketOrder(pizza_market, 30); // cashier gets market bill of $30
		
		assertEquals("Cashier should have one market bill in it. It doesn't.", cashier.marketBills.size(), 1);
		
		// Step 3 - Cashier will pay for the bill
		assertTrue("Cashier's scheduler should return true (next action is to pay market bill), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have zero market bills in it. It doesn't.", cashier.marketBills.size(), 0);
		assertEquals("Cashier should have $20 (50-30) in it. It doesn't.", cashier.register, 20);
		
		// Step 4 - No actions remaining
		assertFalse("Cashier's scheduler should return false (no more actions), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Pizza Market should have logged \"Received bill of $30\", but didn't. His log reads instead:" 
				+ pizza_market.log.getLastLoggedEvent().toString(), pizza_market.log.containsString("Received bill of $30"));
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
	}

	// TEST TWO (Test 2) NORMAL MARKET - Two markets fulfill one order, two bills paid in full
	public void testTwoNormalMarketScenario(){
		// Preconditions
		assertEquals("Cashier should have $50 in it. It doesn't.", cashier.register, 50);
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgHereIsBill is called. " +
				"Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());

		// Step 1 - Cook messages Pizza Market for 10 Pizzas and Meat Market for 5 Steaks, Both markets deliver
		pizza_market.msgINeedFood("Pizza", 10, cook, cashier); // Pizza Market gets order of 10 pizzas from cook
		meat_market.msgINeedFood("Steak", 5, cook, cashier); // Meat Market gets order of 5 steaks from cook

		assertTrue("Pizza Market should have logged \"Received INeedFood from mockcook\", but didn't. His log reads instead:" 
				+ pizza_market.log.getLastLoggedEvent().toString(), pizza_market.log.containsString("Received INeedFood from: " + cook.toString()));
		assertTrue("Meat Market should have logged \"Received INeedFood from mockcook\", but didn't. His log reads instead:" 
				+ meat_market.log.getLastLoggedEvent().toString(), meat_market.log.containsString("Received INeedFood from: " + cook.toString()));
		
		cook.msgHereIsYourFood("Pizza", 10); // Cook gets delivery of 10 pizzas
		
		assertTrue("Cook should have logged \"Received HereIsYourFood from market. Food: Pizza, Amount: 10\", but didn't. His log reads instead:" 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received HereIsYourFood from market. Food: Pizza, Amount: 10"));
		
		cook.msgHereIsYourFood("Steak", 5); // Cook gets delivery of 5 steaks
		
		assertTrue("Cook should have logged \"Received HereIsYourFood from market. Food: Steak, Amount: 5\", but didn't. His log reads instead:" 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received HereIsYourFood from market. Food: Steak, Amount: 5"));
		
		// Step 2 - Cashier is billed for both orders
		cashier.msgHereIsBillForMarketOrder(pizza_market, 30); // cashier gets market bill of $30
		assertEquals("Cashier should have one market bill in it. It doesn't.", cashier.marketBills.size(), 1);
		cashier.msgHereIsBillForMarketOrder(meat_market, 20); // cashier gets market bill of $20
		assertEquals("Cashier should have two market bills in it. It doesn't.", cashier.marketBills.size(), 2);
		
		// Step 3 - Cashier pays for pizza order
		assertTrue("Cashier's scheduler should return true (next action is to pay market bill), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have one market bill in it. It doesn't.", cashier.marketBills.size(), 1);
		assertEquals("Cashier should have $20 (50-30) in it. It doesn't.", cashier.register, 20);
		
		// Step 4 - Cashier pays for steak order
		assertTrue("Cashier's scheduler should return true (next action is to pay market bill), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have zero market bill in it. It doesn't.", cashier.marketBills.size(), 0);
		assertEquals("Cashier should have $0 (20-20) in it. It doesn't.", cashier.register, 0);
		
		// Step 5 - No actions remaining
		assertFalse("Cashier's scheduler should return false (no more actions), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Pizza Market should have logged \"Received bill of $30\", but didn't. His log reads instead:" 
				+ pizza_market.log.getLastLoggedEvent().toString(), pizza_market.log.containsString("Received bill of $30"));
		assertTrue("Meat Market should have logged \"Received bill of $20\", but didn't. His log reads instead:" 
				+ meat_market.log.getLastLoggedEvent().toString(), meat_market.log.containsString("Received bill of $20"));
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());

	}
	
	// TEST ONE (Test 3) NON-NORM MARKET - Cashier cannot afford a market bill
	public void testOneNonNormalMarketScenario(){
		// Preconditions
		assertEquals("Cashier should have $50 in it. It doesn't.", cashier.register, 50);
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgHereIsBill is called. " +
				"Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());

		// Step 1 - Cook orders 30 pizzas from the market, Market delivers
		pizza_market.msgINeedFood("Pizza", 30, cook, cashier); // Pizza Market gets order of 10 pizzas from cook

		assertTrue("Pizza Market should have logged \"Received INeedFood from mockcook\", but didn't. His log reads instead:" 
				+ pizza_market.log.getLastLoggedEvent().toString(), pizza_market.log.containsString("Received INeedFood from: " + cook.toString()));
		
		cook.msgHereIsYourFood("Pizza", 30); // Cook gets delivery of 30 pizzas
		
		assertTrue("Cook should have logged \"Received HereIsYourFood from market. Food: Pizza, Amount: 10\", but didn't. His log reads instead:" 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received HereIsYourFood from market. Food: Pizza, Amount: 30"));
		
		// Step 2 - Cashier is billed $60 for order and tries to pay for it
		cashier.msgHereIsBillForMarketOrder(pizza_market, 60); // cashier gets market bill of $60
		
		assertEquals("Cashier should have one market bill in it. It doesn't.", cashier.marketBills.size(), 1);
		assertTrue("Cashier's scheduler should return true (next action is to pay market bill), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have zero market bills in it. It doesn't.", cashier.marketBills.size(), 0);
		assertEquals("Cashier should have $50 in it. It doesn't.", cashier.register, 50);
	
		// Step 3 - Cashier cannot afford the order, places in bill in IOU list
		assertEquals("Cashier should have one indebted market bill in it. It doesn't.", cashier.indebtedMarketBills.size(), 1);
		assertTrue("Cashier's scheduler should return true (next action is to pay indebted market bill), but didn't.", cashier.pickAndExecuteAnAction());

		// Step 4 - Cashier receives $20 from a customer for a meal
		cashier.msgHereIsBill(20); // cashier receives $20 for a meal
		
		assertEquals("Cashier should have $70 in it. It doesn't.", cashier.register, 70);
		
		// Step 5 - Cashier tries to pay for bill in IOU list
		assertTrue("Cashier's scheduler should return true (next action is to try to pay indebted market bill), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have zero indebted market bills in it. It doesn't.", cashier.indebtedMarketBills.size(), 0);
		assertEquals("Cashier should have $10 in it. It doesn't.", cashier.register, 10);

		// Step 6 - No actions remaining
		assertFalse("Cashier's scheduler should return false (no more actions), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Pizza Market should have logged \"Received bill of $60\", but didn't. His log reads instead:" 
				+ pizza_market.log.getLastLoggedEvent().toString(), pizza_market.log.containsString("Received bill of $60"));
	}
	
	// TEST TWO (Test 4) NON-NORM MARKET - Cashier cannot afford two market bills
	public void testTwoNonNormalMarketScenario(){
		// Preconditions
		assertEquals("Cashier should have $50 in it. It doesn't.", cashier.register, 50);
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgHereIsBill is called. " +
				"Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());

		// Step 1 - Cook orders 10 Pizzas and 5 Steaks from two markets, markets deliver
		pizza_market.msgINeedFood("Pizza", 10, cook, cashier); // Pizza Market gets order of 10 pizzas from cook
		meat_market.msgINeedFood("Steak", 5, cook, cashier); // Meat Market gets order of 5 steaks from cook

		assertTrue("Pizza Market should have logged \"Received INeedFood from mockcook\", but didn't. His log reads instead:" 
				+ pizza_market.log.getLastLoggedEvent().toString(), pizza_market.log.containsString("Received INeedFood from: " + cook.toString()));
		assertTrue("Meat Market should have logged \"Received INeedFood from mockcook\", but didn't. His log reads instead:" 
				+ meat_market.log.getLastLoggedEvent().toString(), meat_market.log.containsString("Received INeedFood from: " + cook.toString()));
		
		cook.msgHereIsYourFood("Pizza", 10); // Cook gets delivery of 10 pizzas
		
		assertTrue("Cook should have logged \"Received HereIsYourFood from market. Food: Pizza, Amount: 10\", but didn't. His log reads instead:" 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received HereIsYourFood from market. Food: Pizza, Amount: 10"));
		
		cook.msgHereIsYourFood("Steak", 5); // Cook gets delivery of 5 steaks
		
		assertTrue("Cook should have logged \"Received HereIsYourFood from market. Food: Steak, Amount: 5\", but didn't. His log reads instead:" 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received HereIsYourFood from market. Food: Steak, Amount: 5"));
		
		// Step 2 - Cashier is billed for both orders and attempts to pay for the pizza bill
		cashier.msgHereIsBillForMarketOrder(pizza_market, 60); // cashier gets market bill of $60
		cashier.msgHereIsBillForMarketOrder(meat_market, 60); // cashier gets market bill of $40
		
		assertEquals("Cashier should have two market bills in it. It doesn't.", cashier.marketBills.size(), 2);
		assertTrue("Cashier's scheduler should return true (next action is to pay market bill), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have one indebted market bill in it. It doesn't.", cashier.indebtedMarketBills.size(), 1);
		assertEquals("Cashier should have one market bill in it. It doesn't.", cashier.marketBills.size(), 1);
				
		// Step 3 - Cashier attempts to pay for the meat market bill
		assertTrue("Cashier's scheduler should return true (next action is to pay market bill), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have $50 in it. It doesn't.", cashier.register, 50);
		assertEquals("Cashier should have two indebted market bills in it. It doesn't.", cashier.indebtedMarketBills.size(), 2);
		assertEquals("Cashier should have zero market bills in it. It doesn't.", cashier.marketBills.size(), 0);
		assertTrue("Cashier's scheduler should return true (next action is to pay indebted market bill), but didn't.", cashier.pickAndExecuteAnAction());

		// Step 4 - Cashier receives $20 from a customer for a meal
		cashier.msgHereIsBill(20); // cashier receives $20 for a meal
		
		assertEquals("Cashier should have $70 in it. It doesn't.", cashier.register, 70);

		// Step 5 - Cashier pays for the pizza bill
		assertTrue("Cashier's scheduler should return true (next action is to try to pay indebted market bill), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have one indebted market bill in it. It doesn't.", cashier.indebtedMarketBills.size(), 1);
		assertEquals("Cashier should have $10 in it. It doesn't.", cashier.register, 10);
		
		// Step 6 - Cashier attempts to pay for the market bill in the IOU list
		assertTrue("Cashier's scheduler should return true (next action is to try to pay indebted market bill), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have one indebted market bill in it. It doesn't.", cashier.indebtedMarketBills.size(), 1);
		assertEquals("Cashier should have $10 in it. It doesn't.", cashier.register, 10);
		
		// Step 7 - Cashier receives $50 from a customer for a meal
		cashier.msgHereIsBill(50); // cashier receives $50 for a meal
		
		assertEquals("Cashier should have $60 in it. It doesn't.", cashier.register, 60);
		
		// Step 8 - Cashier pays for steak bill
		assertTrue("Cashier's scheduler should return true (next action is to try to pay indebted market bill), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have zero indebted market bills in it. It doesn't.", cashier.indebtedMarketBills.size(), 0);
		assertEquals("Cashier should have $0 in it. It doesn't.", cashier.register, 0);

		// Step 9 - No actions remaining
		assertFalse("Cashier's scheduler should return false (no more actions), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Pizza Market should have logged \"Received bill of $60\", but didn't. His log reads instead:" 
				+ pizza_market.log.getLastLoggedEvent().toString(), pizza_market.log.containsString("Received bill of $60"));
		assertTrue("Meat Market should have logged \"Received bill of $60\", but didn't. His log reads instead:" 
				+ meat_market.log.getLastLoggedEvent().toString(), meat_market.log.containsString("Received bill of $60"));
	}

	// TEST ONE (Test 5) NORMAL WAITER - Cashier gets check request from waiter for pizza order
	public void testOneNormalWaiterScenario(){
		customer.cashier = cashier;
		// Preconditions
		assertEquals("Cashier should have $50 in it. It doesn't.", cashier.register, 50);
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgHereIsBill is called. " +
				"Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());

		// Step 1 - Waiter messages cashier, asking for a check for Pizza for customer
		cashier.msgNeedCheckFor("Pizza", customer, waiter); //send the message from a waiter for check for Pizza

		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Cashier should have 1 check requests in it. It doesn't.", cashier.checkRequests.size(), 1);
		
		// Step 2 - Cashier will prepare this check
		assertTrue("Cashier's scheduler should return true (next action is to get check), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have 0 check requests in it. It doesn't.", cashier.checkRequests.size(), 0);		
		
		// Step 3 - No actions remaining
		assertFalse("Cashier's scheduler should return false, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
				+ customer.log.toString(), 0, customer.log.size());
	}

	// TEST ONE (Test 6) NORMAL CUSTOMER - Cashier gets bill from two customers
	public void testTwoNormalCustomerScenario(){
		// Preconditions
		assertEquals("Cashier should have $50 in it. It doesn't.", cashier.register, 50);
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgHereIsBill is called. " +
				"Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());

		// Step 1 - Customer pays cashier bill of 10
		cashier.msgHereIsBill(10); // customer gives bill of $10 to cashier

		assertEquals("Cashier should have $60 in it. It doesn't.", cashier.register, 60);
		
		// Step 2 - Customer pays cashier bill of 6
		cashier.msgHereIsBill(6); // customer gives bill of $6 to cashier
		
		assertEquals("Cashier should have $66 in it. It doesn't.", cashier.register, 66);

		// Step 2 - No actions remaining
		assertFalse("Cashier's scheduler should return false.", cashier.pickAndExecuteAnAction());
	}
}
