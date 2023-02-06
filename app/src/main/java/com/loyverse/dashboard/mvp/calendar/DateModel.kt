package com.loyverse.dashboard.mvp.calendar

import android.os.Build
import java.text.SimpleDateFormat
import java.util.*

class DateModel {

    private val deviceLocaleLanguage = Locale.getDefault().language
    val deviceLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Locale.Builder().setLanguageTag(deviceLocaleLanguage).build()
    } else Locale.getDefault()

    val calendar: Calendar = Calendar.getInstance(deviceLocale)
    val firstDayOfWeek = calendar.firstDayOfWeek

    fun days(): List<Date> {
        val startDate = calendar.time
        val startDay = 1

        calendar.set(Calendar.DATE, startDay)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek
        val daysCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + dayOfWeek
        calendar.add(Calendar.DATE, -dayOfWeek)

        val days = ArrayList<Date>()
        for (i in 0..daysCount) {
            days.add(calendar.time)
            calendar.add(Calendar.DATE, 1)
        }

        calendar.time = startDate
        return days
    }

    fun isCurrentMonth(date: Date): Boolean {
        val dateFormat = SimpleDateFormat("yyyy.MM", deviceLocale)
        val currentMonth = dateFormat.format(calendar.time)
        return currentMonth == dateFormat.format(date)
    }

    fun isEqualsMonth(dateF: Date, dateT: Date): Boolean {
        val dateFormat = SimpleDateFormat("yyyy.MM", deviceLocale)
        val fr = dateFormat.format(dateF)
        val to = dateFormat.format(dateT)
        return fr == to
    }

    fun isEqualsYear(dateF: Date, dateT: Date): Boolean {
        val yearFormat = SimpleDateFormat("yyyy", deviceLocale)
        val fr = yearFormat.format(dateF)
        val to = yearFormat.format(dateT)
        return fr == to
    }

    fun isFutureDays(date: Date): Boolean {
        return date.after(Calendar.getInstance().time)
    }

    fun prevMonth() {
        calendar.add(Calendar.MONTH, -1)
    }
}