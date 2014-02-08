package bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BankManagerImpl implements BankManager {

    // CLASS FIELDS
    //
    // example of a create table statement executed by createDB()
    private static final String CREATE_TABLE_ACCOUNT = "create table ACCOUNT (" + 
	    "AID int, " +
            "ABAL DECIMAL(10,2), " +
	    "primary key (AID)" + 
	    ")";
    private static final String CREATE_TABLE_OPERATION = "create table OPERATION (" +
            "AID int," +
            "OAMOUNT DECIMAL(10,2)," +
            "DATE DATETIME" +
            ")";
    
     private static final String CREATE_TRIGGER_1 = "CREATE TRIGGER positive_balance " +
            "BEFORE UPDATE ON ACCOUNT " +
            "FOR EACH ROW " +
             "BEGIN " +
             "IF NEW.ABAL < 0 THEN " +
             "SIGNAL sqlstate '45001'; " +
             "END IF; " +
            "END;";
     
     private static final String CREATE_TRIGGER_2 = "CREATE TRIGGER update_operation " +
            "AFTER UPDATE ON ACCOUNT " +
            "FOR EACH ROW " +
             "BEGIN " +
             "INSERT INTO OPERATION (AID,OAMOUNT,DATE) VALUES(NEW.AID,NEW.ABAL-OLD.ABAL,NOW()); " +
            "END;";
    
    private static final String CREATE_ACCOUNT = "INSERT INTO ACCOUNT (AID,ABAL) VALUES(?,0)";
    private static final String ADD_BALANCE = "UPDATE ACCOUNT SET ABAL=ABAL + ? WHERE AID=?";
    private static final String GET_BALANCE = "SELECT ABAL FROM ACCOUNT WHERE AID=?";
    
    private static final String GET_OPERATION ="SELECT OAMOUNT FROM OPERATION WHERE AID=? and DATE>=? and DATE<=?";
    
    private Connection MyConnection;
    
    public BankManagerImpl(String url, String user, String password) throws SQLException 
    {
          connect(url, user, password);
    }

    @Override
    public void createDB() throws SQLException 
    {
	// TODO Auto-generated method stub
       Statement s;
         
       try 
        {
            MyConnection.setAutoCommit(false);
            s = MyConnection.createStatement();
            
            s.executeUpdate("drop table ACCOUNT");
            s.executeUpdate("drop table OPERATION");
            
            s.executeUpdate(CREATE_TABLE_ACCOUNT);
            s.executeUpdate(CREATE_TABLE_OPERATION);
            s.executeUpdate(CREATE_TRIGGER_1);
            s.executeUpdate(CREATE_TRIGGER_2);
            
            MyConnection.commit();
        }
       catch (SQLException ex)
       {
           MyConnection.rollback();
           Logger.getLogger(BankManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
           MyConnection.setAutoCommit(true);
        }
        finally
        {
           MyConnection.setAutoCommit(true);
        }

    }

    @Override
    public boolean createAccount(int number) throws SQLException 
    {
	// TODO Auto-generated method stub
        PreparedStatement ps;
        int r = 0;
        
       try 
        {
           MyConnection.setAutoCommit(false);
            
            ps = MyConnection.prepareStatement(CREATE_ACCOUNT);
            ps.setInt(1, number);
            
            r = ps.executeUpdate();
            
            MyConnection.commit();
        }
       catch (SQLException ex)
       {
           MyConnection.rollback();
           Logger.getLogger(BankManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
           MyConnection.setAutoCommit(true);
           return false;
        }
        finally
        {
           MyConnection.setAutoCommit(true);
           return true;
        }
	
    }

    @Override
    public double getBalance(int number) throws SQLException 
    {
	// TODO Auto-generated method stub
        PreparedStatement ps;
        ResultSet R=null;
        
       try 
        {
           MyConnection.setAutoCommit(false);
            
            ps = MyConnection.prepareStatement(GET_BALANCE);
            ps.setInt(1, number);
            
            R = ps.executeQuery();
            
            MyConnection.commit();
        }
       catch (SQLException ex)
       {
           MyConnection.rollback();
           Logger.getLogger(BankManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
           MyConnection.setAutoCommit(true);
           return 0;
        }
        finally
        {
           MyConnection.setAutoCommit(true);
           R.next();
           return R.getDouble(1);
        }
    }

    @Override
    public double addBalance(int number, double amount) throws SQLException 
    {
	// TODO Auto-generated method stub
        PreparedStatement ps;
        
       try 
        {
           MyConnection.setAutoCommit(false);
            
            ps = MyConnection.prepareStatement(ADD_BALANCE);
            ps.setDouble(1, amount);
            ps.setInt(2, number);
            
            ps.executeUpdate();
            
            MyConnection.commit();
        }
       catch (SQLException ex)
       {
           MyConnection.rollback();
           MyConnection.setAutoCommit(true);
           return -1;
        }
        MyConnection.setAutoCommit(true);
        return amount;
    }

    @Override
    public boolean transfert(int from, int to, double amount) throws SQLException 
    {
	// TODO Auto-generated method stub
        PreparedStatement ps;
        int r = 0;
        
       try 
        {
           MyConnection.setAutoCommit(false);
           
           ps=MyConnection.prepareStatement(ADD_BALANCE);
           ps.setDouble(1, - amount);
           ps.setInt(2, from);
           
           ps.executeUpdate();
           
           ps=MyConnection.prepareStatement(ADD_BALANCE);
           ps.setDouble(1, amount);
           ps.setInt(2, to);
           
           ps.executeUpdate();
            
           MyConnection.commit();
        }
       catch (SQLException ex)
       {
           MyConnection.rollback();
           MyConnection.setAutoCommit(true);
           return false;
        }
        MyConnection.setAutoCommit(true);
        return true;
    }

    @Override
    public List<Operation> getOperations(int number, Date from, Date to) throws SQLException 
    {
	// TODO Auto-generated method stub
        List list = new LinkedList();
        PreparedStatement ps;
        ResultSet R = null;
        
        String dateFrom= (from.getYear()+1900) + "-" + (from.getMonth()+1) + "-" + from.getDate() + " " + (from.getHours()) + ":" + from.getMinutes() + ":" + from.getSeconds();
        String dateTo= (to.getYear()+1900) + "-" + (to.getMonth()+1) + "-" + to.getDate() + " " + (to.getHours()) + ":" + to.getMinutes() + ":" + to.getSeconds();

       try
       {   
           MyConnection.setAutoCommit(false);
            
           ps = MyConnection.prepareStatement(GET_OPERATION);
           ps.setInt(1,number);
           ps.setString(2, dateFrom);
           ps.setString(3, dateTo);
           
           R = ps.executeQuery();
            
            MyConnection.commit();
        }
       catch (SQLException ex)
       {
           MyConnection.rollback();
            Logger.getLogger(BankManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            MyConnection.setAutoCommit(true);
            return null;
        }
        finally
        {
            while (R.next())
            {
                list.add(R.getString(1));
            }
            
            if(R!=null)
            {
                R.close();
            }

            MyConnection.setAutoCommit(true);
            return list;
        }
    }

    private void connect(String url, String user, String password) throws SQLException
   {
            System.setProperty("jdbc.drivers", "com.mysql.jdbc.Driver");
            
            MyConnection= DriverManager.getConnection(url,user,password);

            MyConnection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
   }

}
