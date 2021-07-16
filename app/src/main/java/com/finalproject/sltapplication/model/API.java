package com.finalproject.sltapplication.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface API {

    @GET("/users/{user}/repos")
    Call<List<Clothes>> getClothes();

}
