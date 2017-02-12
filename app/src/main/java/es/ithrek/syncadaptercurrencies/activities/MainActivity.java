package es.ithrek.syncadaptercurrencies.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SyncRequest;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import es.ithrek.syncadaptercurrencies.R;
import es.ithrek.syncadaptercurrencies.auth.Authenticator;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private EditText editTextNew;
    private String contentUri = "content://es.ithrek.syncadaptercurrencies.sqlprovider";


    // This is the Adapter being used to display the list's data.
    SimpleCursorAdapter mAdapter;

    // If non-null, this is the current filter the user has provided.
    String mCurFilter;

    private ListView listView;
    private ContentResolver contentResolver;

    private Account account;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        editTextNew = (EditText) findViewById(R.id.editTextNew);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, null,
                new String[]{"_id", "name"},
                new int[]{android.R.id.text1, android.R.id.text2}, 0);

        listView.setAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);

        // Sync Adapter
        // account = new Account(DummyAuthenticator.ACCOUNT_NAME, DummyAuthenticator.ACCOUNT_TYPE);
        account = CreateSyncAccount(this);
        String authority = "es.ithrek.syncadaptercurrencies.sqlprovider";

        // Simple option, will handle everything smartly
        contentResolver = getContentResolver();
        Bundle bundle = new Bundle();
        contentResolver.requestSync(account, authority, bundle);

        // With intervals
        //long interval = 24*60*60; // 12 hours
        //contentResolver.addPeriodicSync(account, authority, bundle, 5);

        // Listener for the swipe down gesture
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("PELLODEBUG", "Update data");
                        Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_LONG).show();
                        MainActivity.this.syncNow(null);
                    }
                }
        );
    }

    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri;

        baseUri = Uri.parse(this.contentUri + "/currencies");

        Log.d("PELLODEBUG", "Creating loader");
        return new CursorLoader(this, baseUri,  // The content URI of the words table
                new String[]{"_id", "name"},               // The columns to return for each row
                "",                        // Selection criteria parameters
                new String[]{""},                     // Selection criteria values
                "");                            // The sort order for the returned rows
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(cursor);

        cursor.moveToFirst();
        String data = "";
        while (!cursor.isAfterLast()) {
            data += "\n" + cursor.getString(1);
            cursor.moveToNext();
        }
        Log.d("PELLODEBUG", "Total records: " + cursor.getCount());

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

    /**
     * add a new Task
     *
     * @param view
     */
    public void addTask(View view) {
        Log.d("PELLODEBUG", "New task > button pressed.");
        Uri uri = Uri.parse(contentUri);
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", editTextNew.getText().toString());
        contentValues.put("id_backend", 0);
        contentValues.put("is_read", 0);
        contentValues.put("value", 1);
        contentValues.put("abbreviation", "an");

        // We finally make the request to the content provider
        Uri resultUri = getContentResolver().insert(
                uri,   // The content URI
                contentValues
        );
        Log.d("PELLODEBUG", "Result Uri after insert: " + uri.toString());
        mAdapter.notifyDataSetChanged();
        getLoaderManager().getLoader(0).forceLoad();

    }

    public void syncNow(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); // Performing a sync no matter if it's off
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); // Performing a sync no matter if it's off
        // Simple option, will ahndle everything smartly
        contentResolver = getContentResolver();
        contentResolver.requestSync(account, "es.ithrek.syncadaptercurrencies.sqlprovider", bundle);
        Log.d("PELLODEBUG", "Sync now was pressed");

        // Stop refresh effect
        Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }


}
