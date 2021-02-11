package com.bignerdranch.android.criminalintent

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent.getIntent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.DialogFragment


class MyDialogFragment : DialogFragment() {
    companion object {
        private const val ARG_PHOTO = "photo"
        fun newInstance(bitmap: Bitmap): MyDialogFragment {
            val fragment = MyDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_PHOTO, bitmap)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var imageView: ImageView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val date: Date = arguments?.getSerializable(ARG_PHOTO) as Date
        val bitmapImage = arguments?.getParcelable<Bitmap>(ARG_PHOTO)


        val view: View = LayoutInflater.from(activity).inflate(R.layout.fragment_dialog, null)

        imageView = view.findViewById(R.id.dialog_image)
        imageView.setImageBitmap(bitmapImage)
        return AlertDialog.Builder(activity)
            .setView(view)
            .setTitle(R.string.image_dialog_title)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> }
            .create()
    }
}