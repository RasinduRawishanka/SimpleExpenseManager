package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseManager.ACCOUNT_NUMBER;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseManager.INITIAL_AMOUNT;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseManager.DATE_OF_TRANSACTION;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseManager.EXPENSE_T;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseManager.TRANSACTION_TABLE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class InDatabaseTransactionDAO implements TransactionDAO {
    private final DataBaseManager databaseManager;
    private SQLiteDatabase SQLdatabase;
    public InDatabaseTransactionDAO(Context con) {
        databaseManager = new DataBaseManager(con);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction t = new Transaction(date,accountNo,expenseType,amount);
        this.databaseManager.insertToDataBase(date,accountNo, expenseType,amount);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> listOfTransactions = new ArrayList<>();
        Cursor cursor = this.databaseManager.getTransactionDataFromDB();
        while(cursor.moveToNext()){
            String date_string = cursor.getString(cursor.getColumnIndex(DATE_OF_TRANSACTION));
            Date n_date = new SimpleDateFormat("dd-MM-yyyy").parse(date_string);
            String account = cursor.getString(cursor.getColumnIndex(ACCOUNT_NUMBER));
            ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(EXPENSE_T)));
            double amount = cursor.getDouble(cursor.getColumnIndex(INITIAL_AMOUNT));
            Transaction newTransaction = new Transaction(n_date,account,expenseType,amount);
            listOfTransactions.add(newTransaction);
        }
        cursor.close();
        return listOfTransactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> listOfTransactions = new ArrayList<>();
        Cursor cursor = this.databaseManager.getTransactionDataFromDB();
        int size = cursor.getCount();
        listOfTransactions = this.getAllTransactionLogs();
        if(size<=limit){
            return listOfTransactions;
        }
        return listOfTransactions.subList(size-limit,size);
    }
}

