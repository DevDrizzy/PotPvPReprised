package net.frozenorb.potpvp.kt.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object TimeUtils {

    private val mmssBuilder: ThreadLocal<StringBuilder> = ThreadLocal.withInitial { java.lang.StringBuilder() }
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm")

    @JvmStatic
    fun formatIntoHHMMSS(secs: Int): String {
        return formatIntoMMSS(secs)
    }

    @JvmStatic
    fun formatLongIntoHHMMSS(secs: Long): String {
        val unconvertedSeconds = secs.toInt()
        return formatIntoMMSS(unconvertedSeconds)
    }

    @JvmStatic
    fun formatIntoMMSS(secs: Int): String {
        var secs = secs
        val seconds = secs % 60
        secs -= seconds
        var minutesCount = (secs / 60).toLong()
        val minutes = minutesCount % 60L
        minutesCount -= minutes
        val hours = minutesCount / 60L
        val result = mmssBuilder.get()
        result.setLength(0)
        if (hours > 0L) {
            if (hours < 10L) {
                result.append("0")
            }
            result.append(hours)
            result.append(":")
        }
        if (minutes < 10L) {
            result.append("0")
        }
        result.append(minutes)
        result.append(":")
        if (seconds < 10) {
            result.append("0")
        }
        result.append(seconds)
        return result.toString()
    }

    @JvmStatic
    fun formatLongIntoMMSS(secs: Long): String {
        val unconvertedSeconds = secs.toInt()
        return formatIntoMMSS(unconvertedSeconds)
    }

    @JvmStatic
    fun formatIntoDetailedString(secs: Int): String {
        if (secs == 0) {
            return "0 seconds"
        }
        val remainder = secs % 86400
        val days = secs / 86400
        val hours = remainder / 3600
        val minutes = remainder / 60 - hours * 60
        val seconds = remainder % 3600 - minutes * 60
        val fDays = if (days > 0) " " + days + " day" + if (days > 1) "s" else "" else ""
        val fHours = if (hours > 0) " " + hours + " hour" + if (hours > 1) "s" else "" else ""
        val fMinutes = if (minutes > 0) " " + minutes + " minute" + if (minutes > 1) "s" else "" else ""
        val fSeconds = if (seconds > 0) " " + seconds + " second" + if (seconds > 1) "s" else "" else ""
        return (fDays + fHours + fMinutes + fSeconds).trim { it <= ' ' }
    }

    @JvmStatic
    fun formatIntoShortString(secs: Int): String {
        if (secs == 0) {
            return "0 seconds"
        }

        val remainder = secs % 86400
        val days = secs / 86400
        val hours = remainder / 3600
        val minutes = remainder / 60 - hours * 60
        val seconds = remainder % 3600 - minutes * 60

        val builder = StringBuilder()

        if (days > 0) {
            if (hours > 0) {
                builder.append("$days day${if (days == 1) "" else "s"} $hours hour${if (hours == 1) "" else "s"}")
            } else {
                builder.append("$days day${if (days == 1) "" else "s"}")
            }
        } else if (hours > 0) {
            if (minutes > 0) {
                builder.append("$hours hour${if (hours == 1) "" else "s"} $minutes minute${if (minutes == 1) "" else "s"}")
            } else {
                builder.append("$hours hour${if (hours == 1) "" else "s"}")
            }
        } else if (minutes > 0) {
            if (seconds > 0) {
                builder.append("$minutes minute${if (minutes == 1) "" else "s"} $seconds second${if (seconds == 1) "" else "s"}")
            } else {
                builder.append("$minutes minute${if (minutes == 1) "" else "s"}")
            }
        }

        return builder.trim().toString()
    }

    @JvmStatic
    fun formatLongIntoDetailedString(secs: Long): String {
        val unconvertedSeconds = secs.toInt()
        return formatIntoDetailedString(unconvertedSeconds)
    }

    @JvmStatic
    fun formatIntoCalendarString(date: Date): String {
        return dateFormat.format(date)
    }

    @JvmStatic
    fun parseTime(time: String): Int {
        if (time == "0" || time == "") {
            return 0
        }
        val lifeMatch = arrayOf("y", "w", "d", "h", "m", "s")
        val lifeInterval = intArrayOf(31_536_000, 604800, 86400, 3600, 60, 1)
        var seconds = -1
        for (i in lifeMatch.indices) {
            val matcher = Pattern.compile("([0-9]+)" + lifeMatch[i]).matcher(time)
            while (matcher.find()) {
                if (seconds == -1) {
                    seconds = 0
                }
                seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i]
            }
        }
        if (seconds == -1) {
            throw IllegalArgumentException("Invalid time provided.")
        }
        return seconds
    }

    @JvmStatic
    fun parseTimeToLong(time: String): Long {
        val unconvertedSeconds = parseTime(time)
        return unconvertedSeconds.toLong()
    }

    @JvmStatic
    fun getSecondsBetween(a: Date, b: Date): Int {
        return getSecondsBetweenLong(a, b).toInt()
    }

    @JvmStatic
    fun getSecondsBetweenLong(a: Date, b: Date): Long {
        val diff = a.getTime() - b.getTime()
        val absDiff = Math.abs(diff)
        return absDiff / 1000L
    }

}