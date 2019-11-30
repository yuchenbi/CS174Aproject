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
	public String deposit(String accountId, double amount) {

		String updateBalance = "select A.* from Account A where A.accountid = ?";

		try(PreparedStatement preparedStatement = _connection.prepareStatement(updateBalance, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE))
		{
			preparedStatement.setString(1, accountId);
			ResultSet resultSet = preparedStatement.executeQuery();

			if(!resultSet.next())
				throw new SQLException("No Account ID matching deposit request");

			if(resultSet.getString("STATUS").equals("CLOSE"))
				throw new SQLException("No deposit allowed on closed account");

			resultSet.updateDouble("BALANCE", resultSet.getDouble("BALANCE") + amount);
			resultSet.updateRow();

			String addTransaction = "insert into Transaction values()";

		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		return null;
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
				"pin varchar(40)," +
				"address varchar2(20)," +
				"primary key (taxid))");
		needed.add("create table team(" +
				"teamid number generated by default on null as identity," +
				"primary varchar2(20) not null," +
				"primary key (teamid)," +
				"foreign key (primary) references customer(taxid))");
		needed.add("create table belongs(" +
				"teamid number," +
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
				"owned number not null," +
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
				"transid number generated by default on null as identity," +
				"amount number(38,2)," +
				"afterbalance number(38,2)," +
				"transdate date," +
				"type varchar2(20)," +
				"clientfrom varchar2(20) not null," +
				"clientto varchar2(20) not null," +
				"primary key (transid)," +
				"foreign key (clientfrom) references account(accountid)," +
				"foreign key (clientto) references account(accountid))");
		needed.add("create table relate(" +
				"accountid varchar2(20)," +
				"transid number," +
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


		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		return "0";

	}

	@Override
	public String topUp(String accountId, double amount) {
		String selection = "select A from pocketaccount A where A.accountid = ?";
		String linkedId = null;
		double afterbalance = 0;
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
					afterbalance = resultSet1.getDouble("BALANCE") - amount;
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
				try(PreparedStatement statement1 = _connection.prepareStatement("insert into transaction values(?, ?, ?, ?, 'TOPUP', ?, ? )"))
				{
					statement1.setString(1, ""+records);
					statement1.setDouble(2, amount);
					statement1.setDouble(3, afterbalance);
					statement1.setDate(4, systemDate);
					statement1.setString(5, accountId);
					statement1.setString(6, linkedId);
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
		int owner = 0;
		try(PreparedStatement preparedStatement = _connection.prepareStatement(getOwner)) {
			preparedStatement.setString(1, linkedId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {

				throw new SQLException("No normal accounts found");
			}
			else
			{
				branch = resultSet.getString(1);
				owner = resultSet.getInt(2);
				double balance = resultSet.getDouble(3);

				if(balance < initialTopUp)
					throw new SQLException("Initial top up amount larger than balance");

			}





		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}



		String insertion = "insert into account values(?,'pocket', 'open', 0, 0,?,? )";
		try (PreparedStatement preparedStatement2 = _connection.prepareStatement(insertion)) {
			preparedStatement2.setString(1, id);
			preparedStatement2.setString(2, branch);
			preparedStatement2.setInt(3, owner);
			preparedStatement2.execute();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return "1";
		}

		insertion = "insert into pocketaccount values(?,?)";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(insertion))
		{
			preparedStatement.setString(1, id);
			preparedStatement.setString(2, linkedId);
			preparedStatement.execute();
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
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

//			Statement statement = _connection.createStatement();
//			ResultSet resultSet = statement.executeQuery("select max(T.transdate) from Transaction T");
//			if(resultSet.next()) {
//				if(resultSet.getDate(1) != null)
//					systemDate = resultSet.getDate(1);
//			}

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

	private String addToTeam(int teamId, String taxid)
	{
		String exists = "select B.* from Belongs B where B.teamid = ? and B.taxid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(exists))
		{
			preparedStatement.setInt(1, teamId);
			preparedStatement.setString(2, taxid);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				throw new SQLException("Already exists in team");
			}
		}catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}


		String select = "select B.* from Belongs B where B.teamid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(select))
		{
			preparedStatement.setInt(1, teamId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				String insert = "insert into Belongs values(?,?)";
				try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insert))
				{
					preparedStatement1.setInt(1, teamId);
					preparedStatement1.setString(2, taxid);
					preparedStatement1.execute();
				}
			}
			else
			{
				String addteam = "insert into Team values(null, ?)";
				try(PreparedStatement preparedStatement1 = _connection.prepareStatement(addteam))
				{
					preparedStatement1.setString(1, taxid);
				}
			}
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
		return "0";
	}


	private void simplyCustomer(String tin, String name, String address)
	{

		String createCus = "insert into Customer values(?,?,'1717',?)";
		try (PreparedStatement preparedStatement = _connection.prepareStatement(createCus)) {
			preparedStatement.setString(1, tin);
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, address);
			preparedStatement.execute();
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}

	}

	@Override
	public String createCustomer(String accountId, String tin, String name, String address) {
		String selection = "select A.* from Account A where A.accountid = ?";
		try(PreparedStatement count = _connection.prepareStatement(selection))
		{
			count.setString(1, accountId);
			ResultSet resultSet = count.executeQuery();
			if(!resultSet.next())
				throw new SQLException("Not an existing account");

			simplyCustomer(tin, name, address);


			String addTeam = "insert into team belongs(?,?)";
			int team = resultSet.getInt("owned");
			try(PreparedStatement preparedStatement = _connection.prepareStatement(addTeam))
			{
				preparedStatement.setInt(1, team);
				preparedStatement.setString(2, tin);
				preparedStatement.execute();
			}
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


		return "0";
	}

	/**
	 * Example of one of the testable functions.
	 */
	@Override
	public String listClosedAccounts()
	{

		String find = "select A.accountid from Account A where A.status = 'CLOSE'";
		StringBuilder result = new StringBuilder();
		try(Statement statement = _connection.createStatement())
		{
			ResultSet resultSet = statement.executeQuery(find);
			result.append(0).append(" ");
			while(resultSet.next())
			{
				result.append(resultSet.getString(1)).append(" ");
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			result = new StringBuilder("1");
		}

		return result.toString();
	}

	/**
	 * Another example.
	 */
	@Override
	public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
	{
		String existing = "select C.* from Customer C where C.taxid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(existing))
		{
			preparedStatement.setString(1, tin);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(!resultSet.next())
				simplyCustomer(tin, name, address);



		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}





		return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
	}
}
