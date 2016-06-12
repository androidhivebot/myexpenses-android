package br.com.jonathanzanella.myexpenses.server;

import java.util.List;

import br.com.jonathanzanella.myexpenses.models.Source;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by jzanella on 6/5/16.
 */
public interface SourceInterface {
    @GET("sources")
    Observable<List<Source>> index(@Query("last-updated-at") long lastUpdatedAt);
    @POST("sources")
    Observable<List<Source>> create(@Body Source source);
    @PUT("sources/{id}")
    Observable<List<Source>> update(@Path("id") String serverId, @Body Source source);
}
