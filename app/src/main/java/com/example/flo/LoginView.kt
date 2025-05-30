package com.example.flo

interface LoginView {
    fun onLoginSuccess(code: String, result: Result)//:Int
    fun onLoginFailure()
}