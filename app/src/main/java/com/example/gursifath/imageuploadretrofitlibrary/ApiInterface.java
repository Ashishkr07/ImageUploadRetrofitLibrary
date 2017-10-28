package com.example.gursifath.imageuploadretrofitlibrary;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by gursifath on 27/10/17.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("/test")
    Call<Answer> getUser(@Field("images") String images,
                             @Field("type") String type);

}
