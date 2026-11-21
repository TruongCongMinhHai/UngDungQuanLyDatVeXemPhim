package com.ute.cinenow.ui.seat

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentSeatSelectionBinding
import com.ute.cinenow.ui.booking.BookingReviewFragment
import com.ute.cinenow.ui.movie.MovieDetailFragment
import com.ute.cinenow.ui.movie.MovieDetailSamples
import com.ute.cinenow.utils.showCineMessage
import java.text.NumberFormat
import java.util.Locale

class SeatSelectionFragment : Fragment() {

    private enum class SeatType {
        STANDARD, VIP
    }

    private data class SeatViewHolder(
        val container: FrameLayout,
        val label: TextView,
        val star: ImageView
    )

    private var _binding: FragmentSeatSelectionBinding? = null
    private val binding get() = _binding!!

    private val bookedSeats = setOf(
        "A4", "A5", "B1", "B2", "B4", "B5", "B9",
        "C5", "D3", "D8", "E4", "E5", "E6",
        "F4", "G7"
    )

    private val vipSeats = setOf(
        "D1", "D2", "D4", "D5", "D6", "D7", "D9", "D10",
        "E3", "E7", "E8", "E9", "E10",
        "F1", "F2", "F3", "F8", "F9", "F10",
        "G1", "G2", "G3", "G4", "G5", "G6", "G9", "G10"
    )

    private val standardPrice: Long = 165_000L
    private val vipPrice: Long = 210_000L

    private var suggestedSeats: List<String> = emptyList()
    private val selectedSeats = mutableSetOf<String>()

    private var useSmartSuggestion: Boolean = false
    private var peopleCount: Int = 1
    private var selectedZone: String = "Khu vực trung tâm"
    private var avoidCorner: Boolean = false
    private var avoidFrontRow: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        useSmartSuggestion = args?.getBoolean(ARG_USE_SMART_SUGGESTION, false) ?: false
        peopleCount = (args?.getInt(ARG_PEOPLE_COUNT, 1) ?: 1).coerceIn(1, 6)
        selectedZone = args?.getString(ARG_SELECTED_ZONE)?.trim().orEmpty()
            .ifBlank { "Khu vực trung tâm" }
        avoidCorner = args?.getBoolean(ARG_AVOID_CORNER, false) ?: false
        avoidFrontRow = args?.getBoolean(ARG_AVOID_FRONT_ROW, false) ?: false

        if (useSmartSuggestion) {
            suggestedSeats = computeSmartSuggestedSeats()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeatSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMovieInfo()
        setupSuggestionModeUI()
        setupSeatClickListeners()
        setupActions()
        refreshSeatUI()
    }

    private fun seatTypeOf(seatId: String): SeatType {
        return if (vipSeats.contains(seatId)) SeatType.VIP else SeatType.STANDARD
    }

    private fun priceOf(seatId: String): Long {
        return if (seatTypeOf(seatId) == SeatType.VIP) vipPrice else standardPrice
    }

    private fun computeSmartSuggestedSeats(): List<String> {
        val lower = selectedZone.lowercase(Locale.getDefault())
        val candidateRows = when {
            lower.contains("gần màn hình") || lower.contains("gan man hinh") -> listOf("C", "D", "B", "E", "A")
            lower.contains("phía sau") || lower.contains("phia sau") -> listOf("G", "H", "F", "E")
            lower.contains("ghế đôi") || lower.contains("ghe doi") -> listOf("G", "F", "H", "E", "D")
            else -> listOf("F", "E", "D", "G", "C", "H")
        }

        val rowsToSearch = candidateRows.filterNot { row ->
            avoidFrontRow && (row == "A" || row == "B")
        }.ifEmpty { candidateRows }

        val colOrder = if (avoidCorner) {
            listOf(4, 5, 6, 7, 3, 8)
        } else {
            listOf(5, 6, 4, 7, 3, 8, 2, 9, 1, 10)
        }

        for (row in rowsToSearch) {
            for (startCol in colOrder) {
                val block = mutableListOf<String>()
                var valid = true
                for (offset in 0 until peopleCount) {
                    val col = startCol + offset
                    if (col > 10 || (avoidCorner && (col <= 2 || col >= 9))) {
                        valid = false
                        break
                    }
                    val seatId = "$row$col"
                    if (bookedSeats.contains(seatId)) {
                        valid = false
                        break
                    }
                    block.add(seatId)
                }
                if (valid && block.size == peopleCount) {
                    return block
                }
            }
        }

        val fallback = mutableListOf<String>()
        for (row in rowsToSearch) {
            for (col in 1..10) {
                val seatId = "$row$col"
                if (!bookedSeats.contains(seatId) && !fallback.contains(seatId)) {
                    fallback.add(seatId)
                    if (fallback.size == peopleCount) {
                        return fallback
                    }
                }
            }
        }
        return fallback
    }

