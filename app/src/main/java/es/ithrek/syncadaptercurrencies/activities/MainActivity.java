package es.ithrek.syncadaptercurrencies.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import es.ithrek.syncadaptercurrencies.R;
import es.ithrek.syncadaptercurrencies.auth.Authenticator;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ListView listView;
    private String contentUri = "content://es.ithrek.syncadaptercurrencies.sqlprovider";
    private CustomListAdapter customizedListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ContentResolver contentResolver;
    private Account account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        setupCustomList(null);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);

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

        setFloatingButtonListener();
    }

    private void setupCustomList(Cursor cursor) {
        customizedListAdapter = new CustomListAdapter(this, cursor);

        listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(customizedListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Intent myIntent = new Intent(MainActivity.this, DetailActivity.class);
                //myIntent.putExtra("Meetup", meetups.get(position));
                //startActivity(myIntent);
            }
        });

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri;

        baseUri = Uri.parse(this.contentUri + "/currencies");

        Log.d("PELLODEBUG", "Creating loader");
        return new CursorLoader(this, baseUri,  // The content URI of the words table
                new String[]{"_id", "name", "abbreviation", "value"},               // The columns to return for each row
                "",                        // Selection criteria parameters
                new String[]{""},                     // Selection criteria values
                "");                            // The sort order for the returned rows
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        customizedListAdapter.swapCursor(cursor);

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
        customizedListAdapter.swapCursor(null);
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

    public void setFloatingButtonListener() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(myIntent);
            }
        });
    }
}
