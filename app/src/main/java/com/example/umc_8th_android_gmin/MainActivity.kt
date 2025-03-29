package com.example.umc_8th_android_gmin

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val image_happy = findViewById<ImageView>(R.id.image_happy)
        image_happy.setOnClickListener {
            Toast.makeText(this, "웃음 사진 선택", Toast.LENGTH_LONG).show()
        }

        val image_excite = findViewById<ImageView>(R.id.image_excite)
        image_excite.setOnClickListener {
            Toast.makeText(this, "흥분 사진 선택", Toast.LENGTH_LONG).show()
        }

        val image_soso = findViewById<ImageView>(R.id.image_soso)
        image_soso.setOnClickListener {
            Toast.makeText(this, "보통 사진 선택", Toast.LENGTH_LONG).show()
        }

        val image_anxious = findViewById<ImageView>(R.id.image_anxious)
        image_anxious.setOnClickListener {
            Toast.makeText(this, "불안 사진 선택", Toast.LENGTH_LONG).show()
        }

        val image_angry = findViewById<ImageView>(R.id.image_angry)
        image_angry.setOnClickListener {
            Toast.makeText(this, "분노 사진 선택", Toast.LENGTH_LONG).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}