package es.ithrek.syncadaptercurrencies.backend;

import java.io.IOException;
import java.util.List;

import es.ithrek.syncadaptercurrencies.models.Currency;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mikel on 12/02/17.
 */

public class CurrencyManager {
    private static final String URL = "http://192.168.1.197:8080"; //replace with real URL

    private CurrencyApiClient currencyApiClient;

    /**
     * constructor, inits currencyApiClient
     */
    public CurrencyManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        currencyApiClient = retrofit.create(CurrencyApiClient.class);
    }

    /**
     * uses retrofit API client to get currencies
     */
    public List<Currency> getCurrencies() {
        Call<List<Currency>> currenciesApiCall =
                currencyApiClient.currencies();
        List<Currency> currencies = null;

        try {
            currencies = currenciesApiCall.execute().body();

        } catch (IOException e) {
            System.err.println("Error calling currencies API");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error " + e.getMessage());
            e.printStackTrace();
        }

        return currencies;
    }


    /**
     * uses retrofit API client to get one currency by id
     *
     * @param id
     * @return
     */
    public Currency getCurrency(Integer id) {
        Call<Currency> currencyApiCall = currencyApiClient.currency(id);
        Currency currency = null;

        try {
            currency = currencyApiCall.execute().body();
        } catch (IOException e) {
            System.err.println("Error calling currency API");
            e.printStackTrace();
        }

        return currency;
    }


    //TODO implement in backend

    /**
     * uses retrofit API client to get last currencies by an id
     *
     * @param id
     * @return
     */
    public List<Currency> getLastCurrencies(Integer id) {
        Call<List<Currency>> currencyApiCall = currencyApiClient.lastCurrencies(id);
        List<Currency> currencies = null;

        try {
            currencies = currencyApiCall.execute().body();
        } catch (IOException e) {
            System.err.println("Error calling last currencies API");
            e.printStackTrace();
        }

        return currencies;
    }

    /**
     * uses retrofit API client to create a new currency
     *
     * @param currency
     * @return
     */
    public int createCurrency(Currency currency) {
        Call<Integer> currencyApiCall = currencyApiClient.create(currency);
        Integer id = null;

        try {
            id = currencyApiCall.execute().body();
        } catch (IOException e) {
            System.err.println("Error calling currency API");
            e.printStackTrace();
        }

        return id.intValue();
    }

    /**
     * uses retrofit API client to update a currency
     *
     * @param currency
     * @return
     */
    public boolean updateCurrency(Currency currency, Integer id) {
        Call<Void> currencyApiCall = currencyApiClient.update(currency, id);
        boolean result = false;

        try {
            result = currencyApiCall.execute().isSuccessful();
        } catch (IOException e) {
            System.err.println("Error calling currency API");
            e.printStackTrace();
        }

        return result;
    }

    /**
     * uses retrofit API client to delete currency by id
     *
     * @param id
     * @return
     */
    public boolean deleteCurrency(Integer id) {
        Call<Void> currencyApiCall = currencyApiClient.delete(id);
        boolean result = false;

        try {
            result = currencyApiCall.execute().isSuccessful();
        } catch (IOException e) {
            System.err.println("Error calling currency API");
            e.printStackTrace();
        }

        return result;
    }
}
