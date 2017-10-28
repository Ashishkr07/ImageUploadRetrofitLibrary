package com.example.gursifath.imageuploadretrofitlibrary;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gursifath on 27/10/17.
 */

public class ApiClient {

    public static final String BASE_URL = "https://flask-new-app.herokuapp.com";
    private static Retrofit retrofit = null;
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
