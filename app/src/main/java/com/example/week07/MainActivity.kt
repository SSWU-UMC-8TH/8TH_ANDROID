package com.example.week07


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.week07.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    var list = ArrayList<Profile>()
    lateinit var customAdapter: CustomAdapter
    lateinit var db: ProfileDatabase

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db= ProfileDatabase.getInstance(this)!!

        Thread{
            val savedContacts = db.profileDao().getAll()
            if(savedContacts.isNotEmpty()){
                list.addAll(savedContacts)
            }
        }.start()

        customAdapter = CustomAdapter(list, this)

        binding.mainProfileLv.adapter = customAdapter

        binding.button.setOnClickListener {
            Thread{
                list.add(Profile("bear", "24", "0000"))
                db.profileDao().insert(Profile("bear", "24", "0000"))

                val list = db.profileDao().getAll()
                Log.d("Inserted Primary Key",list[list.size-1].id.toString())
            }.start()
            customAdapter.notifyDataSetChanged()
        }
    }
}