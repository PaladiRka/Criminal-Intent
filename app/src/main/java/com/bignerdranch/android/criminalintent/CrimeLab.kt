package com.bignerdranch.android.criminalintent

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper
import com.bignerdranch.android.criminalintent.database.CrimeCursorWrapper
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class CrimeLab private constructor(context: Context) {
    private val context: Context = context.applicationContext
    private val database: SQLiteDatabase = CrimeBaseHelper(this.context).writableDatabase
    private val photoFile = null

    companion object {
        var crimeLab: CrimeLab? = null
        fun get(context: Context): CrimeLab {
            if (crimeLab == null) {
                crimeLab = CrimeLab(context)
            }
            return crimeLab!!
        }

        private fun getContentValues(crime: Crime): ContentValues {
            val values = ContentValues()
            values.put(CrimeTable.Cols.UUID, crime.id.toString())
            values.put(CrimeTable.Cols.TITLE, crime.title)
            values.put(CrimeTable.Cols.DATE, crime.date.time)
            values.put(CrimeTable.Cols.SOLVED, if (crime.isSolved) 1 else 0)
            values.put(CrimeTable.Cols.SUSPECT, crime.suspect)
            return values
        }
    }

    fun addCrime(crime: Crime) {
        val values = getContentValues(crime)
        database.insert(CrimeTable.NAME, null, values)
    }

    fun deleteCrime(crime: Crime) {
        database.delete(
            CrimeTable.NAME,
            CrimeTable.Cols.UUID + " = ?",
            Array(1) { crime.id.toString() })
    }

//    fun clearCrimes() {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
////            crimes.removeIf { it.title == "" }
//        } else {
////            crimes.removeAll { it.title == "" }
//        }
//    }

    fun getCrime(id: UUID): Crime? {
        val cursor = queryCrimes(
            CrimeTable.Cols.UUID + " = ?",
            Array(1) { id.toString() }
        )
        try {
            if (cursor.count == 0) {
                return null
            }

            cursor.moveToFirst()
            return cursor.getCrime()
        } finally {
            cursor.close()
        }
    }

    fun getCrimes(): List<Crime> {
        val crimes = ArrayList<Crime>()
        val cursor = queryCrimes(null, null)

        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                crimes.add(cursor.getCrime())
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return crimes
    }

    fun getPhotoFile(crime: Crime): File {
        val filesDir = context.filesDir
        return File(filesDir, crime.getPhotoFilename())
    }

    fun updateCrime(crime: Crime) {
        val uuidString = crime.id.toString()
        val values = getContentValues(crime)

        database.update(
            CrimeTable.NAME,
            values,
            CrimeTable.Cols.UUID + " = ?",
            Array(1) { uuidString })
    }

    private fun queryCrimes(whereClause: String?, whereArgs: Array<String>?): CrimeCursorWrapper {
        val cursor = database.query(
            CrimeTable.NAME,
            null,
            whereClause,
            whereArgs,
            null,
            null,
            null,
        )
        return CrimeCursorWrapper(cursor)
    }
}