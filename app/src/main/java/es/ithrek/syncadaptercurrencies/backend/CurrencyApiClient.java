package es.ithrek.syncadaptercurrencies.backend;

import java.util.List;

import es.ithrek.syncadaptercurrencies.models.Currency;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Mikel on 12/02/17.
 */

public interface CurrencyApiClient {

    @Headers("Accept: application/json")
    @GET("/springPractica/api/currencies")
    Call<List<Currency>> currencies();

    @Headers("Accept: application/json")
    @GET("/springPractica/api/currencies/{id}")
    Call<Currency> currency(
            @Path("id") Integer id);

    @Headers("Accept: application/json")
    @GET("/springPractica/api/currencies/last/{id}")
    Call<List<Currency>> lastCurrencies(
            @Path("id") Integer id);

    @Headers("Accept: application/json")
    @POST("/springPractica/api/currencies/new")
    Call<Integer> create(@Body Currency currency);

    @PUT("/springPractica/api/currencies/update/{id}")
    Call<Void> update(@Body Currency currency, @Path("id") Integer id);

    @DELETE("/springPractica/api/currencies/delete/{id}")
    Call<Void> delete(
            @Path("id") Integer id);
}
