package com.example.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener {

    lateinit var binding: ActivityMainBinding
    private val stopwatches = mutableListOf<Stopwatch>() // в этом списке будут хранится стейты секундомеров
    private val stopwatchAdapter = StopwatchAdapter(this)
    private var nextId = 0
    private val startMinutes: Long  = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }


        
        binding.addNewStopwatchButton.setOnClickListener {

            val startMinutes = binding.editMinutes.text.toString().toLongOrNull()?.times(60000L) ?: 0L

            if(binding.editMinutes.text.toString().equals("")){
                Toast.makeText(this,"Enter a value",Toast.LENGTH_SHORT).show()
            }else if(binding.editMinutes.text.toString().toLong() <= 1440L){
                stopwatches.add(Stopwatch(nextId++, startMinutes, currentMs = startMinutes, false,false))
                stopwatchAdapter.submitList(stopwatches.toList())
            }else{
                Toast.makeText(this,"Enter the correct data",Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun start(id: Int) {
        changeStopwatch(id, null, true, false)
    }


    override fun stop(id: Int, currentMs: Long, isFinished: Boolean) {
        changeStopwatch(id, currentMs, false, true)
    }

  /*  override fun reset(id: Int) {
        changeStopwatch(id, 0L, false)
    }*/

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean, isFinished: Boolean) { //?

        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if(it.id == id){
                newTimers.add(Stopwatch(it.id, startMinutes, currentMs?: it.currentMs, isStarted, isFinished))
            }else{
                newTimers.add(it)
            }
        }
        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }

}
