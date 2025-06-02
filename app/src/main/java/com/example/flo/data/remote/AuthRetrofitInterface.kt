package com.example.flo.data.remote

import com.example.flo.data.entities.User
import com.example.flo.data.entities.loginUser
import retrofit2.Call
import retrofit2.http.*

interface AuthRetrofitInterface {
    @POST("/join")
    fun signUp(@Body user: User): Call<AuthResponse>

    @POST("/login")
    fun login(@Body user: loginUser): Call<AuthResponse>

    @GET("/test")
    fun test(@Header("Authorization") token: String):Call<TestResponse>
}