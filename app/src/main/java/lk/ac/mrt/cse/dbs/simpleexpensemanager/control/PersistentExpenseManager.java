package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;


import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InDatabaseAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InDatabaseTransactionDAO;


public class PersistentExpenseManager extends ExpenseManager {
    public PersistentExpenseManager(Context context){
        super(context);
        setup(context);
    }
    @Override
    public void setup(Context con) {
        TransactionDAO inDatabaseTransactionDAO = new InDatabaseTransactionDAO(con);
        setTransactionsDAO(inDatabaseTransactionDAO);
        AccountDAO inDatabaseAccountDAO = new InDatabaseAccountDAO(con);
        setAccountsDAO(inDatabaseAccountDAO);
    }
}
