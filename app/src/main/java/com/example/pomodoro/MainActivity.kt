package com.example.pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoro.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), StopwatchListener , LifecycleObserver {

    lateinit var binding: ActivityMainBinding
    private val stopwatches = mutableListOf<Stopwatch>() // в этом списке будут хранится стейты секундомеров
    private val stopwatchAdapter = StopwatchAdapter(this)
    private var nextId = 0
    private var startMinutes: Long = 0L



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* мы будет определять момент, когда приложение ушло в фон и когда вышло на передний план.
    Проще всего это делать фиксируя изменение жизненного цикла приложения, используя ProcessLifecycleOwner.*/
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)


       binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // startTime = System.currentTimeMillis()


        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }



        
        binding.addNewStopwatchButton.setOnClickListener {

             startMinutes = binding.editMinutes.text.toString().toLongOrNull()?.times(60000L) ?: 0L

            if(binding.editMinutes.text.toString().equals("")){
                Toast.makeText(this,"Enter a value",Toast.LENGTH_SHORT).show()
            }else if(binding.editMinutes.text.toString().toLong() <= 1440L){
                stopwatches.add(Stopwatch(nextId++, startMinutes, currentMs = startMinutes,currentMsView = startMinutes,false,false))
                stopwatchAdapter.submitList(stopwatches.toList())
            }else{
                Toast.makeText(this,"Enter the correct data",Toast.LENGTH_SHORT).show()
            }

        }


        /*lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                binding.timeView.text = (System.currentTimeMillis() - startTime).displayTime()
                delay(INTERVAL)
            }
        }*/
    }

    override fun start(id: Int) {
        changeStopwatch(id, null, null, true, false)
    }


    override fun stop(id: Int, currentMs: Long, currentMsView: Long, isFinished: Boolean) {
        changeStopwatch(id, currentMs, currentMsView,false, true)
    }

  /*  override fun reset(id: Int) {
        changeStopwatch(id, 0L, false)
    }*/

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(id: Int, currentMs: Long?,currentMsView: Long?, isStarted: Boolean, isFinished: Boolean) { //?

        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if(it.id == id){
                newTimers.add(Stopwatch(it.id, startMinutes, currentMs?: it.currentMs, currentMsView?: it.currentMsView,isStarted, isFinished))
            }else{
                newTimers.add(it)
            }
        }
        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }

    /* В onCreate() добавляем обсервер ProcessLifecycleOwner.get().lifecycle.addObserver(this),
     передаем туда this - теперь измененения жизненного цикла будут передаваться в активити,
     т.е. будут вызываться методы, которые мы пометили соответствующими аннотациями.*/

    //STOP - приложение ушло в фон
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, startMinutes)
       // startService(startIntent)
        startIntent.putExtra(TIME_SYSTEM, System.currentTimeMillis())
        startService(startIntent)
    }

    //START - приложение на переднем плане
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }


}
