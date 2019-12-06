package cs174a;                                             // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// You may have as many imports as you need.
import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.security.MessageDigest;

/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable, BankTeller, Transaction
{

	private OracleConnection _connection;                   // Example connection object to your DB.
	private Date systemDate;
	//private String currentTaxID;

	/**
	 * Default constructor.
	 * DO NOT REMOVE.
	 */
	App()
	{
//		currentTaxID = null;
		systemDate = null;
//		systemDate = Date.valueOf("2015-10-15");
		// TODO: Any actions you need.
	}

//	boolean verifyPIN(String PIN)
//	{
//
//	}

	@Override
	public String deleteAllClosed() {

		if(!lastDay(systemDate))
		{
			System.err.println("Not last day of month, deletion failed");
			return "1";
		}


		String deletion = "delete from Account A where A.type = 'POCKET' and A.accountid in (Select P.accountid from Pocketaccount P where P.linkedid in (select A2.accountid from Account A2 where A2.STATUS='CLOSE'))";
		try(Statement statement = _connection.createStatement())
		{
			statement.executeUpdate(deletion);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		deletion = "delete from Account A where A.status = 'CLOSE'";
		try(Statement statement = _connection.createStatement())
		{
			statement.executeUpdate(deletion);
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		deletion = "delete from belongs B where B.teamid not in (select A.owned from account a)";
		try(Statement statement = _connection.createStatement())
		{
			statement.executeUpdate(deletion);
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		deletion = "delete from team T where T.teamid not in (select B.teamid from Belongs B)";
		try(Statement statement = _connection.createStatement())
		{
			statement.executeUpdate(deletion);
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		deletion = "delete from Customer C where C.taxid not in (select B.taxid from Belongs B)";
		try(Statement statement = _connection.createStatement())
		{
			statement.executeUpdate(deletion);
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}


		return "0";
	}


	@Override
	public String withDrawl(String accountID, double money) {
		String subtraction = "select A.balance from Account A where A.type <> 'POCKET' and A.accountid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(subtraction,ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE))
		{
			preparedStatement.setString(1, accountID);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(!resultSet.next())
				throw new SQLException("No such account for withDrawl");



			else if(resultSet.getDouble("balance") < money)
				throw new SQLException("Account balance too low for withDrawl");

			else if(resultSet.getDouble("balance") - money <= 0.01)
				resultSet.updateString("STATUS", "CLOSE");

			else {resultSet.updateDouble("balance", resultSet.getDouble("balance") - money);
			resultSet.updateRow();}
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		String transac = "insert into transaction values(null, ?, ? , 'WITHDRAWL', ?, null)";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(transac))
		{
			preparedStatement.setDouble(1, money);
			preparedStatement.setDate(2, systemDate);
			preparedStatement.setString(3, accountID);
			preparedStatement.execute();
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		return "0";
	}

	@Override
	public String DTER() {
		if(!lastDay(systemDate))
		{
			System.err.println("Not last day of month, DTER failed");
			return "";
		}
		StringBuilder result = new StringBuilder();
		String summation = "select taxid\n" +
				"from (with t as \n" +
				"(select A2.accountid, sum(T.amount) as amount\n" +
				"from Account A2, Transaction T \n" +
				"where (T.clientto = A2.accountid) and (T.type = 'DEPOSIT' or T.type = 'TRANSFER' or T.type = 'WIRES')\n" +
				"group by A2.accountid)\n" +
				"select C.taxid, sum(t.amount) as indi\n" +
				"From Customer C, Team T, t, Account A\n" +
				"where C.taxid in (select B2.taxid from Belongs B2 where B2.teamid = T.teamid) and T.teamid = A.owned and A.accountid = t.accountid\n" +
				"group by C.taxid)\n" +
				"where indi > 10000";
		try(Statement statement = _connection.createStatement())
		{
			ResultSet resultSet = statement.executeQuery(summation);
			while(resultSet.next())
			{

				result.append(resultSet.getString(1)).append(" ").append(System.lineSeparator());
			}

			return result.toString();
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	@Override
	public String cusReport(String taxId) {
		StringBuilder result = new StringBuilder();
		String selection = "select A.* from Account A where A.owned in (select B.teamid from Belongs B where B.taxid = ?) ";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(selection))
		{
			preparedStatement.setString(1, taxId);
			ResultSet resultSet = preparedStatement.executeQuery();

			result.append("Customer report for user ").append(taxId).append(System.lineSeparator());

			while(resultSet.next())
				result.append(resultSet.getString(1)).append(" ").append(resultSet.getString(2)).append(" ").append(resultSet.getString(3)).append(" ").append(resultSet.getDouble(4)).append(" ").append(resultSet.getDouble(5)).append(System.lineSeparator());

			return result.toString();
		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	@Override
	public String checkTransaction(String accountID) {
		StringBuilder res = new StringBuilder();
		String selection = "select * from Transaction T where T.clientfrom = ? or T.clientto = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(selection))
		{
			preparedStatement.setString(1, accountID);
			preparedStatement.setString(2, accountID);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
				res.append(resultSet.getInt(1)).append(" ").append(resultSet.getDouble(2)).append(" ").append(resultSet.getDate(3)).append(System.lineSeparator());

			return res.toString();
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}


//	public String accrueInterest()

	public String setInterest(String accountID, double NewInterest)
	{
		String selection = "select A.interest from account A where A.accountid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(selection, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE))
		{
			preparedStatement.setString(1, accountID);
			ResultSet resultSet = preparedStatement.executeQuery();

			if(!resultSet.next())
				throw new SQLException("No specified account");

			resultSet.updateDouble("interest", NewInterest);
			resultSet.updateRow();

			return "0";
		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	@Override
	public String addInterest() {
		if(systemDate == null)
		{
			System.err.println("System Date not set");
			return "1";
		}
		if(!lastDay(systemDate))
		{
			System.err.println("Not last day, can't add interest");
		}

		String exist = "select max(T.transdate) from transaction T where T.type = 'ACCRUE'";
		try(Statement statement = _connection.createStatement())
		{
			ResultSet resultSet = statement.executeQuery(exist);
			resultSet.next();

			if(resultSet.getDate(1) != null)
				if(resultSet.getDate(1).compareTo(systemDate) == 0)
					throw new SQLException("Can't add interest again in this month");
		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		String update = "update Account A\n" +
				"set A.balance = A.balance + (select updated from (select D.accountid, sum(d.DIFFERENCE * D.balance * D.interest/12/?) as updated from datebalance D group by D.accountid) where accountid = A.accountid)\n" +
				"where A.ACCOUNTID in (select D.accountid from datebalance D)";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(update))
		{
			preparedStatement.setInt(1, maxDayMonth(systemDate));
			preparedStatement.execute();
		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		String transactions = "insert into transaction select null, sum(d.DIFFERENCE * D.balance * D.interest/?), ?, 'ACCRUE', null, D.accountid  from datebalance D where D.accountid not in (select A2.accountid from Account A2 where A2.interest = 0 or A2.status = 'CLOSE') group by D.accountid";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(transactions))
		{
			preparedStatement.setInt(1, maxDayMonth(systemDate));
			preparedStatement.setDate(2, systemDate);
			preparedStatement.execute();
		}catch (SQLException e)
		{
			System.out.println(e.getMessage());
			return "1";
		}

		return "0";
	}


	@Override
	public String generateMonthlyReport(String taxID) {

		if(!lastDay(systemDate)) {
			System.err.println("Not end of month");
			return "";
		}
		String result = "";
		ArrayList<String> accounts = new ArrayList<String>();
		String existing = "select C.* from Customer C where C.taxid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(existing))
		{
			preparedStatement.setString(1, taxID);
			ResultSet resultSet = preparedStatement.executeQuery();

			if(!resultSet.next())
				throw new SQLException("Can't find specified taxID");
		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}


		String selectAccounts = "select A.accountid from Account A where A.owned in (select B.teamid from Belongs B where B.taxid = ?)";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(selectAccounts))
		{
			preparedStatement.setString(1, taxID);
			ResultSet resultSet = preparedStatement.executeQuery();

			while(resultSet.next())
				accounts.add(resultSet.getString("accountid"));
		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		String warning = "select sum(A.balance) as balance from Account A where A.owned in (select T.teamid from Team T where T.primary = ?)";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(warning))
		{
			preparedStatement.setString(1, taxID);
			ResultSet resultSet = preparedStatement.executeQuery();

			resultSet.next();

			if(resultSet.getDouble(1) > 100000)
				result += "Warning: Account balance exceeds insurance limit" + System.lineSeparator();
		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		for(String within: accounts)
		{
			result += "Account " + within + ": ";
			String balances = "select D.balance from dateBalance D where D.balancedate = (select min(D2.balanceDate) from Datebalance D2) and D.accountID = ?";
			try(PreparedStatement preparedStatement = _connection.prepareStatement(balances))
			{
				preparedStatement.setString(1, within);
				ResultSet resultSet = preparedStatement.executeQuery();

				if(resultSet.next())
					result += "Initial balance: " + resultSet.getDouble("balance");
			} catch (SQLException e)
			{
				System.err.println(e.getMessage());
				return "1";
			}

			balances = "select A.balance from Account A where A.accountid = ?";
			try(PreparedStatement preparedStatement = _connection.prepareStatement(balances))
			{
				preparedStatement.setString(1, within);
				ResultSet resultSet = preparedStatement.executeQuery();

				resultSet.next();

				result += " Final Balance: ";
				result += resultSet.getDouble("balance");
			} catch (SQLException e)
			{
				System.err.println(e.getMessage());
				return "1";
			}
			String owners = "select C.name, C.address from Customer C where C.taxid in (select B.taxid from Belongs B where B.teamid = (select A.owned from Account A where A.accountid = ?))";
			try(PreparedStatement preparedStatement = _connection.prepareStatement(owners))
			{
				preparedStatement.setString(1, within);
				ResultSet resultSet = preparedStatement.executeQuery();

				result += System.lineSeparator();
				result += "Owners: ";

				while(resultSet.next())
					result += "Name: " + resultSet.getString("name") + " Address: " + resultSet.getString("address") + System.lineSeparator();
			} catch (SQLException e)
			{
				System.err.println(e.getMessage());
				return "1";
			}
			String transactions = "select t.* from transaction T where T.clientfrom = ? or T.clientto = ?";
			try(PreparedStatement preparedStatement = _connection.prepareStatement(transactions))
			{
				preparedStatement.setString(1, within);
				preparedStatement.setString(2, within);
				ResultSet resultSet = preparedStatement.executeQuery();

				result += System.lineSeparator();

				result += "Transactions for " + within + ":" + System.lineSeparator();

				while(resultSet.next())
				{
					result += resultSet.getString("TYPE") + " " + resultSet.getDouble("amount") + " " + resultSet.getDate("transdate") + " " + resultSet.getString("clientto") + System.lineSeparator();
				}
			}catch (SQLException e)
			{
				System.err.println(e.getMessage());
				return "1";
			}

			result += System.lineSeparator();
		}

		return result;

//		String selection = "with t as (select A.accountid from Account A where A.owned in (select B.teamid from Belongs B where B.taxid = ?)" +
//				" select * from transaction T, t where T.clientto in t or T.clientfrom in t";
//		try(PreparedStatement preparedStatement = _connection.prepareStatement(selection))
//		{
//			preparedStatement.setString(1, taxID);
//			ResultSet resultSet = preparedStatement.executeQuery();
//			result += "Transactions : " + System.lineSeparator();
//
//			while(resultSet.next())
//			{
//				result +=
//			}
//		}
//		catch(SQLException e)
//		{
//			System.err.println(e.getMessage());
//			return "1";
//		}
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
		double old = 0;
		double newed = 0;

		try(PreparedStatement preparedStatement = _connection.prepareStatement(updateBalance, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE))
		{
			preparedStatement.setString(1, accountId);
			ResultSet resultSet = preparedStatement.executeQuery();

			if(!resultSet.next())
				throw new SQLException("No Account ID matching deposit request");

			if(resultSet.getString("STATUS").equals("CLOSE"))
				throw new SQLException("No deposit allowed on closed account");

			old = resultSet.getDouble("balance");
			newed = old + amount;

			resultSet.updateDouble("BALANCE", resultSet.getDouble("BALANCE") + amount);
			resultSet.updateRow();

			String addTransaction = "insert into Transaction values(null, ?, ?, 'DEPOSIT', ?,?)";
			try(PreparedStatement preparedStatement1 = _connection.prepareStatement(addTransaction))
			{
				preparedStatement1.setDouble(1, amount);
				preparedStatement1.setDate(2, systemDate);
				preparedStatement1.setString(3, null);
				preparedStatement1.setString(4,accountId);
				preparedStatement1.execute();
			}



		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		return "0 "+ old + " " + newed;
	}

	public String encrypt(String password)
	{

		String hashText = null;
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-1");

			byte[] messageDigest = md.digest(password.getBytes());

			BigInteger no = new BigInteger(1, messageDigest);

			hashText = no.toString(16);

			while(hashText.length() < 40)
				hashText = "0" + hashText;


		}catch (Exception e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
		return hashText;
	}

	@Override
	public String dropTables()
	{


		ArrayList<String> needed = new ArrayList<String>();
		needed.add("drop table recorddate");
		needed.add("drop table datebalance");
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

	}

//	private transMoney(String from, )

	@Override
	public String createTables()
	{
		ArrayList<String> needed = new ArrayList<String>();

		needed.add("create table customer(" +
				"taxid varchar2(20)," +
				"name varchar2(20)," +
				"pin char(40)," +
				"address varchar2(30)," +
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
				"foreign key (teamid) references team on delete cascade," +
				"foreign key (taxid) references customer on delete cascade)");
		needed.add("create table account(" +
				"accountid varchar2(20)," +
				"type varchar2(20)," +
				"status varchar2(10)," +
				"interest number(38,5)," +
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
				"foreign key (accountid) references account on delete cascade," +
				"foreign key (linkedid) references account(accountid))");
		needed.add("create table transaction(" +
				"transid number generated by default on null as identity," +
				"amount number(38,2)," +
				"transdate date," +
				"type varchar2(20)," +
				"clientfrom varchar2(20)," +
				"clientto varchar2(20)," +
				"primary key (transid)," +
				"foreign key (clientfrom) references account(accountid) on delete set null," +
				"foreign key (clientto) references account(accountid) on delete set null)");
		needed.add("create table relate(" +
				"accountid varchar2(20)," +
				"transid number," +
				"primary key (accountid, transid)," +
				"foreign key (accountid) references account," +
				"foreign key (transid) references transaction)");
		needed.add("create table datebalance(" +
				"accountid varchar(20)," +
				"balancedate date," +
				"balance number(38,2)," +
				"difference number," +
				"interest number(38,2)," +
				"primary key (accountid, balancedate)," +
				"foreign key (accountid) references Account on delete cascade)");
		needed.add("create table recorddate(" +
				"dateid number generated by default on null as identity," +
				"daterecord date," +
				"primary key (dateid))");
//		needed.add("create table interest(\n" +
//				"    somekey number generated by default on null as identity,\n" +
//				"    accrueDate Date" +
//				")");
//		needed.add("CREATE OR REPLACE TRIGGER TRIGGER1 \n" +
//				"AFTER UPDATE OF BALANCE ON ACCOUNT\n" +
//				"BEGIN\n" +
//				"  UPDATE Account A\n" +
//				"  set A.STATUS = 'CLOSE'\n" +
//				"  WHERE A.BALANCE<=0.01;\n" +
//				"END;");
//		needed.add("CREATE OR REPLACE TRIGGER TRIGGER1 \n" +
//				"AFTER UPDATE OF STATUS ON ACCOUNT \n" +
//				"BEGIN\n" +
//				"  UPDATE Account A\n" +
//				"  set A.STATUS = 'CLOSE'\n" +
//				"END;");
//		needed.add("create or replace TRIGGER \"CLOSELOWACCOUNT\" BEFORE UPDATE OF BALANCE ON ACCOUNT \n" +
//				"REFERENCING NEW AS NEWTABLE \n" +
//				"BEGIN\n" +
//				"  UPDATE Account A\n" +
//				"  set A.status = 'CLOSED'\n" +
//				"  where A.balance = 0;\n" +
//				"END;");
//		needed.add("create or replace TRIGGER \"CLEAREMPTYTEAM\" AFTER DELETE ON ACCOUNT \n" +
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
	public String createPocketAccount(String id, String linkedId, double initialTopUp, String tin) {
		if(initialTopUp <= 0.01)
		{
			System.err.println("initial Top Up amount too small");
			return "1";
		}
		String getOwner = "select A.* from Account A where A.accountid = ?";
		String branch = null;
		int teamid = 0;
		try(PreparedStatement preparedStatement = _connection.prepareStatement(getOwner)) {
			preparedStatement.setString(1, linkedId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {

				throw new SQLException("No normal accounts found");
			}
			else
			{
				if(resultSet.getString("STATUS").equals("CLOSE"))
					throw new SQLException("Can't open Pocket Account on closed Checking/Saving Account");

				if(resultSet.getString("TYPE").equals("POCKET"))
					throw new SQLException("Can't open Pocket Account on Pocket Account");

				branch = resultSet.getString("bankBand");
				double balance = resultSet.getDouble("balance");

				if(balance < initialTopUp)
					throw new SQLException("Initial top up amount larger than balance");

			}

			String insertTeam = "insert into team values(null, ?)";
			try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertTeam, new String[]{"teamid"}))
			{
				preparedStatement1.setString(1, tin);
				preparedStatement1.executeUpdate();

				ResultSet resultSet1 = preparedStatement1.getGeneratedKeys();

				if(!resultSet1.next())
					throw new SQLException("Insert into team failed");

				teamid = resultSet1.getInt(1);
			}

			String insertBelongs = "insert into belongs values(?,?)";
			try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertBelongs))
			{
				preparedStatement1.setInt(1, teamid);
				preparedStatement1.setString(2, tin);
				preparedStatement1.execute();
			}


			String insertion = "insert into account values(?,'POCKET', 'OPEN', 0, 0,?,? )";
			try (PreparedStatement preparedStatement2 = _connection.prepareStatement(insertion)) {
				preparedStatement2.setString(1, id);
				preparedStatement2.setString(2, branch);
				preparedStatement2.setInt(3, teamid);
				preparedStatement2.execute();
			}

			insertion = "insert into pocketaccount values(?,?)";
			try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertion))
			{
				preparedStatement1.setString(1, id);
				preparedStatement1.setString(2, linkedId);
				preparedStatement1.execute();
			}


			topUp(id, initialTopUp);

			return "0 " + id + " " +"POCKET"+" " + initialTopUp + " " + tin;


		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

	}





	public String createPocketAccountWithBranch(String id, String linkedId, double initialTopUp, String tin, String branchIn) {
		if(initialTopUp <= 0.01)
		{
			System.err.println("initial Top Up amount too small");
			return "1";
		}
		String getOwner = "select A.* from Account A where A.accountid = ?";
		String branch = null;
		int teamid = 0;
		try(PreparedStatement preparedStatement = _connection.prepareStatement(getOwner)) {
			preparedStatement.setString(1, linkedId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {

				throw new SQLException("No normal accounts found");
			}
			else
			{
				if(resultSet.getString("STATUS").equals("CLOSE"))
					throw new SQLException("Can't open Pocket Account on closed Checking/Saving Account");

				if(resultSet.getString("TYPE").equals("POCKET"))
					throw new SQLException("Can't open Pocket Account on Pocket Account");

				branch = resultSet.getString("bankBand");
				double balance = resultSet.getDouble("balance");

				if(balance < initialTopUp)
					throw new SQLException("Initial top up amount larger than balance");

			}

			String insertTeam = "insert into team values(null, ?)";
			try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertTeam, new String[]{"teamid"}))
			{
				preparedStatement1.setString(1, tin);
				preparedStatement1.executeUpdate();

				ResultSet resultSet1 = preparedStatement1.getGeneratedKeys();

				if(!resultSet1.next())
					throw new SQLException("Insert into team failed");

				teamid = resultSet1.getInt(1);
			}

			String insertBelongs = "insert into belongs values(?,?)";
			try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertBelongs))
			{
				preparedStatement1.setInt(1, teamid);
				preparedStatement1.setString(2, tin);
				preparedStatement1.execute();
			}


			String insertion = "insert into account values(?,'POCKET', 'OPEN', 0, 0,?,? )";
			try (PreparedStatement preparedStatement2 = _connection.prepareStatement(insertion)) {
				preparedStatement2.setString(1, id);
				preparedStatement2.setString(2, branchIn);
				preparedStatement2.setInt(3, teamid);
				preparedStatement2.execute();
			}

			insertion = "insert into pocketaccount values(?,?)";
			try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertion))
			{
				preparedStatement1.setString(1, id);
				preparedStatement1.setString(2, linkedId);
				preparedStatement1.execute();
			}


			topUp(id, initialTopUp);

			return "0 " + id + " " +"POCKET"+" " + initialTopUp + " " + tin;


		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}







//		String update = "Select A from Account A where A.accountid = ?";
//		try(PreparedStatement preparedStatement = _connection.prepareStatement(update, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE))
//		{
//
//		}
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
		systemDate = null;

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

//			System.out.println(systemDate);


		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		String update = "select R.daterecord from recorddate R where R.dateid = (select max(R2.dateid) from recorddate R2)";
		try(Statement statement = _connection.createStatement())
		{
			ResultSet resultSet = statement.executeQuery(update);

			if(!resultSet.next()) {
				systemDate = null;
				return "0";
			}

			systemDate = resultSet.getDate(1);
		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		System.out.println(systemDate.toString());

		return "0";
	}

//	private int dayDiff(Date date1, Date date2)

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
			return "0 " + df.format(store);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

	}

	@Override
	public String deleteAllTrans() {

		if(!lastDay(systemDate))
		{
			System.err.println("Not last day, can't delete");
			return "1";
		}
		String deletion = "delete from transaction";
		try(Statement statement = _connection.createStatement())
		{
			statement.executeUpdate(deletion);
			return "0";
		}
		catch (SQLException e)
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

	private boolean allNumbers(String input)
	{
		try{
			Integer.parseInt(input);
			return true;
		}catch (NumberFormatException e)
		{
			return false;
		}
	}

	public void setPIN(String taxID, String oldPIN, String newPIN)
	{
		if(newPIN.length()!=4)
		{
			System.err.println("New PIN has incorrect length");
			return;
		}
		else if(!allNumbers(newPIN))
		{
			System.err.println("New PIN contains not only digits");
		}
		if(verifyPIN(taxID, oldPIN))
		{
			String setting = "update Customer C set C.PIN = ? where C.taxid = ?";
			try(PreparedStatement preparedStatement = _connection.prepareStatement(setting))
			{
				preparedStatement.setString(1, encrypt(newPIN));
				preparedStatement.setString(2, taxID);
				preparedStatement.execute();
			} catch (SQLException e)
			{
				System.err.println(e.getMessage());
				return;
			}
		}
	}

	public boolean verifyPIN(String taxID, String PIN)
	{
		String encrypted = encrypt(PIN);
		String selection = "select C.PIN from customer C where C.taxID = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(selection))
		{
			preparedStatement.setString(1, taxID);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(!resultSet.next())
				throw new SQLException("No Account specified by accountID, verifyPIN failed");

			if(!resultSet.getString("PIN").equals(encrypted))
				return false;


		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return false;
		}

		return true;
	}


	private void simplyCustomer(String tin, String name, String address) throws SQLException
	{

		String existing = "select * from Customer C where C.taxid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(existing))
		{
			preparedStatement.setString(1, tin);
			ResultSet resultSet = preparedStatement.executeQuery();

			if(resultSet.next())
				return;
		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return;
		}

		String createCus = "insert into Customer values(?,?,?,?)";
		try (PreparedStatement preparedStatement = _connection.prepareStatement(createCus)) {
			preparedStatement.setString(1, tin);
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, encrypt("1717"));
			preparedStatement.setString(4, address);
			preparedStatement.execute();
		}

	}

	@Override
	public String payFriend(String from, String to, double amount) {
		if(amount < 0)
			return "1";

		double fromNewBalance = 0, toNewBalance = 0;
		double monthlyCharge1 = 0;
		double monthlyCharge2 = 0;

		String origin = "select A.status, A.balance from Account A where A.type='POCKET' and A.accountid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(origin, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE))
		{
			preparedStatement.setString(1, from);
			ResultSet resultSet = preparedStatement.executeQuery();

			try(PreparedStatement statement = _connection.prepareStatement("select T.* from transaction T where T.clientfrom = ? or T.clientto = ?" )) {
				statement.setString(1, from);
				statement.setString(2, from);
				ResultSet resultSet2 = statement.executeQuery();
				if(!resultSet2.next()) {
					monthlyCharge1 = 5.0;
				}
			}

			try(PreparedStatement statement = _connection.prepareStatement("select T.* from transaction T where T.clientfrom = ? or T.clientto = ?" )) {
				statement.setString(1, to);
				statement.setString(2, to);
				ResultSet resultSet3 = statement.executeQuery();
				if(!resultSet3.next()) {
					monthlyCharge2 = 5.0;
				}
			}





			if(!resultSet.next())
				throw new SQLException("No origin Pocket Account");
			if(resultSet.getString("status").equals("CLOSE"))
				throw new SQLException("Origin account closed");

			fromNewBalance = resultSet.getDouble("balance") - amount-monthlyCharge1;
			if(fromNewBalance < 0)
				throw new SQLException("Original account balance too low");





			preparedStatement.setString(1, to);
			resultSet = preparedStatement.executeQuery();
			if(!resultSet.next())
				throw new SQLException("No destination Pocket Account");

			if(resultSet.getString("status").equals("CLOSE"))
				throw new SQLException("Destination account closed");

			toNewBalance = resultSet.getDouble("balance") + amount-monthlyCharge2;
			if(toNewBalance < 0) {
				throw new SQLException("Destination account balance too low(+5 dollar transaction fee)");
			}

			resultSet.updateDouble("balance", toNewBalance);
			resultSet.updateRow();

			preparedStatement.setString(1, from);
			resultSet = preparedStatement.executeQuery();

			resultSet.next();

			resultSet.updateDouble("balance", fromNewBalance);
			if(fromNewBalance<=0.01)
				resultSet.updateString("status", "CLOSE");


			resultSet.updateRow();



			try(PreparedStatement statement1 = _connection.prepareStatement("insert into transaction values(null, ?, ?, 'PAY-FRIEND', ?, ? )"))
			{
				statement1.setDouble(1, amount);
				statement1.setDate(2, systemDate);
				statement1.setString(3, from);
				statement1.setString(4, to);
				statement1.execute();
			}


		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		return "0" + fromNewBalance + " " + toNewBalance;

	}


	@Override
	public String writeCheck(String accountID, double amount) {
		String update = "select A.* from Account A where A.accountid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(update,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE))
		{
			preparedStatement.setString(1, accountID);
			ResultSet resultSet = preparedStatement.executeQuery();

			if(!resultSet.next())
				throw new SQLException("No account as specified in writeCheck");

			if(resultSet.getString("type").equals("POCKET"))
				throw new SQLException("Can't write check on pocket account");

			if(resultSet.getString("status").equals("CLOSE"))
				throw new SQLException("Can't write check on closed account");

			if(resultSet.getDouble("balance") < amount)
				throw new SQLException("Account balance not enough");

			if(resultSet.getDouble("balance") - amount <= 0.01)
				resultSet.updateString("status", "CLOSE");

			resultSet.updateDouble("balance", resultSet.getDouble("balance") - amount);
			resultSet.updateRow();
		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

		String insertion = "insert into transaction values(null, ?, ?, 'CHECK', ?,null)";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(insertion, new String[]{"TRANSID"}))
		{
			preparedStatement.setDouble(1, amount);
			preparedStatement.setDate(2, systemDate);
			preparedStatement.setString(3, accountID);
			preparedStatement.execute();

			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			resultSet.next();
			return "Check Number: " + resultSet.getInt(1);
		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

	}

	public int getDay(Date date)
	{
		if(date == null)
			return 0;

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH) + 1;
	}

	public int getMonth(Date date)
	{
		if(date == null)
			return 0;

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
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


			String addTeam = "insert into belongs values(?,?)";
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

	private void updateDate()
	{
		if(systemDate == null)
			return;

		String insertion = "insert into recorddate values(null, ?)";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(insertion))
		{
			preparedStatement.setDate(1, systemDate);
			preparedStatement.execute();
		}catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return;
		}

		return;
	}

	public boolean isValid(String  test)
	{
		try{
			LocalDate.parse(test,DateTimeFormatter.ISO_LOCAL_DATE);
		}catch (Exception e)
		{
			return false;
		}
		return true;
	}

	@Override
	public String setDate(int year, int month, int day) {
		String panDuan = "";
		String newone1 = ""+year+"-";
		if(month < 10)
			newone1 += "0";
		newone1 += month + "-";
		if(day < 10)
			newone1 += "0";
		newone1 += day;
		Date test = null;
		try {
			test = Date.valueOf(newone1);
		}catch (Exception e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
		panDuan += year;
		if(panDuan.length()!=4)
			return  "1";
		if(!isValid(newone1))
		{
			System.err.println("Not a valid date, failed");
			return "1";
		}

		if(systemDate == null)
		{

			String date = "" + year + "-" + month + "-" + day;
			systemDate = Date.valueOf(date);

			updateDate();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String format = formatter.format(systemDate);


			return "0 " + systemDate.toString();
		}
		else if(lastDay(systemDate))
		{

			String date = "" + year + "-" + month + "-" + day;
			systemDate = Date.valueOf(date);

			String deletion = "delete from datebalance";
			try(Statement statement = _connection.createStatement())
			{
				statement.executeUpdate(deletion);
			}catch (SQLException e)
			{
				System.err.println(e.getMessage());
				return "1";
			}

			String updateBDate = "insert into datebalance\n" +
					"select A.accountid, ? , A.balance, ?, A.interest\n" +
					"from account a";


			try(PreparedStatement preparedStatement = _connection.prepareStatement(updateBDate))
			{
				preparedStatement.setDate(1, systemDate);
				preparedStatement.setInt(2, getDay(systemDate));
				preparedStatement.execute();
			}catch (SQLException e)
			{
				System.err.println(e.getMessage());
				return "1";
			}

			updateDate();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
			String format = formatter.format(systemDate);


			return "0 " + systemDate.toString();
		}
		else
		{
			String selection = "select max(D.balancedate) from datebalance D";

			try(Statement statement = _connection.createStatement())
			{
				ResultSet resultSet = statement.executeQuery(selection);
				resultSet.next();
				Date newone = resultSet.getDate(1);

				if(newone != null && newone.compareTo(systemDate) == 0)
					return "0";
			}catch (SQLException e)
			{
				System.err.println(e.getMessage());
				return "1";
			}

			int dayDiff = 0;
			String ddd="" + year + "-" + month + "-" + day;
			Date another = Date.valueOf(ddd);

			dayDiff = getDay(another) - getDay(systemDate);


			String updateBDate = "insert into datebalance\n" +
					"select A.accountid, ? , A.balance, ?, A.interest\n" +
					"from account a";


			try(PreparedStatement preparedStatement = _connection.prepareStatement(updateBDate))
			{
				preparedStatement.setDate(1, systemDate);
				preparedStatement.setInt(2, dayDiff);
				preparedStatement.execute();
			}catch (SQLException e)
			{
				System.err.println(e.getMessage());
				return "1";
			}

			String date = "" + year + "-" + month + "-" + day;
			systemDate = Date.valueOf(date);

			updateDate();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
			String format = formatter.format(systemDate);


			return "0 " + systemDate.toString();
		}

	}



	private boolean lastDay(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(systemDate);
		int res = cal.getActualMaximum(Calendar.DATE);
		return res == cal.get(Calendar.DAY_OF_MONTH);
	}

	public int maxDayMonth(Date date)
	{
		Calendar cal = Calendar.getInstance();

		cal.setTime(systemDate);
		return cal.getActualMaximum(Calendar.DATE);
	}

	/**
	 * Another example.
	 */
	@Override
	public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
	{
		String type = null;
		if(accountType == AccountType.SAVINGS)
			type = "SAVING";
		else if(accountType == AccountType.INTEREST_CHECKING)
			type = "INTEREST";
		else if(accountType == AccountType.STUDENT_CHECKING)
			type = "STUDENT";
		else
			return "1";

		if(initialBalance < 1000)
			return "1";


		String checkExist = "select count(*) from Account A where A.accountid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(checkExist))
		{
			preparedStatement.setString(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();
			if(resultSet.getInt(1) != 0)
				throw new SQLException("Not new account");
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}


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

		existing = "select * from Account A where A.accountid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(existing))
		{
			preparedStatement.setString(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				throw new SQLException("Already exists one account with same id");

			}
			else
			{
				String insertion = "insert into Team values(null,?)";
				int autoGen = 0;
				try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertion,new String[]{"teamid"}))
				{
					preparedStatement1.setString(1, tin);
					int res = preparedStatement1.executeUpdate();
					if(res == 0)
						throw new SQLException("something weird");
					ResultSet resultSet1 = preparedStatement1.getGeneratedKeys();
					if(resultSet1.next())
						autoGen = resultSet1.getInt(1);
					else
						throw new SQLException("Insertion into Team failed");



				}
				insertion = "insert into Belongs values(?,?)";
				try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertion))
				{
					preparedStatement1.setInt(1, autoGen);
					preparedStatement1.setString(2, tin);
					preparedStatement1.execute();
				}

				insertion = "insert into Account values(?,?,'OPEN',?, ?,null, ?)";
				try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertion))
				{
					preparedStatement1.setString(1, id);
					preparedStatement1.setString(2, type);
					double interest = 0;
					if(type.equals("SAVING"))
						interest = 0.048;
					else if(type.equals("INTEREST"))
						interest = 0.03;
					else
						interest = 0;
					preparedStatement1.setDouble(3, interest);
					preparedStatement1.setDouble(4, initialBalance);
					preparedStatement1.setInt(5,autoGen);
					preparedStatement1.execute();
				}
				insertion = "insert into Transaction values(null, ?,?,'DEPOSIT',?,?)";
				try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertion))
				{
					preparedStatement1.setDouble(1, initialBalance);
					preparedStatement1.setDate(2, systemDate);
					preparedStatement1.setString(3, null);
					preparedStatement1.setString(4, id);
					preparedStatement1.execute();
				}
			}

		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}







		return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
	}






	public String getPin(String taxid) {
		String getPin = "select C.* from customer C where C.taxid =?";
		ResultSet resultSet;
		String pin;
		try(PreparedStatement preparedStatement = _connection.prepareStatement(getPin)) {
			preparedStatement.setString(1, taxid);
			resultSet = preparedStatement.executeQuery();
			if(!resultSet.next()){
				throw new SQLException("No  TaxID matching. ");
			}
			pin = resultSet.getString("pin");
		} catch(SQLException e) {
			System.err.println(e.getMessage());
			return "";
		}
		return pin;
	}

	public ArrayList<String> getAllAccount(String taxid) {
		String getBelongs = "select B.* from belongs B where B.taxid = ?";
		ResultSet belongs;
		ArrayList<String> teamID = new ArrayList<String>();
		ArrayList<String> nothing = new ArrayList<String>();
		try(PreparedStatement preparedStatement = _connection.prepareStatement(getBelongs)) {
			preparedStatement.setString(1, taxid);
			belongs = preparedStatement.executeQuery();
//			belongs.next();
			while(belongs.next()) {
				teamID.add(belongs.getString("teamid"));
			}
//			if(!belongs.next())
//				throw new SQLException("No  TaxID matching. ");
//			teamID = belongs.getString("teamid");
		}catch(SQLException e) {
			System.err.println(e.getMessage());
			return nothing;
		}
		String getAccounts = "select A.* from account A where A.owned = ?";
		ResultSet Accounts;
		ArrayList<String> accountID = new ArrayList<String>();
		for(int i = 0; i < teamID.size(); i++) {
			try(PreparedStatement preparedStatement = _connection.prepareStatement(getAccounts)) {
				preparedStatement.setString(1, teamID.get(i));
				Accounts = preparedStatement.executeQuery();
				if(!Accounts.next())
					throw new SQLException("No  TaxID matching. ");
				accountID.add(Accounts.getString("type"));
				accountID.add(Accounts.getString("accountid"));

			}catch(SQLException e) {
				System.err.println(e.getMessage());
				return nothing;
			}
		}

		return accountID;
	}

	public String transferCustomer(String accountId1, String accountId2, String tid, double amount) {
		String selection = "select A.* from account A where A.accountid = ?";
		double afterbalance = 0;
		double accountbalance = 0;
		try(PreparedStatement preparedStatement = _connection.prepareStatement(selection, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE)) {
			preparedStatement.setString(1, accountId1);
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();

			if(accountId2.equals(accountId1)) {
				throw new SQLException("Receiving account cannot be the same with current account");
			}

			try(PreparedStatement preparedStatement1 = _connection.prepareStatement("select A.* from account A where A.accountid = ?")) {
				preparedStatement1.setString(1, accountId2);
				ResultSet resultSet2 = preparedStatement1.executeQuery();
				if(!resultSet2.next()) {
					throw new SQLException("Receiving account doesn't exist.");
				} else if(resultSet2.getString("STATUS").equals("CLOSE")) {
					throw new SQLException("Receiving account closed");
				} else if(resultSet2.getString("TYPE").equals("POCKET")) {
					throw new SQLException("Receiving account is pocket account");
				}
				String team = resultSet2.getString("owned");

				try(PreparedStatement preparedStatement2 = _connection.prepareStatement("select B.* from belongs B where B.teamid = ? and B.taxid = ?")) {
					preparedStatement2.setString(1, team);
					preparedStatement2.setString(2, tid);
					ResultSet resultSet3 = preparedStatement2.executeQuery();
					if (!resultSet3.next()){
						throw new SQLException("You are not a owner of the receiving account");
					}
				}
			}

//			if(resultSet.getString("type").equals("pocket")) {
//				throw new SQLException("The account is a");
//			}

			if(resultSet.getDouble("balance") < amount)
				throw new SQLException("Account balance too low for transfer");

			if(resultSet.getString("STATUS").equals("CLOSE")) {
				throw new SQLException("The account is closed.");
			}

			resultSet.updateDouble("BALANCE", resultSet.getDouble("BALANCE") - amount);
			resultSet.updateRow();

			try(PreparedStatement preparedStatement3 = _connection.prepareStatement("select A.* from account A where A.accountid = ?", ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE)) {
				preparedStatement3.setString(1, accountId2);
				ResultSet resultSet2 = preparedStatement3.executeQuery();
				resultSet2.next();
				resultSet2.updateDouble("BALANCE", resultSet2.getDouble("BALANCE") + amount);
				resultSet2.updateRow();
			}

			if(resultSet.getDouble("balance") - amount <= 0.01)
				resultSet.updateString("STATUS", "CLOSE");

			afterbalance = resultSet.getDouble("balance") - amount;

			try(PreparedStatement statement1 = _connection.prepareStatement("insert into transaction values(null, ?, ?, 'TRANSFER', ?, ? )"))
			{
				statement1.setDouble(1, amount);
				statement1.setDate(2, systemDate);
				statement1.setString(3, accountId1);
				statement1.setString(4, accountId2);
				statement1.execute();
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return "1";
		}
		return "0";
	}

	public String wire(String accountId1, String accountId2, double amount) {
		String selection = "select A.* from account A where A.accountid = ?";
		double afterbalance = 0;
		double accountbalance = 0;
		try(PreparedStatement preparedStatement = _connection.prepareStatement(selection, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE)) {
			preparedStatement.setString(1, accountId1);
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();

			if(accountId2.equals(accountId1)) {
				throw new SQLException("Receiving account cannot be the same with current account");
			}

			try(PreparedStatement preparedStatement1 = _connection.prepareStatement("select A.* from account A where A.accountid = ?")) {
				preparedStatement1.setString(1, accountId2);
				ResultSet resultSet2 = preparedStatement1.executeQuery();
				if(!resultSet2.next()) {
					throw new SQLException("Receiving account doesn't exist.");
				} else if(resultSet2.getString("STATUS").equals("CLOSE")) {
					throw new SQLException("Receiving account closed");
				} else if(resultSet2.getString("TYPE").equals("POCKET")) {
					throw new SQLException("Receiving account is pocket account");
				}
			}

//			if(resultSet.getString("type").equals("pocket")) {
//				throw new SQLException("The account is a");
//			}

			if(resultSet.getDouble("balance") < amount*1.02)
				throw new SQLException("Account balance too low for transfer");

			if(resultSet.getString("STATUS").equals("CLOSE")) {
				throw new SQLException("The account is closed.");
			}

			resultSet.updateDouble("BALANCE", resultSet.getDouble("BALANCE") - amount*1.02);
			resultSet.updateRow();

			try(PreparedStatement preparedStatement3 = _connection.prepareStatement("select A.* from account A where A.accountid = ?", ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE)) {
				preparedStatement3.setString(1, accountId2);
				ResultSet resultSet2 = preparedStatement3.executeQuery();
				resultSet2.next();
				resultSet2.updateDouble("BALANCE", resultSet2.getDouble("BALANCE") + amount);
				resultSet2.updateRow();
			}

			if(resultSet.getDouble("balance") - amount*1.02 <= 0.01)
				resultSet.updateString("STATUS", "CLOSE");

			afterbalance = resultSet.getDouble("balance") - amount*1.02;

			try(PreparedStatement statement1 = _connection.prepareStatement("insert into transaction values(null, ?, ?, 'WIRES', ?, ? )"))
			{
				statement1.setDouble(1, amount);
				statement1.setDate(2, systemDate);
				statement1.setString(3, accountId1);
				statement1.setString(4, accountId2);
				statement1.execute();
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return "1";
		}
		return "0";
	}

	@Override
	public String topUp(String accountId, double amount) {
		String selection = "select A.* from pocketaccount A where A.accountid = ?";
		String linkedId = null;
		double afterbalance = 0;
		double accountbalance = 0;
		double monthlyCharge = 0;



		try(PreparedStatement preparedStatement = _connection.prepareStatement(selection))
		{
			preparedStatement.setString(1, accountId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(!resultSet.next())
				throw new SQLException("No pocket account specified in topUP");


			linkedId = resultSet.getString("linkedid");


			try(PreparedStatement statement = _connection.prepareStatement("select T.* from transaction T where T.clientfrom = ? or T.clientto = ?" )) {
				statement.setString(1, accountId);
				statement.setString(2, accountId);
				ResultSet resultSet2 = statement.executeQuery();
				if(!resultSet2.next()) {
					monthlyCharge = 5.0;
				}
			}

			try(PreparedStatement statement = _connection.prepareStatement("select A.* from Account A where A.accountid = ?"))
			{
				statement.setString(1, accountId);
				ResultSet resultSet1 = statement.executeQuery();
				resultSet1.next();
				if(resultSet1.getString("STATUS").equals("CLOSE"))
					throw new SQLException("Can't operate on closed Pocket Account");

			}

			try(PreparedStatement statement = _connection.prepareStatement("Select A.* from Account A where A.accountid = ?",ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE))
			{
				statement.setString(1, linkedId);
				ResultSet resultSet1 = statement.executeQuery();
				resultSet1.next();
				if(resultSet1.getString("STATUS").equals("CLOSE"))
					throw new SQLException("No transaction allowed on closed account");
				else if(resultSet1.getDouble("BALANCE") < (amount+monthlyCharge))
					throw new SQLException("Linked Account Balance too low for topup");
				else
				{
					afterbalance = resultSet1.getDouble("BALANCE") - (amount+monthlyCharge);
					if(resultSet1.getDouble("Balance") - (amount+monthlyCharge) <= 0.01)
						resultSet1.updateString("STATUS", "CLOSE");
					resultSet1.updateDouble("BALANCE", resultSet1.getDouble("BALANCE") - (amount+monthlyCharge));

					resultSet1.updateRow();
				}
			}



			try(PreparedStatement statement1 = _connection.prepareStatement("insert into transaction values(null, ?, ?, 'TOPUP', ?, ? )"))
			{
				statement1.setDouble(1, amount);
				statement1.setDate(2, systemDate);
				statement1.setString(3, linkedId);
				statement1.setString(4, accountId);
				statement1.execute();
			}


//			try(PreparedStatement statement = _connection.prepareStatement("Update Account A set A.balance = A.balance + ? where A.accountid = ?"))
			try(PreparedStatement statement = _connection.prepareStatement("select A.* from Account A where A.accountid = ?",ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE))
			{
//
//				statement.setDouble(1, amount);
				statement.setString(1, accountId);
				ResultSet resultSet1 = statement.executeQuery();
				if(!resultSet1.next())
					throw new SQLException("No pocket account id recorded");
				accountbalance = resultSet1.getDouble("balance") + amount;

				resultSet1.updateDouble("balance", accountbalance);

				resultSet1.updateRow();


			}





			return "0 " + afterbalance + " " + accountbalance;




		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	public String purchase(String accountId, double amount) {
		String selection = "select A.* from account A where A.accountid = ?";
		double afterbalance = 0;
		double accountbalance = 0;
		double monthlyCharge = 0.0;
		try(PreparedStatement preparedStatement = _connection.prepareStatement(selection, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE)) {
			preparedStatement.setString(1, accountId);
			ResultSet resultSet1 = preparedStatement.executeQuery();
			resultSet1.next();

			try(PreparedStatement statement = _connection.prepareStatement("select T.* from transaction T where T.clientfrom = ? or T.clientto = ?" )) {
				statement.setString(1, accountId);
				statement.setString(2, accountId);
				ResultSet resultSet2 = statement.executeQuery();
				if(!resultSet2.next()) {
					monthlyCharge = 5.0;
				}
			}

			if(resultSet1.getString("STATUS").equals("CLOSE"))
				throw new SQLException("Can't operate on closed Pocket Account");
			else if(resultSet1.getDouble("BALANCE") < (amount+monthlyCharge))
				throw new SQLException("Pocket Account Balance too low for purchase");
			else {
				afterbalance = resultSet1.getDouble("BALANCE") - (amount+monthlyCharge);
				if(resultSet1.getDouble("Balance") - (amount+monthlyCharge) <= 0.01)
					resultSet1.updateString("STATUS", "CLOSE");
				resultSet1.updateDouble("BALANCE", resultSet1.getDouble("BALANCE") - (amount+monthlyCharge));
				resultSet1.updateRow();
			}

			try(PreparedStatement statement1 = _connection.prepareStatement("insert into transaction values(null, ?, ?, 'PURCHASE', ?, ? )"))
			{
				statement1.setDouble(1, amount);
				statement1.setDate(2, systemDate);
				statement1.setString(3, accountId);
				statement1.setString(4, accountId);
				statement1.execute();
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return "1";
		}
		return "0";
	}

	public String collect(String accountId, double amount) {
		String selection = "select A.* from pocketaccount A where A.accountid = ?";
		String linkedId = null;
		double afterbalance = 0;
		double accountbalance = 0;
		double monthlyCharge = 0;
		try(PreparedStatement preparedStatement = _connection.prepareStatement(selection)) {
			preparedStatement.setString(1, accountId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(!resultSet.next())
				throw new SQLException("No pocket account specified in collect");
			linkedId = resultSet.getString("linkedid");


			try(PreparedStatement statement = _connection.prepareStatement("select T.* from transaction T where T.clientfrom = ? or T.clientto = ?" )) {
				statement.setString(1, accountId);
				statement.setString(2, accountId);
				ResultSet resultSet2 = statement.executeQuery();
				if(!resultSet2.next()) {
					monthlyCharge = 5.0;
				}
			}



			try(PreparedStatement statement = _connection.prepareStatement("select A.* from Account A where A.accountid = ?"))
			{
				statement.setString(1, accountId);
				ResultSet resultSet1 = statement.executeQuery();
				resultSet1.next();
				if(resultSet1.getString("STATUS").equals("CLOSE")) {
					throw new SQLException("Can't operate on closed Pocket Account");
				}
				else if(resultSet1.getDouble("BALANCE") < (amount*1.03+monthlyCharge)) {
					throw new SQLException("Pocket Account Balance too low for collect");
				}
			}

			try(PreparedStatement statement = _connection.prepareStatement("Select A.* from Account A where A.accountid = ?",ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE))
			{
				statement.setString(1, linkedId);
				ResultSet resultSet1 = statement.executeQuery();
				resultSet1.next();
				if(resultSet1.getString("STATUS").equals("CLOSE"))
					throw new SQLException("No transaction allowed on closed account");
				else
				{
					afterbalance = resultSet1.getDouble("BALANCE") + amount;
					resultSet1.updateDouble("BALANCE", resultSet1.getDouble("BALANCE") + amount);

					resultSet1.updateRow();
				}
			}


			try(PreparedStatement statement = _connection.prepareStatement("select A.* from Account A where A.accountid = ?",ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE))
			{
				statement.setString(1, accountId);
				ResultSet resultSet1 = statement.executeQuery();
				if(!resultSet1.next())
					throw new SQLException("No pocket account id recorded");
				else {
					afterbalance = resultSet1.getDouble("BALANCE") - (amount*1.03+monthlyCharge);
					if(resultSet1.getDouble("Balance") - (amount*1.03+monthlyCharge) <= 0.01)
						resultSet1.updateString("STATUS", "CLOSE");
					resultSet1.updateDouble("BALANCE", resultSet1.getDouble("BALANCE") - (amount*1.03+monthlyCharge));

					resultSet1.updateRow();
				}
			}


			try(PreparedStatement statement1 = _connection.prepareStatement("insert into transaction values(null, ?, ?, 'COLLECT', ?, ? )"))
			{
				statement1.setDouble(1, amount);
				statement1.setDate(2, systemDate);
				statement1.setString(3, accountId);
				statement1.setString(4, linkedId);
				statement1.execute();
			}
			return "1";

		}catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	public String createCheckingSavingsAccountWithBranch( AccountType accountType, String id, double initialBalance, String tin, String name, String address, String branch)
	{
		String type = null;
		if(accountType == AccountType.SAVINGS)
			type = "SAVING";
		else if(accountType == AccountType.INTEREST_CHECKING)
			type = "INTEREST";
		else if(accountType == AccountType.STUDENT_CHECKING)
			type = "STUDENT";
		else
			return "1";

		if(initialBalance < 1000)
			return "1";


		String checkExist = "select count(*) from Account A where A.accountid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(checkExist))
		{
			preparedStatement.setString(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();
			if(resultSet.getInt(1) != 0)
				throw new SQLException("Not new account");
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}


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

		existing = "select * from Account A where A.accountid = ?";
		try(PreparedStatement preparedStatement = _connection.prepareStatement(existing))
		{
			preparedStatement.setString(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				throw new SQLException("Already exists one account with same id");

			}
			else
			{
				String insertion = "insert into Team values(null,?)";
				int autoGen = 0;
				try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertion,new String[]{"teamid"}))
				{
					preparedStatement1.setString(1, tin);
					int res = preparedStatement1.executeUpdate();
					if(res == 0)
						throw new SQLException("something weird");
					ResultSet resultSet1 = preparedStatement1.getGeneratedKeys();
					if(resultSet1.next())
						autoGen = resultSet1.getInt(1);
					else
						throw new SQLException("Insertion into Team failed");



				}
				insertion = "insert into Belongs values(?,?)";
				try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertion))
				{
					preparedStatement1.setInt(1, autoGen);
					preparedStatement1.setString(2, tin);
					preparedStatement1.execute();
				}

				insertion = "insert into Account values(?,?,'OPEN',?, ?,?, ?)";
				try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertion))
				{
					preparedStatement1.setString(1, id);
					preparedStatement1.setString(2, type);
					double interest = 0;
					if(type.equals("SAVING"))
						interest = 0.048;
					else if(type.equals("INTEREST"))
						interest = 0.03;
					else
						interest = 0;
					preparedStatement1.setDouble(3, interest);
					preparedStatement1.setDouble(4, initialBalance);
					preparedStatement1.setString(5, branch);
					preparedStatement1.setInt(6,autoGen);
					preparedStatement1.execute();
				}
				insertion = "insert into Transaction values(null, ?,?,'DEPOSIT',?,?)";
				try(PreparedStatement preparedStatement1 = _connection.prepareStatement(insertion))
				{
					preparedStatement1.setDouble(1, initialBalance);
					preparedStatement1.setDate(2, systemDate);
					preparedStatement1.setString(3, null);
					preparedStatement1.setString(4, id);
					preparedStatement1.execute();
				}
			}

		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}







		return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
	}









}