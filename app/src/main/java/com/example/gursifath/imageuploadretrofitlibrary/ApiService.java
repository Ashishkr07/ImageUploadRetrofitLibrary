package com.example.gursifath.imageuploadretrofitlibrary;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by gursifath on 27/10/17.
 */

public interface ApiService {
//    @Multipart
//    @POST("/photos")
//    Call<ServerResponse> uploadPhoto(@Part MultipartBody.Part photo, @Part("photo") RequestBody name);

    @GET("/test/{image}")
     Call<JSONObject> listRepos(
            @Query("image") String image
    );
}
