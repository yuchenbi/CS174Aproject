package cs174a;

import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;

public interface Transaction {

    String withDrawl(String accountID, double money);

    String purchase(String accountID, double money);
    //
//    String transfer();
//
    String collect(String accountID, double amount);
    //
//    String wireTrans();
//
    String writeCheck(String accountID, double amount);
}