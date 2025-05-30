package com.example.flo

import android.content.Context
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
    private lateinit var signUpView: SignUpView

    fun setSignUpView(signUpView: SignUpView){
        this.signUpView=signUpView
    }

    fun signUp(user:User){
        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)
        authService.signUp(user).enqueue(object: Callback<AuthResponse>{
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>){
                val resp=response.body()!!

                when(resp.code){
                    "COMMON200"-> signUpView.onSignUpSuccess(user, response)

                    else-> signUpView.onSignUpFailure(response)
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable){
                Log.d("SIGNUP/FAILURE",t.message.toString())
            }
        })
    }
}