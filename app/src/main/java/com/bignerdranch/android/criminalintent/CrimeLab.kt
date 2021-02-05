package com.bignerdranch.android.criminalintent

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper
import java.util.*

class CrimeLab private constructor(private val context: Context) {
    val crimes = mutableListOf<Crime>()
    val database: SQLiteDatabase = CrimeBaseHelper(context).writableDatabase

    companion object {
        var crimeLab: CrimeLab? = null
        fun get(context: Context): CrimeLab {
            if (crimeLab != null) {
                crimeLab = CrimeLab(context)
            }
            return CrimeLab(context)
        }
    }


    fun addCrime(crime: Crime) {
        crimes += crime
    }

    fun deleteCrime(crime: Crime) {
        crimes.remove(crime)
    }

    fun clearCrimes() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            crimes.removeIf { it.title == "" }
        } else {
            crimes.removeAll { it.title == "" }
        }
    }

    fun getCrime(id: UUID): Crime? {
        for (myCrime in crimes) {
            if (myCrime.id == id) {
                return myCrime
            }
        }
        return null
    }
}