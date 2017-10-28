package com.example.gursifath.imageuploadretrofitlibrary;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by gursifath on 27/10/17.
 */

public interface getApiService {
        @GET("/test/{image}")
        Call<String> listRepos(
                @Query("image") String image
        );

}
