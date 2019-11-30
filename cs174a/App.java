package cs174a;                                             // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// You may have as many imports as you need.
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Properties;

import com.sun.jdi.event.StepEvent;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.proxy.annotation.Pre;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;

/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable
{
	private OracleConnection _connection;                   // Example connection object to your DB.
	private Date systemDate;

	/**
	 * Default constructor.
	 * DO NOT REMOVE.
	 */
	App()
	{

		systemDate = Date.valueOf("2015-10-15");
		// TODO: Any actions you need.
	}

	/**
	 * This is an example access operation to the DB.
	 */
	void exampleAccessToDB()
	{
		// Statement and ResultSet are AutoCloseable and closed automatically.
		try( Statement statement = _connection.createStatement() )
		{
			PreparedStatement seeAll = null;
			String allNames = "select table_name from all_tables where owner = ?";
			seeAll = _connection.prepareStatement(allNames);
			seeAll.setString(1, _connection.getUserName());

			//try( ResultSet resultSet = statement.executeQuery( "select owner, table_name from all_tables where owner = 'C##SIHUA'" ) )\
			try(ResultSet resultSet = seeAll.executeQuery())
			{
				while( resultSet.next() )
					System.out.println( resultSet.getString( "table_namE" ) + " " );
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
		}
	}

	@Override
	public String dropTables()
	{


		ArrayList<String> needed = new ArrayList<String>();

		needed.add("drop table relate");
		needed.add("drop table transaction");
		needed.add("drop table pocketaccount");
		needed.add("drop table account");
		needed.add("drop table belongs");
		needed.add("drop table team");
		needed.add("drop table customer");

		try(Statement statement = _connection.createStatement())
		{
			for(String within: needed)
			{
				statement.execute(within);
			}

			return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
//		PreparedStatement seeAll = null;
//		PreparedStatement dropAll = null;
//		String allNames = "select table_name from all_tables where owner = ?";
//		String dropThe = "drop table ";
//		try
//		{
//
//			seeAll = _connection.prepareStatement(allNames);
//			seeAll.setString(1, _connection.getUserName());
//		}
//		catch(SQLException e)
//		{
//			System.err.println(e.getMessage());
//			return "1";
//		}
//
//		try(ResultSet resultSet = seeAll.executeQuery())
//		{
//
//			while(resultSet.next())
//			{
//				needed.add(resultSet.getString(1));
//			}
//			for(String within:needed)
//			{
//				String temp = dropThe;
//				temp += within;
//				dropAll = _connection.prepareStatement(temp);
//				dropAll.execute();
//			}
//		}
//		catch(SQLException e)
//		{
//			System.err.println(e.getMessage());
//			return "1";
//		}
//
	}

	@Override
	public String createTables()
	{
		ArrayList<String> needed = new ArrayList<String>();

		needed.add("create table customer(" +
				"taxid varchar2(20)," +
				"name varchar2(20)," +
				"pin char(40)," +
				"address varchar2(20)," +
				"primary key (taxid))");
		needed.add("create table team(" +
				"teamid varchar2(20)," +
				"primary varchar2(20) not null," +
				"primary key (teamid)," +
				"foreign key (primary) references customer(taxid))");
		needed.add("create table belongs(" +
				"teamid varchar2(20)," +
				"taxid varchar2(20)," +
				"primary key (teamid, taxid)," +
				"foreign key (teamid) references team," +
				"foreign key (taxid) references customer)");
		needed.add("create table account(" +
				"accountid varchar2(20)," +
				"type varchar2(20)," +
				"status varchar2(10)," +
				"interest number(38,2)," +
				"balance number(38,2)," +
				"bankband varchar2(20)," +
				"owned varchar2(20) not null," +
				"primary key (accountid)," +
				"constraint minBalance check (balance >= 0)," +
				"foreign key (owned) references team(teamid))");
		needed.add("create table pocketaccount(" +
				"accountid varchar2(20)," +
				"linkedid varchar2(20) not null," +
				"primary key (accountid)," +
				"foreign key (accountid) references account," +
				"foreign key (linkedid) references account(accountid))");
		needed.add("create table transaction(" +
				"transid varchar2(20)," +
				"amount number(38,2)," +
				"transdate date," +
				"type varchar2(20)," +
				"clientfrom varchar2(20) not null," +
				"clientto varchar2(20) not null," +
				"primary key (transid)," +
				"foreign key (clientfrom) references account(accountid)," +
				"foreign key (clientto) references account(accountid))");
		needed.add("create table relate(" +
				"accountid varchar2(20)," +
				"transid varchar2(20)," +
				"primary key (accountid, transid)," +
				"foreign key (accountid) references account," +
				"foreign key (transid) references transaction)");
//		needed.add("create or replace TRIGGER \"CLOSELOWACCOUNT\" BEFORE UPDATE OF BALANCE ON ACCOUNT \n" +
//				"REFERENCING NEW AS NEWTABLE \n" +
//				"BEGIN\n" +
//				"  UPDATE Account A\n" +
//				"  set A.status = 'CLOSED'\n" +
//				"  where A.balance = 0;\n" +
//				"END;");
//		needed.add("create or replace TRIGGER \"CLEAREMPTYTEAM\" BEFORE DELETE ON ACCOUNT \n" +
//				"REFERENCING OLD AS OLDONE NEW AS NEWONE \n" +
//				"BEGIN\n" +
//				"  DELETE\n" +
//				"  FROM BELONGS B\n" +
//				"  WHERE B.teamid IN (SELECT T.teamid FROM TEAM T WHERE (SELECT COUNT(*) FROM ACCOUNT A WHERE A.owned = T.teamid) = 0);\n" +
//				"  DELETE\n" +
//				"  FROM TEAM T\n" +
//				"  WHERE (SELECT COUNT(*) FROM ACCOUNT A WHERE A.owned = T.teamid) <= 0.01;\n" +
//				"END;");

		try(Statement statement = _connection.createStatement())
		{
			for(String within: needed)
			{
				statement.execute(within);
			}

			return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}



	}

	@Override
	public String topUp(String accountId, double amount) {
		String selection = "select A from pocketaccount A where A.accountid = ?";
		String linkedId = null;
		try(PreparedStatement preparedStatement = _connection.prepareStatement(selection))
		{
			preparedStatement.setString(1, accountId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(!resultSet.next())
				throw new SQLException("No account specified in topUP");

			linkedId = resultSet.getString(2);

			try(PreparedStatement statement = _connection.prepareStatement("select A from Account A where A.accountid = ?"))
			{
				statement.setString(1, accountId);
				ResultSet resultSet1 = statement.executeQuery();
				resultSet1.next();
				if(resultSet1.getString("STATUS").equals("CLOSE"))
					throw new SQLException("Can't operate on closed Pocket Account");

			}

			try(PreparedStatement statement = _connection.prepareStatement("Select A from Account A where A.accountid = ?",ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE))
			{
				statement.setString(1, linkedId);
				ResultSet resultSet1 = statement.executeQuery();
				resultSet1.next();
				if(resultSet1.getString("STATUS").equals("CLOSE"))
					throw new SQLException("No transaction allowed on closed account");
				else if(resultSet1.getDouble("BALANCE") < amount)
					throw new SQLException("Linked Account Balance too low for topup");
				else
				{
					resultSet1.updateDouble("BALANCE", resultSet1.getDouble("BALANCE") - amount);
					resultSet1.updateRow();
				}
			}

			int records = 0;

			try(PreparedStatement statement = _connection.prepareStatement("select count(*) from transaction"))
			{
				ResultSet resultSet1 = statement.executeQuery();
				resultSet1.next();
				records = resultSet1.getInt(1);
				try(PreparedStatement statement1 = _connection.prepareStatement("insert into transaction values(?, ?, ?, 'TOPUP', ?, ? )"))
				{
					statement1.setString(1, ""+records);
					statement1.setDouble(2, amount);
					statement1.setDate(3, systemDate);
					statement1.setString(4, accountId);
					statement1.setString(5, linkedId);
					statement.execute();
				}
			}

			try(PreparedStatement statement = _connection.prepareStatement("Update Account A set A.balance = A.balance + ? where A.accountid = ?"))
			{
				statement.setDouble(1, amount);
				statement.setString(2, accountId);
				statement.execute();
			}





			return "0";




		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	@Override
	public String createPocketAccount(String id, String linkedId, double initialTopUp, String tin) {
		String getOwner = "select A.bankband, A.owned, A.balance from Account A where A.accountid = ?";
		String branch = null;
		String owner = null;
		boolean created = true;
		try(PreparedStatement preparedStatement = _connection.prepareStatement(getOwner)) {
			preparedStatement.setString(1, linkedId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.next())
				throw new SQLException("No normal accounts found");
			else
			{
				branch = resultSet.getString(1);
				owner = resultSet.getString(2);
				double balance = resultSet.getDouble(3);

				if(balance < initialTopUp)
					throw new SQLException("Initial top up amount larger than balance");

			}



		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			created = false;
		}

		if(!created) {

			String insertion = "insert into account values(?,'pocket', 'open', 0, 0,?,? )";
			try (PreparedStatement preparedStatement2 = _connection.prepareStatement(insertion)) {
				preparedStatement2.setString(1, id);
				preparedStatement2.setString(2, branch);
				preparedStatement2.setString(3, owner);
				preparedStatement2.execute();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				return "1";
			}
		}


		return "0";
	}

	////////////////////////////// Implement all of the methods given in the interface /////////////////////////////////
	// Check the Testable.java interface for the function signatures and descriptions.

	@Override
	public String initializeSystem()
	{
		// Some constants to connect to your DB.
		final String DB_URL = "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";
		final String DB_USER = "c##sihua";
		final String DB_PASSWORD = "7499270";

		// Initialize your system.  Probably setting up the DB connection.
		Properties info = new Properties();
		info.put( OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER );
		info.put( OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD );
		info.put( OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20" );

		try
		{
			OracleDataSource ods = new OracleDataSource();
			ods.setURL( DB_URL );
			ods.setConnectionProperties( info );
			_connection = (OracleConnection) ods.getConnection();

			// Get the JDBC driver name and version.
			DatabaseMetaData dbmd = _connection.getMetaData();
			System.out.println( "Driver Name: " + dbmd.getDriverName() );
			System.out.println( "Driver Version: " + dbmd.getDriverVersion() );

			// Print some connection properties.
			System.out.println( "Default Row Prefetch Value is: " + _connection.getDefaultRowPrefetch() );
			System.out.println( "Database Username is: " + _connection.getUserName() );
			System.out.println();

			Statement statement = _connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select max(T.transdate) from Transaction T");
			if(resultSet.next()) {
				if(resultSet.getDate(1) != null)
					systemDate = resultSet.getDate(1);
			}

			System.out.println(systemDate);

			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	@Override
	public String showBalance(String accountId) {
		String que = "select A.balance from Account A where A.accountid = ?";
		try(PreparedStatement seeBalance = _connection.prepareStatement(que))
		{
			seeBalance.setString(1, accountId);
			ResultSet resultSet = seeBalance.executeQuery();
			if(!resultSet.next())
				throw new SQLException("No such account");

			double store = resultSet.getDouble(1);
			DecimalFormat df = new DecimalFormat("#.##");
			System.out.println("Account "+accountId + " Balance" + df.format(store));
			return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

	}

	@Override
	public String createCustomer(String accountId, String tin, String name, String address) {
		String selection = "select count(*) from Account A where A.accountid = ?";
		try(PreparedStatement count = _connection.prepareStatement(selection))
		{
			count.setString(1, accountId);
			ResultSet resultSet = count.executeQuery();
			if(resultSet.getInt(1) == 0)
				return "1";
//
//			selection = "select count(*) from Customer C where C.tin = ?";
//			try(PreparedStatement count2 = _connection.prepareStatement(selection))
//			{
//				count2.setString(1, tin);
//				resultSet = count2.executeQuery();
//				if(resultSet.getInt(1) > 0)
//					return "1";
//			}

		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}



		String insertion = "insert into Customer values(?, ?, 1717, ?)";
		try(PreparedStatement insert = _connection.prepareStatement(insertion))
		{
			insert.setString(1, tin);
			insert.setString(2, name);
			insert.setString(3, address);
			insert.execute();

		}catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}


		return "0";
	}

	/**
	 * Example of one of the testable functions.
	 */
	@Override
	public String listClosedAccounts()
	{

		return "0 it works!";
	}

	/**
	 * Another example.
	 */
	@Override
	public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
	{
		String inserting = "insert into Account Values()";
		String count= null;
//
//		try(Statement statement = _connection.createStatement())
//		{
//
//		}

		return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
	}
}
