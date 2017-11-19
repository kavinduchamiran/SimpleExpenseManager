package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

/**
 * Created by Kavindu on 11/19/2017.
 */

public class PersistentExpenseManager extends ExpenseManager {
    public DatabaseHelper myDB;
    public PersistentExpenseManager(Context context) {
        myDB = new DatabaseHelper(context);
        setup();
    }

    @Override
    public void setup() {
        /*** Begin generating dummy data for In-Memory implementation ***/

        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(myDB);
        setTransactionsDAO(persistentTransactionDAO);

        AccountDAO perssistentAccountDAO = new PersistentAccountDAO(myDB);
        setAccountsDAO(perssistentAccountDAO);

        // dummy data
        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        Account dummyAcct3 = new Account("15073V", "ComBank ", "Kav Cha", 9999.0);
        getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);
        getAccountsDAO().addAccount(dummyAcct3);

        /*** End ***/
    }
}
