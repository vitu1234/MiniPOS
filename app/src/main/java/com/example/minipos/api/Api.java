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

    //add product supplier to server
    @FormUrlEncoded
    @POST("add_supplier")
    Call<AllDataResponse> add_supplier(
            @Field("fullname") String fullname,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("address") String address,
            @Field("notes") String notes,
            @Field("is_default") int is_default
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

    //update product supplier to server
    @FormUrlEncoded
    @PUT("update_supplier")
    Call<AllDataResponse> update_supplier(
            @Field("supplier_id") int supplier_id,
            @Field("user_id") int user_id,
            @Field("fullname") String fullname,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("address") String address,
            @Field("notes") String notes,
            @Field("is_default") int is_default
    );

    //DELETE METHODS

    //delete category
    @DELETE("delete_category/{category_id}")
    Call<AllDataResponse> deleteCategory(
            @Path("category_id") int category_id
    );

    //delete supplier
    @DELETE("delete_supplier/{supplier_id}")
    Call<AllDataResponse> deleteSupplier(
            @Path("supplier_id") int supplier_id
    );
}
