package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.ute.cinenow.databinding.FragmentInviteMovieBinding
import com.ute.cinenow.utils.showCineMessage

class InviteMovieFragment : Fragment() {

    private var _binding: FragmentInviteMovieBinding? = null
    private val binding get() = _binding!!

    private var targetFriend = "Thảo Vy"
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

        val passedFriend = arguments?.getString("friend_name")
        if (!passedFriend.isNullOrBlank()) {
            targetFriend = passedFriend
            binding.tvInviteTargetTitle.text = "Mời $targetFriend xem phim"
            binding.header.title = "Mời $targetFriend xem phim"
        }

        setupHeader()
        setupTabsAndMatch()
        setupDaySelection()
        setupShowtimeSelection()
        setupInviteButtons()
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            val popped = try { findNavController().popBackStack() } catch (_: Exception) { false }
            if (!popped) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupTabsAndMatch() {
        binding.btnFreeMatch.setOnClickListener {
            showCineMessage("Ghép lịch rảnh", subtitle = "Đang đồng bộ khung giờ rảnh cuối tuần cùng $targetFriend")
        }

        binding.tabNowShowing.setOnClickListener {
            binding.tabNowShowing.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            binding.tabSharedList.backgroundTintList = ColorStateList.valueOf("#00000000".toColorInt())
            showCineMessage("Phim đang chiếu", subtitle = "Hiển thị các phim đang có suất chiếu")
        }

        binding.tabSharedList.setOnClickListener {
            binding.tabSharedList.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            binding.tabNowShowing.backgroundTintList = ColorStateList.valueOf("#00000000".toColorInt())
            showCineMessage("Danh sách chung", subtitle = "Các phim Thảo Vy đã thêm vào Watchlist")
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
                showCineMessage("Đã chọn ngày chiếu", subtitle = dayLabel)
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
                showCineMessage("Đã chọn suất chiếu", subtitle = "$timeLabel ($selectedDay)")
            }
        }
    }

    private fun updateMainInviteButtonText() {
        if (!isMovie1Sent) {
            binding.btnInviteMovie1.text = "Gửi lời mời ($selectedTime - $selectedDay)"
        }
    }

    private var isMovie1Sent = false
    private var isMovie2Sent = false
    private var isMovie3Sent = false

    private fun setupInviteButtons() {
        // NÚT PHIM 1: Hành Tinh Cát 2
        binding.btnInviteMovie1.setOnClickListener {
            toggleInviteState(
                button = binding.btnInviteMovie1,
                isSent = isMovie1Sent,
                onStateChanged = { isMovie1Sent = it },
                movieTitle = "Hành Tinh Cát 2 ($selectedTime - $selectedDay)",
                defaultText = "Gửi lời mời ($selectedTime - $selectedDay)"
            )
        }

        // NÚT PHIM 2: Godzilla x Kong / Oppenheimer
        binding.btnInviteMovie2.setOnClickListener {
            toggleInviteState(
                button = binding.btnInviteMovie2,
                isSent = isMovie2Sent,
                onStateChanged = { isMovie2Sent = it },
                movieTitle = "Oppenheimer (Tối nay 20:00)",
                defaultText = "Gửi lời mời"
            )
        }

        // NÚT PHIM 3: Mai: Kỳ Nguyên Mới
        binding.btnInviteMovie3.setOnClickListener {
            toggleInviteState(
                button = binding.btnInviteMovie3,
                isSent = isMovie3Sent,
                onStateChanged = { isMovie3Sent = it },
                movieTitle = "Mai: Kỳ Nguyên Mới (19:45 - Rạp 01)",
                defaultText = "Gửi lời mời"
            )
        }
    }

    private fun toggleInviteState(
        button: MaterialButton,
        isSent: Boolean,
        onStateChanged: (Boolean) -> Unit,
        movieTitle: String,
        defaultText: String
    ) {
        if (!isSent) {
            onStateChanged(true)
            button.text = "Đã gửi lời mời"
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#94A3B8"))
            showCineMessage(
                message = "Đã gửi lời mời xem $movieTitle tới $targetFriend",
                actionLabel = "HOÀN TÁC",
                onAction = {
                    onStateChanged(false)
                    button.text = defaultText
                    button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5B3DF5"))
                    showCineMessage("Đã thu hồi lời mời xem $movieTitle")
                }
            )
        } else {
            onStateChanged(false)
            button.text = defaultText
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5B3DF5"))
            showCineMessage("Đã thu hồi lời mời xem $movieTitle")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}