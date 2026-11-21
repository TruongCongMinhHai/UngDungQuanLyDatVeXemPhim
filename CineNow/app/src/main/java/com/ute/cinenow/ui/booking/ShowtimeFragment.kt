package com.ute.cinenow.ui.booking
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentShowtimeBinding
import com.ute.cinenow.ui.movie.MovieDetailFragment
import com.ute.cinenow.ui.movie.MovieDetailSamples
import com.ute.cinenow.utils.showCineMessage

class ShowtimeFragment : Fragment() {

    private var _binding: FragmentShowtimeBinding? = null
    private val binding get() = _binding!!

    private var selectedMovieTitle: String = "Hành Tinh Cát 2"
    private var selectedDateLabel: String = "Ngày mai (25/10)"
    private var selectedCinemaShort: String = "Landmark 81"
    private var selectedTime: String = "16:45"
    private var selectedFormat: String = "IMAX LASER"

    // Lưu danh sách tất cả các ô suất chiếu tìm thấy để đổi màu linh hoạt khi người dùng bấm
    private data class SlotItem(
        val layout: LinearLayout,
        val timeTv: TextView,
        val statusTv: TextView,
        val time: String,
        val cinema: String,
        val isSoldOut: Boolean,
        val defaultBgColor: Int,
        val defaultStatusColor: Int
    )

    private val allSlots = mutableListOf<SlotItem>()
    private var currentSelectedSlot: SlotItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowtimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val passedTitle = arguments?.getString(MovieDetailFragment.ARG_MOVIE_TITLE)
        val movie = MovieDetailSamples.find(passedTitle)
        selectedMovieTitle = movie.title

