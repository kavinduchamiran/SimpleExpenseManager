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

    public static final String DATABASE_NAME = "150073v.db";
    public static final int VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public DatabaseHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, null, VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists account (acc_no VARCHAR(30) primary key, bank text(100), acc_owner text(200),balance NUMERIC(12,2), deleted INT(1) default 0)");
        db.execSQL("create table if not exists transactions (transaction_id INTEGER primary key AUTOINCREMENT,acc_no VARCHAR(30) , trans_date Date, expense_type text(15),amount NUMERIC(12,2), deleted int(1) default 0, FOREIGN KEY(acc_no) REFERENCES account(acc_no))");
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
        contentValues.put("acc_no", account.getAccountNo());
        contentValues.put("bank", account.getBankName());
        contentValues.put("acc_owner", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());
        db.insert("account", null, contentValues);
        return true;
    }

    public boolean insertTransaction (String acc_no, String expense_type, String trans_date, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("acc_no", acc_no);
        contentValues.put("expense_type", expense_type);
        contentValues.put("trans_date", trans_date);
        contentValues.put("amount", amount);
        db.insert("transactions", null, contentValues);
        return true;
    }

    public boolean deleteAccount (String acc_no) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deleted", 1);
        db.update("account", contentValues, "acc_no = ? ", new String[]{acc_no});
        return true;
    }

    public boolean updateBalance (String acc_no,double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("balance", balance);
        db.update("account", contentValues, "acc_no = ? ", new String[]{acc_no});
        return true;
    }

    public ArrayList<Account> getAllAccounts() {
        ArrayList<Account> array_list = new ArrayList<Account>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account where deleted=0", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Account account = new Account(res.getString(res.getColumnIndex("acc_no")),res.getString(res.getColumnIndex("bank")),res.getString(res.getColumnIndex("acc_owner")),res.getDouble(res.getColumnIndex("balance")));
            array_list.add(account);
            res.moveToNext();
        }
        return array_list;
    }

    public Account getAccount(String accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account where acc_no= '"+accountNo +"' and deleted=0", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            Account account = new Account(res.getString(res.getColumnIndex("acc_no")),res.getString(res.getColumnIndex("bank")),res.getString(res.getColumnIndex("acc_owner")),res.getDouble(res.getColumnIndex("balance")));
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
            array_list.add(res.getString(res.getColumnIndex("acc_no")));
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
                date = dateFormat.parse(res.getString(res.getColumnIndex("trans_date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Transaction account;
            if(res.getString(res.getColumnIndex("expense_type"))== "EXPENSE") {
                account = new Transaction(date, res.getString(res.getColumnIndex("acc_no")),ExpenseType.EXPENSE , res.getDouble(res.getColumnIndex("amount")));
            }else{
                account = new Transaction(date, res.getString(res.getColumnIndex("acc_no")),ExpenseType.INCOME , res.getDouble(res.getColumnIndex("amount")));
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
                date = dateFormat.parse(res.getString(res.getColumnIndex("trans_date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Transaction account;
            if(res.getString(res.getColumnIndex("expense_type"))== "EXPENSE") {
                account = new Transaction(date, res.getString(res.getColumnIndex("acc_no")),ExpenseType.EXPENSE , res.getDouble(res.getColumnIndex("amount")));
            }else{
                account = new Transaction(date, res.getString(res.getColumnIndex("acc_no")),ExpenseType.INCOME , res.getDouble(res.getColumnIndex("amount")));
            }
            array_list.add(account);
            res.moveToNext();
        }
        return array_list;
    }

    public boolean isAccountValid(String accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account where acc_no= '"+accountNo +"' and deleted=0", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            return true;
        }
        return false;
    }

}