package com.example.flo.data.remote

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.utils.api.LoginView
import com.example.flo.utils.api.SignUpView
import com.example.flo.utils.api.TestView
import com.example.flo.data.entities.User
import com.example.flo.utils.api.getRetrofit
import com.example.flo.data.entities.loginUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
    private lateinit var signUpView: SignUpView
    private lateinit var loginView: LoginView
    private lateinit var testView: TestView

    fun setSignUpView(signUpView: SignUpView){
        this.signUpView=signUpView
    }

    fun setLoginView(loginView: LoginView){
        this.loginView=loginView
    }

    fun setTestView(testView: TestView){
        this.testView=testView
    }

    fun login(user: User){
        val loginuser = loginUser(user.email, user.password)
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

    fun signUp(user: User){
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

    fun test(jwt: String) {
        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)

        val request = "Bearer $jwt"

        authService.test(request).enqueue(object : Callback<TestResponse> {
            override fun onResponse(call: Call<TestResponse>, response: Response<TestResponse>) {
                val resp = response.body()!!

                when (resp.code) {
                    "COMMON200" -> testView.onTestSuccess(resp)

                    else -> testView.onTestFailure(resp)
                }
            }

            override fun onFailure(call: Call<TestResponse>, t: Throwable) {
                Log.d("TEST/FAILURE", t.message.toString())
            }
        })
    }
}