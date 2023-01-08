package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO extends MyDBHelper implements AccountDAO {
    private List<String> accNumber;
    private List<Account> account;

    public PersistentAccountDAO(Context context)
    {
        super(context);
        this.accNumber = new ArrayList<String>();
        this.account = new ArrayList<Account>();
    }

    @Override
    public List<String> getAccountNumbersList()
    {
        this.accNumber = new ArrayList<String>();

        String sql = "select accountNo from accounts";

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(sql,null);

        if (cursor.moveToFirst()){

            do{
                this.accNumber.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return this.accNumber;
    }

    @Override
    public List<Account> getAccountsList() {
        this.account = new ArrayList<Account>();

        String sql = "select * from accounts";

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(sql,null);

        if (cursor.moveToFirst()){

            do{
                String accountNo = cursor.getString(0);
                String bankName = cursor.getString(1);
                String accountHolderName = cursor.getString(2);
                double balance = cursor.getInt(3);

                Account accDetails = new Account(accountNo,bankName,accountHolderName,balance);
                this.account.add(accDetails);


            }while(cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return this.account;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from accounts where accountNo = '"+ accountNo + "' ;";
        Cursor cursor = db.rawQuery(sql,null);
        Account acc = null;

        String bankName = cursor.getString(1);
        String accountHolderName = cursor.getString(2);
        double balance = cursor.getInt(3);

        acc = new Account(accountNo,bankName,accountHolderName,balance);

        cursor.close();
        db.close();
        return acc;
    }

    @Override
    public void addAccount(Account account) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("accountNo", account.getAccountNo());
        contentValues.put("bankName", account.getBankName());
        contentValues.put("accountHolderName", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());

        database.insert("accounts", null, contentValues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String sql = "delete from accounts where accountNo = '"+ accountNo + "' ;";
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL(sql);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase database = this.getWritableDatabase();
        String selectsql = "select balance from accounts where accountNo = '"+ accountNo +"' ;";
        Cursor cursor = database.rawQuery(selectsql,null);
        cursor.moveToFirst();
        double balance = cursor.getDouble(0);
        switch(expenseType){
            case EXPENSE:
                balance  -= amount;
                break;
            case INCOME:
                balance  += amount;
                break;
        }

        String updateSql = "update accounts set balance = ? where accountNo = ? ;";
        SQLiteStatement statement = database.compileStatement(updateSql);
        statement.bindDouble(1,balance);
        statement.bindString(2,accountNo);
        statement.executeUpdateDelete();
        statement.close();
        cursor.close();
        database.close();

    }
}

