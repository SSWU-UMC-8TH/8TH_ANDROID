package com.example.flo.data.remote

import android.util.Log
import com.example.flo.data.entities.User
import com.example.flo.ui.login.LoginView
import com.example.flo.ui.signup.SignUpView
import com.example.flo.utils.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
    private lateinit var signUpView: SignUpView
    private lateinit var loginView: LoginView

    fun setSignUpView(signUpView: SignUpView) {
        this.signUpView = signUpView
    }

    fun setLoginView(loginView: LoginView) {
        this.loginView = loginView
    }

    fun signUp(user: User) {

        val signUpService = getRetrofit().create(AuthRetrofitInterface::class.java)

        signUpService.signUp(user).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d("SIGNUP-RESPONSE-RAW", response.toString())
                if (response.isSuccessful && response.code() == 200) {
                    val signUpResponse = response.body()
                    Log.d("SIGNUP-RESPONSE", signUpResponse.toString())
                    val code = signUpResponse?.code ?: "NULL"
                    if (code == "COMMON200") {
                        signUpView.onSignUpSuccess()
                    } else {
                        Log.e("SIGNUP", "실패 code: $code, message: ${signUpResponse?.message}")
                        signUpView.onSignUpFailure()
                    }
                } else {
                    Log.e("SIGNUP", "HTTP 오류 code: ${response.code()}")
                    signUpView.onSignUpFailure()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.e("SIGNUP-NETWORK", "통신 실패: ${t.message}", t)
                signUpView.onSignUpFailure()
            }
        })
    }


    fun login(user: User) {
        val loginService = getRetrofit().create(AuthRetrofitInterface::class.java)
        loginService.login(user).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.code() == 200) {
                    val loginResponse: AuthResponse = response.body()!!

                    when (val code = loginResponse.code) {
                        "COMMON200" -> loginView.onLoginSuccess(code, loginResponse.result!!)
                        else -> loginView.onLoginFailure()
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                //실패처리
            }
        })
    }

    fun test(jwt: String) {
        val testService = getRetrofit().create(AuthRetrofitInterface::class.java)
        testService.test("Bearer $jwt").enqueue(object : Callback<AuthTestResponse> {
            override fun onResponse(call: Call<AuthTestResponse>, response: Response<AuthTestResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Log.d("TEST", "result: ${response.body()?.result}")
                } else {
                    Log.e("TEST", "code: ${response.body()?.code}, message: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<AuthTestResponse>, t: Throwable) {
                Log.e("TEST", "네트워크 오류: ${t.message}")
            }
        })
    }
}