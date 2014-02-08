package bank;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface BankManager
{

    /**
     * Creates the schema of the bank's database. This includes all the schema
     * elements : tables, and possibly triggers. The database is empty after
     * this method returns.
     * <p>
     * The method will execute a sequence of create table / trigger statement.
     * The statements are hard-coded, as shown in the example in the
     * <code>BankManagerImpl</code> class.
     *
     *
     * @throws SQLException
     *             if an SQL exception occurs
     */
    void createDB() throws SQLException;

    /**
     * Creates a new account with the specified number.
     *
     * @param number
     *            the number of the account
     * @return <code>true</code> if the method succeeds and <code>false</code>
     *         otherwise
     * @throws SQLException
     *             if an SQL exception occurs
     *
     */
    boolean createAccount(int number) throws SQLException;

    /**
     * Returns the balance of the specified account.
     *
     * @param number
     *            the number of the account
     * @return the balance of the account
     * @throws SQLException
     *             if an SQL exception occurs
     */
    double getBalance(int number) throws SQLException;

    /**
     * Adds the specified amount to the specified account. A call to this method
     * performs a deposit if the amount is a positive value, and a withdrawal
     * otherwise. A debit operation without insufficient funds must be refused.
     *
     * @param number
     *            the number of the account
     * @param amount
     *            the amount to add to the account's balance
     * @return the new balance of the account, or -1.0 if the withdrawal could
     *         not be performed
     * @throws SQLException
     *             if an SQL exception occurs
     */
    double addBalance(int number, double amount) throws SQLException;

    /**
     * Transfert the specified amount between the specified accounts.
     *
     * @param from
     *            the number of the debited account
     * @param to
     *            the number of the credited account
     * @param amount
     *            the amount to transfert
     * @return <code>true</code> if the method succeeds and <code>false</code>
     *         otherwise
     * @throws SQLException
     *             if an SQL exception occurs
     */
    boolean transfert(int from, int to, double amount) throws SQLException;

    /**
     * Returns the list of operations on the specified account in the specified
     * time interval.
     *
     * @param number
     *            the number of the account
     * @param from
     *            start date/time (inclusive)
     * @param to
     *            end date/time (inclusive)
     * @return the list of operations on the account
     * @throws SQLException
     *             if an SQL exception occurs
     */
    List<Operation> getOperations(int number, Date from, Date to) throws SQLException;
}
