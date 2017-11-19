package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Kavindu on 11/19/2017.
 */


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "simple_expenses.db";
    public static final int VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public DatabaseHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, null, VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists account (account_no VARCHAR(30) primary key, bank_name text(100), account_holder_name text(200),balance NUMERIC(12,2), deleted INT(1) default 0)");
        db.execSQL("create table if not exists transactions (transaction_id INTEGER primary key AUTOINCREMENT,account_no VARCHAR(30) , transaction_date Date, expense_type text(15),amount NUMERIC(12,2), deleted int(1) default 0, FOREIGN KEY(account_no) REFERENCES account(account_no))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS account");
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);
    }

    public boolean insertAccount (Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_no", account.getAccountNo());
        contentValues.put("bank_name", account.getBankName());
        contentValues.put("account_holder_name", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());
        db.insert("account", null, contentValues);
        return true;
    }

    public boolean insertTransaction (String account_no, String expense_type, String transaction_date, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_no", account_no);
        contentValues.put("expense_type", expense_type);
        contentValues.put("transaction_date", transaction_date);
        contentValues.put("amount", amount);
        db.insert("transactions", null, contentValues);
        return true;
    }

    public boolean deleteAccount (String account_no) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deleted", 1);
        db.update("account", contentValues, "account_no = ? ", new String[]{account_no});
        return true;
    }

    public boolean updateBalance (String account_no,double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("balance", balance);
        db.update("account", contentValues, "account_no = ? ", new String[]{account_no});
        return true;
    }

    public ArrayList<Account> getAllAccounts() {
        ArrayList<Account> array_list = new ArrayList<Account>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account where deleted=0", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Account account = new Account(res.getString(res.getColumnIndex("account_no")),res.getString(res.getColumnIndex("bank_name")),res.getString(res.getColumnIndex("account_holder_name")),res.getDouble(res.getColumnIndex("balance")));
            array_list.add(account);
            res.moveToNext();
        }
        return array_list;
    }

    public Account getAccount(String accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account where account_no= '"+accountNo +"' and deleted=0", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            Account account = new Account(res.getString(res.getColumnIndex("account_no")),res.getString(res.getColumnIndex("bank_name")),res.getString(res.getColumnIndex("account_holder_name")),res.getDouble(res.getColumnIndex("balance")));
            return account;
        }
        return null;
    }

    public ArrayList<String> getAllAccountNumbers() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account where deleted=0", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("account_no")));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> array_list = new ArrayList<Transaction>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from transactions where deleted=0", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = dateFormat.parse(res.getString(res.getColumnIndex("transaction_date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Transaction account;
            if(res.getString(res.getColumnIndex("expense_type"))== "EXPENSE") {
                account = new Transaction(date, res.getString(res.getColumnIndex("account_no")),ExpenseType.EXPENSE , res.getDouble(res.getColumnIndex("amount")));
            }else{
                account = new Transaction(date, res.getString(res.getColumnIndex("account_no")),ExpenseType.INCOME , res.getDouble(res.getColumnIndex("amount")));
            }
            array_list.add(account);
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Transaction> getAllTransactionsLimited(int limit) {
        ArrayList<Transaction> array_list = new ArrayList<Transaction>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from transactions where deleted=0 order by transaction_id DESC LIMIT "+Integer.toString(limit), null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = dateFormat.parse(res.getString(res.getColumnIndex("transaction_date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Transaction account;
            if(res.getString(res.getColumnIndex("expense_type"))== "EXPENSE") {
                account = new Transaction(date, res.getString(res.getColumnIndex("account_no")),ExpenseType.EXPENSE , res.getDouble(res.getColumnIndex("amount")));
            }else{
                account = new Transaction(date, res.getString(res.getColumnIndex("account_no")),ExpenseType.INCOME , res.getDouble(res.getColumnIndex("amount")));
            }
            array_list.add(account);
            res.moveToNext();
        }
        return array_list;
    }

    public boolean isAccountValid(String accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account where account_no= '"+accountNo +"' and deleted=0", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            return true;
        }
        return false;
    }

}

