package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Kavindu on 11/19/2017.
 */

public class PersistentTransactionDAO implements TransactionDAO {
    private DatabaseHelper dbHelper;

    public PersistentTransactionDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        String strDate = sdf.format(date);
        dbHelper.insertTransaction( strDate, accountNo, expenseType.toString(), amount);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return dbHelper.getAllTransactions();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        return dbHelper.getAllTransactionsLimited(limit);
    }
}
