package com.example.pomodoro

const val START_TIME = "00:00:00"
const val UNIT_TEN_MS = 10L
const val PERIOD  = 1000L * 60L * 60L * 24L
const val FILL = 0
const val INVALID = "INVALID"
const val COMMAND_START = "COMMAND_START"
const val COMMAND_STOP = "COMMAND_STOP"
const val COMMAND_ID = "COMMAND_ID"
const val STARTED_TIMER_TIME_MS = "STARTED_TIMER_TIME"
const val CHANNEL_ID = "Channel_ID"
const val NOTIFICATION_ID = 777
const val INTERVAL = 1000L
const val TIME_SYSTEM = "TIME_SYSTEM"

fun Long.displayTime(): String {
    if (this <= 0L) {
        return START_TIME
    }
    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60

    return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
}

private fun displaySlot(count: Long): String {
    return if (count / 10L > 0) {
        "$count"
    } else {
        "0$count"
    }
}