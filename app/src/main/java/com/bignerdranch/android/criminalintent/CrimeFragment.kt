package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bignerdranch.android.criminalintent.MyDialogFragment.Companion.newInstance
import java.io.File
import java.util.*

enum class Direction {
    ONLY_RIGHT, ONLY_LEFT, BOTH, NOWHERE
}

class CrimeFragment : Fragment() {
    companion object {
        private const val ARG_CRIME_MODE = "crime_mode"
        private const val ARG_CRIME_ID = "crime_id"
        private const val DIALOG_DATE = "DialogDate"
        private const val REQUEST_DATE = 0
        private const val REQUEST_CONTACT = 1
        private const val REQUEST_PHOTO = 2
        fun newInstance(crimeId: UUID, direction: Direction): CrimeFragment {
            val args = Bundle()
            args.putSerializable(ARG_CRIME_ID, crimeId)
            args.putSerializable(CrimeFragment.ARG_CRIME_MODE, direction)
            val fragment = CrimeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun returnResult() {
        activity?.setResult(Activity.RESULT_OK, null)
    }

    private lateinit var photoFile: File
    private lateinit var crime: Crime
    private lateinit var fragmentView: CrimeFragmentView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crime = CrimeLab.get(requireActivity()).getCrime(crimeId)!!
        photoFile = CrimeLab.get(requireActivity()).getPhotoFile(crime)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_crime -> {
                CrimeLab.get(requireActivity()).deleteCrime(crime)
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
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentView = CrimeFragmentView(view)
        fragmentView.titleField.setText(crime.title)
        fragmentView.titleField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime = crime.copy(title = s.toString())
                returnResult()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        fragmentView.reportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport())
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            val chooseIntent = Intent.createChooser(intent, getString(R.string.send_report))
            startActivity(chooseIntent)
        }

        fragmentView.dateButton.setOnClickListener {
            val dialog = DatePickerFragment.newInstance(crime.date)
            dialog.setTargetFragment(this, REQUEST_DATE)
            fragmentManager?.let { manager -> dialog.show(manager, DIALOG_DATE) }
        }
        updateDate()

        val mode = arguments?.getSerializable(ARG_CRIME_MODE)
        fragmentView.homeButton.isEnabled = ((mode != Direction.ONLY_RIGHT) && (mode != Direction.NOWHERE))
        fragmentView.homeButton.setOnClickListener {
            val pager = activity?.findViewById<ViewPager2>(R.id.crime_view_pager)
            if (pager != null) {
                pager.currentItem = 0
            }
        }

        fragmentView.endButton.isEnabled = ((mode != Direction.ONLY_LEFT) && (mode != Direction.NOWHERE))
        fragmentView.endButton.setOnClickListener {
            val pager = activity?.findViewById<ViewPager2>(R.id.crime_view_pager)
            if (pager != null) {
                pager.currentItem = CrimeLab.get(requireActivity()).getCrimes().size - 1
            }
        }

        fragmentView.solvedCheckBox.isChecked = crime.isSolved
        fragmentView.solvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            crime = crime.copy(isSolved = isChecked)
            returnResult()
        }

        val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

        fragmentView.suspectCallButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            val phone = getContactNumber(requireActivity(), crime.suspect)
            intent.data = Uri.parse("tel:$phone")
            requireContext().startActivity(intent)
        }
        fragmentView.suspectCallButton.isEnabled = crime.suspect != ""

        fragmentView.suspectButton.setOnClickListener {
            startActivityForResult(pickContact, REQUEST_CONTACT)
        }

        if (crime.suspect != "") {
            fragmentView.suspectButton.text = crime.suspect
        }

        val packageManager = activity?.packageManager
        if (packageManager?.resolveActivity(
                pickContact,
                PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            fragmentView.suspectButton.isEnabled = false
        }

        val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val canTakePhoto = packageManager?.let { captureImage.resolveActivity(it) } != null
        fragmentView.photoButton.isEnabled = canTakePhoto
        fragmentView.photoButton.setOnClickListener {
            val uri = FileProvider.getUriForFile(
                requireActivity(),
                "com.bignerdranch.android.criminalintent.fileprovider",
                photoFile
            )
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            val cameraActivities = requireActivity().packageManager.queryIntentActivities(
                captureImage,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            for (cameraActivity in cameraActivities) {
                requireActivity().grantUriPermission(
                    cameraActivity.activityInfo.packageName,
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            startActivityForResult(captureImage, REQUEST_PHOTO)
        }

        fragmentView.photoView.setOnClickListener {
            if (photoFile.exists()) {
                val view: MyDialogFragment = newInstance(PictureUtils.getScaledBitmap(photoFile.path, requireActivity()))
                fragmentManager?.let { manager -> view.show(manager, DIALOG_DATE) }
            }
        }
        updatePhotoView()
    }

    override fun onPause() {
        super.onPause()

        CrimeLab.get(requireActivity()).updateCrime(crime)
    }

    private fun getContactNumber(context: Context, name: String): String? {
        var out: String? = null
        val cr = context.contentResolver
        val cursor = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            "DISPLAY_NAME = '$name'",
            null,
            null
        ) ?: return null
        try {
            if (cursor.moveToFirst()) {
                val contactId: String =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                //  Get all phone numbers.
                val phones = cr.query(
                    Phone.CONTENT_URI, null,
                    Phone.CONTACT_ID + " = " + contactId, null, null
                ) ?: return null
                try {
                    phones.moveToFirst()
                    out = phones.getString(phones.getColumnIndex(Phone.NUMBER))

                } finally {
                    phones.close()
                }
            }
        } finally {
            cursor.close()
        }

        return out
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_DATE) {
            val date = data?.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
            crime = crime.copy(date = date)
            updateDate()
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            val contactUri: Uri = data.data as Uri
            val queryFields = Array(1) { ContactsContract.Contacts.DISPLAY_NAME }
            val cursor = activity?.contentResolver?.query(contactUri, queryFields, null, null, null)
            try {
                if (cursor!!.count == 0) {
                    crime = crime.copy(suspect = "")
                    fragmentView.suspectButton.text = ""
                    return
                }

                cursor.moveToFirst()
                val suspect = cursor.getString(0)
                crime = crime.copy(suspect = suspect)
                fragmentView.suspectButton.text = suspect
            } finally {
                cursor?.close()
            }
        } else if (requestCode == REQUEST_PHOTO) {
            val uri = FileProvider.getUriForFile(
                requireActivity(),
                "com.bignerdranch.android.criminalintent.fileprovider",
                photoFile
            )
            requireActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            updatePhotoView()
        }
    }

    private fun updateDate() {
        fragmentView.dateButton.text = DateFormat.format("EEEE, MMM dd, yyyy", crime.date)
    }

    private fun getCrimeReport(): String {
        val solvedString =
            if (crime.isSolved) getString(R.string.crime_report_solved) else getString(R.string.crime_report_unsolved)
        val dateFormat = "EEE, MMM dd"
        val dateString = DateFormat.format(dateFormat, crime.date).toString()
        val title = crime.title

        val suspect =
            if (crime.suspect == "") getString(R.string.crime_report_no_suspect) else getString(R.string.crime_report_suspect, crime.suspect)
        return getString(R.string.crime_report, title, dateString, solvedString, suspect)
    }

    private fun updatePhotoView() {
        if (!photoFile.exists()) {
            fragmentView.photoView.setImageDrawable(null)
        } else {
            val bitmap = PictureUtils.getScaledBitmap(photoFile.path, requireActivity())
            fragmentView.photoView.setImageBitmap(bitmap)
        }
    }
}