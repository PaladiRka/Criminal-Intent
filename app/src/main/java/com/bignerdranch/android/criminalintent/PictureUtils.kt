package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point

class PictureUtils {
    companion object {
        fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
            val option = BitmapFactory.Options()
            option.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, option)

            val srcWidth: Float = option.outWidth.toFloat()
            val srcHeight: Float = option.outHeight.toFloat()

            val inSampleSize : Int
            if (srcHeight > destHeight || srcWidth > destWidth) {
                val heightScale = srcHeight / destHeight
                val widthScale = srcWidth / destWidth
                inSampleSize = Math.round(if (heightScale > widthScale) heightScale else widthScale)
            } else {
                inSampleSize = 1
            }
            val optionNew = BitmapFactory.Options()
            optionNew.inSampleSize = inSampleSize

            return BitmapFactory.decodeFile(path, optionNew)
        }

        fun getScaledBitmap(path: String, activity: Activity): Bitmap {
            val size = Point()
            activity.windowManager.defaultDisplay.getSize(size)
            return getScaledBitmap(path, size.x, size.y)
        }
    }
}