        bindMovieInfo(movie.title, movie.posterRes)
        updateSummaryText()
        setupHeaderBack()
        setupDays()
        setupAreaAndFormatFilters()
        scanAndSetupAllShowtimeSlots()
        setupContinueButton()
    }

    private fun bindMovieInfo(title: String, posterRes: Int) {
        binding.imgMoviePoster.setImageResource(posterRes)
        binding.header.title = "Chọn suất chiếu"
    }

    private fun updateSummaryText() {
        binding.tvSelectedSummary.text = "$selectedCinemaShort • $selectedTime • $selectedDateLabel"
    }

    private fun setupHeaderBack() {
        binding.header.onBackClick = {
            val poppedNav = try { findNavController().navigateUp() } catch (_: Exception) { false }
            if (!poppedNav) {
                if (!findNavController().popBackStack()) {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    // --- 1. CHỌN NGÀY CHIẾU (day24 - day28) ---
    private fun selectDay(selectedLayout: LinearLayout, label: String) {
        selectedDateLabel = label
        updateSummaryText()

        val allDays = listOf(
            binding.day24,
            binding.day25,
            binding.day26,
            binding.day27,
            binding.day28
        )

        for (day in allDays) {
            val isSelected = (day == selectedLayout)
            day.backgroundTintList = ColorStateList.valueOf(
                if (isSelected) Color.parseColor("#4338CA") else Color.WHITE
            )
            for (i in 0 until day.childCount) {
                val child = day.getChildAt(i) as? TextView ?: continue
                when (i) {
                    0 -> child.setTextColor(Color.parseColor(if (isSelected) "#E0E7FF" else "#64748B"))
                    1 -> child.setTextColor(if (isSelected) Color.WHITE else Color.parseColor("#0F172A"))
                    2 -> child.setTextColor(Color.parseColor(if (isSelected) "#E0E7FF" else "#64748B"))
                }
            }
        }
        showCineMessage("Đã chọn ngày chiếu", subtitle = "$selectedMovieTitle • $label")
    }

    private fun setupDays() {
        val days = listOf(
            binding.day24 to "Hôm nay (24/10)",
            binding.day25 to "Ngày mai (25/10)",
            binding.day26 to "Cuối tuần (26/10)",
            binding.day27 to "Chủ nhật (27/10)",
            binding.day28 to "Thứ hai (28/10)"
        )

        for ((view, label) in days) {
            view.isClickable = true
            view.isFocusable = true
            view.setOnClickListener {
                selectDay(view, label)
            }
        }
    }

    // --- 2. BẤM CHỌN KHU VỰC VÀ BỘ LỌC ĐỊNH DẠNG (2D, IMAX Laser, 4DX, ScreenX) ---
    private fun setupAreaAndFormatFilters() {
        val mainContent = (binding.root.getChildAt(1) as? ViewGroup)?.getChildAt(0) as? LinearLayout ?: return

        // Tìm nút khu vực "Bình Thạnh & TP. Thủ Đức"
        for (i in 0 until mainContent.childCount) {
            val child = mainContent.getChildAt(i)
            if (child is LinearLayout && child.orientation == LinearLayout.HORIZONTAL) {
                val firstTv = child.getChildAt(0) as? TextView
                if (firstTv?.text?.contains("Bình Thạnh", ignoreCase = true) == true) {
                    firstTv.isClickable = true
                    firstTv.isFocusable = true
                    firstTv.setOnClickListener {
                        showCineMessage("Khu vực rạp", subtitle = "Đang chọn: Bình Thạnh & TP. Thủ Đức")
                    }
                    break
                }
            }
        }

        // Tìm thanh cuộn ngang chứa các thẻ định dạng
        for (i in 0 until mainContent.childCount) {
            val child = mainContent.getChildAt(i)
            if (child is HorizontalScrollView) {
                val formatLayout = child.getChildAt(0) as? LinearLayout ?: continue
                val formatChips = mutableListOf<TextView>()
                for (j in 0 until formatLayout.childCount) {
                    val tv = formatLayout.getChildAt(j) as? TextView ?: continue
                    formatChips.add(tv)
                }

                for (chip in formatChips) {
                    chip.isClickable = true
                    chip.isFocusable = true
                    chip.setOnClickListener {
                        val formatName = chip.text.toString().trim()
                        selectedFormat = formatName
                        updateSummaryText()

                        // Đổi màu thẻ được chọn
                        for (other in formatChips) {
                            if (other == chip) {
                                other.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4338CA"))
                                other.setTextColor(Color.WHITE)
                            } else {
                                other.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                                other.setTextColor(Color.parseColor("#475569"))
                            }
                        }
                        showCineMessage("Đã chọn định dạng", subtitle = formatName)
                    }
                }
                break
            }
        }
    }

    // --- 3. TỰ ĐỘNG QUÉT & KÍCH HOẠT TẤT CẢ SUẤT CHIẾU CỦA 3 CỤM RẠP ---
    private fun scanAndSetupAllShowtimeSlots() {
        allSlots.clear()
        val mainContent = (binding.root.getChildAt(1) as? ViewGroup)?.getChildAt(0) as? LinearLayout ?: return

        var currentCinemaName = "Landmark 81"

        fun scanView(view: View) {
            if (view is CardView) {
                // Kiểm tra tên rạp trong CardView
                val cardContent = view.getChildAt(0) as? ViewGroup
                if (cardContent != null) {
                    for (k in 0 until cardContent.childCount) {
                        val sub = cardContent.getChildAt(k)
                        if (sub is TextView && sub.text.contains("CineNow", ignoreCase = true)) {
                            val txt = sub.text.toString()
                            currentCinemaName = when {
                                txt.contains("Landmark", ignoreCase = true) -> "Landmark 81"
                                txt.contains("Vạn Hạnh", ignoreCase = true) -> "Vạn Hạnh Mall"
                                txt.contains("Thủ Đức", ignoreCase = true) -> "Thủ Đức"
                                else -> "CineNow"
                            }
                        }
                    }
                }
            }

            // Mỗi ô suất chiếu là một LinearLayout có đúng 2 TextView (Giờ + Số ghế/Trạng thái)
            if (view is LinearLayout && view.childCount == 2 && view.getChildAt(0) is TextView && view.getChildAt(1) is TextView) {
                val tvTime = view.getChildAt(0) as TextView
                val tvStatus = view.getChildAt(1) as TextView
                val timeText = tvTime.text.toString().trim()

                // Kiểm tra định dạng giờ chiếu (ví dụ 09:30, 16:45, 21:15)
                if (timeText.matches(Regex("\\d{1,2}:\\d{2}"))) {
                    val statusText = tvStatus.text.toString().trim()
                    val isSoldOut = statusText.contains("Hết vé", ignoreCase = true)
                    val isNearFull = statusText.contains("Gần kín", ignoreCase = true)

                    val defaultBg = if (isNearFull) Color.parseColor("#FFF1F2") else Color.parseColor("#F8FAFC")
                    val defaultStatusColor = if (isNearFull) Color.parseColor("#E11D48") else Color.parseColor("#64748B")

                    val slotItem = SlotItem(
                        layout = view,
                        timeTv = tvTime,
                        statusTv = tvStatus,
                        time = timeText,
                        cinema = currentCinemaName,
                        isSoldOut = isSoldOut,
                        defaultBgColor = defaultBg,
                        defaultStatusColor = defaultStatusColor
                    )
                    allSlots.add(slotItem)

                    // Nhận diện ô đang được chọn ban đầu (16:45)
                    if (view.id == R.id.slot1645 || (timeText == "16:45" && currentCinemaName == "Landmark 81")) {
                        currentSelectedSlot = slotItem
                    }

                    view.isClickable = true
                    view.isFocusable = true
                    view.setOnClickListener {
                        onSlotClicked(slotItem)
                    }
                    return
                }
            }

            if (view is ViewGroup) {
                for (idx in 0 until view.childCount) {
                    scanView(view.getChildAt(idx))
                }
            }
        }

        scanView(mainContent)
    }

    private fun onSlotClicked(slot: SlotItem) {
        if (slot.isSoldOut) {
            showCineMessage("Suất chiếu đã hết vé", subtitle = "${slot.time} • ${slot.cinema} (Vui lòng chọn suất khác)")
            return
        }

        // Hoàn trả ô suất chiếu cũ về màu mặc định
        currentSelectedSlot?.let { prev ->
            prev.layout.backgroundTintList = ColorStateList.valueOf(prev.defaultBgColor)
            prev.timeTv.setTextColor(Color.parseColor("#0F172A"))
            prev.statusTv.setTextColor(prev.defaultStatusColor)
            if (prev.statusTv.text.toString().contains("Đang chọn", ignoreCase = true)) {
                prev.statusTv.text = "Còn ghế"
            }
        }

        // Bật màu tím nổi bật cho ô vừa chọn
        slot.layout.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4338CA"))
        slot.timeTv.setTextColor(Color.WHITE)
        slot.statusTv.setTextColor(Color.parseColor("#E0E7FF"))
        slot.statusTv.text = "Đang chọn"

        currentSelectedSlot = slot
        selectedTime = slot.time
        selectedCinemaShort = slot.cinema
        updateSummaryText()

        showCineMessage(
            message = "Đã chọn suất chiếu",
            subtitle = "$selectedMovieTitle • ${slot.time} (${slot.cinema})"
        )
    }

    // --- 4. NÚT TIẾP TỤC CHỌN GHẾ ---
    private fun setupContinueButton() {
        binding.btnContinueSeat.isClickable = true
        binding.btnContinueSeat.isEnabled = true
        binding.btnContinueSeat.setOnClickListener {
            showCineMessage(
                message = "Đã chọn suất chiếu",
                subtitle = "$selectedMovieTitle • $selectedTime ($selectedCinemaShort)"
            )
            val args = bundleOf(
                MovieDetailFragment.ARG_MOVIE_TITLE to selectedMovieTitle,
                "showtime" to selectedTime,
                "cinema" to selectedCinemaShort,
                "dateLabel" to selectedDateLabel,
                "format" to selectedFormat
            )
            try {
                findNavController().navigate(R.id.smartSeatSetupFragment, args)
            } catch (e: Exception) {
                try {
                    findNavController().navigate(R.id.seatSelectionFragment, args)
                } catch (e2: Exception) {
                    showCineMessage("Không thể mở màn hình chọn ghế: ${e2.message}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}