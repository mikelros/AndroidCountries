package es.ithrek.syncadaptercurrencies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import es.ithrek.syncadaptercurrencies.backend.CurrencyManager;
import es.ithrek.syncadaptercurrencies.models.Currency;

/**
 * Created by Mikel on 11/02/17.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final AccountManager mAccountManager;
    private ContentResolver contentResolver;
    private CurrencyManager currencyManager;
    private String contentUri = "content://es.ithrek.syncadaptercurrencies.sqlprovider";

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        currencyManager = new CurrencyManager();
        mAccountManager = AccountManager.get(context);
        contentResolver = context.getContentResolver();
    }

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mAccountManager = AccountManager.get(context);
        contentResolver = context.getContentResolver();
    }


    //Review this code
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("DEBUG", "SyncAdapter working for: " + account.name);
        Cursor cursor = null;

        try {

            deleteLocalOnBackend(cursor, provider);

            deleteAllFromDeleted(provider);

            updateLocalOnBackend(cursor, provider);

            deleteAllFromUpdated(provider);

            updateFromBackend(cursor, provider);

            addFromLocalToBackend(cursor, provider);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteLocalOnBackend(Cursor cursor, ContentProviderClient provider) throws RemoteException {
        cursor = provider.query(
                Uri.parse(contentUri + "/deleted"),
                new String[]{"id_backend"},
                "",
                new String[]{""},
                "");

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues contentValues = null;
            while (cursor.isAfterLast() == false) {
                currencyManager.deleteCurrency(cursor.getInt(cursor.getColumnIndex("id_backend")));
                cursor.moveToNext();
            }
        }
    }

    private void deleteAllFromDeleted(ContentProviderClient provider) throws RemoteException {
        int rows = provider.delete(
                Uri.parse(contentUri + "/delete/deleted"),
                null,
                null
        );

        Log.d("DEBUG", "Deleted from deleted " + rows + " # of rows");
    }


    private void updateLocalOnBackend(Cursor cursor, ContentProviderClient provider) throws RemoteException {
        cursor = provider.query(
                Uri.parse(contentUri + "/updated"),
                new String[]{"_id", "name", "value", "abbreviation", "id_backend"},
                "",
                new String[]{""},
                "");

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues contentValues = null;
            while (cursor.isAfterLast() == false) {
                Currency currency = new Currency();
                currency.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                currency.setName(cursor.getString(cursor.getColumnIndex("name")));
                currency.setValue(cursor.getInt(cursor.getColumnIndex("value")));
                currency.setAbbreviation(cursor.getString(cursor.getColumnIndex("abbreviation")));
                currency.setId_backend(cursor.getInt(cursor.getColumnIndex("id_backend")));

                currencyManager.updateCurrency(currency, currency.getId_backend());
                cursor.moveToNext();
            }
        }
    }

    private void deleteAllFromUpdated(ContentProviderClient provider) throws RemoteException {
        int rows = provider.delete(
                Uri.parse(contentUri + "/delete/updated"),
                null,
                null
        );
    }

    private void updateFromBackend(Cursor cursor, ContentProviderClient provider) throws RemoteException {
        int lastBackendId = 0;

        // Get Last backend_id locally
        cursor = provider.query(
                Uri.parse(contentUri + "/currencies/last/backend"),
                new String[]{"_id", "name", "value", "abbreviation", "id_backend"},
                "",                        // The columns to return for each row
                new String[]{""},                     // Selection criteria
                "");

        if (cursor.getCount() > 0) {
            lastBackendId = cursor.getInt(cursor.getColumnIndex("id_backend"));
            Log.d("DEBUG", "id_backend:" + cursor.getInt(cursor.getColumnIndex("id_backend")));
        }
        Log.d("DEBUG'", "Last backend Id: " + lastBackendId);

        // Get currencies from the remote server
        List<Currency> currencies = currencyManager.getLastCurrencies(lastBackendId);
        Log.d("DEBUG", currencies.toString());

        for (Currency currency : currencies) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", currency.getName());
            contentValues.put("abbreviation", currency.getAbbreviation());
            contentValues.put("value", currency.getValue());
            contentValues.put("id_backend", currency.getId());

            // We finally make the request to the content provider
            Uri resultUri = provider.insert(
                    Uri.parse(contentUri),   // The content URI
                    contentValues
            );
            Log.d("DEBUG", "Inserted in local db: " + currency.getName());
        }
    }

    private void addFromLocalToBackend(Cursor cursor, ContentProviderClient provider) throws RemoteException {
        int lastLocalId = 0;

        // get all local record with id_backend = 0
        // send them to backend
        cursor = provider.query(
                Uri.parse(contentUri + "/currencies/last/local"),   // The content URI of the words table
                new String[]{"_id", "name", "value", "abbreviation", "id_backend"},
                "",                        // The columns to return for each row
                new String[]{""},                     // Selection criteria
                "");
        if (cursor.getCount() > 0) {
            lastLocalId = cursor.getInt(0);
            Log.d("PELLODEBUG", "Last local Id: " + cursor.getString(0));

            // send array of currencies
            cursor.moveToFirst();
            ContentValues contentValues = null;
            while (cursor.isAfterLast() == false) {

                Currency currency = new Currency();
                //currency.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                currency.setName(cursor.getString(cursor.getColumnIndex("name")));
                currency.setValue(cursor.getInt(cursor.getColumnIndex("value")));
                currency.setAbbreviation(cursor.getString(cursor.getColumnIndex("abbreviation")));

                Log.d("SYNCADAPTER", currency.toString());
                Log.d("SYNCADAPTER", "cursor: " + cursor.getString(3));

                int id = currencyManager.createCurrency(currency);
                contentValues = new ContentValues();
                contentValues.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
                contentValues.put("name", currency.getName());
                contentValues.put("abbreviation", currency.getAbbreviation());
                contentValues.put("value", currency.getValue());
                contentValues.put("id_backend", id);
                Log.d("DEBUG", "Sent data to backend: " + currency.getName());

                cursor.moveToNext();
            }
            // We finally make the request to the content provider
            // To mark local records as sent.
            // TODO, we should wait for the ACK from server
            int total = provider.update(
                    Uri.parse(contentUri),   // The content URI
                    contentValues, "", new String[]{""}
            );
            Log.d("DEBUG", "Locally mark as sent " + total);
        }
    }

}
