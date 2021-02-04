package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import java.util.*

enum class Direction {
    ONLY_RIGHT, ONLY_LEFT, BOTH
}

class CrimeFragment : Fragment() {
    companion object {
        const val ARG_CRIME_MODE = "crime_mode"
        private const val ARG_CRIME_ID = "crime_id"
        private const val DIALOG_DATE = "DialogDate"
        private const val REQUEST_DATE = 0
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle()
            args.putSerializable(ARG_CRIME_ID, crimeId)

            val fragment = CrimeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun returnResult() {
        activity?.setResult(Activity.RESULT_OK, null)
    }

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var homeButton: Button
    private lateinit var endButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crime = CrimeLab.getCrime(crimeId)!!
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_crime -> {
                CrimeLab.deleteCrime(crime)
                activity?.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = v.findViewById(R.id.crime_title)
        titleField.setText(crime.title)
        titleField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
                returnResult()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        dateButton = v.findViewById(R.id.crime_date)
        dateButton.setOnClickListener {
            val dialog = DatePickerFragment.newInstance(crime.date)
            dialog.setTargetFragment(this, REQUEST_DATE)
            fragmentManager?.let { manager -> dialog.show(manager, DIALOG_DATE) }
        }
        updateDate()

        homeButton = v.findViewById(R.id.home_button)
        val mode = arguments?.getSerializable(ARG_CRIME_MODE)
        homeButton.isEnabled = (mode != Direction.ONLY_RIGHT)
        homeButton.setOnClickListener {
            val pager = activity?.findViewById<View>(R.id.crime_view_pager) as ViewPager2
            pager.currentItem = 0
        }

        endButton = v.findViewById(R.id.end_button)
        endButton.isEnabled = (mode != Direction.ONLY_LEFT)
        endButton.setOnClickListener {
            val pager = activity?.findViewById<View>(R.id.crime_view_pager) as ViewPager2
            pager.currentItem = CrimeLab.crimes.size - 1
        }

        solvedCheckBox = v.findViewById(R.id.crime_solved)
        solvedCheckBox.isChecked = crime.isSolved
        solvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
            returnResult()
        }

        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_DATE) {
            val date = data?.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
            crime.date = date
            updateDate()
        }
    }

    private fun updateDate() {
        dateButton.text = DateFormat.format("EEEE, MMM dd, yyyy", crime.date)
    }

}