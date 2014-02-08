package test;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import bank.BankManager;
import bank.BankManagerImpl;
import bank.Operation;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleTest {

    //
    // CONSTANTS
    //
    private static final int MAX_ACCOUNTS = 10;
    private static final int MAX_CUSTOMERS = 5;
    //
    // CLASS FIELDS
    //
    private static int testTotal = 0;
    private static int testOK = 0;

    //
    // HELPER CLASSES
    //
    static class CustomerEmulator extends Thread 
    {

	private BankManager manager;
	private String customer;

	public CustomerEmulator(BankManager m, String c) {
	    manager = m;
	    customer = c;
	}

        @Override
	public String toString() 
        {
	    return customer + "[" + manager + "]";
	}

        @Override
	public void run()
        {
	    System.out.println(this + ": starting");
            
            double b;
            boolean s;
            Date now = new Date();
            
            try 
            {
                if("multi-customer0".equals(customer))
                {  
                   s= manager.transfert(6, 7, 100.0);
                   check(this + "transfert-0",  s);
                   
                   List<Operation> o1 = manager.getOperations(6, new Date(now.getTime() - 24*60*60*1000), now);
	           System.out.println("operations on account #6 = " + o1);
                   
                   List<Operation> o2 = manager.getOperations(7, new Date(now.getTime() - 24*60*60*1000), now);
	           System.out.println("operations on account #7 = " + o2);
                }
                if("multi-customer1".equals(customer))
                {
                   b = manager.addBalance(6, 1000.0);
                   check(this + ": addBalance",  b == 1000.0);
                   List<Operation> o1 = manager.getOperations(6, new Date(now.getTime() - 24*60*60*1000), now);
	           System.out.println("operations on account #6 = " + o1);
                }
                if("multi-customer2".equals(customer))
                {  
                   s= manager.transfert(6, 8, 100.0);
                   check(this + "transfert-1",  s);
                   
                   List<Operation> o1 = manager.getOperations(6, new Date(now.getTime() - 24*60*60*1000), now);
	           System.out.println("operations on account #6 = " + o1);
                   
                   List<Operation> o2 = manager.getOperations(8, new Date(now.getTime() - 24*60*60*1000), now);
	           System.out.println("operations on account #8 = " + o2);
                }
                if("multi-customer3".equals(customer))
                {  
                   s= manager.transfert(8, 9, 50.25);
                   check(this + "transfert-2",  s);
                   
                   List<Operation> o1 = manager.getOperations(8, new Date(now.getTime() - 24*60*60*1000), now);
	           System.out.println("operations on account #8 = " + o1);
                   
                   List<Operation> o2 = manager.getOperations(9, new Date(now.getTime() - 24*60*60*1000), now);
	           System.out.println("operations on account #9 = " + o2);
                }
                if("multi-customer4".equals(customer))
                {  
                   b = manager.addBalance(9, -25.25);
                   check(this + ": addBalance",  b != -1);
                   List<Operation> o1 = manager.getOperations(9, new Date(now.getTime() - 24*60*60*1000), now);
	           System.out.println("operations on account #9 = " + o1);
                }
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(SimpleTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        
	    System.out.println(this + ": exiting");
	}

    }

    //
    // HELPER METHODS
    //
    private static void check(String test, boolean ok) {
	testTotal += 1;
	System.out.print(test + ": ");
	if (ok) {
	    testOK += 1;
	    System.out.println("ok");
	} else {
	    System.out.println("FAILED");
	}
    }

    private static void singleUserTests(BankManager m, String c) throws SQLException {

	// deposit 1000 on account #1
	double b = m.addBalance(1, 1000.0);
	check("addBalance",  b == 1000.0);

	// transfert 500 from account #1 to account #2
	boolean s = m.transfert(1, 2, 250.0);
	check("transfert-1",  s);
	check("transfert-2",  m.getBalance(1) == 750.0);
	check("transfert-3",  m.getBalance(2) == 250.0);
	
	// check operations on account #1 between yesterday and now
	Date now = new Date();
        
	List<Operation> o1 = m.getOperations(1, new Date(now.getTime() - 24*60*60*1000), now);
	System.out.println("operations on account #1 = " + o1);
	check("getOperations-1", o1.size() == 2);
        
	List<Operation> o2 = m.getOperations(2, new Date(now.getTime() - 24*60*60*1000), now);
	System.out.println("operations on account #2 = " + o2);
	check("getOperations-1", o2.size() == 1);
        
        //Check that an account can't have a negative balance
        b=m.addBalance(3, 900);
        check("Add Balance",  b == 900);
	b=m.addBalance(3, -1000.0);
        check("Adding Balance failed",  b == -1);
	check("Balance unchanged",  m.getBalance(3) == 900);
        
        s=m.transfert(3, 4, 1000);
        check("transfert Failed", !s);
        
        List<Operation> o3 = m.getOperations(3, new Date(now.getTime() - 24*60*60*1000), now);
	System.out.println("operations on account #3 = " + o3);
	check("getOperations-1", o3.size() == 1);
        
    }

    //
    // MAIN
    //
    public static void main(String[] args) {

	// check parameters
	if (args.length != 3) {
	    System.err.println("usage: SimpleTest <url> <user> <password>");
	    System.exit(-1);
	}

	try {
	    // create ReservationManager object
	    BankManager manager = new BankManagerImpl(args[0], args[1], args[2]);

	    // create the database
	    manager.createDB();
	    
	    // populate the database
	    for (int i = 0; i < MAX_ACCOUNTS; i++) 
            {
		manager.createAccount(i + 1);
	    }

	    // execute single-user tests
	    singleUserTests(manager, "single-customer");

	    // execute multi-user tests
	    for (int i = 0; i < MAX_CUSTOMERS; i++) {
		BankManager m = new BankManagerImpl(args[0], args[1], args[2]);
		new CustomerEmulator(m, "multi-customer" + i).start();
	    }

	} catch (Exception e) {
	    System.err.println("test aborted: " + e);
	    //e.printStackTrace();
	}

	// print test results
	if (testTotal == 0) 
        {
	    System.out.println("no test performed");
	} 
        else 
        {
	    String r = "test results: ";
	    r += "total=" + testTotal;
	    r += ", ok=" + testOK + "(" + ((testOK * 100) / testTotal) + "%)";
	    System.out.println(r);
	}

    }
}
