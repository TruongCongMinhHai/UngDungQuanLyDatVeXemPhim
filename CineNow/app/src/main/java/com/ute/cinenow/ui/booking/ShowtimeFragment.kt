package com.ute.cinenow.ui.booking

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentShowtimeBinding
import com.ute.cinenow.databinding.ItemShowtimeCinemaBinding
import com.ute.cinenow.databinding.ItemShowtimeGroupBinding
import com.ute.cinenow.databinding.ItemShowtimeSlotBinding
import com.ute.cinenow.ui.movie.MovieDetailFragment
import com.ute.cinenow.ui.movie.MovieDetailSamples
import com.ute.cinenow.utils.showCineMessage

class ShowtimeFragment : Fragment() {

    private var _binding: FragmentShowtimeBinding? = null
    private val binding get() = _binding!!

    private var selectedMovieTitle: String = "Hành Tinh Cát 2"

    // --- Ngày chiếu ---
    private val dayLabels = listOf(
        "Hôm nay (24/10)",
        "Ngày mai (25/10)",
        "Cuối tuần (26/10)",
        "Chủ nhật (27/10)",
        "Thứ hai (28/10)"
    )
    private var selectedDayIndex = 1
    private val selectedDateLabel get() = dayLabels[selectedDayIndex]

    // --- Bộ lọc ---
    private var selectedArea: ShowArea = ShowtimeSamples.defaultArea
    private var formatFilter: ShowFormat? = null   // null = Tất cả định dạng
    private var visibleCinemaCount = 0

    // --- Suất chiếu đang chọn (selectedKey = null nghĩa là chưa chọn) ---
    private var selectedKey: String? = null
    private var selectedCinemaShort: String = ""
    private var selectedTime: String = ""
    private var selectedFormat: String = ""
    private var isInitialized = false

    // Một ô suất chiếu đang hiển thị trên màn hình
    private class SlotView(
        val key: String,
        val binding: ItemShowtimeSlotBinding,
        val cinema: ShowCinema,
        val format: ShowFormat,
        val showtime: Showtime
    )

    private val slotViews = mutableMapOf<String, SlotView>()

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

