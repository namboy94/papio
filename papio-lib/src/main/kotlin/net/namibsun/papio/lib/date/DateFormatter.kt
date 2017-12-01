package net.namibsun.papio.lib.date

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException

class DateFormatter {

    fun getTodayString(): String {
        val now = ZonedDateTime.now()
        val year = now.year.toString().padStart(4, '0')
        val month = now.monthValue.toString().padStart(2, '0')
        val day = now.dayOfMonth.toString().padStart(2, '0')
        return "$year-$month-$day"
    }

    fun validateDateString(date: String): Boolean {
        return try {
            LocalDateTime.parse("${date}T00:00:00")
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }
}