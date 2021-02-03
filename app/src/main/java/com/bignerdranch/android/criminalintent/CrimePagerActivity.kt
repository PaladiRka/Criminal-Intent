package com.bignerdranch.android.criminalintent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import java.util.*

class CrimePagerActivity: AppCompatActivity() {
    companion object {
        private const val EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id"

        fun newIntent(packageContext: Context, crimeId: UUID): Intent {
            val intent = Intent(packageContext, CrimePagerActivity::class.java)
            intent.putExtra(EXTRA_CRIME_ID, crimeId)
            return intent
        }
    }

    private lateinit var viewPager: ViewPager2
    private lateinit var crimes: List<Crime>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)
        viewPager = findViewById(R.id.crime_view_pager)
        val crimeId: UUID = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID

        crimes = CrimeLab.crimes

        class MyFragmentStateAdapter(fm: FragmentManager) : FragmentStateAdapter(fm, lifecycle) {
            override fun getItemCount(): Int = crimes.size

            override fun createFragment(position: Int): Fragment {
                val crime = crimes[position]
                val fragment = CrimeFragment.newInstance(crime.id)

//                val homeButton = fragment.activity?.findViewById<View>(R.id.home_button) as Button
//                homeButton.isEnabled = position != 0
//                val endButton = fragment.activity?.findViewById<View>(R.id.end_button) as Button
//                endButton.isEnabled = position != (crimes.size - 1)

//                homeButton.setOnClickListener {
//                    val pager = activity!!.findViewById<View>(R.id.crime_view_pager) as ViewPager2
//                    pager.currentItem = 0
//                }
//
//                endButton = v.findViewById(R.id.end_button)
//                endButton.setOnClickListener {
//                    val pager = activity!!.findViewById<View>(R.id.crime_view_pager) as ViewPager2
//                    pager.currentItem = CrimeLab.crimes.size - 1
//                }

                return fragment
            }
        }
        val fragmentManager: FragmentManager = supportFragmentManager
        viewPager.adapter = MyFragmentStateAdapter(fragmentManager)
        for((i, _) in crimes.withIndex()){
            if(crimes[i].id == crimeId){
                viewPager.currentItem = i
                break
            }
        }

    }


}