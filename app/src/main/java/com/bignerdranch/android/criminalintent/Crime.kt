package com.bignerdranch.android.criminalintent

import java.util.*

class Crime(val requiresPolice: Boolean) {
    val id: UUID = UUID.randomUUID()
        get() = field
    val date: Date = Date()
        get() = field
    var title: String = null.toString()
        get() = field
        set(value) {
            field = value
        }
    var isSolved: Boolean = false
}