    private fun setupMovieInfo() {
        val movieTitleArg = arguments?.getString(MovieDetailFragment.ARG_MOVIE_TITLE)
        val movieDetail = MovieDetailSamples.find(movieTitleArg)
        val displayTitle = movieDetail.title.ifBlank {
            movieTitleArg ?: "Hành Tinh Cát: Phần 2"
        }
        binding.tvShowtimeInfo.text = "16:45 • $displayTitle • IMAX 2D"
    }

    private fun setupSuggestionModeUI() {
        if (useSmartSuggestion && suggestedSeats.isNotEmpty()) {
            binding.cardSmartSuggestion.visibility = View.VISIBLE
            binding.btnResetSeats.visibility = View.VISIBLE
            binding.tvSmartBannerTitle.text = "Gợi ý SmartSeat: ${suggestedSeats.joinToString(", ")}"
            binding.tvSmartBannerSubtitle.text = "Phù hợp $peopleCount người • $selectedZone"
        } else {
            binding.cardSmartSuggestion.visibility = View.GONE
            binding.btnResetSeats.visibility = View.GONE
        }
    }

    private fun setupActions() {
        binding.btnResetSeats.setOnClickListener {
            if (suggestedSeats.isEmpty()) {
                showCineMessage("Không có ghế gợi ý để khôi phục")
                return@setOnClickListener
            }
            selectedSeats.clear()
            selectedSeats.addAll(suggestedSeats)
            refreshSeatUI()
            showCineMessage("Đã khôi phục ghế gợi ý ban đầu")
        }

        binding.btnContinueCheckout.setOnClickListener {
            val seatsToProceed = if (selectedSeats.isNotEmpty()) {
                selectedSeats.sorted()
            } else {
                suggestedSeats.sorted()
            }
            if (seatsToProceed.isEmpty()) {
                showCineMessage("Vui lòng chọn ít nhất 1 ghế")
                return@setOnClickListener
            }

            val totalPrice = seatsToProceed.sumOf { priceOf(it) }
            val movieTitle = arguments?.getString(MovieDetailFragment.ARG_MOVIE_TITLE)
                ?: "Hành Tinh Cát: Phần 2"

            val reviewFragment = BookingReviewFragment().apply {
                arguments = Bundle().apply {
                    putString(MovieDetailFragment.ARG_MOVIE_TITLE, movieTitle)
                    putString("arg_showtime_info", "16:45 • IMAX 2D")
                    putStringArrayList("arg_selected_seats", ArrayList(seatsToProceed))
                    putLong("arg_total_price", totalPrice)
                }
            }

            val containerId = (view?.parent as? ViewGroup)?.id ?: return@setOnClickListener
            parentFragmentManager.beginTransaction()
                .replace(containerId, reviewFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun buildSeatViewMap(): Map<String, SeatViewHolder> = mapOf(
        "A1" to SeatViewHolder(binding.seatA1, binding.tvSeatA1, binding.ivStarA1),
        "A2" to SeatViewHolder(binding.seatA2, binding.tvSeatA2, binding.ivStarA2),
        "A3" to SeatViewHolder(binding.seatA3, binding.tvSeatA3, binding.ivStarA3),
        "A4" to SeatViewHolder(binding.seatA4, binding.tvSeatA4, binding.ivStarA4),
        "A5" to SeatViewHolder(binding.seatA5, binding.tvSeatA5, binding.ivStarA5),
        "A6" to SeatViewHolder(binding.seatA6, binding.tvSeatA6, binding.ivStarA6),
        "A7" to SeatViewHolder(binding.seatA7, binding.tvSeatA7, binding.ivStarA7),
        "A8" to SeatViewHolder(binding.seatA8, binding.tvSeatA8, binding.ivStarA8),
        "A9" to SeatViewHolder(binding.seatA9, binding.tvSeatA9, binding.ivStarA9),
        "A10" to SeatViewHolder(binding.seatA10, binding.tvSeatA10, binding.ivStarA10),

        "B1" to SeatViewHolder(binding.seatB1, binding.tvSeatB1, binding.ivStarB1),
        "B2" to SeatViewHolder(binding.seatB2, binding.tvSeatB2, binding.ivStarB2),
        "B3" to SeatViewHolder(binding.seatB3, binding.tvSeatB3, binding.ivStarB3),
        "B4" to SeatViewHolder(binding.seatB4, binding.tvSeatB4, binding.ivStarB4),
        "B5" to SeatViewHolder(binding.seatB5, binding.tvSeatB5, binding.ivStarB5),
        "B6" to SeatViewHolder(binding.seatB6, binding.tvSeatB6, binding.ivStarB6),
        "B7" to SeatViewHolder(binding.seatB7, binding.tvSeatB7, binding.ivStarB7),
        "B8" to SeatViewHolder(binding.seatB8, binding.tvSeatB8, binding.ivStarB8),
        "B9" to SeatViewHolder(binding.seatB9, binding.tvSeatB9, binding.ivStarB9),
        "B10" to SeatViewHolder(binding.seatB10, binding.tvSeatB10, binding.ivStarB10),

        "C1" to SeatViewHolder(binding.seatC1, binding.tvSeatC1, binding.ivStarC1),
        "C2" to SeatViewHolder(binding.seatC2, binding.tvSeatC2, binding.ivStarC2),
        "C3" to SeatViewHolder(binding.seatC3, binding.tvSeatC3, binding.ivStarC3),
        "C4" to SeatViewHolder(binding.seatC4, binding.tvSeatC4, binding.ivStarC4),
        "C5" to SeatViewHolder(binding.seatC5, binding.tvSeatC5, binding.ivStarC5),
        "C6" to SeatViewHolder(binding.seatC6, binding.tvSeatC6, binding.ivStarC6),
        "C7" to SeatViewHolder(binding.seatC7, binding.tvSeatC7, binding.ivStarC7),
        "C8" to SeatViewHolder(binding.seatC8, binding.tvSeatC8, binding.ivStarC8),
        "C9" to SeatViewHolder(binding.seatC9, binding.tvSeatC9, binding.ivStarC9),
        "C10" to SeatViewHolder(binding.seatC10, binding.tvSeatC10, binding.ivStarC10),

        "D1" to SeatViewHolder(binding.seatD1, binding.tvSeatD1, binding.ivStarD1),
        "D2" to SeatViewHolder(binding.seatD2, binding.tvSeatD2, binding.ivStarD2),
        "D3" to SeatViewHolder(binding.seatD3, binding.tvSeatD3, binding.ivStarD3),
        "D4" to SeatViewHolder(binding.seatD4, binding.tvSeatD4, binding.ivStarD4),
        "D5" to SeatViewHolder(binding.seatD5, binding.tvSeatD5, binding.ivStarD5),
        "D6" to SeatViewHolder(binding.seatD6, binding.tvSeatD6, binding.ivStarD6),
        "D7" to SeatViewHolder(binding.seatD7, binding.tvSeatD7, binding.ivStarD7),
        "D8" to SeatViewHolder(binding.seatD8, binding.tvSeatD8, binding.ivStarD8),
        "D9" to SeatViewHolder(binding.seatD9, binding.tvSeatD9, binding.ivStarD9),
        "D10" to SeatViewHolder(binding.seatD10, binding.tvSeatD10, binding.ivStarD10),

        "E1" to SeatViewHolder(binding.seatE1, binding.tvSeatE1, binding.ivStarE1),
        "E2" to SeatViewHolder(binding.seatE2, binding.tvSeatE2, binding.ivStarE2),
        "E3" to SeatViewHolder(binding.seatE3, binding.tvSeatE3, binding.ivStarE3),
        "E4" to SeatViewHolder(binding.seatE4, binding.tvSeatE4, binding.ivStarE4),
        "E5" to SeatViewHolder(binding.seatE5, binding.tvSeatE5, binding.ivStarE5),
        "E6" to SeatViewHolder(binding.seatE6, binding.tvSeatE6, binding.ivStarE6),
        "E7" to SeatViewHolder(binding.seatE7, binding.tvSeatE7, binding.ivStarE7),
        "E8" to SeatViewHolder(binding.seatE8, binding.tvSeatE8, binding.ivStarE8),
        "E9" to SeatViewHolder(binding.seatE9, binding.tvSeatE9, binding.ivStarE9),
        "E10" to SeatViewHolder(binding.seatE10, binding.tvSeatE10, binding.ivStarE10),

        "F1" to SeatViewHolder(binding.seatF1, binding.tvSeatF1, binding.ivStarF1),
        "F2" to SeatViewHolder(binding.seatF2, binding.tvSeatF2, binding.ivStarF2),
        "F3" to SeatViewHolder(binding.seatF3, binding.tvSeatF3, binding.ivStarF3),
        "F4" to SeatViewHolder(binding.seatF4, binding.tvSeatF4, binding.ivStarF4),
        "F5" to SeatViewHolder(binding.seatF5, binding.tvSeatF5, binding.ivStarF5),
        "F6" to SeatViewHolder(binding.seatF6, binding.tvSeatF6, binding.ivStarF6),
        "F7" to SeatViewHolder(binding.seatF7, binding.tvSeatF7, binding.ivStarF7),
        "F8" to SeatViewHolder(binding.seatF8, binding.tvSeatF8, binding.ivStarF8),
        "F9" to SeatViewHolder(binding.seatF9, binding.tvSeatF9, binding.ivStarF9),
        "F10" to SeatViewHolder(binding.seatF10, binding.tvSeatF10, binding.ivStarF10),

        "G1" to SeatViewHolder(binding.seatG1, binding.tvSeatG1, binding.ivStarG1),
        "G2" to SeatViewHolder(binding.seatG2, binding.tvSeatG2, binding.ivStarG2),
        "G3" to SeatViewHolder(binding.seatG3, binding.tvSeatG3, binding.ivStarG3),
        "G4" to SeatViewHolder(binding.seatG4, binding.tvSeatG4, binding.ivStarG4),
        "G5" to SeatViewHolder(binding.seatG5, binding.tvSeatG5, binding.ivStarG5),
        "G6" to SeatViewHolder(binding.seatG6, binding.tvSeatG6, binding.ivStarG6),
        "G7" to SeatViewHolder(binding.seatG7, binding.tvSeatG7, binding.ivStarG7),
        "G8" to SeatViewHolder(binding.seatG8, binding.tvSeatG8, binding.ivStarG8),
        "G9" to SeatViewHolder(binding.seatG9, binding.tvSeatG9, binding.ivStarG9),
        "G10" to SeatViewHolder(binding.seatG10, binding.tvSeatG10, binding.ivStarG10),

        "H1" to SeatViewHolder(binding.seatH1, binding.tvSeatH1, binding.ivStarH1),
        "H2" to SeatViewHolder(binding.seatH2, binding.tvSeatH2, binding.ivStarH2),
        "H3" to SeatViewHolder(binding.seatH3, binding.tvSeatH3, binding.ivStarH3),
        "H4" to SeatViewHolder(binding.seatH4, binding.tvSeatH4, binding.ivStarH4),
        "H5" to SeatViewHolder(binding.seatH5, binding.tvSeatH5, binding.ivStarH5),
        "H6" to SeatViewHolder(binding.seatH6, binding.tvSeatH6, binding.ivStarH6),
        "H7" to SeatViewHolder(binding.seatH7, binding.tvSeatH7, binding.ivStarH7),
        "H8" to SeatViewHolder(binding.seatH8, binding.tvSeatH8, binding.ivStarH8),
        "H9" to SeatViewHolder(binding.seatH9, binding.tvSeatH9, binding.ivStarH9),
        "H10" to SeatViewHolder(binding.seatH10, binding.tvSeatH10, binding.ivStarH10)
    )

    private fun setupSeatClickListeners() {
        val seatMap = buildSeatViewMap()
        seatMap.forEach { (seatId, holder) ->
            holder.container.setOnClickListener {
                if (bookedSeats.contains(seatId)) {
                    showCineMessage("Ghế $seatId đã được đặt")
                    return@setOnClickListener
                }
                if (selectedSeats.contains(seatId)) {
                    selectedSeats.remove(seatId)
                } else {
                    if (selectedSeats.size >= peopleCount) {
                        showCineMessage("Tối đa $peopleCount ghế theo số người đã chọn")
                        return@setOnClickListener
                    }
                    selectedSeats.add(seatId)
                }
                refreshSeatUI()
            }
        }
    }

    private fun refreshSeatUI() {
        val bookedBgColor = "#CBD5E1".toColorInt()
        val bookedTextColor = "#94A3B8".toColorInt()
        val selectedBgColor = "#4F46E5".toColorInt()
        val selectedTextColor = "#FFFFFF".toColorInt()
        val vipBgColor = "#EDE9FE".toColorInt()
        val standardBgColor = "#F1F5F9".toColorInt()
        val vipTextColor = "#312E81".toColorInt()
        val standardTextColor = "#0F172A".toColorInt()

        val seatMap = buildSeatViewMap()
        for ((seatId, holder) in seatMap) {
            val colNumber = seatId.drop(1)
            val isVip = seatTypeOf(seatId) == SeatType.VIP

            when {
                bookedSeats.contains(seatId) -> {
                    holder.label.text = "×"
                    holder.container.backgroundTintList = ColorStateList.valueOf(bookedBgColor)
                    holder.label.setTextColor(bookedTextColor)
                    holder.star.visibility = View.GONE
                }
                selectedSeats.contains(seatId) -> {
                    holder.label.text = colNumber
                    holder.container.backgroundTintList = ColorStateList.valueOf(selectedBgColor)
                    holder.label.setTextColor(selectedTextColor)
                    holder.star.visibility = if (isVip) View.VISIBLE else View.GONE
                }
                isVip -> {
                    holder.label.text = colNumber
                    holder.container.backgroundTintList = ColorStateList.valueOf(vipBgColor)
                    holder.label.setTextColor(vipTextColor)
                    holder.star.visibility = View.VISIBLE
                }
                else -> {
                    holder.label.text = colNumber
                    holder.container.backgroundTintList = ColorStateList.valueOf(standardBgColor)
                    holder.label.setTextColor(standardTextColor)
                    holder.star.visibility = View.GONE
                }
            }
        }
        updateCheckoutSummary()
    }

    private fun updateCheckoutSummary() {
        if (selectedSeats.isEmpty()) {
            binding.tvSelectedSeatSummary.text = "Chưa chọn ghế nào"
            binding.tvSmartSeatReason.text = if (useSmartSuggestion && suggestedSeats.isNotEmpty()) {
                "Gợi ý SmartSeat: ${suggestedSeats.joinToString(", ")} ($selectedZone)."
            } else {
                "Vui lòng chọn ghế trên sơ đồ phòng chiếu."
            }
            binding.tvTotalPrice.text = formatVnd(0L)
        } else {
            val sorted = selectedSeats.sorted()
            val vipCount = sorted.count { seatTypeOf(it) == SeatType.VIP }
            val stdCount = sorted.size - vipCount
            val typeSummary = when {
                vipCount > 0 && stdCount > 0 -> "VIP: $vipCount, Thường: $stdCount"
                vipCount > 0 -> "Ghế VIP ($vipCount)"
                else -> "Ghế thường ($stdCount)"
            }
            binding.tvSelectedSeatSummary.text = "Đang chọn: ${sorted.joinToString(", ")} • $typeSummary"
            binding.tvSmartSeatReason.text = if (sorted.size < peopleCount) {
                "Còn thiếu ${peopleCount - sorted.size} ghế theo số người đã chọn."
            } else {
                "Đã chọn ${sorted.size} ghế ($selectedZone) tại phòng chiếu IMAX."
            }
            val total = sorted.sumOf { priceOf(it) }
            binding.tvTotalPrice.text = formatVnd(total)
        }
    }

    private fun formatVnd(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"))
        return "${formatter.format(amount)}đ"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_USE_SMART_SUGGESTION = "arg_use_smart_suggestion"
        const val ARG_PEOPLE_COUNT = "arg_people_count"
        const val ARG_SELECTED_ZONE = "arg_selected_zone"
        const val ARG_SEAT_TYPE = "arg_seat_type"
        const val ARG_AVOID_CORNER = "arg_avoid_corner"
        const val ARG_AVOID_FRONT_ROW = "arg_avoid_front_row"
    }
}