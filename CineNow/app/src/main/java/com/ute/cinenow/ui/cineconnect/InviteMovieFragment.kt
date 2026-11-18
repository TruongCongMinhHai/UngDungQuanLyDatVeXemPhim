package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentInviteMovieBinding

class InviteMovieFragment : Fragment() {

    private var _binding: FragmentInviteMovieBinding? = null
    private val binding get() = _binding!!

    // Biến lưu trạng thái ngày đang chọn (Mặc định là Hôm nay 20/04)
    private var selectedDateStr = "Hôm nay 20/04"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInviteMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupTabs()
        setupDateSelection()
        setupInvitationActions()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCheckSchedule.setOnClickListener {
            Toast.makeText(requireContext(), "Đang khớp lịch rảnh tuần này với Thảo Vy...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTabs() {
        binding.tabShowing.setOnClickListener {
            binding.tabShowing.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
            binding.tabShowing.setTextColor(Color.WHITE)

            binding.tabMatch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F1F5F9"))
            binding.tabMatch.setTextColor(Color.parseColor("#475569"))

            Toast.makeText(requireContext(), "Hiển thị phim đang chiếu (12 phim)", Toast.LENGTH_SHORT).show()
        }

        binding.tabMatch.setOnClickListener {
            binding.tabMatch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
            binding.tabMatch.setTextColor(Color.WHITE)

            binding.tabShowing.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F1F5F9"))
            binding.tabShowing.setTextColor(Color.parseColor("#475569"))

            Toast.makeText(requireContext(), "Hiển thị 3 phim trùng sở thích trong Watchlist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDateSelection() {
        // Xử lý chọn ngày rảnh cho phim Hành Tinh Cát 2
        binding.btnDate1.setOnClickListener {
            selectDateButton(1)
            selectedDateStr = "Hôm nay 20/04"
            updateShowtimesForDate(selectedDateStr)
            Toast.makeText(requireContext(), "Đã chọn ngày: $selectedDateStr", Toast.LENGTH_SHORT).show()
        }
        binding.btnDate2.setOnClickListener {
            selectDateButton(2)
            selectedDateStr = "Chủ Nhật 21/04"
            updateShowtimesForDate(selectedDateStr)
            Toast.makeText(requireContext(), "Đã chọn ngày: $selectedDateStr", Toast.LENGTH_SHORT).show()
        }
        binding.btnDate3.setOnClickListener {
            selectDateButton(3)
            selectedDateStr = "T2 22/04"
            updateShowtimesForDate(selectedDateStr)
            Toast.makeText(requireContext(), "Đã chọn ngày: $selectedDateStr", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectDateButton(index: Int) {
        val selectedBg = ColorStateList.valueOf(Color.parseColor("#5850EC"))
        val selectedText = Color.WHITE
        val unselectedBg = ColorStateList.valueOf(Color.parseColor("#F1F5F9"))
        val unselectedText = Color.parseColor("#475569")

        binding.btnDate1.backgroundTintList = if (index == 1) selectedBg else unselectedBg
        binding.btnDate1.setTextColor(if (index == 1) selectedText else unselectedText)

        binding.btnDate2.backgroundTintList = if (index == 2) selectedBg else unselectedBg
        binding.btnDate2.setTextColor(if (index == 2) selectedText else unselectedText)

        binding.btnDate3.backgroundTintList = if (index == 3) selectedBg else unselectedBg
        binding.btnDate3.setTextColor(if (index == 3) selectedText else unselectedText)
    }

    // Cập nhật động nội dung suất chiếu / nút bấm tương ứng với ngày được chọn
    private fun updateShowtimesForDate(dateStr: String) {
        binding.btnSendInvite1.text = "🎟️ Gửi lời mời (19:00 - $dateStr)"
    }

    private fun setupInvitationActions() {
        // Gửi lời mời phim Hành Tinh Cát 2 kèm theo ngày đã chọn
        binding.btnSendInvite1.setOnClickListener {
            Toast.makeText(requireContext(), "Đã gửi thiệp mời xem 'Hành Tinh Cát: Phần 2' vào lúc 19:00 - $selectedDateStr đến Thảo Vy!", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Gửi lời mời phim Godzilla x Kong
        binding.btnSendInvite2.setOnClickListener {
            Toast.makeText(requireContext(), "Đã gửi thiệp mời xem 'Godzilla x Kong' đến Thảo Vy!", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Gửi lời mời phim Mai
        binding.btnSendInvite3.setOnClickListener {
            Toast.makeText(requireContext(), "Đã gửi thiệp mời xem 'Mai: Kỷ Nguyên Mới' đến Thảo Vy!", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b