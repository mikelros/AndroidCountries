package es.ithrek.syncadaptercurrencies.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import es.ithrek.syncadaptercurrencies.Contract;
import es.ithrek.syncadaptercurrencies.models.Currency;

/**
 * Intermediary class between the Activity and the DB.
 * CRUD operations will be located here.
 * <p>
 * Created by Mikel on 11/02/17.
 */

public class DbAdapter {

    private SQLiteDatabase db;

    private SQLiteHelper dbHelper;

    private final Context context;

    /**
     * @param context
     */
    public DbAdapter(Context context) {
        this.context = context;
    }


    /**
     * SQLiteHelper opens the connection (creating the db if it doesn't exist).
     *
     * @return SQLiteDatabase object
     * @throws SQLException
     */
    public SQLiteDatabase open() throws SQLException {
        dbHelper = new SQLiteHelper(context);

        db = dbHelper.getWritableDatabase();

        Log.d("DEBUG", "DB received: " + db.toString());

        return db;
    }

    /**
     * Closes the connection to the db
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Inserts a row given the currency
     *
     * @param currency
     * @return row ID
     */
    public long insertCurrency(Currency currency) {
        ContentValues row = new ContentValues();
        Log.d("DEBUG", "DbAdapter> Insert: " + currency.toString());

        row.put(Contract.CURRENCY_NAME, currency.getName());
        //not sure that this id belongs here..
        //row.put(Contract.CURRENCY_ID, currency.getId());
        row.put(Contract.CURRENCY_ABBREVIATION, currency.getAbbreviation());
        row.put(Contract.CURRENCY_VALUE, currency.getValue());
        row.put(Contract.CURRENCY_ID_BACKEND, currency.getId_backend());

        return db.insert(Contract.TABLE_CURRENCY, null, row);
    }

    /**
     * Removes the currency with the specified id
     *
     * @param id
     * @return # of modified rows
     */
    public int deleteCurrency(long id) {
        addToDeleted(id);
        return db.delete(Contract.TABLE_CURRENCY, Contract.CURRENCY_ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * Removes all rows from deleted
     *
     * @return # of modified rows
     */
    public int deleteDeleted() {
        return db.delete(Contract.TABLE_DELETED, null, null);
    }

    private void addToDeleted(long id) {
        Cursor cursor = db.query(true, Contract.TABLE_CURRENCY, new String[]{Contract.CURRENCY_ID_BACKEND}, Contract.CURRENCY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        int id_backend = cursor.getInt(cursor.getColumnIndex(Contract.CURRENCY_ID_BACKEND));

        ContentValues row = new ContentValues();
        row.put(Contract.CURRENCY_ID_BACKEND, id_backend);

        db.insert(Contract.TABLE_DELETED, null, row);
    }

    public Cursor getDeleted() {
        return db.query(Contract.TABLE_DELETED, new String[]{Contract.CURRENCY_ID_BACKEND}, null, null, null, null, null);
    }

    public Cursor getUpdated() {
        return db.query(Contract.TABLE_UPDATED, new String[]{Contract.CURRENCY_ID, Contract.CURRENCY_NAME, Contract.CURRENCY_VALUE, Contract.CURRENCY_ABBREVIATION, Contract.CURRENCY_ID_BACKEND}, null, null, null, null, null);
    }

    /**
     * Gets all rows from currency
     *
     * @return Cursor
     */
    public Cursor getCurrencies() {
        return db.query(Contract.TABLE_CURRENCY, new String[]{Contract.CURRENCY_ID, Contract.CURRENCY_NAME, Contract.CURRENCY_VALUE, Contract.CURRENCY_ABBREVIATION, Contract.CURRENCY_ID_BACKEND}, null, null, null, null, null);
    }

    /**
     * Gets the specified currency by its id
     *
     * @param id
     * @return Cursor
     * @throws SQLException
     */
    public Cursor getCurrency(long id) throws SQLException {
        Cursor row = db.query(true, Contract.TABLE_CURRENCY, new String[]{Contract.CURRENCY_ID, Contract.CURRENCY_NAME, Contract.CURRENCY_VALUE, Contract.CURRENCY_ABBREVIATION, Contract.CURRENCY_ID_BACKEND},
                Contract.CURRENCY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }

    /**
     * Gets the last local row
     *
     * @return último id local
     * @throws SQLException
     */
    public Cursor getLastLocalRow() throws SQLException {
        Cursor row = db.query(true, Contract.TABLE_CURRENCY, new String[]{Contract.CURRENCY_ID, Contract.CURRENCY_NAME, Contract.CURRENCY_VALUE, Contract.CURRENCY_ABBREVIATION, Contract.CURRENCY_ID_BACKEND},
                Contract.CURRENCY_ID_BACKEND + " = 0", null, null, null, null, null); // limit 1 ???

        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }

    /**
     * Gets the last row downloaded by the server
     *
     * @return last backend id
     * @throws SQLException
     */
    public Cursor getLastBackendRow() throws SQLException {
        Cursor row = db.query(true, Contract.TABLE_CURRENCY, new String[]{Contract.CURRENCY_ID, Contract.CURRENCY_NAME, Contract.CURRENCY_VALUE, Contract.CURRENCY_ABBREVIATION, Contract.CURRENCY_ID_BACKEND},
                null, null, null, null, Contract.CURRENCY_ID_BACKEND + " DESC", " 1");

        if (row != null) {
            row.moveToFirst();
        }

        return row;
    }

    /**
     * Updates the currency info given its id
     *
     * @param id
     * @param currency
     * @return int # of modified rows
     */
    public int updateCurrency(long id, Currency currency) {
        addToUpdated(id, currency);

        ContentValues row = new ContentValues();

        row.put(Contract.CURRENCY_NAME, currency.getName());
        row.put(Contract.CURRENCY_ID, id);
        row.put(Contract.CURRENCY_ABBREVIATION, currency.getAbbreviation());
        row.put(Contract.CURRENCY_VALUE, currency.getValue());
        row.put(Contract.CURRENCY_ID_BACKEND, currency.getId_backend());

        return db.update(Contract.TABLE_CURRENCY, row, Contract.CURRENCY_ID + "=?", new String[]{String.valueOf(id)});
    }

    private long addToUpdated(long id, Currency currency) {
        ContentValues row = new ContentValues();
        row.put(Contract.CURRENCY_NAME, currency.getName());
        row.put(Contract.CURRENCY_ID, id);
        row.put(Contract.CURRENCY_ABBREVIATION, currency.getAbbreviation());
        row.put(Contract.CURRENCY_VALUE, currency.getValue());
        row.put(Contract.CURRENCY_ID_BACKEND, currency.getId_backend());

        return db.insert(Contract.TABLE_UPDATED, null, row);
    }

    public int deleteUpdated() {
        return db.delete(Contract.TABLE_UPDATED, null, null);
    }


}
