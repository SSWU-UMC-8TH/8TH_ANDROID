package com.example.flo

import retrofit2.Call
import retrofit2.http.*

interface AuthRetrofitInterface {
    @POST("/join")
    fun signUp(@Body user: User): Call<AuthResponse>

    @POST("/login")
    fun login(@Body user: loginUser): Call<AuthResponse>
}