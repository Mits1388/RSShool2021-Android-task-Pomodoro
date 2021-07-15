package com.example.pomodoro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.pomodoro.databinding.FragmentPomodoroBinding


class PomodoroFragment : Fragment() {

    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.addNewStopwatchButton.setOnClickListener {
            Toast.makeText(activity,"[fq",Toast.LENGTH_LONG).show()
        }
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPomodoroBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PomodoroFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}