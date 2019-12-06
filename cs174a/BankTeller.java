package cs174a;

public interface BankTeller {
    boolean accrued = false;
    String deleteAllClosed();

    String generateMonthlyReport(String AccountId);

    String addInterest();

    String checkTransaction(String accountid);

    String DTER();

    String cusReport(String taxId);

    String deleteAllTrans();

}