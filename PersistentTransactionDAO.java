package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends MyDBHelper implements TransactionDAO {

    private List<Transaction> transactions;

    public PersistentTransactionDAO(Context context) {
        super(context);
        this.transactions = new ArrayList<Transaction>();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase database= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        String stringDate = calender.get(Calendar.YEAR) + "," + calender.get(Calendar.MONTH) +","+ calender.get(Calendar.DAY_OF_MONTH);

        contentValues.put("accountNo", accountNo);
        contentValues.put("date", stringDate);
        contentValues.put("amount", amount);
        contentValues.put("type", String.valueOf(expenseType));

        database.insert("transactions", null, contentValues);


    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        transactions = new ArrayList<Transaction>();

        String sql = "select * from transactions";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);

        if (cursor.moveToFirst()){

            do{
                String[] strdate = cursor.getString(1).split(",");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(strdate[0]),Integer.parseInt(strdate[1]), Integer.parseInt(strdate[2]));
                Date transactionDate = calendar.getTime();

                String accountNo = cursor.getString(2);

                String type = cursor.getString(3);
                ExpenseType expenseType = ExpenseType.valueOf(type.toUpperCase());

                double amount = cursor.getDouble(4);

                Transaction acc = new Transaction(transactionDate,accountNo,expenseType,amount);
                transactions.add(acc);


            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        transactions = getAllTransactionLogs();
        int size = transactions.size();

        if (size <= limit) {
            return transactions;
        }

        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
