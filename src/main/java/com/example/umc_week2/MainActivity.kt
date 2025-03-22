package com.example.umc_week2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.umc_week2.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        val scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        val scaleOut = AnimationUtils.loadAnimation(this, R.anim.scale_out)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.search -> {
                    val searchIcon = bottomNavigationView.findViewById<View>(R.id.search)
                    searchIcon.startAnimation(scaleIn)
                    searchIcon.startAnimation(scaleOut)
                    val intent = Intent(this, Search::class.java)
                    startActivity(intent)

                    true
                }

                else -> false
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}