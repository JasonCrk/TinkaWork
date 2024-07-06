package com.latinka.tinkawork.timeRecord.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.firestore.QueryDocumentSnapshot

import com.latinka.tinkawork.R
import com.latinka.tinkawork.timeRecord.ui.viewholders.TimeRecordsViewHolder

class TimeRecordsAdapter(
    private val onTimeRecordNavigation: (timeRecordId: String) -> Unit
) : RecyclerView.Adapter<TimeRecordsViewHolder>() {

    private var timeRecords: List<QueryDocumentSnapshot> = emptyList()

    fun setTimeRecords(data: List<QueryDocumentSnapshot>) { timeRecords = data }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeRecordsViewHolder {
        return TimeRecordsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_time_record, parent, false))
    }

    override fun onBindViewHolder(holder: TimeRecordsViewHolder, position: Int) {
        holder.render(timeRecords[position], onTimeRecordNavigation)
    }

    override fun getItemCount(): Int = timeRecords.size
}