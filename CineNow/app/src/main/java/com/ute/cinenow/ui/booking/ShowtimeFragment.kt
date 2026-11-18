package com.ute.cinenow.ui.booking

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
import com.ute.cinenow.databinding.FragmentShowtimeBinding

class ShowtimeFragment : Fragment() {

    private var _binding: FragmentShowtimeBinding? = null
    private val binding get() = _binding!!

    // Ban đầu chưa chọn gì (null)
    private var selectedCinema: String? = null
    private var selectedTime: String? = null
    private var selectedDateText: String? = null
    private var selectedFormat: String? = null

    data class SlotItem(
        val container: LinearLayout,
        val tvTime: TextView,
        val tvSub: TextView,
        val defaultSubText: String,
        val isNearlyFull: Boolean,
        val cinemaName: String,
        val timeText: String,
        val formatText: String
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowtimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderActions()
        setupDatePicker()
        setupFormatFilter()
        setupShowtimeSlots()
    }

    private fun setupHeaderActions() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSelectLocation.setOnClickListener {
            Toast.makeText(requireContext(), "Đang hiển thị rạp gần Bình Thạnh & TP. Thủ Đức", Toast.LENGTH_SHORT).show()
        }

        binding.btnContinue.setOnClickListener {
            try {
                findNavController().navigate(R.id.smartSeatSetupFragment)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Chuyển sang chọn ghế: $selectedCinema - $selectedTime", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDatePicker() {
        data class DayItem(
            val layout: LinearLayout,
            val tvName: TextView,
            val tvNum: TextView,
            val tvLabel: TextView,
            val summaryDate: String
        )

        val days = listOf(
            DayItem(binding.day1, binding.tvDay1Name, binding.tvDay1Num, binding.tvDay1Label, "Hôm nay (24/10)"),
            DayItem(binding.day2, binding.tvDay2Name, binding.tvDay2Num, binding.tvDay2Label, "Ngày mai (25/10)"),
            DayItem(binding.day3, binding.tvDay3Name, binding.tvDay3Num, binding.tvDay3Label, "Cuối tuần (26/10)"),
            DayItem(binding.day4, binding.tvDay4Name, binding.tvDay4Num, binding.tvDay4Label, "Chủ nhật (27/10)"),
            DayItem(binding.day5, binding.tvDay5Name, binding.tvDay5Num, binding.tvDay5Label, "Thứ hai (28/10)")
        )

        days.forEach { clickedDay ->
            clickedDay.layout.setOnClickListener {
                // Trả tất cả ngày về nền trắng
                days.forEach { d ->
                    d.layout.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                    d.tvName.setTextColor(Color.parseColor("#64748B"))
                    d.tvNum.setTextColor(Color.parseColor("#0F172A"))
                    d.tvLabel.setTextColor(Color.parseColor("#94A3B8"))
                }
                // Bấm vào ngày nào thì ngày đó sáng màu xanh/tím
                clickedDay.layout.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
                clickedDay.tvName.setTextColor(Color.WHITE)
                clickedDay.tvNum.setTextColor(Color.WHITE)
                clickedDay.tvLabel.setTextColor(Color.parseColor("#E0E7FF"))

                selectedDateText = clickedDay.summaryDate
                updateBottomSummary()
            }
        }
    }

    private fun setupFormatFilter() {
        val chips = listOf(
            binding.chipAll,
            binding.chip2D,
            binding.chipIMAX,
            binding.chip4DX,
            binding.chipScreenX
        )

        chips.forEach { clickedChip ->
            clickedChip.setOnClickListener {
                chips.forEach { chip ->
                    chip.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F1F5F9"))
                    chip.setTextColor(Color.parseColor("#475569"))
                }
                clickedChip.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
                clickedChip.setTextColor(Color.WHITE)

                when (clickedChip.id) {
                    R.id.chipAll -> {
                        binding.cardCinema1.visibility = View.VISIBLE
                        binding.cardCinema2.visibility = View.VISIBLE
                        binding.cardCinema3.visibility = View.VISIBLE
                    }
                    R.id.chipIMAX -> {
                        binding.cardCinema1.visibility = View.VISIBLE
                        binding.cardCinema2.visibility = View.GONE
                        binding.cardCinema3.visibility = View.GONE
                    }
                    R.id.chip2D -> {
                        binding.cardCinema1.visibility = View.GONE
                        binding.cardCinema2.visibility = View.VISIBLE
                        binding.cardCinema3.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.cardCinema1.visibility = View.VISIBLE
                        binding.cardCinema2.visibility = View.GONE
                        binding.cardCinema3.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupShowtimeSlots() {
        val allSlots = listOf(
            SlotItem(binding.slot11, binding.tvTime11, binding.tvSub11, "Còn 42 ghế", false, "Landmark 81", "09:30", "IMAX LASER"),
            SlotItem(binding.slot12, binding.tvTime12, binding.tvSub12, "Còn 18 ghế", false, "Landmark 81", "13:15", "IMAX LASER"),
            SlotItem(binding.slot13, binding.tvTime13, binding.tvSub13, "Còn 25 ghế", false, "Landmark 81", "16:45", "IMAX LASER"),
            SlotItem(binding.slot14, binding.tvTime14, binding.tvSub14, "Gần kín", true, "Landmark 81", "20:00", "IMAX LASER"),
            SlotItem(binding.slot15, binding.tvTime15, binding.tvSub15, "Suất khuya", false, "Landmark 81", "22:30", "IMAX LASER"),
            SlotItem(binding.slot21, binding.tvTime21, binding.tvSub21, "Còn 64 ghế", false, "Vạn Hạnh Mall", "10:00", "STANDARD 2D"),
            SlotItem(binding.slot22, binding.tvTime22, binding.tvSub22, "Còn 51 ghế", false, "Vạn Hạnh Mall", "14:30", "STANDARD 2D"),
            SlotItem(binding.slot23, binding.tvTime23, binding.tvSub23, "Còn 23 ghế", false, "Vạn Hạnh Mall", "18:00", "STANDARD 2D"),
            SlotItem(binding.slot24, binding.tvTime24, binding.tvSub24, "Còn 38 ghế", false, "Vạn Hạnh Mall", "21:15", "STANDARD 2D"),
            SlotItem(binding.slot31, binding.tvTime31, binding.tvSub31, "Còn 72 ghế", false, "Thủ Đức", "11:15", "STANDARD 2D"),
            SlotItem(binding.slot32, binding.tvTime32, binding.tvSub32, "Còn 45 ghế", false, "Thủ Đức", "15:00", "STANDARD 2D"),
            SlotItem(binding.slot33, binding.tvTime33, binding.tvSub33, "Còn 12 ghế", false, "Thủ Đức", "19:30", "STANDARD 2D")
        )

        binding.slot34.setOnClickListener {
            Toast.makeText(requireContext(), "Suất chiếu 21:45 đã hết vé!", Toast.LENGTH_SHORT).show()
        }

        allSlots.forEach { clickedSlot ->
            clickedSlot.container.setOnClickListener {
                // Trả tất cả suất chiếu về nền xám mặc định
                allSlots.forEach { s ->
                    s.container.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F1F5F9"))
                    s.tvSub.text = s.defaultSubText
                    if (s.isNearlyFull) {
                        s.tvTime.setTextColor(Color.parseColor("#DC2626"))
                        s.tvSub.setTextColor(Color.parseColor("#DC2626"))
                    } else {
                        s.tvTime.setTextColor(Color.parseColor("#0F172A"))
                        s.tvSub.setTextColor(Color.parseColor("#64748B"))
                    }
                }

                // Bấm vào ô giờ nào thì ô đó mới đổi màu xanh/tím
                clickedSlot.container.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
                clickedSlot.tvTime.setTextColor(Color.WHITE)
                clickedSlot.tvSub.text = "Đang chọn"
                clickedSlot.tvSub.setTextColor(Color.parseColor("#E0E7FF"))

                // Lưu thông tin vừa bấm
                selectedCinema = clickedSlot.cinemaName
                selectedTime = clickedSlot.timeText
                selectedFormat = clickedSlot.formatText
                updateBottomSummary()
            }
        }
    }

    private fun updateBottomSummary() {
        val dateDisplay = selectedDateText ?: "Hôm nay (24/10)"
        if (selectedCinema != null && selectedTime != null) {
            // Khi đã bấm chọn giờ chiếu: hiện thông tin & kích hoạt nút Tiếp tục
            binding.dotIndicator.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
            binding.tvSelectedSummary.text = "$selectedCinema • $selectedTime • $dateDisplay $selectedFormat"
            binding.tvSelectedSummary.setTextColor(Color.parseColor("#0F172A"))

            binding.btnContinue.isEnabled = true
            binding.btnContinue.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
        } else if (selectedDateText != null) {
            // Mới bấm chọn ngày, chưa bấm giờ chiếu
            binding.tvSelectedSummary.text = "Đã chọn $dateDisplay • Vui lòng chọn giờ chiếu"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}