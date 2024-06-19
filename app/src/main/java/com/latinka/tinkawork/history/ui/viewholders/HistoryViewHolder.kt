package com.latinka.tinkawork.history.ui.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.latinka.tinkawork.R
import com.latinka.tinkawork.history.domain.History

class HistoryViewHolder(inflater: LayoutInflater,
                        viewGroup: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(
            R.layout.item_history, viewGroup, false)) {

    private var fecha: TextView? = null
    private var dia : TextView? = null

    init {
        fecha = itemView.findViewById(R.id.dayHistoryRegister)
        dia = itemView.findViewById(R.id.dayNameHistoryRegister)
    }
    fun bind(history: History){
        fecha?.text = history.fecha
        dia?.text = history.dia
    }
}