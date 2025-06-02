package com.example.flo.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.data.entities.User
import com.example.flo.data.remote.AuthService
import com.example.flo.data.remote.Result
import com.example.flo.databinding.ActivityLoginBinding
import com.example.flo.ui.main.MainActivity
import com.example.flo.ui.signup.SignUpActivity


class LoginActivity : AppCompatActivity(), LoginView {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginSignUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.loginSignInBtn.setOnClickListener {
            login()
            startMainActivity()
        }
    }

    private fun login() {
        if (binding.loginIdEt.text.toString().isEmpty() || binding.loginDirectInputEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.loginPasswordEt.text.toString().isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val authService = AuthService()
        authService.setLoginView(this)

        authService.login(getUser())
    }

    private fun getUser(): User {
        val email = binding.loginIdEt.text.toString() + "@" + binding.loginDirectInputEt.text.toString()
        val password = binding.loginPasswordEt.text.toString()

        return User(name = "", email = email, password = password)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun saveJwt2(accessToken: String) {
        val spf = getSharedPreferences("auth2" , MODE_PRIVATE)
        val editor = spf.edit()

        editor.putString("jwt", accessToken)
        editor.apply()
    }

    override fun onLoginSuccess(code : String , result: Result) {
        if (code == "COMMON200" && result.accessToken != null) {
            saveJwt2(result.accessToken)
            startMainActivity()
        } else {
            Log.e("LOGIN", "accessToken이 null입니다.")
        }
    }

    override fun onLoginFailure() {
        Log.e("LOGIN", "로그인 실패 - 서버 응답 실패 혹은 네트워크 오류")
    }
}