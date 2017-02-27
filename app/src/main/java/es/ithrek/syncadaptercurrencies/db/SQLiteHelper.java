package es.ithrek.syncadaptercurrencies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import es.ithrek.syncadaptercurrencies.Contract;

/**
 * Created by Mikel on 11/02/17.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "currencies.db";
    public static final int DB_VERSION = 1;
    public static final String SQLDDLCURRENCY = "CREATE TABLE " + Contract.TABLE_CURRENCY +
            " (" + Contract.CURRENCY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + Contract.CURRENCY_ABBREVIATION + " TEXT NOT NULL," +
            " " + Contract.CURRENCY_NAME + " TEXT NOT NULL," +
            " " + Contract.CURRENCY_VALUE + " REAL NOT NULL," +
            " " + Contract.CURRENCY_ID_BACKEND + " INTEGER NOT NULL DEFAULT 0);";

    public static final String SQLDDLDELETED = "CREATE TABLE " + Contract.TABLE_DELETED +
            " (" + Contract.CURRENCY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + Contract.CURRENCY_ID_BACKEND + " INTEGER NOT NULL);";

    public static final String SQLDDLUPDATED = "CREATE TABLE " + Contract.TABLE_UPDATED +
            " (" + Contract.CURRENCY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + Contract.CURRENCY_ABBREVIATION + " TEXT NOT NULL," +
            " " + Contract.CURRENCY_NAME + " TEXT NOT NULL," +
            " " + Contract.CURRENCY_VALUE + " REAL NOT NULL," +
            " " + Contract.CURRENCY_ID_BACKEND + " INTEGER NOT NULL DEFAULT 0);";


    /**
     * @param context
     */
    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * onCreate
     * When the db doesn't exist
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_CURRENCY);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_UPDATED);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_DELETED);
        db.execSQL(SQLDDLCURRENCY);
        db.execSQL(SQLDDLDELETED);
        db.execSQL(SQLDDLUPDATED);


        Log.d("DEBUG", "Ok, DB CREATED!");
    }

    /**
     * onUpgrade
     * Auto upgrades when a new version is out
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SqLiteHelper", "Upgrading from version " + oldVersion
                + " to " + newVersion + ". Data will be wiped.");

        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_CURRENCY);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_UPDATED);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_DELETED);
        onCreate(db);

        Log.d("DEBUG", "Ok, DB RECREATED!");
    }
}
