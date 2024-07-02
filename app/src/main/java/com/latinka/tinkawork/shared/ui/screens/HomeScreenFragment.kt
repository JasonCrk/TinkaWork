package com.latinka.tinkawork.shared.ui.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope

import com.google.android.material.button.MaterialButton

import com.latinka.tinkawork.R
import com.latinka.tinkawork.databinding.FragmentHomeScreenBinding
import com.latinka.tinkawork.shared.viewmodel.HomeScreenViewModel
import com.latinka.tinkawork.shared.viewmodel.states.HomeScreenEvent
import com.latinka.tinkawork.timeRecord.data.models.WorkingStatus

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class HomeScreenFragment : Fragment() {

    private lateinit var binding: FragmentHomeScreenBinding
    private val homeScreenViewModel : HomeScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onListeningEvents()
        homeScreenViewModel.startHomeState()
    }

    private fun onListeningEvents() {
        lifecycleScope.launch {
            homeScreenViewModel.screenEvent.collectLatest { event ->
                binding.apply {
                    when (event) {
                        HomeScreenEvent.Loading -> {}
                        is HomeScreenEvent.Success -> {
                            circularProgress.visibility = View.INVISIBLE
                            onWorkingStatus(event.status)
                        }
                        is HomeScreenEvent.Error -> {
                            Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onWorkingStatus(status: WorkingStatus) {
        binding.apply {
            workingStatus.visibility = View.VISIBLE
            workingStatus.text = status.fullName

            when (status) {
                WorkingStatus.DAY_OFF, WorkingStatus.EARLY -> {}
                WorkingStatus.ENABLE_TO_WORK -> {
                    workingTimer.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.dark_white)
                    )
                    workingStatus.setBackgroundResource(R.drawable.bg_working_state_working)
                    workingStatus.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.green)
                    )

                    disableMaterialButton(departureButton)
                    disableMaterialButton(breakButton)
                    enableEntryButton()
                    enableEntryVerticalLine()
                }
                WorkingStatus.WORKING -> {
                    setWorkingStyle()

                    disableMaterialButton(entryButton)
                    disableVerticalLine(entryVerticalLine)
                    disableMaterialButton(departureButton)
                    enableBreakButton()
                    enableBreakVerticalLine()
                }
                WorkingStatus.RELAXING -> {
                    setRelaxingStyle()

                    enableStopBreakButton()

                    disableMaterialButton(entryButton)
                    disableVerticalLine(entryVerticalLine)
                    disableMaterialButton(departureButton)
                    enableStopBreakVerticalLine()
                }
                WorkingStatus.WORKING_AFTER_RELAXING -> {
                    setWorkingStyle()

                    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                    homeScreenViewModel.screenState.value.apply {
                        breakTimeRegisterContent.text = "${dateFormat.format(startBreakTime!!)} - ${dateFormat.format(endBreakTime!!)}"
                        breakTimeRegister.visibility = View.VISIBLE
                    }

                    disableAllButtons()
                }
                WorkingStatus.ENABLE_TO_DEPARTURE -> {
                    setWorkingStyle()

                    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                    homeScreenViewModel.screenState.value.apply {
                        breakTimeRegisterContent.text = "${dateFormat.format(startBreakTime!!)} - ${dateFormat.format(endBreakTime!!)}"
                        breakTimeRegister.visibility = View.VISIBLE
                    }

                    disableMaterialButton(entryButton)
                    disableVerticalLine(entryVerticalLine)
                    disableMaterialButton(breakButton)
                    disableVerticalLine(breakVerticalLine)
                    enableDepartureButton()
                }
                WorkingStatus.WORK_COMPLETED -> {
                    setDepartureStyle()

                    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                    homeScreenViewModel.screenState.value.apply {
                        breakTimeRegisterContent.text = "${dateFormat.format(startBreakTime!!)} - ${dateFormat.format(endBreakTime!!)}"
                        departureTimeRegisterContent.text = dateFormat.format(endWorkTime!!)
                        breakTimeRegister.visibility = View.VISIBLE
                        departureTimeRegister.visibility = View.VISIBLE
                    }

                    disableAllButtons()
                }
            }
        }
    }

    private fun disableMaterialButton(button: MaterialButton) {
        button.isEnabled = false
        button.background.setTint(
            ContextCompat.getColor(requireContext(), R.color.bg_disable)
        )
        button.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.disable_text)
        )
        button.setIconTintResource(R.color.disable_text)
    }

    private fun enableEntryButton() {
        binding.apply {
            entryButton.isEnabled = true
            entryButton.background.setTint(
                ContextCompat.getColor(requireContext(), R.color.green)
            )
            entryButton.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
            entryButton.setIconTintResource(R.color.white)
        }
    }

    private fun enableBreakButton() {
        binding.apply {
            breakButton.isEnabled = true
            breakButton.text = ContextCompat.getString(requireContext(), R.string.break_button)
            breakButton.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_coffee_24)
            breakButton.background.setTint(
                ContextCompat.getColor(requireContext(), R.color.yellow)
            )
            breakButton.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.black)
            )
            breakButton.setIconTintResource(R.color.black)
        }
    }

    private fun enableStopBreakButton() {
        binding.apply {
            breakButton.isEnabled = true
            breakButton.text = ContextCompat.getString(requireContext(), R.string.stop_break_button)
            breakButton.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_resume_work_24)
            breakButton.background.setTint(
                ContextCompat.getColor(requireContext(), R.color.green)
            )
            breakButton.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
            breakButton.setIconTintResource(R.color.white)
        }
    }

    private fun enableDepartureButton() {
        binding.apply {
            departureButton.isEnabled = true
            departureButton.background.setTint(
                ContextCompat.getColor(requireContext(), R.color.red)
            )
            departureButton.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
            departureButton.setIconTintResource(R.color.white)
        }
    }

    private fun disableVerticalLine(view: View) {
        view.background.setTint(
            ContextCompat.getColor(requireContext(), R.color.bg_disable)
        )
    }

    private fun enableEntryVerticalLine() {
        binding.entryVerticalLine.background.setTint(
            ContextCompat.getColor(requireContext(), R.color.green)
        )
    }

    private fun enableBreakVerticalLine() {
        binding.breakVerticalLine.background.setTint(
            ContextCompat.getColor(requireContext(), R.color.yellow)
        )
    }

    private fun enableStopBreakVerticalLine() {
        binding.breakVerticalLine.background.setTint(
            ContextCompat.getColor(requireContext(), R.color.green)
        )
    }

    private fun setWorkingStyle() {
        binding.apply {
            workingTimer.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.green)
            )
            workingStatus.setBackgroundResource(R.drawable.bg_working_state_working)
            workingStatus.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.green)
            )
        }
    }

    private fun setRelaxingStyle() {
        binding.apply {
            workingTimer.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.dark_yellow)
            )
            workingStatus.setBackgroundResource(R.drawable.bg_working_state_break)
            workingStatus.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.yellow_dark_to_yellow)
            )
        }
    }

    private fun setDepartureStyle() {
        binding.apply {
            workingTimer.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.red)
            )
            workingStatus.setBackgroundResource(R.drawable.bg_working_state_completed)
            workingStatus.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.red)
            )
        }
    }

    private fun disableAllButtons() {
        binding.apply {
            disableMaterialButton(entryButton)
            disableVerticalLine(entryVerticalLine)
            disableMaterialButton(breakButton)
            disableVerticalLine(breakVerticalLine)
            disableMaterialButton(departureButton)
        }
    }

    companion object {
        fun newInstance() : HomeScreenFragment = HomeScreenFragment()
    }
}