package com.latinka.tinkawork.timeRecord.ui.viewholders

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.firestore.QueryDocumentSnapshot

import com.latinka.tinkawork.R
import com.latinka.tinkawork.timeRecord.ui.adapters.TimeRecordsAdapter

class AttendanceGroupsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val dateView = view.findViewById<TextView>(R.id.dateView)
    private val recyclerEntryTimeRecordsView = view.findViewById<RecyclerView>(R.id.recyclerEntryTimeRecords)

    @SuppressLint("SetTextI18n")
    fun render(
        key: String,
        elements: List<QueryDocumentSnapshot>,
        onTimeRecordNavigation: (timeRecordId: String) -> Unit
    ) {
        dateView.text = key[0].uppercase() + key.substring(1)

        val timeRecordAdapter = TimeRecordsAdapter(onTimeRecordNavigation)
        timeRecordAdapter.setTimeRecords(elements)
        recyclerEntryTimeRecordsView.adapter = timeRecordAdapter
    }
}