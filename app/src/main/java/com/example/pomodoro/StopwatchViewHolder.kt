package com.example.pomodoro

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.databinding.StopwatchItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.minutes


class StopwatchViewHolder(private val binding: StopwatchItemBinding,// передаем во ViewHolder сгенерированный класс байдинга для разметки элемента RecyclerView. В родительский ViewHolder передаем bindig.root т.е. ссылку на View данного элемента RecyclerView
                          private val listener: StopwatchListener,
                          private val resources: Resources
                          ): RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null
   // private var current = 0L

    fun bind(stopwatch: Stopwatch) { //в метод bind передаем экземпляр Stopwatch, он приходит к нам из метода onBindViewHolder адаптера и содержит актуальные параметры для данного элемента списка.

        binding.root.setCardBackgroundColor(resources.getColor(R.color.white)) // установка цвета нач и после уд
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime() // выводим время секундомера.



        //customView установка Current, Period
        binding.customViewOne.setPeriod(stopwatch.startMinutes)
        binding.customViewOne.setCurrent(stopwatch.currentMsView)


        // Never use GlobalScope for real projects !!!
        /*GlobalScope.launch {
            while (current < PERIODI * REPEAT) {
                current += INTERVAL
                binding.customViewOne.setCurrent(current)
                delay(INTERVAL)
            }
        }*/



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


                if(stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs, stopwatch.currentMsView,false)


       //  binding.customViewOne.setPeriod(PERIODI)
                // Never use GlobalScope for real projects !!!
             /*   GlobalScope.launch {
                    while (current < PERIODI * REPEAT) {
                        current += INTERVAL
                        binding.customViewOne.setCurrent(current)
                        delay(INTERVAL)
                    }
                }*/


            } else {
                listener.start(stopwatch.id)

                 // binding.customViewOne.setCurrent(0)

              /*  GlobalScope.launch {
                        binding.customViewOne.setCurrent(current)
                        delay(INTERVAL)
                }
*/

            }
        }

        //binding.restartButton.setOnClickListener { listener.reset(stopwatch.id) }

        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }

    }


    @SuppressLint("SetTextI18n")
    private fun stopTimer(stopwatch: Stopwatch) {

       /* val drawable = resources.getDrawable(R.drawable.ic_baseline_play_arrow_24)
        binding.startPauseButton.setImageDrawable(drawable)*/

        binding.buttonStartStop.setText("start")

        timer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    @SuppressLint("SetTextI18n")
    private fun startTimer(stopwatch: Stopwatch) {

      /*  val drawable = resources.getDrawable(R.drawable.ic_baseline_pause_24)
        binding.startPauseButton.setImageDrawable(drawable)*/

     binding.buttonStartStop.setText("stop")



        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()
         binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()


      // binding.customViewOne.setPeriod(stopwatch.startMinutes)

    // binding.customViewOne.setCurrent(stopwatch.currentMs)

       /* GlobalScope.launch {
            while (current < PERIODI * REPEAT) {
                current += INTERVAL
                binding.customViewOne.setCurrent(current)
                delay(INTERVAL)
            }
        }
        */
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer? {

        return object : CountDownTimer(PERIOD, UNIT_TEN_MS){

            var interval = UNIT_TEN_MS

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                //binding.customViewOne.setPeriod(stopwatch.startMinutes)
                stopwatch.currentMs -= interval
                stopwatch.currentMsView += interval
                //var x = stopwatch.currentMs + interval
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()

                if(stopwatch.currentMs.displayTime().equals(START_TIME)){
                    binding.buttonStartStop.setText("end")
                    binding.buttonStartStop.isClickable = false
                        binding.root.setCardBackgroundColor(resources.getColor(R.color.red_200)) // установка цвета после завершения
                    binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                    binding.customViewOne.setCurrent(0)
                    binding.blinkingIndicator.isInvisible = true
                    (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
                }else {
                    binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                    binding.customViewOne.setPeriod(stopwatch.startMinutes)
                    binding.customViewOne.setCurrent(stopwatch.currentMsView)

                }
            }

            override fun onFinish() {
             binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                //binding.customViewOne.setCurrent(0)
            }

        }
    }

/*
    private fun Long.displayTime(): String { //данный метод расширения для Long конвертирует текущее значение таймера в миллисекундах в формат “HH:MM:SS:MsMs” и возвращает соответствующую строку
        if (this <= 0L) {
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60
        //val ms = this % 1000 / 10

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}" // displaySlot(ms)}:$
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {
        //private const val INTERVAL = 100L
      //  private const val PERIODI = 1000L * 120 // 30 sec
      //  private const val REPEAT = 10 // 10 times


        private const val START_TIME = "00:00:00"
        private const val UNIT_TEN_MS = 10L

        private const val PERIOD  = 1000L * 60L * 60L * 24L // Day
    }
 */
}