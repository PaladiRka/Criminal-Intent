package com.bignerdranch.android.criminalintent

import java.util.*

object CrimeLab {

    val crimes = mutableListOf<Crime>()

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