/*

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "Expensemanager.db";
    public static final String TABLE1_NAME = "account_table";
    public static final String TABLE2_NAME = "transaction_table";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists account_table (accountNo varchar(20) primary key, bankName text(20), accountHolderName text(50), balance numeric(12,2), deleted INT(1) default 0)");
        sqLiteDatabase.execSQL("create table if not exists transaction_table (transactionID INTEGER primary key AUTOINCREMENT, date Date, accountNo varchar(20), expenseType text(20), amount numeric(12,2), deleted INT(1) default 0, FOREIGN KEY(accountNo) REFERENCES account_table(accountNo))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS account_table");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS transaction_table");
        onCreate(sqLiteDatabase);
    }

    public void  insertAccount(Account account){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("accountNo", account.getAccountNo());
        values.put("bankName", account.getBankName());
        values.put("accountHolderName", account.getAccountHolderName());
        values.put("balance", account.getBalance());
        database.insert("account_table", null, values);
    }

    public void insertTransaction(String date, String accountNo, String expenseType, double amount){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("accountNo", accountNo);
        values.put("expenseType", expenseType);
        values.put("amount", amount);
        database.insert("transaction_table", null, values);
    }

    List<Transaction> getAllTransactions() {
        ArrayList<Transaction> array_list = new ArrayList<Transaction>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from transaction_table", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            Date date = null;
            try {
                date = dateFormat.parse(res.getString(res.getColumnIndex("date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Transaction t;
            if(res.getString(res.getColumnIndex("expenseType"))== "EXPENSE") {
                t = new Transaction(date, res.getString(res.getColumnIndex("accountNo")),ExpenseType.EXPENSE , res.getDouble(res.getColumnIndex("amount")));
            }else{
                t = new Transaction(date, res.getString(res.getColumnIndex("accountNo")),ExpenseType.INCOME , res.getDouble(res.getColumnIndex("amount")));
            }
            array_list.add(t);
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Transaction> getAllTransactionsLimited(int limit) {
        ArrayList<Transaction> array_list = new ArrayList<Transaction>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from transaction_table order by transactionID DESC LIMIT "+Integer.toString(limit), null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            Date date = null;
            try {
                date = dateFormat.parse(res.getString(res.getColumnIndex("date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Transaction t;
            if(res.getString(res.getColumnIndex("expenseType"))== "EXPENSE") {
                t = new Transaction(date, res.getString(res.getColumnIndex("accountNo")),ExpenseType.EXPENSE , res.getDouble(res.getColumnIndex("amount")));
            }else{
                t = new Transaction(date, res.getString(res.getColumnIndex("accountNo")),ExpenseType.INCOME , res.getDouble(res.getColumnIndex("amount")));
            }
            array_list.add(t);
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllAccountNumbers() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account_table", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("accountNo")));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Account> getAllAccounts() {
        ArrayList<Account> array_list = new ArrayList<Account>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account_table", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Account account = new Account(res.getString(res.getColumnIndex("accountNo")),res.getString(res.getColumnIndex("bankName")),res.getString(res.getColumnIndex("accountHolderName")),res.getDouble(res.getColumnIndex("balance")));
            array_list.add(account);
            res.moveToNext();
        }
        return array_list;
    }

    public Account getAccount(String accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account_table where accountNo= '"+accountNo +"' ", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            Account account = new Account(res.getString(res.getColumnIndex("accountNo")),res.getString(res.getColumnIndex("bankName")),res.getString(res.getColumnIndex("accountHolderName")),res.getDouble(res.getColumnIndex("balance")));
            return account;
        }
        return null;
    }

    public boolean isAccountValid(String accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account_table where accountNo= '"+accountNo +"' ", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            return true;
        }
        return false;
    }

    public boolean deleteAccount (String account_no) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deleted", 1);
        db.update("account_table", contentValues, "accountNo = ? ", new String[]{account_no});
        return true;
    }

    public boolean updateBalance (String account_no,double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("balance", balance);
        db.update("account_table", contentValues, "accountNo = ? ", new String[]{account_no});
        return true;
    }

}
*/
