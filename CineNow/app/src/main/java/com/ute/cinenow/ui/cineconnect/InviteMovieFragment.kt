package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentInviteMovieBinding
import com.ute.cinenow.utils.showCineMessage

class InviteMovieFragment : Fragment() {

    private var _binding: FragmentInviteMovieBinding? = null
    private val binding get() = _binding!!

    private var selectedDay = "Hôm nay 20/04"
    private var selectedTime = "19:00"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInviteMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setupTabsAndMatch()
        setupDaySelection()
        setupShowtimeSelection()
        setupInviteButtons()
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupTabsAndMatch() {
        binding.btnFreeMatch.setOnClickListener {
            showCineMessage("Đang khớp lịch rảnh cùng Thảo Vy")
        }

        binding.tabNowShowing.setOnClickListener {
            binding.tabNowShowing.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            binding.tabSharedList.backgroundTintList = ColorStateList.valueOf("#00000000".toColorInt())
            showCineMessage("Hiển thị 12 phim đang chiếu")
        }

        binding.tabSharedList.setOnClickListener {
            binding.tabSharedList.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            binding.tabNowShowing.backgroundTintList = ColorStateList.valueOf("#00000000".toColorInt())
            showCineMessage("Hiển thị 3 phim trùng trong danh sách chung")
        }
    }

    private fun setupDaySelection() {
        val dayViews: List<Pair<TextView, String>> = listOf(
            Pair(binding.day1, "Hôm nay 20/04"),
            Pair(binding.day2, "Chủ Nhật 21/04"),
            Pair(binding.day3, "T2 22/04")
        )

        val activeColor = "#5B3DF5".toColorInt()
        val inactiveColor = "#F1F5F9".toColorInt()
        val inactiveTextColor = "#334155".toColorInt()

        for (item in dayViews) {
            val dayView: TextView = item.first
            val dayLabel: String = item.second

            dayView.setOnClickListener {
                selectedDay = dayLabel

                for (other in dayViews) {
                    other.first.backgroundTintList = ColorStateList.valueOf(inactiveColor)
                    other.first.setTextColor(inactiveTextColor)
                }

                dayView.backgroundTintList = ColorStateList.valueOf(activeColor)
                dayView.setTextColor("#FFFFFF".toColorInt())
                updateMainInviteButtonText()
                showCineMessage("Đã chọn ngày: $dayLabel")
            }
        }
    }

    private fun setupShowtimeSelection() {
        val slotViews: List<Pair<View, String>> = listOf(
            Pair(binding.showtime1, "19:00"),
            Pair(binding.showtime2, "20:30"),
            Pair(binding.showtime3, "21:45")
        )

        val activeSlotColor = "#EEF2FF".toColorInt()
        val inactiveSlotColor = "#F8FAFC".toColorInt()

        for (item in slotViews) {
            val slotView: View = item.first
            val timeLabel: String = item.second

            slotView.setOnClickListener {
                selectedTime = timeLabel

                for (other in slotViews) {
                    other.first.backgroundTintList = ColorStateList.valueOf(inactiveSlotColor)
                }

                slotView.backgroundTintList = ColorStateList.valueOf(activeSlotColor)
                updateMainInviteButtonText()
                showCineMessage("Đã chọn suất chiếu: $timeLabel")
            }
        }
    }

    private fun updateMainInviteButtonText() {
        binding.btnInviteMovie1.text = "Gửi lời mời ($selectedTime - $selectedDay)"
    }

    private fun setupInviteButtons() {
        binding.btnInviteMovie1.setOnClickListener {
            sendMovieInvite("Hành Tinh Cát: Phần 2 ($selectedTime - $selectedDay)")
        }

        binding.btnInviteMovie2.setOnClickListener {
            sendMovieInvite("Godzilla x Kong: Đế Chế (Tối nay 20:00)")
        }

        binding.btnInviteMovie3.setOnClickListener {
            sendMovieInvite("Mai: Kỳ Nguyên Mới (19:45 - Rạp 01)")
        }
    }

    private fun sendMovieInvite(detail: String) {
        showCineMessage(
            message = "Đã gửi lời mời xem $detail tới Thảo Vy",
            actionLabel = "HOÀN TÁC",
            onAction = {
                showCineMessage("Đã thu hồi lời mời xem $detail")
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}