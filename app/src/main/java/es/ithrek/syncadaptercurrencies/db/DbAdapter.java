package es.ithrek.syncadaptercurrencies.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

        row.put("name", currency.getName());
        row.put("_id", currency.getId());
        row.put("abbreviation", currency.getAbbreviation());
        row.put("value", currency.getValue());
        row.put("id_backend", currency.getId_backend());

        return db.insert("currency", null, row);
    }

    /**
     * Removes the currency with the specified id
     *
     * @param id
     * @return # of modified rows
     */
    public int deleteCurrency(long id) {
        addToDeleted(id);
        return db.delete("currency", "_id=?", new String[]{String.valueOf(id)});
    }

    /**
     * Removes all rows from deleted
     *
     * @return # of modified rows
     */
    public int deleteDeleted() {
        return db.delete("deleted", null, null);
    }

    private void addToDeleted(long id) {
        Cursor cursor = db.query(true, "currency", new String[]{"id_backend"}, "_id=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        int id_backend = cursor.getInt(cursor.getColumnIndex("id_backend"));

        ContentValues row = new ContentValues();
        row.put("id_backend", id_backend);

        db.insert("deleted", null, row);
    }

    public Cursor getDeleted() {
        return db.query("deleted", new String[]{"id_backend"}, null, null, null, null, null);
    }

    public Cursor getUpdated() {
        return db.query("updated", new String[]{"_id", "name", "value", "abbreviation", "id_backend"}, null, null, null, null, null);
    }

    /**
     * Gets all rows from currency
     *
     * @return Cursor
     */
    public Cursor getCurrencies() {
        return db.query("currency", new String[]{"_id", "name", "value", "abbreviation", "id_backend"}, null, null, null, null, null);
    }

    /**
     * Gets the specified currency by its id
     *
     * @param id
     * @return Cursor
     * @throws SQLException
     */
    public Cursor getCurrency(long id) throws SQLException {
        Cursor row = db.query(true, "currency", new String[]{"_id", "name", "value", "abbreviation", "id_backend"},
                "_id =?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }

    /**
     * Gets the last local row
     *
     * @return último id recibido del servidor ?????????????????????????????????????????????????????????????
     * @throws SQLException
     */
    public Cursor getLastLocalRow() throws SQLException {
        Cursor row = db.query(true, "currency", new String[]{"_id", "name", "abbreviation", "value", "id_backend"},
                "id_backend = 0", null, null, null, null, null); // limit 1 ???

        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }


    public int setCurrencyBackendReceived() throws SQLException {
        ContentValues row = new ContentValues();

        row.put("id_backend", -1);

        return db.update("currency", row, "id_backend=0", null);
    }

    /**
     * Gets the last row downloaded by the server
     *
     * @return last backend id
     * @throws SQLException
     */
    public Cursor getLastBackendRow() throws SQLException {
        Cursor row = db.query(true, "currency", new String[]{"_id", "name", "abbreviation", "value", "id_backend"},
                null, null, null, null, "id_backend DESC", " 1");

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

        row.put("name", currency.getName());
        row.put("_id", id);
        row.put("abbreviation", currency.getAbbreviation());
        row.put("value", currency.getValue());
        row.put("id_backend", currency.getId_backend());

        return db.update("currency", row, "_id=?", new String[]{String.valueOf(id)});
    }

    private long addToUpdated(long id, Currency currency) {
        ContentValues row = new ContentValues();
        row.put("_id", id);
        row.put("name", currency.getName());
        row.put("value", currency.getValue());
        row.put("abbreviation", currency.getAbbreviation());
        row.put("id_backend", currency.getId_backend());


        return db.insert("updated", null, row);
    }

    public int deleteUpdated() {
        return db.delete("updated", null, null);
    }



}
