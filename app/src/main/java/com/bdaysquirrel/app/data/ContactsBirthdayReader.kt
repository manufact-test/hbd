package com.bdaysquirrel.app.data

import android.content.Context
import android.provider.ContactsContract
import java.time.MonthDay
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ContactBirthdayCandidate(
    val contactId: Long,
    val name: String,
    val day: Int,
    val month: Int,
    val year: Int?,
) {
    val stableKey: String
        get() = "$contactId:$month:$day:${year ?: 0}"
}

class ContactsBirthdayReader(
    context: Context,
) {
    private val appContext = context.applicationContext

    suspend fun readBirthdays(): List<ContactBirthdayCandidate> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Event.START_DATE,
        )
        val selection = buildString {
            append(ContactsContract.Data.MIMETYPE)
            append(" = ? AND ")
            append(ContactsContract.CommonDataKinds.Event.TYPE)
            append(" = ?")
        }
        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY.toString(),
        )

        val rawCandidates = mutableListOf<ContactBirthdayCandidate>()
        appContext.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            ContactsContract.Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC",
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID)
            val nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME)
            val dateIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Event.START_DATE,
            )

            while (cursor.moveToNext()) {
                val parsed = parseContactBirthdayDate(cursor.getString(dateIndex)) ?: continue
                val name = cursor.getString(nameIndex)?.trim().orEmpty()
                rawCandidates += ContactBirthdayCandidate(
                    contactId = cursor.getLong(idIndex),
                    name = name.ifBlank { "Без имени" },
                    day = parsed.day,
                    month = parsed.month,
                    year = parsed.year,
                )
            }
        }

        rawCandidates
            .groupBy { candidate ->
                "${candidate.contactId}:${candidate.month}:${candidate.day}"
            }
            .values
            .map { duplicates ->
                duplicates.maxByOrNull { if (it.year == null) 0 else 1 } ?: duplicates.first()
            }
            .sortedWith(
                compareBy<ContactBirthdayCandidate> {
                    it.name.lowercase(Locale.getDefault())
                }.thenBy { it.month }.thenBy { it.day },
            )
    }
}

internal data class ParsedContactBirthday(
    val day: Int,
    val month: Int,
    val year: Int?,
)

internal fun parseContactBirthdayDate(rawValue: String?): ParsedContactBirthday? {
    val value = rawValue?.trim().orEmpty()
    if (value.isBlank()) return null

    val withoutYear = Regex("^--(\\d{2})-(\\d{2})$").matchEntire(value)
    if (withoutYear != null) {
        val month = withoutYear.groupValues[1].toIntOrNull() ?: return null
        val day = withoutYear.groupValues[2].toIntOrNull() ?: return null
        return validatedBirthday(day = day, month = month, year = null)
    }

    val fullDate = Regex("^(\\d{4})-(\\d{2})-(\\d{2})(?:T.*)?$").matchEntire(value)
    if (fullDate != null) {
        val year = fullDate.groupValues[1].toIntOrNull()?.takeIf { it > 0 }
        val month = fullDate.groupValues[2].toIntOrNull() ?: return null
        val day = fullDate.groupValues[3].toIntOrNull() ?: return null
        return validatedBirthday(day = day, month = month, year = year)
    }

    val compact = Regex("^(\\d{4})(\\d{2})(\\d{2})$").matchEntire(value)
    if (compact != null) {
        val year = compact.groupValues[1].toIntOrNull()?.takeIf { it > 0 }
        val month = compact.groupValues[2].toIntOrNull() ?: return null
        val day = compact.groupValues[3].toIntOrNull() ?: return null
        return validatedBirthday(day = day, month = month, year = year)
    }

    return null
}

private fun validatedBirthday(
    day: Int,
    month: Int,
    year: Int?,
): ParsedContactBirthday? = runCatching {
    MonthDay.of(month, day)
    ParsedContactBirthday(day = day, month = month, year = year)
}.getOrNull()
