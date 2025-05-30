package com.example.flo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        val userDB = SongDatabase.getInstance(this)
        val user = getUser()

        if(userDB.userDao().getUser(user.email, user.password)!=null){
            Toast.makeText(this,"존재하는 이메일입니다.",Toast.LENGTH_SHORT).show()
            return
        }

        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)
        authService.signUp(user).enqueue(object: Callback<AuthResponse>{
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>){
                val resp=response.body()!!
                resp?:Toast.makeText(this@SignUpActivity,"회원가입에 실패하였습니다.",Toast.LENGTH_SHORT).show()

                when(resp.code){
                    "COMMON200"-> {
                        Log.d("SIGNUP/SUCCESS",response.toString())

                        userDB.userDao().insert(user)

                        Log.d("SIGNUPACT", user.toString())
                        finish()
                    }

                    else-> Toast.makeText(this@SignUpActivity, resp.message, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable){
                Log.d("SIGNUP/FAILURE",t.message.toString())
            }
        })
    }

    private fun initClickListener(){
        binding.btnSignUp.setOnClickListener {
            signUp()
        }
    }
}