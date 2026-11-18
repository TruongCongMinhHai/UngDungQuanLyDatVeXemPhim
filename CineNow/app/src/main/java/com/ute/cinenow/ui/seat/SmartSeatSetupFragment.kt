package com.ute.cinenow.ui.seat

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentSmartSeatSetupBinding

class SmartSeatSetupFragment : Fragment() {

    private var _binding: FragmentSmartSeatSetupBinding? = null
    private val binding get() = _binding!!

    private var peopleCount = 1
    private var selectedZone = "Khu vực trung tâm"
    private var isAvoidCorner = false
    private var isAvoidFrontRow = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmartSeatSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupPeopleCounter()
        setupZoneOptions()
        setupExtraPreferences()
        setupNavigationButtons()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
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
                Toast.makeText(requireContext(), "Tối đa 8 ghế liền nhau", Toast.LENGTH_SHORT).show()
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

        options.forEach { clicked ->
            clicked.container.setOnClickListener {
                // Trả tất cả về nền xám nhạt
                options.forEach { opt ->
                    opt.container.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
                    opt.dot.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
                    opt.title.setTextColor(Color.parseColor("#0F172A"))
                }

                // Bấm vào khu vực nào thì sáng nền tím nhạt + chấm tròn tím đậm
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
            Toast.makeText(
                requireContext(),
                "Đang gợi ý $peopleCount ghế tại $selectedZone...",
                Toast.LENGTH_SHORT
            ).show()
            navigateToSeatSelection()
        }

        binding.btnManualSelect.setOnClickListener {
            navigateToSeatSelection()
        }
    }

    private fun navigateToSeatSelection() {
        try {
            findNavController().navigate(R.id.seatSelectionFragment)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Chuyển sang sơ đồ chọn ghế", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}