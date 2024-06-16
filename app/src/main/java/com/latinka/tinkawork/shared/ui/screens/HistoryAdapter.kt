package com.latinka.tinkawork.shared.ui.screens

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(val list: List<History>):
RecyclerView.Adapter<HistoryViewHolder>(){
    //Instancias del View Holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        return HistoryViewHolder(inflater, parent)
    }

    //Numero de elementos a mostrar
    override fun getItemCount(): Int {
        return  list.size
    }

    //Asignacion de los datos al View Holder
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history= list[position]
        holder.bind(history)

        //Evento Click de Card View Para ver los detalles del item
        holder.itemView.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(
                context, AssistanceDetailsFragment::class.java)
            context.startActivity(intent)
        }
    }
}