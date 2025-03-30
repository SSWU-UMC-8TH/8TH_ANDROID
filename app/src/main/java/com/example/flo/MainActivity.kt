package com.example.flo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.flo.databinding.ActivityMainBinding

private const val TAG_HOME = "home_fragment"
private const val TAG_AROUND = "around_fragment"
private const val TAG_SEARCH = "search_fragment"
private const val TAG_MYPAGE = "mypage_fragment"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_HOME, HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.aroundFragment -> setFragment(TAG_AROUND, AroundFragment())
                R.id.searchFragment -> setFragment(TAG_SEARCH, SearchFragment())
                R.id.mypageFragment -> setFragment(TAG_MYPAGE, MypageFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {

        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()
        var selectedFragment : Fragment? = manager.findFragmentByTag(tag)

        val home = manager.findFragmentByTag(TAG_HOME)
        val around = manager.findFragmentByTag(TAG_AROUND)
        val search = manager.findFragmentByTag(TAG_SEARCH)
        val mypage = manager.findFragmentByTag(TAG_MYPAGE)

        home?.let { fragTransaction.hide(it) }
        around?.let { fragTransaction.hide(it) }
        search?.let { fragTransaction.hide(it) }
        mypage?.let { fragTransaction.hide(it) }

        if (selectedFragment == null) {
            fragTransaction.add(R.id.mainFrame, fragment, tag)
        } else {
            fragTransaction.show(selectedFragment)
        }

        fragTransaction.commit()
    }

}