package com.example.gursifath.imageuploadretrofitlibrary;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gursifath on 27/10/17.
 */

public class ServerResponse {

    boolean success;
    @SerializedName("message")
    String message;

    String getMessage() {
        return message;
    }

    boolean getSuccess() {
        return success;
    }
}
