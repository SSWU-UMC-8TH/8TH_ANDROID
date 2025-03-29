package com.example.bottomnavigation

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // 2초 후 MainActivity로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            // MainActivity로 이동
            val intent = Intent(this, NaviActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.anim_splash, R.anim.anim_main)
            startActivity(intent, options.toBundle())
            finish()
        }, 2000) // 2000ms = 2초
    }
}