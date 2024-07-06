package com.latinka.tinkawork.timeRecord.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.firestore.QueryDocumentSnapshot

import com.latinka.tinkawork.R
import com.latinka.tinkawork.timeRecord.ui.viewholders.AttendanceGroupsViewHolder

class AttendanceGroupsAdapter(
    private val groups: Map<String, List<QueryDocumentSnapshot>>,
    private val onTimeRecordNavigation: (timeRecordId: String) -> Unit
) : RecyclerView.Adapter<AttendanceGroupsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceGroupsViewHolder {
        return AttendanceGroupsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_attendance_group, parent, false))
    }

    override fun onBindViewHolder(holder: AttendanceGroupsViewHolder, position: Int) {
        val key = groups.keys.elementAt(position)
        val timeRecords = groups.values.elementAt(position)

        holder.render(key, timeRecords, onTimeRecordNavigation)
    }

    override fun getItemCount(): Int = groups.size
}