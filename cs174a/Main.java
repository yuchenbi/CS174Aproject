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
			ArrayList<String> Schecking = new ArrayList<String>();
			ArrayList<String> Saving = new ArrayList<String>();
			ArrayList<String> Pocket = new ArrayList<String>();
			ArrayList<String> IChecking = new ArrayList<String>();

			int i = 0;
			app.createCheckingSavingsAccount(AccountType.SAVINGS, "account" + i, 1000.00, "361721022", "Alfred Hitchcock", "6667 El Colegio #40");
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
			app.createPocketAccount("account" + i, "account1", 0, "231403227");
			i++;

			do {
				System.out.println("Which interface?(ATM-App or Bank)");
				UI = input.nextLine();
				if (!UI.equals("ATM-App") && !UI.equals("Bank")) {
					System.out.println("Invalid Input.");
				}
			} while (!UI.equals("ATM-App") && !UI.equals("Bank"));


			if (UI.equals("ATM-App")) {
				System.out.println("User ID: ");
				inputUserID = input.nextLine();
				String quriedPin = app.getPin(inputUserID);
//			System.out.println(quriedPin);
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
											break;

										case "Transfer":
											break;

										case "Wire":
											break;

										case "Quit":
											break;
									}
								}
							}
						} while (!inputOpration.equals("Quit"));


					}
				} while (!inputPin.equals(quriedPin));

			}
		}
	}





}







