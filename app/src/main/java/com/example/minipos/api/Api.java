package com.example.minipos.api;

import com.example.minipos.models.AllDataResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    //add product with picture
    @Multipart
    @POST("add_product")
    Call<AllDataResponse> addProductWithPic(
            @Part MultipartBody.Part file,
            @Part("supplier_id") int supplier_id,
            @Part("category_id") int category_id,
            @Part("product_name") String product_name,
            @Part("product_code") String product_code,
            @Part("product_cost") String product_cost,
            @Part("product_price") String product_price,
            @Part("product_quantity") String product_quantity,
            @Part("product_threshold") String product_threshold,
            @Part("product_description") String product_description

    );

    //edit product without picture
    @FormUrlEncoded
    @POST("add_product")
    Call<AllDataResponse> addProduct(
            @Field("supplier_id") int supplier_id,
            @Field("category_id") int category_id,
            @Field("product_name") String product_name,
            @Field("product_code") String product_code,
            @Field("product_cost") String product_cost,
            @Field("product_price") String product_price,
            @Field("product_quantity") String product_quantity,
            @Field("product_threshold") String product_threshold,
            @Field("product_description") String product_description

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

    //edit product with picture
    @Multipart
    @PUT("update_product")
    Call<AllDataResponse> editProductWithPic(
            @Part MultipartBody.Part file,
            @Part("product_id") int product_id,
            @Part("supplier_id") int supplier_id,
            @Part("category_id") int category_id,
            @Part("product_name") String product_name,
            @Part("product_code") String product_code,
            @Part("product_cost") String product_cost,
            @Part("product_price") String product_price,
            @Part("product_quantity") String product_quantity,
            @Part("product_threshold") String product_threshold,
            @Part("product_description") String product_description

    );

    //edit product without picture
    @FormUrlEncoded
    @PUT("update_product")
    Call<AllDataResponse> editProduct(
            @Field("product_id") int product_id,
            @Field("supplier_id") int supplier_id,
            @Field("category_id") int category_id,
            @Field("product_name") String product_name,
            @Field("product_code") String product_code,
            @Field("product_cost") String product_cost,
            @Field("product_price") String product_price,
            @Field("product_quantity") String product_quantity,
            @Field("product_threshold") String product_threshold,
            @Field("product_description") String product_description

    );

    //change product picture
    @Multipart
    @POST("update_product_image")
    Call<AllDataResponse> changeProductImage(
            @Part MultipartBody.Part file,
            @Part("product_id") int product_id
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

    //delete supplier
    @DELETE("delete_product/{product_id}")
    Call<AllDataResponse> deleteProduct(
            @Path("product_id") int product_id
    );
}
