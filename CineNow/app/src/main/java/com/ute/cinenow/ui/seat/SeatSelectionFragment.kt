package com.ute.cinenow.ui.seat

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentSeatSelectionBinding
import com.ute.cinenow.ui.booking.BookingReviewFragment
import com.ute.cinenow.ui.movie.MovieDetailFragment
import com.ute.cinenow.ui.movie.MovieDetailSamples
import com.ute.cinenow.utils.showCineMessage
import java.util.Locale

class SeatSelectionFragment : Fragment() {

    private var _binding: FragmentSeatSelectionBinding? = null
    private val binding get() = _binding!!

    private val occupiedSeats = setOf(
        "A4", "B1", "B2", "B4", "B5", "B9",
        "C5", "D3", "D8", "E4", "E5", "E6",
        "F4", "G7"
    )

    private var suggestedSeats: List<String> = emptyList()
    private val selectedSeats = mutableSetOf<String>()
    private val seatPrice = 165_000

    private var useSmartSuggestion: Boolean = false
    private var peopleCount: Int = 1
    private var selectedZone: String = "Khu vực trung tâm"
    private var avoidCorner: Boolean = false
    private var avoidFrontRow: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        useSmartSuggestion = args?.getBoolean(ARG_USE_SMART_SUGGESTION, false) ?: false
        peopleCount = (args?.getInt(ARG_PEOPLE_COUNT, 1) ?: 1).coerceIn(1, 8)
        selectedZone = args?.getString(ARG_SELECTED_ZONE) ?: "Khu vực trung tâm"
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

