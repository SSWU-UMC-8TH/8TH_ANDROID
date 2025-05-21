package com.example.flo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flo.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClickListener()
    }

    private fun getUser():User{
        val email:String=binding.editTextId.text.toString()+ "@"+binding.editTextEmail.text.toString()
        val pwd:String=binding.editTextPassword.text.toString()

        return User(email,pwd)
    }

    private fun signUp(){
        if(binding.editTextId.text.toString().isEmpty()||binding.editTextPassword.text.toString().isEmpty()){
            Toast.makeText(this,"이메일을 입력해주세요.",Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.editTextPassword.text.toString()!=binding.editTextCheck.text.toString()){
            Toast.makeText(this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
            return
        }

        val userDB = SongDatabase.getInstance(this)
        userDB.userDao().insert(getUser())

        val user = userDB.userDao().getUsers()
        Log.d("SIGNUPACT",user.toString())
    }

    private fun initClickListener(){
        binding.btnSignUp.setOnClickListener {
            signUp()
            finish()
        }
    }
}