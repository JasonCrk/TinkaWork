package com.latinka.tinkawork.shared.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.latinka.tinkawork.R

import com.latinka.tinkawork.databinding.FragmentHistoryScreenBinding

class HistoryScreenFragment : Fragment() {

    private lateinit var binding: FragmentHistoryScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val historyRecycler = view.findViewById<RecyclerView>(R.id.recyclerHistory)

        val listHistory = listOf<History>(
            History(
                fecha = "10",
                dia = "Lunes"
            ),
            History(
                fecha = "11",
                dia = "Martes"
            ),
            History(
                fecha = "12",
                dia = "Miercoles"
            ),
            History(
                fecha = "13",
                dia = "Jueves"
            ),
            History(
                fecha =  "14",
                dia = "Viernes"
            ),
            History(
                fecha = "10",
                dia = "Lunes"
            ),
            History(
                fecha = "11",
                dia = "Martes"
            ),
            History(
                fecha = "12",
                dia = "Miercoles"
            ),
            History(
                fecha = "13",
                dia = "Jueves"
            ),
            History(
                fecha =  "14",
                dia = "Viernes"
            )
        )
        val adapter = HistoryAdapter(listHistory)
        historyRecycler.adapter = adapter
        historyRecycler.layoutManager= GridLayoutManager(view.context, 3);
    }
}