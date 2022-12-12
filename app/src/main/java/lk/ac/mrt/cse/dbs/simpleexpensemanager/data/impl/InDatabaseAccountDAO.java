package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseManager.ACCOUNT_NUMBER;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseManager.ACCOUNT_TABLE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseManager.BANK_BRANCH;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseManager.A_HOLDERS_NAME;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseManager.INITIAL_BALANCE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class InDatabaseAccountDAO implements AccountDAO {
    private final DataBaseManager database_manager;
    private SQLiteDatabase SQL_database;
    public InDatabaseAccountDAO(Context context){
        this.database_manager = new DataBaseManager(context);
    }

    // get accounts data from database as cursor object
    public Account getAccount(String accountNum) throws InvalidAccountException {
        Cursor cursor = this.database_manager.getAccountDataFromDB(accountNum);
        if (cursor != null){
            cursor.moveToFirst();
            Account account = new Account(accountNum, cursor.getString(cursor.getColumnIndex(BANK_BRANCH)),
                    cursor.getString(cursor.getColumnIndex(A_HOLDERS_NAME)), cursor.getDouble(cursor.getColumnIndex(INITIAL_BALANCE)));
            return account;
        }
        else {
            String exceptionMessage = "Account " + accountNum + " is invalid.";
            throw new InvalidAccountException(exceptionMessage);
        }

    }

    public List<String> getAccountNumbersList(){
        List<String> accountNumbersList = new ArrayList<String>();
        for (int i = 0; i < this.getAccountsList().size(); i++) {
            accountNumbersList.add(this.getAccountsList().get(i).getAccountNo());
        }
        return accountNumbersList;

    }

    public List<Account> getAccountsList() {
        List<Account> accountsList = new ArrayList<Account>();
        Cursor cursor = this.database_manager.getAccountDataFromDB();

        while(cursor.moveToNext()) {
            String accountNum = cursor.getString(cursor.getColumnIndex(ACCOUNT_NUMBER));
            String bankName = cursor.getString(cursor.getColumnIndex(BANK_BRANCH));
            String holdersName = cursor.getString(cursor.getColumnIndex(A_HOLDERS_NAME));
            double initialBalance = cursor.getDouble(cursor.getColumnIndex(INITIAL_BALANCE));
            Account account = new Account(accountNum,bankName,holdersName,initialBalance);
            accountsList.add(account);
        }
        cursor.close();
        return accountsList;
    }


    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        this.database_manager.removeAccountFromDB(accountNo);
    }
    @Override
    public void addAccount(Account account) {
        this.database_manager.insertToDataBase(account.getAccountNo(),account.getBankName(),account.getAccountHolderName(),account.getBalance());
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        SQL_database = database_manager.getWritableDatabase();
        String selection = ACCOUNT_NUMBER + " = ?";
        Cursor cursor = SQL_database.query(  //process the query
                ACCOUNT_TABLE,
                new String[]{INITIAL_BALANCE},
                selection,
                new String[]{accountNo},
                null,
                null,
                null
        );
        double accountBalance;
        if(cursor.moveToFirst()){
            accountBalance = cursor.getDouble(0);
        }
        else{
            String invalid = "Account " + accountNo + "is invalid";
            throw new InvalidAccountException(invalid);
        }

        ContentValues rowValues = new ContentValues();
        if(expenseType == ExpenseType.INCOME){
            rowValues.put(INITIAL_BALANCE,accountBalance+amount);
        }
        else{
            rowValues.put(INITIAL_BALANCE, accountBalance - amount);
        }
        SQL_database.update(ACCOUNT_TABLE,rowValues,ACCOUNT_NUMBER + " =?", new String[] {accountNo});
        cursor.close();
        SQL_database.close();

    }

}



