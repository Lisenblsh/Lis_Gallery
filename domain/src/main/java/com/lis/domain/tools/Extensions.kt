package com.lis.domain.tools

import java.text.MessageFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Long.getStringDurationsFromLong(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this).toInt()
    val minutes = (TimeUnit.MILLISECONDS.toMinutes(this)
            - TimeUnit.HOURS.toMinutes(hours.toLong())).toInt()
    val seconds = (TimeUnit.MILLISECONDS.toSeconds(this)
            - TimeUnit.MINUTES.toSeconds(minutes.toLong())
            - TimeUnit.HOURS.toSeconds(hours.toLong())).toInt()
    return convertTimeFormat(hours, minutes, seconds)
}

private fun convertTimeFormat(hours: Int, minutes: Int, seconds: Int): String {
    var oldFormat = "m:s"
    var newFormat = "mm:ss"
    var stringSongDuration = MessageFormat.format("{0}:{1}", minutes, seconds)
    if (hours > 0) {
        oldFormat = "h:m:s"
        newFormat = "hh:mm:ss"
        stringSongDuration = MessageFormat.format("{0}:{1}:{2}", hours, minutes, seconds)
    }
    try {
        stringSongDuration = SimpleDateFormat(newFormat, Locale.getDefault())
            .format(SimpleDateFormat(oldFormat, Locale.getDefault()).parse(stringSongDuration))
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return stringSongDuration
}