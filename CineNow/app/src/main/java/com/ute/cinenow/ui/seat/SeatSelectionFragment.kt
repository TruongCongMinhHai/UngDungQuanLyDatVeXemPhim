package com.ute.cinenow.ui.seat

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentSeatSelectionBinding
import com.ute.cinenow.ui.movie.MovieDetailFragment
import com.ute.cinenow.ui.movie.MovieDetailSamples
import com.ute.cinenow.utils.showCineMessage

class SeatSelectionFragment : Fragment() {

    private var _binding: FragmentSeatSelectionBinding? = null
    private val binding get() = _binding!!

    private val suggestedSeats = listOf("F5", "F6", "F7")
    private val selectedSeats = mutableSetOf<String>()
    private val seatPrice = 165_000
    private var useSmartSuggestion: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        useSmartSuggestion = arguments?.getBoolean(ARG_USE_SMART_SUGGESTION, false) ?: false
        if (savedInstanceState == null && useSmartSuggestion) {
            selectedSeats.addAll(suggestedSeats)
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
            showCineMessage("Đã khôi phục ghế gợi ý SmartSeat")
        }

        binding.btnContinueCheckout.setOnClickListener {
            if (selectedSeats.isEmpty()) {
                showCineMessage("Vui lòng chọn ít nhất 1 ghế")
            } else {
                val seatListText = selectedSeats.sorted().joinToString(", ")
                showCineMessage(
                    message = "Đang chuyển sang thanh toán",
                    subtitle = "Ghế đã chọn: $seatListText"
                )
            }
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
                if (selectedSeats.contains(seatName)) {
                    selectedSeats.remove(seatName)
                } else {
                    selectedSeats.add(seatName)
                }
                refreshSeatUI()
            }
        }
    }

    private fun refreshSeatUI() {
        val seatMap = buildSelectableSeatMap()
        seatMap.forEach { (seatName, seatView) ->
            if (selectedSeats.contains(seatName)) {
                seatView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
                seatView.setTextColor(Color.WHITE)
            } else {
                seatView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F3F0FF"))
                seatView.setTextColor(Color.parseColor("#312E81"))
            }
        }
        updateCheckoutSummary()
    }

    private fun updateCheckoutSummary() {
        if (selectedSeats.isEmpty()) {
            binding.tvSelectedSeatSummary.text = "Chưa chọn ghế nào"
            binding.tvSmartSeatReason.text = "Vui lòng chọn ghế trên sơ đồ phòng chiếu."
            binding.tvTotalPrice.text = "0đ"
        } else {
            val sortedSeats = selectedSeats.sorted().joinToString(", ")
            binding.tvSelectedSeatSummary.text = "Đang chọn: $sortedSeats"
            binding.tvSmartSeatReason.text = if (useSmartSuggestion) {
                "Thỏa mãn: ${selectedSeats.size} ghế đang chọn tại phòng chiếu IMAX."
            } else {
                "Đã chọn thủ công ${selectedSeats.size} ghế trên sơ đồ IMAX."
            }
            val totalPrice = selectedSeats.size * seatPrice
            val formattedPrice = String.format("%,dđ", totalPrice).replace(',', '.')
            binding.tvTotalPrice.text = formattedPrice
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_USE_SMART_SUGGESTION = "arg_use_smart_suggestion"
    }
}