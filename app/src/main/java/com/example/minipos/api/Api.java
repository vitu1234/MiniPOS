package com.example.minipos.api;

import com.example.minipos.models.AllDataResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Api {

    //POST METHODS

    //add product category to server
    @FormUrlEncoded
    @POST("add_category")
    Call<AllDataResponse> add_category(
            @Field("category_name") String category_name,
            @Field("notes") String notes
    );


    //GET METHODS

    //get all data
    @GET("all_data")
    Call<AllDataResponse> getAllData(
    );


    //PUT/UPDATE METHODS

    //update product category to server
    @FormUrlEncoded
    @PUT("update_category")
    Call<AllDataResponse> update_category(
            @Field("category_id") int category_id,
            @Field("category_name") String category_name,
            @Field("notes") String notes
    );

    //DELETE METHODS

    //delete category
    @DELETE("delete_category/{category_id}")
    Call<AllDataResponse> deleteDeparture(
            @Path("category_id") int category_id
    );
}
