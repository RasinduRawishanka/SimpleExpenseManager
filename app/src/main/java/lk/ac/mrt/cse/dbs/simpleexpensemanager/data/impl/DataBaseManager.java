package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DataBaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "200540H.sqllite";

    // Account table name initialize
    public static final String ACCOUNT_TABLE ="Account" ;


    // Details of account table(columns)
    public static final String ACCOUNT_NUMBER = "AccountNumbers";
    public static final String BANK_BRANCH = "BankName";
    public static final String A_HOLDERS_NAME = "AccountHolderName";
    public static final String INITIAL_BALANCE = "InitialBalance";

    // transaction table details(Columns)
    public static final String TRANSACTION_TABLE = "TransactionTable";
    public static final String TRANSACTION_ID = "T_ID";
    public static final String EXPENSE_T = "ExpenseType";
    public static final String INITIAL_AMOUNT = "Amount";
    public static final String DATE_OF_TRANSACTION = "TransactionDate";

    public DataBaseManager(Context con){
        super(con, DATABASE_NAME,null,1);
    }

    public void onCreate(SQLiteDatabase sqlDatabase){
        // create table for store accounts details
        String createAccountDetailsTableStatement ="CREATE TABLE " + ACCOUNT_TABLE + "(" +
                ACCOUNT_NUMBER + " TEXT PRIMARY KEY, " +
                BANK_BRANCH + " TEXT NOT NULL, " +
                A_HOLDERS_NAME + " TEXT NOT NULL, " +
                INITIAL_BALANCE + " REAL NOT NULL);";

        String transactionDetailsTableStatement ="CREATE TABLE " + TRANSACTION_TABLE + "(" +
                TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE_OF_TRANSACTION + " TEXT NOT NULL, " +
                EXPENSE_T + " TEXT NOT NULL, " +
                INITIAL_AMOUNT + " REAL NOT NULL, " +
                ACCOUNT_NUMBER + " TEXT," +
                "FOREIGN KEY (" + ACCOUNT_NUMBER + ") REFERENCES " + ACCOUNT_TABLE + "(" + ACCOUNT_NUMBER + "));";
        // execute account table query
        sqlDatabase.execSQL(createAccountDetailsTableStatement);
        // execute transaction table query
        sqlDatabase.execSQL(transactionDetailsTableStatement);
    }
    public void onUpgrade(SQLiteDatabase sqlDatabase,int old,int newV){
        // drop table statement of the account table
        String removeAccountTable = "DROP TABLE IF EXISTS " + ACCOUNT_TABLE+";";
        // drop table statement of the transaction table
        String removeTransactionTable = "DROP TABLE IF EXISTS " + TRANSACTION_TABLE+";";
        // execute the drop statement of the account table
        sqlDatabase.execSQL(removeAccountTable);
        // execute the drop statement of the transaction table
        sqlDatabase.execSQL(removeTransactionTable);
        onCreate(sqlDatabase);
    }

    public void insertToDataBase(String accountNum, String bankN, String accountHN, double IBalance){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues new_content = new ContentValues();
        new_content.put(ACCOUNT_NUMBER, accountNum);
        new_content.put(BANK_BRANCH, bankN);
        new_content.put(A_HOLDERS_NAME, accountHN);
        new_content.put(INITIAL_BALANCE, IBalance);
        long insert_status = database.insert(ACCOUNT_TABLE, null, new_content);

    }

    // get accounts data from database as cursor object
    public Cursor getAccountDataFromDB(String accountNum){
        SQLiteDatabase SQLDatabase = this.getReadableDatabase();
        String selection = ACCOUNT_NUMBER + " = ?";
        Cursor cursor = SQLDatabase.query(
                ACCOUNT_TABLE,   // process the query
                new String[]{ACCOUNT_NUMBER, BANK_BRANCH, A_HOLDERS_NAME, INITIAL_BALANCE},
                selection,
                new String[]{accountNum},
                null,
                null,
                null
        );

        return cursor;
    }

    public Cursor getAccountDataFromDB(){
        SQLiteDatabase SQLDatabase = this.getReadableDatabase();
        String selection = ACCOUNT_NUMBER + " = ?";
        Cursor cursor = SQLDatabase.query(
                ACCOUNT_TABLE,
                new String[]{ACCOUNT_NUMBER, BANK_BRANCH, A_HOLDERS_NAME, INITIAL_BALANCE},
                null,
                null,
                null,
                null,
                null
        );

        return cursor;
    }

    public void removeAccountFromDB(String accountNum) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(ACCOUNT_TABLE,ACCOUNT_NUMBER + " = ?",new String[]{accountNum});
        database.close();
    }

    // store values in transaction table in database
    public void insertToDataBase(Date date, String accountNo, ExpenseType expenseType, double amount ) {
        SQLiteDatabase SQLdatabase  = this.getWritableDatabase();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues rowValues = new ContentValues();
        rowValues.put(ACCOUNT_NUMBER,accountNo);
        rowValues.put(DATE_OF_TRANSACTION, dateFormat.format(date));
        rowValues.put(INITIAL_AMOUNT,amount);
        rowValues.put(EXPENSE_T,String.valueOf(expenseType));
        SQLdatabase.insert(TRANSACTION_TABLE,null,rowValues);
        SQLdatabase.close();
    }

    public Cursor getTransactionDataFromDB(){
        SQLiteDatabase SQLdatabase = this.getReadableDatabase();
        Cursor cursor = SQLdatabase.query( //generate quary
                TRANSACTION_TABLE,
                new String[]{DATE_OF_TRANSACTION, ACCOUNT_NUMBER, EXPENSE_T, INITIAL_AMOUNT},
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

}

