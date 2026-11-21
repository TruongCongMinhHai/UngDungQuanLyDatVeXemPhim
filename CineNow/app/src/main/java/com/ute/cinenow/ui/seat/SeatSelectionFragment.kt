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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentSeatSelectionBinding
import com.ute.cinenow.ui.movie.MovieDetailFragment
import com.ute.cinenow.ui.movie.MovieDetailSamples
import com.ute.cinenow.utils.showCineMessage
import java.text.NumberFormat
import java.util.Locale

class SeatSelectionFragment : Fragment() {

    private data class SeatViewHolder(
        val container: FrameLayout,
        val label: TextView,
        val star: ImageView
    )

    private var _binding: FragmentSeatSelectionBinding? = null
    private val binding get() = _binding!!

    // Dữ liệu ghế từ MockSeatRepository (đã cập nhật VIP D4..D7, E4..E7, F4..F7, G4..G7)
    private val allSeatsMap: Map<String, Seat> by lazy {
        MockSeatRepository.getSeats().associateBy { it.seatId }
    }

    // Nhóm ghế ban đầu do SmartSeat gợi ý (dùng để khôi phục chính xác)
    private var initialSuggestedSeats: List<Seat> = emptyList()
    // Danh sách ghế đang được người dùng chọn
    private val selectedSeats = mutableListOf<Seat>()

    private var useSmartSuggestion: Boolean = false
    private var seatPreference: SeatPreference = SeatPreference()
    private var seatSuggestion: SeatSuggestion? = null
    private var peopleCount: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        useSmartSuggestion = args?.getBoolean(ARG_USE_SMART_SUGGESTION, false) ?: false

        val passedPref = args?.getSerializable(ARG_SEAT_PREFERENCE) as? SeatPreference
        val passedSuggestion = args?.getSerializable(ARG_SEAT_SUGGESTION) as? SeatSuggestion

        peopleCount = passedPref?.numberOfPeople ?: (args?.getInt(ARG_PEOPLE_COUNT, 1) ?: 1).coerceIn(1, 6)
        seatPreference = passedPref ?: SeatPreference(numberOfPeople = peopleCount)
        seatSuggestion = passedSuggestion

        if (useSmartSuggestion) {
            if (passedSuggestion != null && passedSuggestion.isSuccess) {
                initialSuggestedSeats = passedSuggestion.seats
            } else {
                val suggestion = SmartSeatAlgorithm.findBestSeats(seatPreference, MockSeatRepository.getSeats())
                seatSuggestion = suggestion
                initialSuggestedSeats = suggestion.seats
            }
            selectedSeats.clear()
            selectedSeats.addAll(initialSuggestedSeats)
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
        if (useSmartSuggestion && initialSuggestedSeats.isNotEmpty() && seatSuggestion != null) {
            val suggestion = seatSuggestion!!
            binding.cardSmartSuggestion.visibility = View.VISIBLE
            binding.btnResetSeats.visibility = View.VISIBLE

            val seatNames = initialSuggestedSeats.joinToString(", ") { it.seatId }
            binding.tvSmartBannerTitle.text = "Gợi ý SmartSeat: $seatNames"
            binding.tvSmartMatchBadge.text = "${suggestion.matchPercentage}% Tương thích"
            binding.tvSmartBannerSubtitle.text = suggestion.reason
        } else {
            binding.cardSmartSuggestion.visibility = View.GONE
            binding.btnResetSeats.visibility = View.GONE
        }
    }

    private fun setupActions() {
        // Nút Khôi phục về nhóm ghế SmartSeat ban đầu
        binding.btnResetSeats.setOnClickListener {
            if (initialSuggestedSeats.isEmpty()) {
                showCineMessage("Không có ghế gợi ý để khôi phục")
                return@setOnClickListener
            }
            selectedSeats.clear()
            selectedSeats.addAll(initialSuggestedSeats)
            refreshSeatUI()
            showCineMessage("Đã khôi phục nhóm ghế SmartSeat ban đầu")
        }

        // Nút Tiếp tục đặt vé
        binding.btnContinueCheckout.setOnClickListener {
            if (selectedSeats.isEmpty()) {
                showCineMessage("Vui lòng chọn ghế trước khi tiếp tục")
                return@setOnClickListener
            }

            if (selectedSeats.size < peopleCount) {
                showCineMessage("Bạn cần chọn đủ $peopleCount ghế theo yêu cầu")
                return@setOnClickListener
            }

            val sortedSeats = selectedSeats.sortedBy { it.seatId }
            val seatCodes = ArrayList(sortedSeats.map { it.seatId })
            val totalPrice = sortedSeats.sumOf { it.price }

            val movieTitle = arguments?.getString(MovieDetailFragment.ARG_MOVIE_TITLE)
                ?: "Hành Tinh Cát: Phần 2"

            val reviewArgs = bundleOf(
                MovieDetailFragment.ARG_MOVIE_TITLE to movieTitle,
                "arg_showtime_info" to "16:45 • IMAX 2D",
                "arg_selected_seats" to seatCodes,
                "arg_total_price" to totalPrice
            )

            try {
                findNavController().navigate(R.id.bookingReviewFragment, reviewArgs)
            } catch (e: Exception) {
                showCineMessage("Chuyển sang màn hình kiểm tra đặt vé")
            }
        }
    }

