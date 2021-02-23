package com.bignerdranch.android.criminalintent

import android.view.View
import android.widget.*

class CrimeFragmentView(view: View) {

    val titleField: EditText = view.findViewById(R.id.crime_title)
    val dateButton: Button = view.findViewById(R.id.crime_date)
    val solvedCheckBox: CheckBox = view.findViewById(R.id.crime_solved)
    val homeButton: Button = view.findViewById(R.id.home_button)
    val endButton: Button = view.findViewById(R.id.end_button)
    val reportButton: Button = view.findViewById(R.id.crime_report)
    val suspectButton: Button = view.findViewById(R.id.crime_suspect)
    val suspectCallButton: Button = view.findViewById(R.id.crime_suspect_call)
    val photoButton: ImageButton = view.findViewById(R.id.crime_camera)
    val photoView: ImageView = view.findViewById(R.id.crime_photo)

}