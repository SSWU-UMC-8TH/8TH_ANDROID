package com.example.flo.ui.login

import com.example.flo.data.remote.Result

interface LoginView {
    fun onLoginSuccess(code : String, result : Result)
    fun onLoginFailure()
}