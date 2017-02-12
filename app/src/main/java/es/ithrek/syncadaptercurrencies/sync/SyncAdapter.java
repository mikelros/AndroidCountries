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
        int lastLocalId = 0;
        int lastBackendId = 0;
        Cursor cursor = null;

        try {

            /////////////////// UPDATE FROM BACKEND /////////////////////
            // Get Last backend_id locally
            cursor = provider.query(
                    Uri.parse(contentUri + "/currencies/last/backend"),
                    new String[]{"_id", "name", "value", "abbreviation", "id_backend", "is_read"},
                    "",                        // The columns to return for each row
                    new String[]{""},                     // Selection criteria
                    "");

            if (cursor.getCount() > 0) {
                lastBackendId = cursor.getInt(cursor.getColumnIndex("id_backend"));
                Log.d("DEBUG", "backend_id:" + cursor.getInt(cursor.getColumnIndex("id_backend")));
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
                contentValues.put("id_backend", currency.getId()); //currency id of the backend

                // We finally make the request to the content provider
                Uri resultUri = provider.insert(
                        Uri.parse(contentUri),   // The content URI
                        contentValues
                );
                Log.d("DEBUG", "Inserted in local db: " + currency.getName());
            }

            /////////////////// UPDATE FROM LOCAL TO BACKEND
            // get all local record with id_backend = 0
            // send them to backend
            // update local id_backend with -1
            cursor = provider.query(
                    Uri.parse(contentUri + "/currencies/last/local"),   // The content URI of the words table
                    new String[]{"_id", "name", "value", "abbreviation", "id_backend", "is_read"},
                    "",                        // The columns to return for each row
                    new String[]{""},                     // Selection criteria
                    "");
            if (cursor.getCount() > 0) {
                lastLocalId = cursor.getInt(0);
                Log.d("PELLODEBUG", "Last local Id: " + cursor.getString(0));

                // send array of currencies
                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {

                    Currency currency = new Currency();
                    //currency.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    currency.setName(cursor.getString(cursor.getColumnIndex("name")));
                    currency.setValue(cursor.getInt(cursor.getColumnIndex("value")));
                    currency.setAbbreviation(cursor.getString(cursor.getColumnIndex("abbreviation")));

                    Log.d("SYNCADAPTER", currency.toString());
                    Log.d("SYNCADAPTER", "cursor: " + cursor.getString(3));

                    currencyManager.createCurrency(currency);
                    Log.d("DEBUG", "Sent data to backend: " + currency.getName());

                    cursor.moveToNext();
                }
                // We finally make the request to the content provider
                // To mark local records as sent.
                // TODO, we should wait for the ACK from server
                int total = provider.update(
                        Uri.parse(contentUri),   // The content URI
                        null, "", new String[]{""}
                );
                Log.d("DEBUG", "Locally mark as sent " + total);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
