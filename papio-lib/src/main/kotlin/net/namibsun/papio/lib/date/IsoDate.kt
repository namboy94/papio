package net.namibsun.papio.lib.date

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException

/**
 * Class that models an ISO-8601 date string
 * @param date: The ISO-8601 date string
 * @throws IllegalArgumentException If the provided date string is not valid ISO-8601
 */
data class IsoDate(private var date: String = "today") : Comparable<IsoDate> {

    /**
     * Initializes date.
     * If date is set to "today", the current date will be determined and set.
     * The date string is checked to be a valid ISO-8601 date string (YYYY-MM-DD)
     */
    init {

        if (this.date == "today") {
            val now = ZonedDateTime.now()
            val year = now.year.toString().padStart(4, '0')
            val month = now.monthValue.toString().padStart(2, '0')
            val day = now.dayOfMonth.toString().padStart(2, '0')
            this.date = "$year-$month-$day"
        }

        try {
            LocalDateTime.parse("${this.date}T00:00:00")
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Date ${this.date} is not a valid ISO 8601 date String.")
        }
    }

    /**
     * Retrieves the raw date string
     * @return The underlying date string
     */
    override fun toString(): String {
        return this.date
    }

    /**
     * Retrieves the year of this date
     * @return The year of the date as a String
     */
    fun getYear(): String {
        return this.date.split("-")[0]
    }

    /**
     * Retrieves the month of this date
     * @return The month of the date as a String
     */
    fun getMonth(): String {
        return this.date.split("-")[1]
    }

    /**
     * Retrieves the day of this date
     * @return The day of the date as a String
     */
    fun getDay(): String {
        return this.date.split("-")[2]
    }

    /**
     * Compares this IsoObject with another IsoDate object
     * @param other: The IsoDate to which to compare to
     * @return -1 if the argument is a later date than this one
     *          0 if the dates are equal
     *         +1 if the argument is an earlier date than this one
     */
    override fun compareTo(other: IsoDate): Int {
        return if (this.date == other.date) {
            0
        } else {
            val thisDateInt = "${this.getYear()}${this.getMonth()}${this.getDay()}"
            val otherDateInt = "${other.getYear()}${other.getMonth()}${other.getDay()}"
            if (thisDateInt < otherDateInt) {
                -1
            } else {
                +1
            }
        }
    }
}
