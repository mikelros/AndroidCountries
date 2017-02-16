package es.ithrek.syncadaptercurrencies.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import es.ithrek.syncadaptercurrencies.models.Currency;

/**
 * It extends ContentProvider class providing a common way
 * to manage data with a CRUD-like methods set.
 * <p>
 * Created by Mikel on 11/02/17.
 */

public class DatabaseContentProvider extends ContentProvider {
    // We set uriMatcher to get params passed to URIs.
    // So we can give different values depending on those params
    private UriMatcher uriMatcher;
    // Our data
    private MatrixCursor mCursor;
    private DbAdapter dbAdapter;

    /**
     * default constructor.
     */
    public DatabaseContentProvider() {

    }

    /**
     * called when provider is started, so we use it to initialize data.
     */
    @Override
    public boolean onCreate() {
        Log.d("DEBUG", "ContentProvider> onCreate, init data.");
        dbAdapter = new DbAdapter(getContext());
        dbAdapter.open();

        initUris();
        return true;
    }

    /**
     * init content provider Uris
     * we set some kind of uri patterns to route them to different queries
     */
    private void initUris() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // This will match: content://es.ithrek.syncadaptercurrencies.sqlprovider/currencies
        uriMatcher.addURI("es.ithrek.syncadaptercurrencies.sqlprovider", "currencies/", 1);

        // This will match: content://es.ithrek.syncadaptercurrencies.sqlprovider/currency/2
        uriMatcher.addURI("es.ithrek.syncadaptercurrencies.sqlprovider", "currency/*/", 2);

        // the last one from the backend
        // This will match: content://es.ithrek.syncadaptercurrencies.sqlprovider/currencies/last/backend
        uriMatcher.addURI("es.ithrek.syncadaptercurrencies.sqlprovider", "currencies/last/backend", 3);

        // This will match: content://es.ithrek.syncadaptercurrencies.sqlprovider./currencies/last/local
        uriMatcher.addURI("es.ithrek.syncadaptercurrencies.sqlprovider", "currencies/last/local", 4);

    }


    /**
     * we query the database, depending on uriMatcher we can execute
     * different queries.
     * The parameters of the query are the same of a SQLite helper query.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        ContentResolver cr = getContext().getContentResolver();
        Cursor c;

        Log.d("DEBUG", "ContentProvider> query " + uri + " match:" + uriMatcher.match(uri));
        switch (uriMatcher.match(uri)) {
            case 1:
                Log.d("PELLODEBUG", "query to 1. ");
                c = dbAdapter.getCurrencies();
                c.setNotificationUri(cr, uri);
                return c;
            case 2:
                Log.d("PELLODEBUG", "query to 2. " + uri.getLastPathSegment());
                //return dbAdapter.getCurrency();
            case 3:
                Log.d("PELLODEBUG", "query to 3. " + uri.getLastPathSegment());
                c = dbAdapter.getLastBackendRow();
                c.setNotificationUri(cr, uri);
                return c;
            case 4:
                Log.d("PELLODEBUG", "query to 4. " + uri.getLastPathSegment());
                c = dbAdapter.getLastLocalRow();
                c.setNotificationUri(cr, uri);
                return c;

            default:
                break;
        }

        return mCursor;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d("DEBUG", "CP> " + uri);
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        Log.d("DEBUG", "CP> " + uri);
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d("DEBUG", "CP> insert " + uri);

        Currency currency = new Currency();
        currency.setId(values.getAsInteger("id"));
        currency.setName(values.getAsString("name"));
        currency.setAbbreviation(values.getAsString("abbreviation"));
        currency.setValue(values.getAsInteger("value"));
        currency.setId_backend(values.getAsInteger("id_backend"));

        dbAdapter.insertCurrency(currency);
        getContext().getContentResolver().notifyChange(uri, null);
        Uri resultUri = Uri.parse("content://es.ithrek.syncadaptercurrencies.sqlprovider/1");
        return resultUri;

    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d("PELLODEBUG", "CP> " + uri);
        Currency currency = new Currency();
        currency.setId(values.getAsInteger("_id"));
        currency.setName(values.getAsString("name"));
        currency.setAbbreviation(values.getAsString("abbreviation"));
        currency.setValue(values.getAsInteger("value"));
        currency.setId_backend(values.getAsInteger("id_backend"));
        getContext().getContentResolver().notifyChange(uri, null);
        return dbAdapter.updateCurrency(currency.getId(), currency);

    }
}
