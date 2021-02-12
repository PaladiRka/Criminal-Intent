package com.bignerdranch.android.criminalintent.database

import android.database.Cursor
import android.database.CursorWrapper
import com.bignerdranch.android.criminalintent.Crime
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable
import java.util.*

class CrimeCursorWrapper(cursor: Cursor?) : CursorWrapper(cursor) {

    fun getCrime(): Crime {
        val uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID))
        val title = getString(getColumnIndex(CrimeTable.Cols.TITLE))
        val date = getLong(getColumnIndex(CrimeTable.Cols.DATE))
        val isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED))
        val suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT))

        return Crime(false, UUID.fromString(uuidString), title, Date(date), isSolved != 0, suspect)
    }
}