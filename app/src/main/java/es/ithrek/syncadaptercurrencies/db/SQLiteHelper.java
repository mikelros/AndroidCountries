package es.ithrek.syncadaptercurrencies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mikel on 11/02/17.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "currencies.db";
    public static final int DB_VERSION = 1;
    public static final String SQLDDL = "CREATE TABLE currency" +
            " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " abbreviation TEXT NOT NULL," +
            " name TEXT NOT NULL," +
            " value REAL NOT NULL," +
            " id_backend INTEGER NOT NULL DEFAULT 0," +
            " is_read INTEGER NOT NULL DEFAULT 0);";


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
        db.execSQL("DROP TABLE IF EXISTS currency");
        db.execSQL(SQLDDL);

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

        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);

        Log.d("DEBUG", "Ok, DB RECREATED!");
    }
}
