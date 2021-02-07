package com.bignerdranch.android.criminalintent

import java.util.*

class Crime(val requiresPolice: Boolean, val id: UUID) {
    constructor(requiresPolice: Boolean) : this(requiresPolice, UUID.randomUUID())
    var date: Date = Date()
    var title: String = ""
    var isSolved: Boolean = false
}