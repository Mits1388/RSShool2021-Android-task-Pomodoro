package com.example.pomodoro

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.databinding.StopwatchItemBinding


class StopwatchViewHolder(private val binding: StopwatchItemBinding,// передаем во ViewHolder сгенерированный класс байдинга для разметки элемента RecyclerView. В родительский ViewHolder передаем bindig.root т.е. ссылку на View данного элемента RecyclerView
                          private val listener: StopwatchListener,
                          private val resources: Resources
                          ): RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) { //в метод bind передаем экземпляр Stopwatch, он приходит к нам из метода onBindViewHolder адаптера и содержит актуальные параметры для данного элемента списка.
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime() //пока просто выводим время секундомера.

        if(stopwatch.isStarted){
            startTimer(stopwatch)
        }else{
            stopTimer(stopwatch)
        }
        initButtonsListeners(stopwatch)
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {

       /* binding.startPauseButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs)
            } else {
                listener.start(stopwatch.id)
            }*/

        binding.buttonStartStop.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs)
            } else {
                listener.start(stopwatch.id)
            }
        }

        //binding.restartButton.setOnClickListener { listener.reset(stopwatch.id) }

        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }

    }

    private fun stopTimer(stopwatch: Stopwatch) {

       /* val drawable = resources.getDrawable(R.drawable.ic_baseline_play_arrow_24)
        binding.startPauseButton.setImageDrawable(drawable)*/

        binding.buttonStartStop.setText("start")

        timer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun startTimer(stopwatch: Stopwatch) {

      /*  val drawable = resources.getDrawable(R.drawable.ic_baseline_pause_24)
        binding.startPauseButton.setImageDrawable(drawable)*/

        binding.buttonStartStop.setText("stop")
        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

         binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer? {

        return object : CountDownTimer(PERIOD, UNIT_TEN_MS){
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                stopwatch.currentMs += interval
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            }

        }
    }


    private fun Long.displayTime(): String { //данный метод расширения для Long конвертирует текущее значение таймера в миллисекундах в формат “HH:MM:SS:MsMs” и возвращает соответствующую строку
        if (this <= 0L) {
            return START_TIME
        }
       // val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60
        val ms = this % 1000 / 10

        return "${displaySlot(m)}:${displaySlot(s)}:${displaySlot(ms)}" // displaySlot(h)}:$
    }
    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {
        private const val START_TIME = "00:00:00"

        private const val UNIT_TEN_MS = 10L
        private const val PERIOD  = 1000L * 60L * 60L * 24L // Day
    }
}