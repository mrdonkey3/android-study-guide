package com.dk.androidstudyguide

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.dk.androidstudyguide.adapter.FragmentAdapter
import com.dk.androidstudyguide.frags.BlankFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val frags = arrayListOf(BlankFragment())
        val titles = arrayListOf("title1")
        viewpager.adapter = FragmentAdapter(supportFragmentManager, frags, titles)
        tabLayout.setupWithViewPager(viewpager)
        tabLayout.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        tabLayout.post {
            val measuredWidth = window.decorView.measuredWidth
            println("--->measuredWidth:$measuredWidth")
            tabLayout.getTabAt(0)?.view?.layoutParams = LinearLayout.LayoutParams(
                1000,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
//            tabLayout.getChildAt(0).requestLayout()
        }
    }
}