    private fun computeSmartSuggestedSeats(): List<String> {
        val candidateRows = when (selectedZone) {
            "Gần màn hình" -> listOf("C", "D", "B", "E", "A")
            "Hàng ghế phía sau" -> listOf("G", "H", "F", "E")
            "Ghế đôi" -> listOf("G", "F", "H", "E", "D")
            else -> listOf("F", "E", "D", "G", "C", "H")
        }

        val filteredRows = mutableListOf<String>()
        for (row in candidateRows) {
            if (avoidFrontRow && (row == "A" || row == "B" || row == "C")) {
                continue
            }
            filteredRows.add(row)
        }
        val rowsToSearch = filteredRows.ifEmpty { candidateRows }

        val colOrder = if (avoidCorner) {
            listOf(4, 5, 6, 7, 3, 8)
        } else {
            listOf(5, 6, 4, 7, 3, 8, 2, 9, 1, 10)
        }

        for (row in rowsToSearch) {
            for (startCol in colOrder) {
                val block = mutableListOf<String>()
                var validBlock = true
                for (offset in 0 until peopleCount) {
                    val col = startCol + offset
                    if (col > 10) {
                        validBlock = false
                        break
                    }
                    if (avoidCorner && col !in 3..8) {
                        validBlock = false
                        break
                    }
                    val seatCode = "$row$col"
                    if (occupiedSeats.contains(seatCode)) {
                        validBlock = false
                        break
                    }
                    block.add(seatCode)
                }
                if (validBlock && block.size == peopleCount) {
                    return block
                }
            }
        }

        val fallback = mutableListOf<String>()
        for (row in rowsToSearch) {
            for (col in 1..10) {
                val seatCode = "$row$col"
                if (!occupiedSeats.contains(seatCode) && !fallback.contains(seatCode)) {
                    fallback.add(seatCode)
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
        if (useSmartSuggestion) {
            binding.cardSmartSuggestion.visibility = View.VISIBLE
            binding.btnResetSeats.visibility = View.VISIBLE
            val suggestionText = if (suggestedSeats.isNotEmpty()) {
                "Gợi ý: ${suggestedSeats.joinToString(", ")} • $selectedZone"
            } else {
                "Phù hợp $peopleCount người • $selectedZone"
            }
            binding.tvSmartSeatReason.text = suggestionText
        } else {
            binding.cardSmartSuggestion.visibility = View.GONE
            binding.btnResetSeats.visibility = View.GONE
        }
    }

    private fun setupActions() {
        binding.btnResetSeats.setOnClickListener {
            selectedSeats.clear()
            selectedSeats.addAll(suggestedSeats)
            refreshSeatUI()
            showCineMessage("Đã áp dụng ghế gợi ý SmartSeat")
        }

        binding.btnContinueCheckout.setOnClickListener {
            val seatsToProceed = if (selectedSeats.isNotEmpty()) {
                selectedSeats.sorted()
            } else {
                suggestedSeats.sorted()
            }
            val totalPrice = seatsToProceed.size * seatPrice
            val movieTitle = arguments?.getString(MovieDetailFragment.ARG_MOVIE_TITLE)
                ?: "Hành Tinh Cát: Phần 2"

            val reviewFragment = BookingReviewFragment().apply {
                arguments = Bundle().apply {
                    putString(MovieDetailFragment.ARG_MOVIE_TITLE, movieTitle)
                    putString("arg_showtime_info", "16:45 • IMAX 2D")
                    putStringArrayList("arg_selected_seats", ArrayList(seatsToProceed))
                    putInt("arg_seat_price", seatPrice)
                    putInt("arg_total_price", totalPrice)
                }
            }

            val containerId = (view?.parent as? ViewGroup)?.id ?: return@setOnClickListener
            parentFragmentManager.beginTransaction()
                .replace(containerId, reviewFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun buildSelectableSeatMap() = mapOf(
        "F5" to binding.seatF5,
        "F6" to binding.seatF6,
        "F7" to binding.seatF7,
        "F1" to binding.seatF1,
        "F2" to binding.seatF2,
        "F3" to binding.seatF3,
        "F8" to binding.seatF8,
        "F9" to binding.seatF9,
        "F10" to binding.seatF10,
        "C1" to binding.seatC1,
        "C2" to binding.seatC2,
        "C3" to binding.seatC3,
        "C4" to binding.seatC4,
        "C6" to binding.seatC6,
        "C7" to binding.seatC7
    )

    private fun setupSeatClickListeners() {
        val seatMap = buildSelectableSeatMap()
        seatMap.forEach { (seatName, seatView) ->
            seatView.setOnClickListener {
                if (occupiedSeats.contains(seatName)) {
                    showCineMessage("Ghế $seatName đã được đặt")
                    return@setOnClickListener
                }
                if (selectedSeats.contains(seatName)) {
                    selectedSeats.remove(seatName)
                } else {
                    if (selectedSeats.size >= 8) {
                        showCineMessage("Tối đa 8 ghế mỗi lần đặt")
                        return@setOnClickListener
                    }
                    selectedSeats.add(seatName)
                }
                refreshSeatUI()
            }
        }
    }

    private fun refreshSeatUI() {
        val occupiedBgColor = "#E2E8F0".toColorInt()
        val occupiedTextColor = "#94A3B8".toColorInt()
        val selectedBgColor = "#5850EC".toColorInt()
        val selectedTextColor = "#FFFFFF".toColorInt()
        val unselectedBgColor = "#F3F0FF".toColorInt()
        val unselectedTextColor = "#312E81".toColorInt()

        val seatMap = buildSelectableSeatMap()
        for (entry in seatMap) {
            val seatName = entry.key
            val seatView = entry.value
            when {
                occupiedSeats.contains(seatName) -> {
                    seatView.backgroundTintList = ColorStateList.valueOf(occupiedBgColor)
                    seatView.setTextColor(occupiedTextColor)
                }
                selectedSeats.contains(seatName) -> {
                    seatView.backgroundTintList = ColorStateList.valueOf(selectedBgColor)
                    seatView.setTextColor(selectedTextColor)
                }
                else -> {
                    seatView.backgroundTintList = ColorStateList.valueOf(unselectedBgColor)
                    seatView.setTextColor(unselectedTextColor)
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
            binding.tvTotalPrice.text = "0đ"
        } else {
            val sortedSeats = selectedSeats.sorted().joinToString(", ")
            binding.tvSelectedSeatSummary.text = "Đang chọn: $sortedSeats"
            binding.tvSmartSeatReason.text = if (useSmartSuggestion) {
                "Thỏa mãn: ${selectedSeats.size} ghế ($selectedZone) tại phòng chiếu IMAX."
            } else {
                "Đã chọn thủ công ${selectedSeats.size} ghế trên sơ đồ IMAX."
            }
            val totalPrice = selectedSeats.size * seatPrice
            val formattedPrice = String.format(Locale.getDefault(), "%,dđ", totalPrice)
                .replace(',', '.')
            binding.tvTotalPrice.text = formattedPrice
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_USE_SMART_SUGGESTION = "arg_use_smart_suggestion"
        const val ARG_PEOPLE_COUNT = "arg_people_count"
        const val ARG_SELECTED_ZONE = "arg_selected_zone"
        const val ARG_AVOID_CORNER = "arg_avoid_corner"
        const val ARG_AVOID_FRONT_ROW = "arg_avoid_front_row"
    }
}