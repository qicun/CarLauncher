/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.text.format.DateFormat
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.util.ContextUtil
import java.util.Calendar
import java.util.Locale

class Dates {

    private val PATTERN_TIME_HM_24 = "yyyy/MM/dd  HH:mm"
    private val PATTERN_TIME_HM_12 = "yyyy/MM/dd  hh:mm aa"

    private val formatHm24 = SimpleDateFormat(PATTERN_TIME_HM_24, Locale.getDefault())

    private val formatHm12 = SimpleDateFormat(PATTERN_TIME_HM_12, Locale.getDefault())

    fun getTimeFormat(context: Context = ContextUtil.context): String {
        return if (is24HourFormat(context)) {
            formatHm24.format(System.currentTimeMillis())
        } else {
            formatHm24.format(System.currentTimeMillis())
        }
//            .removeRange(0, 10)
    }

    private fun is24HourFormat(context: Context = ContextUtil.context): Boolean {
        return DateFormat.is24HourFormat(context)
    }

    fun getNowDateWeek(context: Context): String {
        val calendar = Calendar.getInstance()
        val mon = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val week = calendar[Calendar.DAY_OF_WEEK]
        return String.format("%d月%d日  %s", mon + 1, day, getDayOfWeek(context, week))
    }

    private fun getDayOfWeek(context: Context, week: Int): String {
        context.getStringArray(R.array.week).apply {
            if (size == 7) {
                return when (week) {
                    Calendar.MONDAY -> this[1]
                    Calendar.TUESDAY -> this[2]
                    Calendar.WEDNESDAY -> this[3]
                    Calendar.THURSDAY -> this[4]
                    Calendar.FRIDAY -> this[5]
                    Calendar.SATURDAY -> this[6]
                    Calendar.SUNDAY -> this[0]
                    else -> ""
                }
            }
        }
        return ""
    }

}