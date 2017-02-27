package es.ithrek.syncadaptercurrencies;

/**
 * Created by Mikel on 27/02/17.
 */

public class Contract {
    public final static String TABLE_CURRENCY = "currency";
    public final static String CURRENCY_ID = "_id";
    public final static String CURRENCY_ABBREVIATION = "abbreviation";
    public final static String CURRENCY_NAME = "name";
    public final static String CURRENCY_VALUE = "value";
    public final static String CURRENCY_ID_BACKEND = "id_backend";

    public final static String TABLE_DELETED = "deleted";
    public final static String TABLE_UPDATED = "updated";

    public final static String CONTENT_URI = "content://es.ithrek.syncadaptercurrencies.sqlprovider";

    public final static String AUTHORITY = "es.ithrek.syncadaptercurrencies.sqlprovider";
    public final static String PATH_ALL_CURRENCIES = "currencies";
    public final static String PATH_ONE_CURRENCY = "currency/id";
    public final static String PATH_LAST_BACKEND = "currencies/last/backend";
    public final static String PATH_LAST_LOCAL = "currencies/last/local";
    public final static String PATH_ALL_DELETED = "deleted";
    public final static String PATH_DELETE_DELETED = "delete/deleted";
    public final static String PATH_DELETE_CURRENCY = "delete/currencies";
    public final static String PATH_DELETE_UPDATED = "delete/updated";
    public final static String PATH_ALL_UPDATED = "updated";

    public static final String URL = "http://192.168.1.197:8080"; //replace with real URL


}