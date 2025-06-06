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
import com.kakao.sdk.user.UserApiClient
import com.bumptech.glide.Glide



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
        }

        binding.loginKakakoLoginIv.setOnClickListener {
            kakaoLogin()
        }

    }

    private fun login() {
        val email = binding.loginIdEt.text.toString() + "@" + binding.loginDirectInputEt.text.toString()
        val password = binding.loginPasswordEt.text.toString()
        Log.d("LOGIN_ATTEMPT", "email: $email, password: $password")
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

    private fun saveJwt2(accessToken: String) {
        val spf = getSharedPreferences("auth2" , MODE_PRIVATE)
        val editor = spf.edit()

        editor.putString("jwt", accessToken)
        editor.apply()
    }

    override fun onLoginSuccess(code : String , result: Result) {
        if (code == "COMMON200" && result.accessToken != null) {
            Log.d("LOGIN_SUCCESS", "code: $code, token: ${result.accessToken}")
            saveJwt2(result.accessToken)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Log.e("LOGIN", "accessToken이 null입니다.")
        }
    }

    override fun onLoginFailure() {
        Log.e("LOGIN", "로그인 실패 - 서버 응답 실패 혹은 네트워크 오류")
    }

    private fun kakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e("KAKAO_LOGIN", "카카오톡 로그인 실패", error)
                    loginWithKakaoAccount()
                } else if (token != null) {
                    Log.i("KAKAO_LOGIN", "로그인 성공 ${token.accessToken}")
                    getUserInfo()
                }
            }
        } else {
            loginWithKakaoAccount()
        }
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                Log.e("KAKAO_LOGIN", "카카오계정 로그인 실패", error)
            } else if (token != null) {
                Log.i("KAKAO_LOGIN", "로그인 성공 ${token.accessToken}")
                getUserInfo()
            }
        }
    }

    private fun getUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KAKAO_LOGIN", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                val nickname = user.kakaoAccount?.profile?.nickname
                val profileImgUrl = user.kakaoAccount?.profile?.profileImageUrl
                val email = user.kakaoAccount?.email

                Log.i("KAKAO_LOGIN", "닉네임: $nickname")
                Log.i("KAKAO_LOGIN", "프로필 이미지: $profileImgUrl")
                Log.i("KAKAO_LOGIN", "이메일: $email")

                //여기서 저장해야 값이 null 아님
                val spf = getSharedPreferences("auth2", MODE_PRIVATE)
                spf.edit()
                    .putString("jwt", "KAKAO_LOGIN")
                    .putInt("userId", 1)
                    .putString("nickname", nickname)
                    .putString("profile", profileImgUrl)
                    .putString("email", email)
                    .apply()

                Toast.makeText(this, "환영합니다, $nickname!", Toast.LENGTH_SHORT).show()

                // 메인 화면으로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}