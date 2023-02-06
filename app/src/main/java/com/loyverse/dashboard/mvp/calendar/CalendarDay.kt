package com.loyverse.dashboard.mvp.calendar

import java.text.SimpleDateFormat
import java.util.*


data class CalendarDay(val year: Int, val month: Int, val day: Int) {

    fun getDate(): Date {
        val originalFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val monthFormat = if (month.toString().length == 1) { "0$month" } else "$month"
        val result = year.toString() + monthFormat + day.toString()
        return originalFormat.parse(result)
    }
}