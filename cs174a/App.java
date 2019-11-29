package cs174a;                                             // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// You may have as many imports as you need.
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Statement;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
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
//			try( ResultSet resultSet = statement.executeQuery("drop table SOME;" +
//					"create table SOME2(" +
//					"firstVa VARCHAR(20)," +
//					"Primary key (firstVa));" +
//					"create table some3(" +
//					"firstVa VARCHAR(20)," +
//					"Primary key (firstVa)," +
//					"Foreign key (firstVa) references SOME2);"))
//			{
//				while( resultSet.next() )
//					System.out.println( resultSet.getString( 1 ) + " " + resultSet.getString( 2 ) + " " );
//			}

			try( ResultSet resultSet = statement.executeQuery( "select owner, table_name from all_tables where owner = 'C##SIHUA'" ) )
			{
				while( resultSet.next() )
					System.out.println( resultSet.getString( 1 ) + " " + resultSet.getString( 2 ) + " " );
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
		}
	}


	public String dropTables()
	{
		ArrayList<String> needed = new ArrayList<String>;
		needed.add("drop table relate");
		needed.add("drop table transaction");
		needed.add("drop table ")
	}

	public String createTables()
	{
		ArrayList<String> needed = new ArrayList<String>;

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
				"foreign key (linkedid) references account(accountid)," +
				"check ('Pocket' = (select A.type from Account A where A.accountid = accountid) and 'Pocket' <> (select A.type from Account A where A.accountid = linkedid)))");
		needed.add("create table transaction(" +
				"transid varchar2(20)," +
				"amount number(38,2)," +
				"transdate date," +
				"type varchar2(20)," +
				"clientfrom varchar2(20) not null," +
				"clientto varchar2(20) not null," +
				"primary key (transid)," +
				"foreign key (clientfrom) references account(acoountid)," +
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
				statement.executeQuery(within);
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return 1;
		}

		return 0;

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

		return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
	}
}
