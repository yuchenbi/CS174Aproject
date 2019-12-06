package cs174a;                         // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// DO NOT REMOVE THIS IMPORT.
import cs174a.Testable.*;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * This is the class that launches your application.
 * DO NOT CHANGE ITS NAME.
 * DO NOT MOVE TO ANY OTHER (SUB)PACKAGE.
 * There's only one "main" method, it should be defined within this Main class, and its signature should not be changed.
 */
public class Main
{
	/**
	 * Program entry point.
	 * DO NOT CHANGE ITS NAME.
	 * DON'T CHANGE THE //!### TAGS EITHER.  If you delete them your program won't run our tests.
	 * No other function should be enclosed by the //!### tags.
	 */
	//!### COMENZAMOS
	public static void main( String[] args )
	{
		runApp();
	}
	//!### FINALIZAMOS





	public static void runApp() {
		App app = new App();                        // We need the default constructor of your App implementation.  Make sure such
		// constructor exists.
		String r = app.initializeSystem();          // We'll always call this function before testing your system.
		if (r.equals("0")) {
			app.exampleAccessToDB();                // Example on how to connect to the DB.
//
			app.dropTables();
			app.createTables();
//			runApp(app);
			Scanner input = new Scanner(System.in);
			String createAcc;
			String taxid;
			String name;
			String pin;
			String address;
			String login;
			String UI;
			String inputUserID;
			String inputPin;
			String inputAccount;
			String inputAccount2;
			String inputOpration;
			String inputDepsit;
			Double inputDepsitAmount;
			String inputTopUp;
			Double inputTopUpAmount;
			String inputPurchase;
			Double inputPurcahseAmount;
			String inputCollect;
			Double inputCollectAmount;
			String inputPay;
			Double inputPayAmount;
			String inputWithdrawal;
			Double inputWithdrawalAmount;
			String inputTransfer;
			Double inputTransferAmount;
			String inputWire;
			Double inputWireAmount;
			String inputBankerOperation;
			String inputBankerAccountId;
			String inputBankerTaxId;
			String temp;
			String intputBankerAccountType;
			String newAccountId;
			String newInitialBalance;
			Double newInitialBalanceAmount;
			String newTaxId;
			String newName;
			String newAddress;
			String newBranch;
			String newLinkedId;
			String newYear;
			int newYearValue;
			String newMon;
			int newMonValue;
			String newDay;
			int newDayValue;
			String inputBankerCheck;
			Double inputBankerCheckAmount;
			ArrayList<String> Schecking = new ArrayList<String>();
			ArrayList<String> Saving = new ArrayList<String>();
			ArrayList<String> Pocket = new ArrayList<String>();
			ArrayList<String> IChecking = new ArrayList<String>();

//			int i = 0;
			app.setDate(2011, 3, 1);
			app.createCheckingSavingsAccountWithBranch(AccountType.STUDENT_CHECKING, "17431", 1200.00, "344151573", "Joe Pepsi", "3210 State St", "San Francisco");
			app.setPIN("344151573", "1717", "3692");
			app.createCustomer("17431", "412231856", "Cindy Laugher", "7000 Hollister");
			app.setPIN("412231856", "1717", "3764");
			app.createCustomer("17431", "322175130", "Ivan Lendme", "1235 Johnson Dr");
			app.setPIN("322175130", "1717", "8471");

			app.createCheckingSavingsAccountWithBranch(AccountType.STUDENT_CHECKING, "54321", 21000.00, "212431965", "Hurryson Ford", "678 State St", "Los Angeles");
			app.setPIN("212431965", "1717", "3532");
			app.createCustomer("54321", "412231856", "Cindy Laugher", "7000 Hollister");
			app.createCustomer("54321", "122219876", "Elizabeth Sailor", "4321 State St");
			app.setPIN("122219876", "1717", "3856");
			app.createCustomer("54321", "203491209", "Nam-Hoi Chung", "1997 People's St HK");
			app.setPIN("203491209", "1717", "5340");

			app.createCheckingSavingsAccountWithBranch(AccountType.STUDENT_CHECKING, "12121", 1200.00, "207843218", "David Copperfill", "1357 State St", "Goleta");
			app.setPIN("207843218", "1717", "8582");

			app.createCheckingSavingsAccountWithBranch(AccountType.INTEREST_CHECKING, "41725", 15000.00, "201674933", "George Brush", "5346 Foothill Av", "Los Angeles");
			app.setPIN("201674933", "1717", "9824");
			app.createCustomer("41725", "401605312", "Fatal Castro", "3756 La Cumbre Plaza");
			app.setPIN("401605312", "1717", "8193");
			app.createCustomer("41725", "231403227", "Billy Clinton", "5777 Hollister");
			app.setPIN("231403227", "1717", "1468");

			app.createCheckingSavingsAccountWithBranch(AccountType.INTEREST_CHECKING, "76543", 8456.00, "212116070", "Li Kung", "2 People's Rd Beijing", "Santa Barbara");
			app.setPIN("212116070", "1717", "9173");
			app.createCustomer("76543", "188212217", "Magic Jordon", "3852 Court Rd");
			app.setPIN("188212217", "1717", "7351");

			app.createCheckingSavingsAccountWithBranch(AccountType.INTEREST_CHECKING, "93156", 2000000.00, "209378521", "Kelvin Costner", "Santa Cruz #3579", "Goleta");
			app.setPIN("209378521", "1717", "4659");
			app.createCustomer("93156", "188212217", "Magic Jordon", "3852 Court Rd");
			app.createCustomer("93156", "210389768", "Olive Stoner", "6689 El Colegio #151");
			app.setPIN("210389768", "1717", "8452");
			app.createCustomer("93156", "122219876", "Elizabeth Sailor", "4321 State St");
			app.createCustomer("93156", "203491209", "Nam-Hoi Chung", "1997 People's St HK");

			app.createCheckingSavingsAccountWithBranch(AccountType.SAVINGS, "43942", 1289.00, "361721022", "Alfred Hitchcock", "6667 El Colegio #40", "Santa Barbara");
			app.setPIN("361721022", "1717", "1234");
			app.createCustomer("43942", "400651982", "Pit Wilson", "911 State St");
			app.setPIN("400651982", "1717", "1821");
			app.createCustomer("43942", "212431965", "Hurryson Ford", "678 State St");
			app.createCustomer("43942", "322175130", "Ivan Lendme", "1235 Johnson Dr");

			app.createCheckingSavingsAccountWithBranch(AccountType.SAVINGS, "29107", 34000.00, "209378521", "Kelvin Costner", "Santa Cruz #3579", "Los Angeles");
			app.createCustomer("29107", "400651982", "Li Kung", "2 People's Rd Beijing");
			app.createCustomer("29107", "210389768", "Olive Stoner", "6689 El Colegio #151");

			app.createCheckingSavingsAccountWithBranch(AccountType.SAVINGS, "19023", 2300.00, "412231856", "Cindy Laugher", "7000 Hollister", "San Francisco");
			app.createCustomer("19023", "201674933", "George Brush", "5346 Foothill Av");
			app.createCustomer("19023", "401605312", "Fatal Castro", "3756 La Cumbre Plaza");

			app.createCheckingSavingsAccountWithBranch(AccountType.SAVINGS, "32156", 1000.00, "188212217", "Magic Jordon", "3852 Court Rd", "Goleta");
			app.createCustomer("32156", "207843218", "David Copperfill", "1357 State St");
			app.createCustomer("32156", "122219876", "Elizabeth Sailor", "4321 State St");
			app.createCustomer("32156", "344151573", "Joe Pepsi", "3210 State St");
			app.createCustomer("32156", "203491209", "Nam-Hoi Chung", "1997 People's St HK");
			app.createCustomer("32156", "210389768", "Olive Stoner", "6689 El Colegio #151");

			app.createPocketAccountWithBranch("53027", "12121", 50.00, "207843218", "Goleta");
			app.createPocketAccountWithBranch("43947", "29107", 30.00, "212116070", "Isla Vista");
			app.createPocketAccountWithBranch("60413", "43942", 20.00, "400651982", "Santa Cruz");
			app.createPocketAccountWithBranch("67521", "19023", 100.00, "401605312", "3756 La Cumbre Plaza");

//			app.generateMonthlyReport("207843218");
//			app.generateMonthlyReport("207843218");



//			i++;
//			app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING, "account" + i, 1000.00, "361721022", "Alfred Hitchcock", "6667 El Colegio #40");
//			i++;
//			app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING, "account" + i, 1000.00, "231403227", "Billy Clinton", "5777 Hollister");
//			i++;
//			app.createCheckingSavingsAccount(AccountType.STUDENT_CHECKING, "account" + i, 1000.00, "412231856 ", "Cindy Laugher", "7000 Hollister");
//			i++;
//			app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING, "account" + i, 1000.00, "207843218", "David Copperfill", "1357 State St");
//			i++;
//			app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING, "account" + i, 1000.00, "122219876", "Elizabeth Sailor", "4321 State St");
//			i++;
//			app.createPocketAccount("account" + i, "account0", 100, "361721022");
//			i++;
//			app.createPocketAccount("account" + i, "account1", 0, "231403227");
//			i++;




			do {
				System.out.println("Which interface?(ATM-App or Bank), SetDate or Quit");
				UI = input.nextLine();
				if (!UI.equals("ATM-App") && !UI.equals("Bank") && !UI.equals("SetDate") && !UI.equals("Quit")) {
					System.out.println("Invalid Input.");
				}

				if(UI.equals("SetDate")) {
					System.out.println("What date do you want to set?");
					System.out.println("Year: ");
					newYear = input.nextLine();
					newYearValue = Integer.parseInt(newYear);
					System.out.println("Month: ");
					newMon = input.nextLine();
					newMonValue = Integer.parseInt(newMon);
					System.out.println("Date: ");
					newDay = input.nextLine();
					newDayValue = Integer.parseInt(newDay);
					app.setDate(newYearValue, newMonValue, newDayValue);
				}

				if (UI.equals("ATM-App")) {


//					System.out.println(quriedPin);
					do {
						System.out.println("User ID: ");
						inputUserID = input.nextLine();
						System.out.println("Pin: ");
						inputPin = input.nextLine();
						if (app.verifyPIN(inputUserID, inputPin)) {
							System.out.println("Log in Successfully!");
							System.out.println("You have following accounts, which one do you want to access?(Type the account name): ");
							ArrayList<String> account = new ArrayList<String>();
							account = app.getAllAccount(inputUserID);

							for (int k = 0; k < account.size(); k++) {
								if (account.get(k).equals("STUDENT")) {
									Schecking.add(account.get(k + 1));
								} else if (account.get(k).equals("INTEREST")) {
									IChecking.add(account.get(k + 1));
								} else if (account.get(k).equals("POCKET")) {
									Pocket.add(account.get(k + 1));
								} else if (account.get(k).equals("SAVING")) {
									Saving.add(account.get(k + 1));
								}
							}
							for (int j = 0; j < account.size(); j++) {
								System.out.println(account.get(j) + " " + account.get(j + 1));
								j++;
							}

							do {
								inputAccount = input.nextLine();
								if (!Schecking.contains(inputAccount) && !IChecking.contains(inputAccount)
										&& !Pocket.contains(inputAccount) && !Saving.contains(inputAccount)) {
									System.out.println("Invalid Input, enter again: ");
								}
							} while (!Schecking.contains(inputAccount) && !IChecking.contains(inputAccount)
									&& !Pocket.contains(inputAccount) && !Saving.contains(inputAccount));

							System.out.println("Now you are in: " + inputAccount);
							do {
								if (Pocket.contains(inputAccount)) {
									System.out.println("What do you want to do?(Top-up, Purchase, Collect, Pay-Friend or Quit)");
									inputOpration = input.nextLine();
									if (!inputOpration.equals("Purchase") && !inputOpration.equals("Top-up") && !inputOpration.equals("Collect") && !inputOpration.equals("Pay-Friend")
											&& !inputOpration.equals("Quit")) {
										System.out.println("Invalid Input. ");
									} else {
										switch (inputOpration) {
											case "Top-up":
												do {
													System.out.println("Enter Amount (>=0): ");
													inputTopUp = input.nextLine();
													inputTopUpAmount = Double.parseDouble(inputTopUp);
													if (inputTopUpAmount < 0) {
														System.out.println("Invalid Amount. ");
													}
												} while (inputTopUpAmount < 0);
												app.topUp(inputAccount, inputTopUpAmount);
												break;

											case "Purchase":
												do {
													System.out.println("Enter Amount (>=0): ");
													inputPurchase = input.nextLine();
													inputPurcahseAmount = Double.parseDouble(inputPurchase);
													if (inputPurcahseAmount < 0) {
														System.out.println("Invalid Amount. ");
													}
												} while (inputPurcahseAmount < 0);
												app.purchase(inputAccount, inputPurcahseAmount);
												break;

											case "Collect":
												do {
													System.out.println("Enter Amount (>=0): ");
													inputCollect = input.nextLine();
													inputCollectAmount = Double.parseDouble(inputCollect);
													if (inputCollectAmount < 0) {
														System.out.println("Invalid Amount. ");
													}
												} while (inputCollectAmount < 0);
												app.collect(inputAccount, inputCollectAmount);
												break;

											case "Pay-Friend":
												System.out.println("Enter the pocket account you want to sent to: ");
												inputAccount2 = input.nextLine();
												do {
													System.out.println("Enter Amount (>=0): ");
													inputPay = input.nextLine();
													inputPayAmount = Double.parseDouble(inputPay);
													if (inputPayAmount < 0) {
														System.out.println("Invalid Amount. ");
													}
												} while (inputPayAmount < 0);
												app.payFriend(inputAccount, inputAccount2, inputPayAmount);
												break;

											case "Quit":
												break;
										}
									}
								} else {
									System.out.println("What do you want to do?(Deposit, Withdrawal, Transfer, Wire or Quit)");
									inputOpration = input.nextLine();
									if (!inputOpration.equals("Deposit") && !inputOpration.equals("Withdrawal") && !inputOpration.equals("Transfer")
											&& !inputOpration.equals("Wire") && !inputOpration.equals("Quit")) {
										System.out.println("Invalid Input. ");
									} else {
										switch (inputOpration) {
											case "Deposit":
												do {
													System.out.println("Enter Amount (>=0): ");
													inputDepsit = input.nextLine();
													inputDepsitAmount = Double.parseDouble(inputDepsit);
													if (inputDepsitAmount < 0) {
														System.out.println("Invalid Amount. ");
													}
												} while (inputDepsitAmount < 0);
												app.deposit(inputAccount, inputDepsitAmount);
												break;

											case "Withdrawal":
												do {
													System.out.println("Enter Amount (>=0): ");
													inputWithdrawal = input.nextLine();
													inputWithdrawalAmount = Double.parseDouble(inputWithdrawal);
													if (inputWithdrawalAmount < 0) {
														System.out.println("Invalid Amount. ");
													}
												} while (inputWithdrawalAmount < 0);
												app.withDrawl(inputAccount, inputWithdrawalAmount);
												break;

											case "Transfer":
												System.out.println("Enter the account you want to sent to: ");
												inputAccount2 = input.nextLine();
												do {
													System.out.println("Enter Amount (>=0): ");
													inputTransfer = input.nextLine();
													inputTransferAmount = Double.parseDouble(inputTransfer);
													if (inputTransferAmount < 0) {
														System.out.println("Invalid Amount. ");
													}
												} while (inputTransferAmount < 0);
												app.transferCustomer(inputAccount, inputAccount2, inputUserID, inputTransferAmount);
												break;

											case "Wire":
												System.out.println("Enter the account you want to sent to: ");
												inputAccount2 = input.nextLine();
												do {
													System.out.println("Enter Amount (>=0): ");
													inputWire = input.nextLine();
													inputWireAmount = Double.parseDouble(inputWire);
													if (inputWireAmount < 0) {
														System.out.println("Invalid Amount. ");
													}
												} while (inputWireAmount < 0);
												app.wire(inputAccount, inputAccount2, inputWireAmount);
												break;

											case "Quit":
												break;
										}
									}
								}
							} while (!inputOpration.equals("Quit"));
						} else {
							System.out.println("Wrong pin or tax ID");
						}
					} while (!app.verifyPIN(inputUserID, inputPin));
				}



				if(UI.equals("Bank")) {
					do {
						do {
							System.out.println("What do you want to do?");
							System.out.println("Enter Check Transaction(1)");
							System.out.println("Generate Monthly Statement(2)");
							System.out.println("List Closed Accounts(3)");
							System.out.println("Generate Government Drug and Tax Evasion Report (DTER)(4)");
							System.out.println("Customer Report(5)");
							System.out.println("Add Interest(6)");
							System.out.println("Create Account(7)");
							System.out.println("Delete Closed Accounts and Customers(8)");
							System.out.println("Delete Transactions(9)");
							System.out.println("Quit(0)");
							System.out.println("Set Date(10)");
							inputBankerOperation = input.nextLine();
							if (!inputBankerOperation .equals("1") && !inputBankerOperation.equals("2") && !inputBankerOperation.equals("3") &&
									!inputBankerOperation .equals("4") && !inputBankerOperation.equals("5") && !inputBankerOperation.equals("6") &&
									!inputBankerOperation .equals("7") && !inputBankerOperation.equals("8") && !inputBankerOperation.equals("9") &&
									!inputBankerOperation.equals("0") && !inputBankerOperation.equals("10")) {
								System.out.println("Invalid input");
							}
						} while (!inputBankerOperation .equals("1") && !inputBankerOperation.equals("2") && !inputBankerOperation.equals("3") &&
								!inputBankerOperation .equals("4") && !inputBankerOperation.equals("5") && !inputBankerOperation.equals("6") &&
								!inputBankerOperation .equals("7") && !inputBankerOperation.equals("8") && !inputBankerOperation.equals("9") &&
								!inputBankerOperation.equals("0") && !inputBankerOperation.equals("10"));

						switch (inputBankerOperation) {
							case "1":
								System.out.println("Enter Account ID: ");
								inputBankerAccountId = input.nextLine();
								do {
									System.out.println("Enter Amount (>=0): ");
									inputBankerCheck = input.nextLine();
									inputBankerCheckAmount = Double.parseDouble(inputBankerCheck);
									if (inputBankerCheckAmount < 0) {
										System.out.println("Invalid Amount. ");
									}
								} while (inputBankerCheckAmount < 0);
								app.writeCheck(inputBankerAccountId, inputBankerCheckAmount);
								break;
							case "2":
								System.out.println("Enter Tax ID: ");
								inputBankerTaxId = input.nextLine();
								temp = app.generateMonthlyReport(inputBankerTaxId);
								System.out.println(temp);
								break;
							case "3":
								temp = app.listClosedAccounts();
								System.out.println(temp);
								break;

							case "4":
								temp = app.DTER();
								System.out.println(temp);
								break;

							case "5":
								System.out.println("Enter Tax ID: ");
								inputBankerTaxId = input.nextLine();
								temp = app.cusReport(inputBankerTaxId);
								System.out.println(temp);
								break;
							case "6":
								temp = app.addInterest();
								if(!temp.equals("1")) {
									System.out.println("Success");
								}
								break;

							case "7":
								do{
									System.out.println("What type of account?");
									System.out.println("Student Checking(1)");
									System.out.println("Interest Checking(2)");
									System.out.println("Saving(3)");
									System.out.println("Pocket(4)");
									intputBankerAccountType = input.nextLine();
									if(!intputBankerAccountType.equals("1") && !intputBankerAccountType.equals("2") &&
											!intputBankerAccountType.equals("3") && !intputBankerAccountType.equals("4")) {
										System.out.println("Invalid input");
									}
								} while(!intputBankerAccountType.equals("1") && !intputBankerAccountType.equals("2") &&
										!intputBankerAccountType.equals("3") && !intputBankerAccountType.equals("4"));

								switch (intputBankerAccountType) {
									case "1":
										System.out.println("Account ID: ");
										newAccountId = input.nextLine();
										System.out.println("InitialBalance: ");
										newInitialBalance = input.nextLine();
										newInitialBalanceAmount = Double.parseDouble(newInitialBalance);
										System.out.println("Tax ID: ");
										newTaxId = input.nextLine();
										System.out.println("Name: ");
										newName = input.nextLine();
										System.out.println("Address: ");
										newAddress = input.nextLine();
										System.out.println("Branch Name: ");
										newBranch= input.nextLine();
										app.createCheckingSavingsAccountWithBranch(AccountType.STUDENT_CHECKING,newAccountId, newInitialBalanceAmount, newTaxId, newName, newAddress, newBranch);
										break;
									case "2":
										System.out.println("Account ID: ");
										newAccountId = input.nextLine();
										System.out.println("InitialBalance: ");
										newInitialBalance = input.nextLine();
										newInitialBalanceAmount = Double.parseDouble(newInitialBalance);
										System.out.println("Tax ID: ");
										newTaxId = input.nextLine();
										System.out.println("Name: ");
										newName = input.nextLine();
										System.out.println("Address: ");
										newAddress = input.nextLine();
										System.out.println("Branch Name: ");
										newBranch= input.nextLine();
										app.createCheckingSavingsAccountWithBranch(AccountType.INTEREST_CHECKING,newAccountId, newInitialBalanceAmount, newTaxId, newName, newAddress, newBranch);

										break;
									case "3":
										System.out.println("Account ID: ");
										newAccountId = input.nextLine();
										System.out.println("InitialBalance: ");
										newInitialBalance = input.nextLine();
										newInitialBalanceAmount = Double.parseDouble(newInitialBalance);
										System.out.println("Tax ID: ");
										newTaxId = input.nextLine();
										System.out.println("Name: ");
										newName = input.nextLine();
										System.out.println("Address: ");
										newAddress = input.nextLine();
										System.out.println("Branch Name: ");
										newBranch= input.nextLine();
										app.createCheckingSavingsAccountWithBranch(AccountType.SAVINGS,newAccountId, newInitialBalanceAmount, newTaxId, newName, newAddress, newBranch);
										break;
									case "4":
										System.out.println("Account ID: ");
										newAccountId = input.nextLine();
										System.out.println("Linked ID: ");
										newLinkedId = input.nextLine();
										System.out.println("InitialBalance: ");
										newInitialBalance = input.nextLine();
										newInitialBalanceAmount = Double.parseDouble(newInitialBalance);
										System.out.println("Tax ID: ");
										newTaxId = input.nextLine();
										System.out.println("Branch Name: ");
										newBranch= input.nextLine();
										app.createPocketAccountWithBranch(newAccountId, newLinkedId,newInitialBalanceAmount, newTaxId, newBranch);
										break;
								}
								break;

							case "8":
								temp = app.deleteAllClosed();
								if(temp.equals("1")) {
									System.out.println("Something went wrong");
								} else {
									System.out.println("Done");
								}
								break;

							case "9":
								app.deleteAllTrans();
								break;

							case "10":
								System.out.println("What date do you want to set?");
								System.out.println("Year: ");
								newYear = input.nextLine();
								newYearValue = Integer.parseInt(newYear);
								System.out.println("Month: ");
								newMon = input.nextLine();
								newMonValue = Integer.parseInt(newMon);
								System.out.println("Date: ");
								newDay = input.nextLine();
								newDayValue = Integer.parseInt(newDay);
								app.setDate(newYearValue, newMonValue, newDayValue);
								break;
							case "0":
								break;
						}
					}while(!inputBankerOperation.equals("0"));
				}




			} while (!UI.equals("Quit"));

		}
	}
}







