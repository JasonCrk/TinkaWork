package com.latinka.tinkawork.timeRecord.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope

import com.google.firebase.auth.FirebaseAuth
import com.latinka.tinkawork.R

import com.latinka.tinkawork.databinding.FragmentAssistanceDetailsScreenBinding
import com.latinka.tinkawork.timeRecord.viewmodel.AssistanceDetailsScreenViewModel
import com.latinka.tinkawork.timeRecord.viewmodel.events.AssistanceDetailsScreenEvent

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssistanceDetailsScreenFragment : Fragment() {

    private lateinit var binding: FragmentAssistanceDetailsScreenBinding

    private val assistanceDetailsScreenViewModel : AssistanceDetailsScreenViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssistanceDetailsScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToHistoryScreenBtn.setOnClickListener {
            goBack()
        }

        arguments?.let {
            val timeRecordId = it.getString("timeRecordId")

            if (timeRecordId == null) {
                goBack()
                return
            }

            onListeningEvents()
            assistanceDetailsScreenViewModel
                .loadingTimeRecord(timeRecordId, auth.currentUser!!.uid)
        }
    }

    private fun goBack() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_container, HistoryScreenFragment.newInstance())
        transaction.commit()
    }

    private fun onListeningEvents() {
        lifecycleScope.launch {
            assistanceDetailsScreenViewModel.screenEvent.collectLatest { event ->
                binding.apply {
                    when (event) {
                        AssistanceDetailsScreenEvent.Loading -> {
                        }
                        AssistanceDetailsScreenEvent.Success -> {
                            stopLoading()
                            showEntryTimeDate()
                            showTotalWorkingTime()
                            showAssistanceTimes()
                        }
                        is AssistanceDetailsScreenEvent.Error -> {
                        }
                    }
                }
            }
        }
    }

    private fun stopLoading() {
        binding.apply {
            assistanceTimesLoading.visibility = View.GONE
            entryTimeDateLoading.visibility = View.GONE
            totalWorkingTimeLoading.visibility = View.GONE
        }
    }

    private fun showTotalWorkingTime() {
        binding.apply {
            totalWorkingTime.visibility = View.VISIBLE
            assistanceDetailsScreenViewModel.screenState.value.totalWorkingTime.let {
                totalWorkingTime.text = it ?: "00:00:00"
            }
        }
    }

    private fun showEntryTimeDate() {
        binding.apply {
            entryTimeDate.visibility = View.VISIBLE
            assistanceDetailsScreenViewModel.screenState.value.entryTimeFullDate?.let {
                entryTimeDate.text = it
            }
        }
    }

    private fun showAssistanceTimes() {
        binding.apply {
            assistanceTimes.visibility = View.VISIBLE

            val state = assistanceDetailsScreenViewModel.screenState.value

            entryTimeRecord.text = state.entryTime

            if (state.startBreakAndEndBreakTimes != null) {
                breakTimesContainer.visibility = View.VISIBLE
                breakTimes.text = state.startBreakAndEndBreakTimes
            }

            if (state.departureTime != null) {
                departureTimeRecordContainer.visibility = View.VISIBLE
                departureTimeRecord.text = state.departureTime
            }
        }
    }

    companion object {
        fun newInstance(timeRecordId: String): AssistanceDetailsScreenFragment {
            val fragment = AssistanceDetailsScreenFragment()
            val args = Bundle()
            args.putString("timeRecordId", timeRecordId)
            fragment.arguments = args
            return fragment
        }
    }
}