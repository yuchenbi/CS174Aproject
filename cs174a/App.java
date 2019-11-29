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
					System.out.println( resultSet.getString( 1 ) + " " );
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
		return "0";
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
				"status varchar2(5)," +
				"interest number(38,2)," +
				"balance number(38,2)," +
				"bankband varchar2(20)," +
				"owned varchar2(20) not null," +
				"primary key (accountid)," +
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

		try(Statement statement = _connection.createStatement())
		{
			for(String within: needed)
			{
				statement.execute(within);
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
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
			double store = resultSet.getDouble(1);
			DecimalFormat df = new DecimalFormat("#.##");
			System.out.println("Account "+accountId + " Balance" + df.format(store));
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
		return "0";
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

			selection = "select count(*) from Customer C where C.tin = ?";
			try(PreparedStatement count2 = _connection.prepareStatement(selection))
			{
				count2.setString(1, tin);
				resultSet = count2.executeQuery();
				if(resultSet.getInt(1) > 0)
					return "1";
			}

		}catch (SQLException e)
		{
			return "1";
		}

		String insertion = "insert into Customer ";





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

		return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
	}
}
