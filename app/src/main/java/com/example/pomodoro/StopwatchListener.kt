package com.example.pomodoro

interface StopwatchListener {

    fun start(id: Int)

    fun stop(id: Int, currentMs: Long, currentMsView: Long , isFinish: Boolean)

    //fun reset(id: Int)

    fun delete(id: Int)

}