        bindMovieInfo(movie.posterRes)
        setupHeaderBack()
        setupDays()
        setupAreaPicker()
        setupFormatChips()
        applyDefaultSelectionOnce()
        renderCinemas()
        setupContinueButton()
    }

    private fun bindMovieInfo(posterRes: Int) {
        binding.imgMoviePoster.setImageResource(posterRes)
        binding.header.title = "Chọn suất chiếu"
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

    /**
     * Dùng showCineMessage chung của project (không sửa CineMessage.kt).
     * Thanh dưới cùng của layout có id "bottom_nav" nên showCineMessage tự neo thông báo
     * phía trên thanh này => không che nút "Tiếp tục chọn ghế".
     */
    private fun message(title: String, subtitle: String? = null) {
        if (_binding == null) return
        showCineMessage(title, subtitle)
    }

    // ------------------------------------------------------------------
    // 1. CHỌN NGÀY CHIẾU
    // ------------------------------------------------------------------
    private fun dayLayouts() = listOf(binding.day24, binding.day25, binding.day26, binding.day27, binding.day28)

    private fun setupDays() {
        dayLayouts().forEachIndexed { index, day ->
            day.isClickable = true
            day.isFocusable = true
            day.setOnClickListener {
                selectedDayIndex = index
                styleDays()
                updateSummary()
                message("Đã chọn ngày chiếu", "$selectedMovieTitle • $selectedDateLabel")
            }
        }
        styleDays()
    }

    private fun styleDays() {
        dayLayouts().forEachIndexed { index, day ->
            val isSelected = index == selectedDayIndex
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
    }

    // ------------------------------------------------------------------
    // 2. BỘ LỌC KHU VỰC (bấm vào nút -> hiện danh sách để chọn)
    // ------------------------------------------------------------------
    private fun setupAreaPicker() {
        binding.btnArea.isClickable = true
        binding.btnArea.isFocusable = true
        binding.btnArea.text = selectedArea.label
        binding.btnArea.setOnClickListener { showAreaMenu() }
    }

    private fun showAreaMenu() {
        val areas = ShowtimeSamples.areas
        val popup = PopupMenu(requireContext(), binding.btnArea, Gravity.START)
        areas.forEachIndexed { index, area ->
            popup.menu.add(Menu.NONE, index, index, area.label).apply {
                isCheckable = true
                isChecked = area.id == selectedArea.id
            }
        }
        popup.menu.setGroupCheckable(Menu.NONE, true, true)
        popup.setOnMenuItemClickListener { item ->
            onAreaPicked(areas[item.itemId])
            true
        }
        popup.show()
    }

    private fun onAreaPicked(area: ShowArea) {
        if (area.id == selectedArea.id) return
        selectedArea = area
        binding.btnArea.text = area.label
        renderCinemas()
        message("Đã chọn khu vực", "${area.label} • $visibleCinemaCount cụm rạp")
    }

    // ------------------------------------------------------------------
    // 3. BỘ LỌC ĐỊNH DẠNG (Tất cả, 2D, IMAX Laser, 4DX, ScreenX)
    // ------------------------------------------------------------------
    private fun formatChips(): List<Pair<TextView, ShowFormat?>> = listOf(
        binding.chipAll to null,
        binding.chip2d to ShowFormat.STANDARD_2D,
        binding.chipImax to ShowFormat.IMAX_LASER,
        binding.chip4dx to ShowFormat.FOUR_DX,
        binding.chipScreenX to ShowFormat.SCREEN_X
    )

    private fun setupFormatChips() {
        for ((chip, format) in formatChips()) {
            chip.isClickable = true
            chip.isFocusable = true
            chip.setOnClickListener {
                if (formatFilter == format) return@setOnClickListener
                formatFilter = format
                styleFormatChips()
                renderCinemas()
                message(
                    "Đã lọc định dạng",
                    "${format?.chipLabel ?: "Tất cả định dạng"} • $visibleCinemaCount cụm rạp"
                )
            }
        }
        styleFormatChips()
    }

    private fun styleFormatChips() {
        for ((chip, format) in formatChips()) {
            val isSelected = format == formatFilter
            chip.backgroundTintList = ColorStateList.valueOf(
                if (isSelected) Color.parseColor("#4338CA") else Color.WHITE
            )
            chip.setTextColor(if (isSelected) Color.WHITE else Color.parseColor("#475569"))
            chip.setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    // ------------------------------------------------------------------
    // 4. HIỂN THỊ DANH SÁCH RẠP THEO KHU VỰC + ĐỊNH DẠNG
    // ------------------------------------------------------------------
    private fun renderCinemas() {
        val container = binding.cinemaContainer
        container.removeAllViews()
        slotViews.clear()

        val visible = ShowtimeSamples.cinemasFor(selectedArea.id, formatFilter)
        visibleCinemaCount = visible.size
        binding.tvCinemaCount.text = "${visible.size} cụm rạp"

        val filter = formatFilter
        binding.tvEmpty.text = if (filter != null) {
            "Không có suất chiếu ${filter.title} tại ${selectedArea.label}.\nHãy thử chọn định dạng hoặc khu vực khác."
        } else {
            "Chưa có rạp nào tại ${selectedArea.label}."
        }
        binding.tvEmpty.visibility = if (visible.isEmpty()) View.VISIBLE else View.GONE

        for ((cinema, groups) in visible) {
            container.addView(buildCinemaCard(container, cinema, groups))
        }

        // Suất đang chọn không còn hiển thị (do đổi khu vực / định dạng) -> bỏ chọn
        val key = selectedKey
        if (key != null && !slotViews.containsKey(key)) {
            clearSelection()
        }
        updateSummary()
    }

    private fun buildCinemaCard(parent: ViewGroup, cinema: ShowCinema, groups: List<ShowGroup>): View {
        val card = ItemShowtimeCinemaBinding.inflate(LayoutInflater.from(requireContext()), parent, false)

        card.tvCinemaName.text = cinema.name
        if (cinema.badge != null) {
            card.tvBadge.text = cinema.badge
            card.tvBadge.visibility = View.VISIBLE
        }
        card.tvCinemaAddress.text =
            if (cinema.distance != null) "Cách bạn ${cinema.distance} • ${cinema.address}" else cinema.address
        card.tvPrice.text = cinema.priceRange

        cinema.features.forEachIndexed { index, feature ->
            card.layoutFeatures.addView(buildFeatureChip(feature, withMargin = index > 0))
        }
        for (group in groups) {
            card.layoutGroups.addView(buildGroup(card.layoutGroups, cinema, group))
        }
        return card.root
    }

    private fun buildFeatureChip(text: String, withMargin: Boolean): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 10f
            setTextColor(Color.parseColor("#475569"))
            setBackgroundResource(R.drawable.bg_rounded_icon)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F1F5F9"))
            setPadding(dp(7), dp(2), dp(7), dp(2))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { if (withMargin) marginStart = dp(6) }
        }
    }

    private fun buildGroup(parent: ViewGroup, cinema: ShowCinema, group: ShowGroup): View {
        val inflater = LayoutInflater.from(requireContext())
        val g = ItemShowtimeGroupBinding.inflate(inflater, parent, false)
        g.tvGroupTitle.text = "${group.format.title} • ${group.language}"

        group.times.chunked(SLOTS_PER_ROW).forEachIndexed { rowIndex, rowTimes ->
            val row = LinearLayout(requireContext()).apply { orientation = LinearLayout.HORIZONTAL }

            for (showtime in rowTimes) {
                row.addView(buildSlot(row, cinema, group.format, showtime))
            }
            // Hàng cuối thiếu ô thì thêm ô trống vô hình để các ô vẫn đều nhau
            repeat(SLOTS_PER_ROW - rowTimes.size) {
                row.addView(View(requireContext()).apply {
                    visibility = View.INVISIBLE
                    layoutParams = LinearLayout.LayoutParams(0, dp(50), 1f).apply {
                        setMargins(dp(4), 0, dp(4), 0)
                    }
                })
            }

            // Margin âm -4dp để bù margin 4dp của từng ô, ô đầu/cuối thẳng lề với nội dung thẻ
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(-dp(4), if (rowIndex == 0) 0 else dp(8), -dp(4), 0) }
            g.layoutSlotRows.addView(row, lp)
        }
        return g.root
    }

    private fun buildSlot(parent: ViewGroup, cinema: ShowCinema, format: ShowFormat, showtime: Showtime): View {
        val b = ItemShowtimeSlotBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
        val key = slotKey(cinema.id, format, showtime.time)
        val slot = SlotView(key, b, cinema, format, showtime)
        slotViews[key] = slot

        b.tvSlotTime.text = showtime.time
        styleSlot(slot, selected = key == selectedKey)

        b.root.isClickable = true
        b.root.isFocusable = true
        b.root.setOnClickListener { onSlotClicked(slot) }
        return b.root
    }

    private fun styleSlot(slot: SlotView, selected: Boolean) {
        val b = slot.binding
        val st = slot.showtime
        val soldOut = st.seatsLeft <= 0
        val nearFull = !soldOut && st.seatsLeft <= NEAR_FULL_SEATS

        b.root.alpha = if (soldOut) 0.45f else 1f

        val bg: String
        val timeColor: String
        val statusColor: String
        val statusText: String
        when {
            selected -> {
                bg = "#4338CA"; timeColor = "#FFFFFF"; statusColor = "#E0E7FF"; statusText = "Đang chọn"
            }
            soldOut -> {
                bg = "#F1F5F9"; timeColor = "#94A3B8"; statusColor = "#94A3B8"; statusText = "Hết vé"
            }
            nearFull -> {
                bg = "#FFF1F2"; timeColor = "#0F172A"; statusColor = "#E11D48"; statusText = "Gần kín"
            }
            else -> {
                bg = "#F8FAFC"; timeColor = "#0F172A"; statusColor = "#64748B"
                statusText = st.note ?: "Còn ${st.seatsLeft} ghế"
            }
        }
        b.root.backgroundTintList = ColorStateList.valueOf(Color.parseColor(bg))
        b.tvSlotTime.setTextColor(Color.parseColor(timeColor))
        b.tvSlotStatus.setTextColor(Color.parseColor(statusColor))
        b.tvSlotStatus.text = statusText
    }

    private fun onSlotClicked(slot: SlotView) {
        val st = slot.showtime
        if (st.seatsLeft <= 0) {
            message("Suất chiếu đã hết vé", "${st.time} • ${slot.cinema.shortName} (Vui lòng chọn suất khác)")
            return
        }

        // Trả ô cũ về màu mặc định, bật màu nổi bật cho ô mới
        selectedKey?.let { slotViews[it] }?.let { styleSlot(it, selected = false) }
        styleSlot(slot, selected = true)

        selectedKey = slot.key
        selectedCinemaShort = slot.cinema.shortName
        selectedTime = st.time
        selectedFormat = slot.format.title
        updateSummary()

        message("Đã chọn suất chiếu", "$selectedMovieTitle • ${st.time} (${slot.cinema.shortName})")
    }

    private fun applyDefaultSelectionOnce() {
        if (isInitialized) return   // quay lại từ màn khác: giữ nguyên lựa chọn cũ
        isInitialized = true

        val cinema = ShowtimeSamples.cinemas.firstOrNull { it.id == ShowtimeSamples.DEFAULT_CINEMA_ID } ?: return
        selectedKey = slotKey(cinema.id, ShowtimeSamples.DEFAULT_FORMAT, ShowtimeSamples.DEFAULT_TIME)
        selectedCinemaShort = cinema.shortName
        selectedTime = ShowtimeSamples.DEFAULT_TIME
        selectedFormat = ShowtimeSamples.DEFAULT_FORMAT.title
    }

    private fun clearSelection() {
        selectedKey = null
        selectedCinemaShort = ""
        selectedTime = ""
        selectedFormat = ""
    }

    private fun updateSummary() {
        val hasSelection = selectedKey != null
        binding.tvSelectedSummary.text = if (hasSelection) {
            "$selectedCinemaShort • $selectedTime • $selectedDateLabel"
        } else {
            "Chưa chọn suất chiếu • $selectedDateLabel"
        }
        binding.tvSelectedFormat.text = selectedFormat.uppercase()
        binding.btnContinueSeat.alpha = if (hasSelection) 1f else 0.5f
    }

    // ------------------------------------------------------------------
    // 5. NÚT TIẾP TỤC CHỌN GHẾ
    // ------------------------------------------------------------------
    private fun setupContinueButton() {
        binding.btnContinueSeat.isClickable = true
        binding.btnContinueSeat.isEnabled = true
        binding.btnContinueSeat.setOnClickListener {
            if (selectedKey == null) {
                message("Chưa chọn suất chiếu", "Vui lòng chọn một suất chiếu để tiếp tục")
                return@setOnClickListener
            }
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
                    message("Không thể mở màn hình chọn ghế", e2.message)
                }
            }
        }
    }

    private fun slotKey(cinemaId: String, format: ShowFormat, time: String) = "$cinemaId|${format.name}|$time"

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        slotViews.clear()
        _binding = null
    }

    private companion object {
        const val SLOTS_PER_ROW = 4
        const val NEAR_FULL_SEATS = 8   // còn <= 8 ghế thì hiện "Gần kín"
    }
}
