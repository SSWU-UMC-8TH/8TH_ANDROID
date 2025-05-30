package com.example.flo

import retrofit2.Response

interface SignUpView {
    fun onSignUpSuccess(user: User, response: Response<AuthResponse>)
    fun onSignUpFailure(response: Response<AuthResponse>)
}