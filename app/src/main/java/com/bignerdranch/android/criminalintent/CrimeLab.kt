package com.bignerdranch.android.criminalintent

import java.util.*

object CrimeLab {

    val crimes = mutableListOf<Crime>()

    fun addCrime(crime: Crime) {
        crimes += crime
    }

    fun getCrime(id: UUID): Crime? {
        for (myCrime: Crime in crimes) {
            if (myCrime.id == id) {
                return myCrime
            }
        }
        return null
    }
}