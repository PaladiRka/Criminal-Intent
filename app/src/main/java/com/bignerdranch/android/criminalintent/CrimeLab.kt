package com.bignerdranch.android.criminalintent

import java.util.*

object CrimeLab {

    val crimes = mutableListOf<Crime>()
        get() = field
    init {
        for(i:Int in 0..100) {
            val crime = Crime(i % 3 == 0)
            crime.title = "Crime #$i"
            crime.isSolved = (i % 2 == 0)
            crimes += crime
        }
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