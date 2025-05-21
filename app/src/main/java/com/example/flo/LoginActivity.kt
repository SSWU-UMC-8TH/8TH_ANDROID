package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
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
            Toast.makeText(this,"이메일을 입력해주세요.",Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.editTextPassword.text.toString().isEmpty()){
            Toast.makeText(this,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
            return
        }

        val email:String=binding.editTextId.text.toString()+ "@"+binding.editTextEmail.text.toString()
        val pwd:String=binding.editTextPassword.text.toString()

        val songDB = SongDatabase.getInstance(this)
        val user = songDB.userDao().getUser(email,pwd)

        user?.let{
            Log.d("LOGINACT/GET_USER","userId: ${user.id}, $user")
            saveJwt(user.id)
            startMainActivity()
        }
        Toast.makeText(this,"회원 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun saveJwt(jwt:Int){
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()

        editor.putInt("jwt",jwt)
        editor.apply()
    }

    private fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }
}