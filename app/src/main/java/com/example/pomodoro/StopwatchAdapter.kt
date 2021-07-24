package com.example.pomodoro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.pomodoro.databinding.StopwatchItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopwatchAdapter(
    private val listener: StopwatchListener
): ListAdapter<Stopwatch, StopwatchViewHolder>(itemComparator) {


    //В onCreateViewHolder инфлейтим View и возвращаем созданный ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(layoutInflater, parent, false)

        return StopwatchViewHolder(binding,listener,binding.root.context.resources)
    }


    // onBindViewHolder вызывается в момент создания айтема, в моменты пересоздания (например, айтем вышел за пределы экрана, затем вернулся) и в моменты обновления айтемов (этим у нас занимается DiffUtil)
    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        holder.bind(getItem(position)) //для конкретного ViewHolder обновляем параметры
    }

    private companion object {
//Имплементация DiffUtil помогает понять RecyclerView какой айтем изменился (был удален, добавлен) и контент какого айтема изменился - чтобы правильно проиграть анимацию и показать результат пользователю.
        private val itemComparator = object : DiffUtil.ItemCallback<Stopwatch>() {


            override fun areItemsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.id == newItem.id
            }

    //В areContentsTheSame лучше проверять на равество только те параметры модели, которые влияют на её визуальное представление на экране.
            override fun areContentsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.currentMs == newItem.currentMs &&
                        oldItem.isStarted == newItem.isStarted &&
                        oldItem.isFinished== newItem.isFinished &&
                        oldItem.currentMsView == newItem.currentMsView &&
                        oldItem.startMinutes == newItem.startMinutes  // может и не надо
            }
        }
    }
}