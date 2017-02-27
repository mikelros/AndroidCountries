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

import es.ithrek.syncadaptercurrencies.Contract;
import es.ithrek.syncadaptercurrencies.backend.CurrencyManager;
import es.ithrek.syncadaptercurrencies.models.Currency;

/**
 * Created by Mikel on 11/02/17.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final AccountManager mAccountManager;
    private ContentResolver contentResolver;
    private CurrencyManager currencyManager;

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
                Uri.parse(Contract.CONTENT_URI + "/" + Contract.PATH_ALL_DELETED),
                new String[]{Contract.CURRENCY_ID_BACKEND},
                "",
                new String[]{""},
                "");

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues contentValues = null;
            while (cursor.isAfterLast() == false) {
                currencyManager.deleteCurrency(cursor.getInt(cursor.getColumnIndex(Contract.CURRENCY_ID_BACKEND)));
                cursor.moveToNext();
            }
        }
    }

    private void deleteAllFromDeleted(ContentProviderClient provider) throws RemoteException {
        int rows = provider.delete(
                Uri.parse(Contract.CONTENT_URI + "/" + Contract.PATH_DELETE_DELETED),
                null,
                null
        );

        Log.d("DEBUG", "Deleted from deleted " + rows + " # of rows");
    }


    private void updateLocalOnBackend(Cursor cursor, ContentProviderClient provider) throws RemoteException {
        cursor = provider.query(
                Uri.parse(Contract.CONTENT_URI + "/" + Contract.PATH_ALL_UPDATED),
                new String[]{Contract.CURRENCY_ID, Contract.CURRENCY_NAME, Contract.CURRENCY_ABBREVIATION, Contract.CURRENCY_VALUE, Contract.CURRENCY_ID_BACKEND},
                "",
                new String[]{""},
                "");

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues contentValues = null;
            while (cursor.isAfterLast() == false) {
                Currency currency = new Currency();
                currency.setId(cursor.getInt(cursor.getColumnIndex(Contract.CURRENCY_ID)));
                currency.setName(cursor.getString(cursor.getColumnIndex(Contract.CURRENCY_NAME)));
                currency.setValue(cursor.getInt(cursor.getColumnIndex(Contract.CURRENCY_VALUE)));
                currency.setAbbreviation(cursor.getString(cursor.getColumnIndex(Contract.CURRENCY_ABBREVIATION)));
                currency.setId_backend(cursor.getInt(cursor.getColumnIndex(Contract.CURRENCY_ID_BACKEND)));

                currencyManager.updateCurrency(currency, currency.getId_backend());
                cursor.moveToNext();
            }
        }
    }

    private void deleteAllFromUpdated(ContentProviderClient provider) throws RemoteException {
        int rows = provider.delete(
                Uri.parse(Contract.CONTENT_URI + "/" + Contract.PATH_DELETE_UPDATED),
                null,
                null
        );
    }

    private void updateFromBackend(Cursor cursor, ContentProviderClient provider) throws RemoteException {
        int lastBackendId = 0;

        // Get Last backend_id locally
        cursor = provider.query(
                Uri.parse(Contract.CONTENT_URI + "/" + Contract.PATH_LAST_BACKEND),
                new String[]{Contract.CURRENCY_ID, Contract.CURRENCY_NAME, Contract.CURRENCY_ABBREVIATION, Contract.CURRENCY_VALUE, Contract.CURRENCY_ID_BACKEND},
                "",                        // The columns to return for each row
                new String[]{""},                     // Selection criteria
                "");

        if (cursor.getCount() > 0) {
            lastBackendId = cursor.getInt(cursor.getColumnIndex(Contract.CURRENCY_ID_BACKEND));
            Log.d("DEBUG", "id_backend:" + cursor.getInt(cursor.getColumnIndex(Contract.CURRENCY_ID_BACKEND)));
        }
        Log.d("DEBUG'", "Last backend Id: " + lastBackendId);

        // Get currencies from the remote server
        List<Currency> currencies = currencyManager.getLastCurrencies(lastBackendId);
        Log.d("DEBUG", currencies.toString());

        for (Currency currency : currencies) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.CURRENCY_NAME, currency.getName());
            contentValues.put(Contract.CURRENCY_ABBREVIATION, currency.getAbbreviation());
            contentValues.put(Contract.CURRENCY_VALUE, currency.getValue());
            contentValues.put(Contract.CURRENCY_ID_BACKEND, currency.getId());

            // We finally make the request to the content provider
            Uri resultUri = provider.insert(
                    Uri.parse(Contract.CONTENT_URI),   // The content URI
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
                Uri.parse(Contract.CONTENT_URI + "/" + Contract.PATH_LAST_LOCAL),   // The content URI of the words table
                new String[]{Contract.CURRENCY_ID, Contract.CURRENCY_NAME, Contract.CURRENCY_ABBREVIATION, Contract.CURRENCY_VALUE, Contract.CURRENCY_ID_BACKEND},
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
                currency.setName(cursor.getString(cursor.getColumnIndex(Contract.CURRENCY_NAME)));
                currency.setValue(cursor.getInt(cursor.getColumnIndex(Contract.CURRENCY_VALUE)));
                currency.setAbbreviation(cursor.getString(cursor.getColumnIndex(Contract.CURRENCY_ABBREVIATION)));

                Log.d("SYNCADAPTER", currency.toString());
                Log.d("SYNCADAPTER", "cursor: " + cursor.getString(3));

                int id = currencyManager.createCurrency(currency);
                contentValues = new ContentValues();
                contentValues.put(Contract.CURRENCY_ID, cursor.getInt(cursor.getColumnIndex(Contract.CURRENCY_ID)));
                contentValues.put(Contract.CURRENCY_NAME, currency.getName());
                contentValues.put(Contract.CURRENCY_ABBREVIATION, currency.getAbbreviation());
                contentValues.put(Contract.CURRENCY_VALUE, currency.getValue());
                contentValues.put(Contract.CURRENCY_ID_BACKEND, id);
                Log.d("DEBUG", "Sent data to backend: " + currency.getName());

                cursor.moveToNext();
            }
            // We finally make the request to the content provider
            // To mark local records as sent.
            // TODO, we should wait for the ACK from server
            int total = provider.update(
                    Uri.parse(Contract.CONTENT_URI),   // The content URI
                    contentValues, "", new String[]{""}
            );
            Log.d("DEBUG", "Locally mark as sent " + total);
        }
    }

}
