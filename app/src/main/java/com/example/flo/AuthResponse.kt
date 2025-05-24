package com.example.flo

import com.google.gson.annotations.SerializedName


data class AuthResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Result?
)

data class Result(
    val memberId: Int,
    val createdAt: String,
    val updatedAt: String
)
