package com.bignerdranch.android.criminalintent.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Parcel
import android.os.Parcelable

class CrimeBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {
    companion object {
        const val VERSION = 1
        const val DATABASE_NAME = "crimeBase.db"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "create table" + CrimeDbSchema.NAME + "(" +
                    " _id integer primary key autoincrement, " +
                    CrimeDbSchema.CrimeTable.Cols.UUID + ", " +
                    CrimeDbSchema.CrimeTable.Cols.TITLE + ", " +
                    CrimeDbSchema.CrimeTable.Cols.DATE + ", " +
                    CrimeDbSchema.CrimeTable.Cols.SOLVED + ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

}