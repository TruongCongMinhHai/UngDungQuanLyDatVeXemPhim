package com.ute.cinenow.ui.seat

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentSmartSeatSetupBinding
import com.ute.cinenow.ui.movie.MovieDetailFragment
import com.ute.cinenow.utils.showCineMessage

class SmartSeatSetupFragment : Fragment() {

    private var _binding: FragmentSmartSeatSetupBinding? = null
    private val binding get() = _binding!!

    private var peopleCount = 1
    private var selectedZone: String? = null
    private var isAvoidCorner = false
    private var isAvoidFrontRow = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmartSeatSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPeopleCounter()
        setupZoneOptions()
        setupExtraPreferences()
        setupNavigationButtons()
    }

    private fun setupPeopleCounter() {
        updatePeopleUI()
        binding.btnMinus.setOnClickListener {
            if (peopleCount > 1) {
                peopleCount--
                updatePeopleUI()
            }
        }
        binding.btnPlus.setOnClickListener {
            if (peopleCount < 8) {
                peopleCount++
                updatePeopleUI()
            } else {
                showCineMessage("Tối đa 8 ghế liền nhau")
            }
        }
    }

    private fun updatePeopleUI() {
        binding.tvPeopleCount.text = peopleCount.toString()
        binding.tvPeopleBadge.text = "$peopleCount người"
    }

    private fun setupZoneOptions() {
        data class ZoneOption(
            val container: LinearLayout,
            val dot: View,
            val title: TextView,
            val name: String
        )

        val options = listOf(
            ZoneOption(binding.optCenter, binding.dotOptCenter, binding.tvOptCenterTitle, "Khu vực trung tâm"),
            ZoneOption(binding.optFront, binding.dotOptFront, binding.tvOptFrontTitle, "Gần màn hình"),
            ZoneOption(binding.optBack, binding.dotOptBack, binding.tvOptBackTitle, "Hàng ghế phía sau"),
            ZoneOption(binding.optCouple, binding.dotOptCouple, binding.tvOptCoupleTitle, "Ghế đôi")
        )

        for (clicked in options) {
            clicked.container.setOnClickListener {
                for (opt in options) {
                    opt.container.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
                    opt.dot.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
                    opt.title.setTextColor(Color.parseColor("#0F172A"))
                }
                clicked.container.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EDE9FE"))
                clicked.dot.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
                clicked.title.setTextColor(Color.parseColor("#312E81"))
                selectedZone = clicked.name
            }
        }
    }

    private fun setupExtraPreferences() {
        binding.extraAvoidCorner.setOnClickListener {
            isAvoidCorner = !isAvoidCorner
            binding.checkAvoidCorner.backgroundTintList = ColorStateList.valueOf(
                if (isAvoidCorner) Color.parseColor("#5850EC") else Color.parseColor("#CBD5E1")
            )
        }
        binding.extraAvoidFrontRow.setOnClickListener {
            isAvoidFrontRow = !isAvoidFrontRow
            binding.checkAvoidFrontRow.backgroundTintList = ColorStateList.valueOf(
                if (isAvoidFrontRow) Color.parseColor("#5850EC") else Color.parseColor("#CBD5E1")
            )
        }
    }

    private fun setupNavigationButtons() {
        binding.btnFindSeats.setOnClickListener {
            val zone = selectedZone
            if (zone == null) {
                showCineMessage("Vui lòng chọn khu vực ngồi")
                return@setOnClickListener
            }

            showCineMessage(
                message = "Đã áp dụng gợi ý ghế",
                subtitle = "$peopleCount người • $zone"
            )
            navigateToSeatSelection(
                useSmartSuggestion = true,
                zone = zone
            )
        }
        binding.btnManualSelect.setOnClickListener {
            navigateToSeatSelection(
                useSmartSuggestion = false,
                zone = selectedZone ?: "Khu vực trung tâm"
            )
        }
    }

    private fun navigateToSeatSelection(useSmartSuggestion: Boolean, zone: String) {
        val args = bundleOf(
            SeatSelectionFragment.ARG_USE_SMART_SUGGESTION to useSmartSuggestion,
            SeatSelectionFragment.ARG_PEOPLE_COUNT to peopleCount,
            SeatSelectionFragment.ARG_SELECTED_ZONE to zone,
            SeatSelectionFragment.ARG_AVOID_CORNER to isAvoidCorner,
            SeatSelectionFragment.ARG_AVOID_FRONT_ROW to isAvoidFrontRow
        )
        val movieTitle = arguments?.getString(MovieDetailFragment.ARG_MOVIE_TITLE)
        if (!movieTitle.isNullOrBlank()) {
            args.putString(MovieDetailFragment.ARG_MOVIE_TITLE, movieTitle)
        }

        try {
            findNavController().navigate(R.id.seatSelectionFragment, args)
        } catch (e: Exception) {
            showCineMessage("Chuyển sang sơ đồ chọn ghế")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}