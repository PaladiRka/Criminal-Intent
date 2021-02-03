package com.bignerdranch.android.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

abstract class SingleFragmentActivity : AppCompatActivity() {

    protected abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val fm: FragmentManager = supportFragmentManager
        val fragment: Fragment? = fm.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            val newFragment = createFragment()
            fm.beginTransaction().add(R.id.fragment_container, newFragment).commit()
        }
    }
}