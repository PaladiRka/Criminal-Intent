package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment() {
    companion object {
        private const val ARG_DATE = "date"
        const val EXTRA_DATE = "com.bignerdranch.android.criminalintent.date*"
        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle()
            args.putSerializable(ARG_DATE, date)

            val fragment = DatePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var datePicker: DatePicker
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date: Date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val view: View = LayoutInflater.from(activity).inflate(R.layout.dialog_date, null)

        datePicker = view.findViewById(R.id.dialog_date_picker)
        datePicker.init(year, month, day, null)

        return AlertDialog.Builder(activity)
            .setView(view)
            .setTitle(R.string.date_picker_title)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                sendResult(Activity.RESULT_OK, GregorianCalendar(datePicker.year, datePicker.month, datePicker.dayOfMonth).time)
            }.create()
    }

    private fun sendResult(resultCode: Int, date: Date) {
        if (targetFragment != null) {
            val intent = Intent()
            intent.putExtra(EXTRA_DATE, date)

            targetFragment!!.onActivityResult(targetRequestCode, resultCode, intent)
        }
    }
}