    private fun setupSeatClickListeners() {
        val seatMap = buildSeatViewMap()

        for ((seatId, holder) in seatMap) {
            val seatData = allSeatsMap[seatId] ?: continue

            holder.container.setOnClickListener {
                when (seatData.status) {
                    SeatStatus.BOOKED -> {
                        showCineMessage("Ghế $seatId đã được đặt trước")
                        return@setOnClickListener
                    }
                    SeatStatus.BLOCKED -> {
                        showCineMessage("Ghế $seatId đang bảo trì / tạm khóa")
                        return@setOnClickListener
                    }
                    SeatStatus.AVAILABLE -> {
                        val isAlreadySelected = selectedSeats.any { it.seatId == seatId }

                        if (isAlreadySelected) {
                            selectedSeats.removeAll { it.seatId == seatId }
                        } else {
                            if (selectedSeats.size >= peopleCount) {
                                showCineMessage("Tối đa $peopleCount ghế theo số người bạn đã chọn")
                                return@setOnClickListener
                            }
                            selectedSeats.add(seatData)
                        }
                        refreshSeatUI()
                    }
                }
            }
        }
    }

    private fun refreshSeatUI() {
        val bookedBgColor = "#CBD5E1".toColorInt()
        val bookedTextColor = "#94A3B8".toColorInt()
        val selectedBgColor = "#5850EC".toColorInt()
        val selectedTextColor = "#FFFFFF".toColorInt()
        val vipBgColor = "#EDE9FE".toColorInt()
        val vipTextColor = "#312E81".toColorInt()
        val standardBgColor = "#F1F5F9".toColorInt()
        val standardTextColor = "#0F172A".toColorInt()

        val seatMap = buildSeatViewMap()

        for ((seatId, holder) in seatMap) {
            val seat = allSeatsMap[seatId] ?: continue
            val colNumber = seat.number.toString()
            val isSelected = selectedSeats.any { it.seatId == seatId }
            val isVip = seat.seatType == SeatType.VIP

            when {
                seat.status == SeatStatus.BOOKED || seat.status == SeatStatus.BLOCKED -> {
                    holder.label.text = "×"
                    holder.container.backgroundTintList = ColorStateList.valueOf(bookedBgColor)
                    holder.label.setTextColor(bookedTextColor)
                    holder.star.visibility = View.GONE
                }
                isSelected -> {
                    holder.label.text = colNumber
                    holder.container.backgroundTintList = ColorStateList.valueOf(selectedBgColor)
                    holder.label.setTextColor(selectedTextColor)
                    holder.star.visibility = if (isVip) View.VISIBLE else View.GONE
                }
                isVip -> {
                    // Đúng 16 ghế VIP (D4..D7, E4..E7, F4..F7, G4..G7)
                    holder.label.text = colNumber
                    holder.container.backgroundTintList = ColorStateList.valueOf(vipBgColor)
                    holder.label.setTextColor(vipTextColor)
                    holder.star.visibility = View.VISIBLE
                }
                else -> {
                    // Tất cả ghế còn lại là Standard
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
            binding.tvSmartSeatReason.text = if (useSmartSuggestion && initialSuggestedSeats.isNotEmpty()) {
                "Gợi ý SmartSeat: ${initialSuggestedSeats.joinToString { it.seatId }} (${seatPreference.preferredZone.displayName})."
            } else {
                "Vui lòng chọn đủ $peopleCount ghế trên sơ đồ phòng chiếu."
            }
            binding.tvTotalPrice.text = formatVnd(0L)
            return
        }

        val sorted = selectedSeats.sortedBy { it.seatId }
        val seatNames = sorted.joinToString(", ") { it.seatId }
        val vipCount = sorted.count { it.seatType == SeatType.VIP }
        val stdCount = sorted.count { it.seatType == SeatType.STANDARD }

        val typeSummary = when {
            vipCount > 0 && stdCount > 0 -> "VIP: $vipCount, Thường: $stdCount"
            vipCount > 0 -> "Ghế VIP ($vipCount)"
            else -> "Ghế thường ($stdCount)"
        }

        binding.tvSelectedSeatSummary.text = "• Đang chọn: $seatNames • $typeSummary"

        binding.tvSmartSeatReason.text = if (sorted.size < peopleCount) {
            "⚠️ Còn thiếu ${peopleCount - sorted.size} ghế theo yêu cầu (Cần chọn đủ $peopleCount ghế)."
        } else {
            "✓ Đã chọn đủ ${sorted.size} ghế tại phòng chiếu 03 IMAX."
        }

        val totalAmount = sorted.sumOf { it.price }
        binding.tvTotalPrice.text = formatVnd(totalAmount)
    }

    private fun formatVnd(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"))
        return "${formatter.format(amount)}đ"
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_USE_SMART_SUGGESTION = "arg_use_smart_suggestion"
        const val ARG_SEAT_PREFERENCE = "arg_seat_preference"
        const val ARG_SEAT_SUGGESTION = "arg_seat_suggestion"
        const val ARG_PEOPLE_COUNT = "arg_people_count"
        const val ARG_SELECTED_ZONE = "arg_selected_zone"
    }
}