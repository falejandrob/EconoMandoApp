package com.economando.economandoapp.Interface;

import com.economando.economandoapp.entities.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {
    @FormUrlEncoded
    @POST("login")
    Call<User> login(@Field(value = "email", encoded = true) String username,
                     @Field(value = "password", encoded = true) String password);
    @FormUrlEncoded
    @POST("loginToken")
    Call<User> loginWithToken(@Field(value = "token", encoded = true) String token);

}

