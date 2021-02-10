package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
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
    ONLY_RIGHT, ONLY_LEFT, BOTH, NOWHERE
}

class CrimeFragment : Fragment() {
    companion object {
        const val ARG_CRIME_MODE = "crime_mode"
        private const val ARG_CRIME_ID = "crime_id"
        private const val DIALOG_DATE = "DialogDate"
        private const val REQUEST_DATE = 0
        private const val REQUEST_CONTACT = 1
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
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crime = CrimeLab.get(activity!!).getCrime(crimeId)!!
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_crime -> {
                CrimeLab.get(activity!!).deleteCrime(crime)
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

        reportButton = v.findViewById(R.id.crime_report)
        reportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport())
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            val chooseIntent = Intent.createChooser(intent, getString(R.string.send_report))
            startActivity(chooseIntent)
        }

        dateButton = v.findViewById(R.id.crime_date)
        dateButton.setOnClickListener {
            val dialog = DatePickerFragment.newInstance(crime.date)
            dialog.setTargetFragment(this, REQUEST_DATE)
            fragmentManager?.let { manager -> dialog.show(manager, DIALOG_DATE) }
        }
        updateDate()

        homeButton = v.findViewById(R.id.home_button)
        val mode = arguments?.getSerializable(ARG_CRIME_MODE)
        homeButton.isEnabled = ((mode != Direction.ONLY_RIGHT) && (mode != Direction.NOWHERE))
        homeButton.setOnClickListener {
            val pager = activity?.findViewById<View>(R.id.crime_view_pager) as ViewPager2
            pager.currentItem = 0
        }

        endButton = v.findViewById(R.id.end_button)
        endButton.isEnabled = ((mode != Direction.ONLY_LEFT) && (mode != Direction.NOWHERE))
        endButton.setOnClickListener {
            val pager = activity?.findViewById<View>(R.id.crime_view_pager) as ViewPager2
            pager.currentItem = CrimeLab.get(activity!!).getCrimes().size - 1
        }

        solvedCheckBox = v.findViewById(R.id.crime_solved)
        solvedCheckBox.isChecked = crime.isSolved
        solvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
            returnResult()
        }

        val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        suspectButton = v.findViewById(R.id.crime_suspect)
        suspectButton.setOnClickListener {
            startActivityForResult(pickContact, REQUEST_CONTACT)
        }

        if (crime.suspect != "") {
            suspectButton.text = crime.suspect
        }

        val packageManager = activity?.packageManager
        if (packageManager?.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            suspectButton.isEnabled = false
        }

        return v
    }

    override fun onPause() {
        super.onPause()

        CrimeLab.get(activity!!).updateCrime(crime)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_DATE) {
            val date = data?.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
            crime.date = date
            updateDate()
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            val contactUri: Uri = data.data as Uri
            val queryFields = Array(1) { ContactsContract.Contacts.DISPLAY_NAME }
            val cursor = activity?.contentResolver?.query(contactUri, queryFields, null, null, null)
            try {
                if (cursor!!.count == 0) {
                    crime.suspect = ""
                    suspectButton.text = ""
                    return
                }

                cursor.moveToFirst()
                val suspect = cursor.getString(0)
                crime.suspect = suspect
                suspectButton.text = suspect
            } finally {
                cursor?.close()
            }
        }
    }

    private fun updateDate() {
        dateButton.text = DateFormat.format("EEEE, MMM dd, yyyy", crime.date)
    }

    private fun getCrimeReport(): String {
        val solvedString =
            if (crime.isSolved) getString(R.string.crime_report_solved) else getString(R.string.crime_report_unsolved)
        val dateFormat = "EEE, MMM dd"
        val dateString = DateFormat.format(dateFormat, crime.date).toString()
        val title = crime.title

        val suspect =
            if (crime.suspect == "") getString(R.string.crime_report_no_suspect) else getString(R.string.crime_report_suspect)
        return getString(R.string.crime_report, title, dateString, solvedString, suspect)
    }

}