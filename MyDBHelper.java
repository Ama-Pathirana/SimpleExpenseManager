package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(Context context)
    {
        super(context,"200448H.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        String createAccSql = "CREATE TABLE accounts(accountNo text primary key, bankName text, accountHolderName text, balance real);";
        database.execSQL(createAccSql);

        String createTransactionSql = "create table transactions(transactionId integer primary key autoincrement, date text, accountNo text, type text, amount real, foreign key(accountNo) references account(accountNo));";
        database.execSQL(createTransactionSql);


    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int j, int k) {

    }
}
