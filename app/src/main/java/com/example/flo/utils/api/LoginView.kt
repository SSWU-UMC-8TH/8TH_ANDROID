package com.example.flo.utils.api

import com.example.flo.data.remote.Result

interface LoginView {
    fun onLoginSuccess(code: String, result: Result)//:Int
    fun onLoginFailure()
}