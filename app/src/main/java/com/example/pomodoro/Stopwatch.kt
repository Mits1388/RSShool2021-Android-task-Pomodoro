package com.example.pomodoro

data class Stopwatch(
    val id: Int, //чтобы отличать айтемы друг от друга
    var startMinutes: Long,
    var currentMs: Long, //количество миллисекунд прошедших со старта
    var isStarted: Boolean = true, // работает ли секундомер или остановлен
    var isFinished: Boolean = false
)
