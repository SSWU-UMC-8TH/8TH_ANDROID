package com.example.flo.utils.api

import com.example.flo.data.entities.User
import com.example.flo.data.remote.AuthResponse
import retrofit2.Response

interface SignUpView {
    fun onSignUpSuccess(user: User, response: Response<AuthResponse>)
    fun onSignUpFailure(response: Response<AuthResponse>)
}