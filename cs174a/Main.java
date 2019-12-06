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
			ArrayList<String> Schecking = new ArrayList<String>();
			ArrayList<String> Saving = new ArrayList<String>();
			ArrayList<String> Pocket = new ArrayList<String>();
			ArrayList<String> IChecking = new ArrayList<String>();

			int i = 0;
			app.createCheckingSavingsAccount(AccountType.SAVINGS, "account" + i, 1000.00, "361721022", "Alfred Hitchcock", "6667 El Colegio #40");
			i++;
			app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING, "account" + i, 1000.00, "361721022", "Alfred Hitchcock", "6667 El Colegio #40");
			i++;
			app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING, "account" + i, 1000.00, "231403227", "Billy Clinton", "5777 Hollister");
			i++;
			app.createCheckingSavingsAccount(AccountType.STUDENT_CHECKING, "account" + i, 1000.00, "412231856 ", "Cindy Laugher", "7000 Hollister");
			i++;
			app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING, "account" + i, 1000.00, "207843218", "David Copperfill", "1357 State St");
			i++;
			app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING, "account" + i, 1000.00, "122219876", "Elizabeth Sailor", "4321 State St");
			i++;
			app.createPocketAccount("account" + i, "account0", 100, "361721022");
			i++;
//			app.createPocketAccount("account" + i, "account1", 0, "231403227");
//			i++;




			do {
				System.out.println("Which interface?(ATM-App or Bank), setDate or Quit");
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
					System.out.println("User ID: ");
					inputUserID = input.nextLine();
					String quriedPin = app.getPin(inputUserID);
					do {
						System.out.println("Pin: ");
						inputPin = input.nextLine();
						if (!inputPin.equals(quriedPin)) {
							System.out.println("Wrong Pin. ");
						} else {
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


						}
					} while (!inputPin.equals(quriedPin));

				} else {
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
								temp = app.checkTransaction(inputBankerAccountId);
								System.out.println(temp);
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
										app.createPocketAccount(newAccountId, newLinkedId,newInitialBalanceAmount, newTaxId);
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







