package com.example.flo.ui.signin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.utils.api.LoginView
import com.example.flo.ui.signup.SignUpActivity
import com.example.flo.utils.db.SongDatabase
import com.example.flo.data.remote.AuthService
import com.example.flo.data.remote.Result
import com.example.flo.databinding.ActivityLoginBinding
import com.example.flo.ui.main.MainActivity

class LoginActivity : AppCompatActivity(), LoginView {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClickListener()
    }

    private fun initClickListener() {
        binding.signUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.signInTv.setOnClickListener {
            login()
        }
    }

    private fun login(){
        if(binding.editTextId.text.toString().isEmpty()||binding.editTextPassword.text.toString().isEmpty()){
            Toast.makeText(this,"이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.editTextPassword.text.toString().isEmpty()){
            Toast.makeText(this,"비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val email:String=binding.editTextId.text.toString()+ "@"+binding.editTextEmail.text.toString()
        val pwd:String=binding.editTextPassword.text.toString()

        val songDB = SongDatabase.Companion.getInstance(this)
        val user = songDB.userDao().getUser(email,pwd)

        if(user==null){
            Toast.makeText(this,"회원 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val authService = AuthService()
        authService.setLoginView(this)

        authService.login(user)

        Toast.makeText(this,user.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onLoginSuccess(code: String, result: Result) {
        when(code){
            "COMMON200"-> {
                saveJwt(result.jwt!!)
                startMainActivity()
            }
        }
    }

    override fun onLoginFailure() {
        TODO("Not yet implemented")
    }

/*    private fun saveJwt(jwt:Int){
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()

        editor.putInt("jwt",jwt)
        editor.apply()
    }*/

    private fun saveJwt(jwt:String){
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()

        editor.putString("jwt",jwt)
        editor.apply()
    }

    private fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }
}