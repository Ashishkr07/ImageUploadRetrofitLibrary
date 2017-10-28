package com.example.gursifath.imageuploadretrofitlibrary;

import retrofit2.http.POST;

/**
 * Created by gursifath on 28/10/17.
 */

public class Answer {

    private float score;

    public Answer(float score) {
        this.score = score;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
