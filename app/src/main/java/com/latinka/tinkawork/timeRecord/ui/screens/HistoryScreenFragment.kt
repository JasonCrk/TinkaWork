package com.latinka.tinkawork.timeRecord.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope

import com.google.firebase.auth.FirebaseAuth
import com.latinka.tinkawork.R

import com.latinka.tinkawork.databinding.FragmentHistoryScreenBinding
import com.latinka.tinkawork.timeRecord.ui.adapters.AttendanceGroupsAdapter
import com.latinka.tinkawork.timeRecord.viewmodel.HistoryScreenViewModel
import com.latinka.tinkawork.timeRecord.viewmodel.events.HistoryScreenEvent

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryScreenFragment : Fragment() {

    private lateinit var binding: FragmentHistoryScreenBinding

    private val auth = FirebaseAuth.getInstance()
    private val historyScreenViewModel : HistoryScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onListeningEvents()
        historyScreenViewModel.loadingAssistants(auth.currentUser!!.uid)
    }

    private fun onListeningEvents() {
        lifecycleScope.launch {
            historyScreenViewModel.historyScreenEvent.collectLatest { event ->
                binding.apply {
                    when (event) {
                        HistoryScreenEvent.Loading -> {}
                        is HistoryScreenEvent.Success -> {
                            val historyAdapter = AttendanceGroupsAdapter(event.groups) {
                                val transaction = parentFragmentManager.beginTransaction()
                                transaction.replace(
                                    R.id.nav_container,
                                    AssistanceDetailsScreenFragment.newInstance(it))
                                transaction.commit()
                            }
                            recyclerHistory.adapter = historyAdapter
                        }
                        is HistoryScreenEvent.Error -> {
                            Log.e("HISTORY_ERROR", event.message)
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() : HistoryScreenFragment = HistoryScreenFragment()
    }
}