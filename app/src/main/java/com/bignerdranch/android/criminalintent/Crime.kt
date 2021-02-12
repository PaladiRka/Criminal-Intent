package com.bignerdranch.android.criminalintent

import java.util.*

class Crime(
    val requiresPolice: Boolean,
    val id: UUID,
    val title: String,
    val date: Date,
    val isSolved: Boolean,
    val suspect: String
) {
    constructor(requiresPolice: Boolean) : this(
        requiresPolice,
        UUID.randomUUID(),
        "",
        Date(),
        false,
        ""
    )

    fun getPhotoFilename(): String {
        return "IMG_ $id.jpg"
    }

    fun copy(
        requiresPolice: Boolean = this.requiresPolice,
        id: UUID = this.id,
        title: String = this.title,
        date: Date = this.date,
        isSolved: Boolean = this.isSolved,
        suspect: String = this.suspect
    ) = Crime(requiresPolice, id, title, date, isSolved, suspect)
}