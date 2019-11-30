package cs174a;

import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;

public interface Transaction {
    ArrayList<Integer> daysOfMonty = new ArrayList<Integer>();

    String Deposit();

    String TopUp();

    String withDrawl();

    String Purchase();

    String Transfer();

    String Collect();

    String payFriend();

    String wireTrans();

    String writeCheck();

    String accrueInterest();
}
