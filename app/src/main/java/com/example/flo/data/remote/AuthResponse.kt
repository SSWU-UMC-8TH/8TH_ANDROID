package com.example.flo.data.remote

import com.google.gson.annotations.SerializedName


data class AuthResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Result?
)

data class Result(
    val memberId: Int,
    val accessToken: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class AuthTestResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: String? = null
)

