package com.latinka.tinkawork.history.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.latinka.tinkawork.R
import com.latinka.tinkawork.history.domain.History
import com.latinka.tinkawork.history.ui.viewholders.HistoryViewHolder

class HistoryAdapter(private val list: List<History>): RecyclerView.Adapter<HistoryViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        return HistoryViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history= list[position]
        holder.bind(history)

        holder.itemView.setOnClickListener{
        }
    }
}