package com.example.flo

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
    private lateinit var signUpView: SignUpView
    private lateinit var loginView: LoginView

    fun setSignUpView(signUpView: SignUpView){
        this.signUpView=signUpView
    }

    fun setLoginView(loginView: LoginView){
        this.loginView=loginView
    }

    fun login(user:User){
        val loginuser = loginUser(user.email,user.password)
        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)
        authService.login(loginuser).enqueue(object: Callback<AuthResponse>{
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>){
                val resp=response.body()!!

                when(val code=resp.code){
                    "COMMON200"-> loginView.onLoginSuccess(code, resp.result!! )

                    else-> loginView.onLoginFailure()
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable){
                Log.d("LOGIN/FAILURE",t.message.toString())
            }
        })
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