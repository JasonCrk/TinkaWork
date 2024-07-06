package com.latinka.tinkawork.timeRecord.ui.viewholders

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot

import com.latinka.tinkawork.R
import com.latinka.tinkawork.timeRecord.data.db.TimeRecordFields

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimeRecordsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val dayOfMonth = view.findViewById<TextView>(R.id.dayOfMonth)
    private val dayOfWeekName = view.findViewById<TextView>(R.id.nameDayOfWeek)
    private val cardView = view.findViewById<CardView>(R.id.timeRecordCard)

    @SuppressLint("SetTextI18n")
    fun render(
        timeRecord: QueryDocumentSnapshot,
        onTimeRecordNavigation: (timeRecordId: String) -> Unit
    ) {
        val timeRecordCreatedAt = (timeRecord.data[TimeRecordFields.CREATED_AT] as Timestamp).toDate()

        val calendar = Calendar.getInstance()
        calendar.time = timeRecordCreatedAt

        dayOfMonth.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val weekName = SimpleDateFormat("EEEE", Locale("es", "ES")).format(calendar.time)

        dayOfWeekName.text = weekName[0].uppercase() + weekName.substring(1)

        cardView.setOnClickListener { onTimeRecordNavigation(timeRecord.id) }
    }
}