//스레드 간단 실습
package com.example.umc_week4

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.umc_week4.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        val a = A()
        val b = B()

        a.start()
        b.start()
    }

    class A : Thread(){
        override fun run(){
            super.run()
            for (i in 1..1000){
                Log.d("test", "first : $i")
            }
        }
    }

    class B : Thread(){
        override fun run(){
            super.run()
            for (i in 1000 downTo 1){
                Log.d("test", "second : $i")
            }
        }
    }
}
