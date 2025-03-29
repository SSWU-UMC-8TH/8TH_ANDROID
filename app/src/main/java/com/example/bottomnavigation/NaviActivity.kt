package com.example.bottomnavigation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.bottomnavigation.databinding.ActivityNaviBinding

private const val TAG_HOME = "home_fragment"
private const val TAG_PENCIL = "pencil_fragment"
private const val TAG_CALENDAR = "calender_fragment"
private const val TAG_USER = "user_fragment"

private val fragmentPosition = mapOf<String, Int>(TAG_HOME to 1, TAG_PENCIL to 2, TAG_CALENDAR to 3, TAG_USER to 4)

class NaviActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNaviBinding
    private var recentPosition = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->

            when(item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.calenderFragment -> setFragment(TAG_CALENDAR, CalendarFragment())
                R.id.userFragment-> setFragment(TAG_USER, UserFragment())
                R.id.pencilFragment-> setFragment(TAG_PENCIL, PencilFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        val calender = manager.findFragmentByTag(TAG_CALENDAR)
        val home = manager.findFragmentByTag(TAG_HOME)
        val user = manager.findFragmentByTag(TAG_USER)
        val pencil = manager.findFragmentByTag(TAG_PENCIL)

        var selectedFragment : Fragment? = manager.findFragmentByTag(tag)

        if (tag == TAG_HOME) {
            //왼쪽에서 나오고 오른쪽으로 나감
            fragTransaction.setCustomAnimations(
                R.anim.anim_from_left,
                R.anim.anim_to_right
            )
        }

        else if (tag == TAG_PENCIL){
            if(recentPosition < fragmentPosition[TAG_PENCIL]!!){
                //오른쪽에서 나오고 왼쪽으로 나감
                fragTransaction.setCustomAnimations(
                    R.anim.anim_from_right,
                    R.anim.anim_to_left
                )
            }
            else{
                //왼쪽에서 나오고 오른쪽으로 나감
                fragTransaction.setCustomAnimations(
                    R.anim.anim_from_left,
                    R.anim.anim_to_right
                )
            }
        }

        else if (tag == TAG_CALENDAR) {
            //오른쪽에서 나오고 왼쪽으로 나감
            if(recentPosition < fragmentPosition[TAG_CALENDAR]!!){
                //오른쪽에서 나오고 왼쪽으로 나감
                fragTransaction.setCustomAnimations(
                    R.anim.anim_from_right,
                    R.anim.anim_to_left
                )
            }
            else{
                //왼쪽에서 나오고 오른쪽으로 나감
                fragTransaction.setCustomAnimations(
                    R.anim.anim_from_left,
                    R.anim.anim_to_right
                )
            }
        }

        else if (tag == TAG_USER){
            //오른쪽에서 나오고 왼쪽으로 나감
            fragTransaction.setCustomAnimations(
                R.anim.anim_from_right,
                R.anim.anim_to_left
            )
        }

        calender?.let { fragTransaction.hide(it) }
        home?.let { fragTransaction.hide(it) }
        user?.let { fragTransaction.hide(it) }
        pencil?.let { fragTransaction.hide(it) }


        if (selectedFragment != null)
            fragTransaction.show(selectedFragment)

        else
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)

        recentPosition = fragmentPosition[tag]!!

        fragTransaction.commit()
    }
}