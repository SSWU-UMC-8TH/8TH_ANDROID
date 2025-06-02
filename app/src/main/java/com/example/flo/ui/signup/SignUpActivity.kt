package com.example.flo.ui.signup

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.utils.api.SignUpView
import com.example.flo.utils.db.SongDatabase
import com.example.flo.data.entities.User
import com.example.flo.data.remote.AuthResponse
import com.example.flo.data.remote.AuthService
import com.example.flo.databinding.ActivitySignUpBinding
import retrofit2.Response

class SignUpActivity : AppCompatActivity(), SignUpView {
    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClickListener()
    }

    private fun getUser(): User {
        val email:String=binding.editTextId.text.toString()+ "@"+binding.editTextEmail.text.toString()
        val pwd:String=binding.editTextPassword.text.toString()
        val name:String=binding.signupNameEt.text.toString()

        return User(email, pwd, name)
    }

//    private fun signUp(){
//        if(binding.editTextId.text.toString().isEmpty()||binding.editTextPassword.text.toString().isEmpty()){
//            Toast.makeText(this,"이메일을 입력해주세요.",Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if(binding.editTextPassword.text.toString()!=binding.editTextCheck.text.toString()){
//            Toast.makeText(this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val userDB = SongDatabase.getInstance(this)
//        userDB.userDao().insert(getUser())
//
//        val user = userDB.userDao().getUsers()
//        Log.d("SIGNUPACT",user.toString())
//    }
    private fun signUp() {
        if (binding.editTextId.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.signupNameEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.editTextPassword.text.toString().isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.editTextPassword.text.toString() != binding.editTextCheck.text.toString()) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val userDB = SongDatabase.Companion.getInstance(this)
        val user = getUser()

        if(userDB.userDao().getUser(user.email, user.password)!=null){
            Toast.makeText(this,"존재하는 이메일입니다.", Toast.LENGTH_SHORT).show()
            return
        }


        val authService = AuthService()
        authService.setSignUpView(this)

        authService.signUp(user)
    }

    private fun initClickListener(){
        binding.btnSignUp.setOnClickListener {
            signUp()
        }
    }

    override fun onSignUpSuccess(user: User, response: Response<AuthResponse>) {
        Log.d("SIGNUP/SUCCESS",response.toString())

        val userDB = SongDatabase.getInstance(this)
        userDB.userDao().insert(user)

        Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()

        Log.d("SIGNUPACT", user.toString())
        finish()
    }

    override fun onSignUpFailure(response: Response<AuthResponse>) {
        Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()
    }
}