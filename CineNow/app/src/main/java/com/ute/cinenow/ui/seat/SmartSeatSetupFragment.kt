package com.ute.cinenow.ui.seat

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentSmartSeatSetupBinding
import com.ute.cinenow.ui.movie.MovieDetailFragment
import com.ute.cinenow.utils.showCineMessage

class SmartSeatSetupFragment : Fragment() {

    private var _binding: FragmentSmartSeatSetupBinding? = null
    private val binding get() = _binding!!

    private var peopleCount: Int = 1
    private var selectedZone: SeatZone = SeatZone.CENTER
    private var selectedSeatType: SeatTypePreference = SeatTypePreference.ANY
    private var isAvoidCorner: Boolean = false
    private var isAvoidFrontRow: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmartSeatSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPeopleCounter()
        setupZoneOptions()
        injectAndSetupSeatTypeCard()
        setupExtraPreferences()
        setupNavigationButtons()
    }

    private fun setupPeopleCounter() {
        updatePeopleUI()

        binding.btnMinus.setOnClickListener {
            if (peopleCount > 1) {
                peopleCount--
                updatePeopleUI()
            }
        }

        binding.btnPlus.setOnClickListener {
            if (peopleCount < 6) {
                peopleCount++
                updatePeopleUI()
            } else {
                showCineMessage("SmartSeat hỗ trợ tối đa 6 ghế liền nhau")
            }
        }
    }

    private fun updatePeopleUI() {
        binding.tvPeopleCount.text = peopleCount.toString()
        binding.tvPeopleBadge.text = "$peopleCount người"

        binding.btnMinus.isEnabled = peopleCount > 1
        binding.btnMinus.alpha = if (peopleCount > 1) 1.0f else 0.35f

        binding.btnPlus.isEnabled = peopleCount < 6
        binding.btnPlus.alpha = if (peopleCount < 6) 1.0f else 0.35f
    }

    private fun setupZoneOptions() {
        data class ZoneOptionHolder(
            val container: LinearLayout,
            val dot: View,
            val title: TextView,
            val zone: SeatZone
        )

        val options = listOf(
            ZoneOptionHolder(binding.optCenter, binding.dotOptCenter, binding.tvOptCenterTitle, SeatZone.CENTER),
            ZoneOptionHolder(binding.optFront, binding.dotOptFront, binding.tvOptFrontTitle, SeatZone.FRONT),
            ZoneOptionHolder(binding.optBack, binding.dotOptBack, binding.tvOptBackTitle, SeatZone.BACK),
            ZoneOptionHolder(binding.optCouple, binding.dotOptCouple, binding.tvOptCoupleTitle, SeatZone.COUPLE)
        )

        fun highlightZone(target: ZoneOptionHolder) {
            for (opt in options) {
                opt.container.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
                opt.dot.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
                opt.title.setTextColor(Color.parseColor("#0F172A"))
            }
            target.container.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EDE9FE"))
            target.dot.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
            target.title.setTextColor(Color.parseColor("#312E81"))
            selectedZone = target.zone
        }

        highlightZone(options.first())

        for (clicked in options) {
            clicked.container.setOnClickListener {
                highlightZone(clicked)
            }
        }
    }

    /**
     * Tự động tạo và gắn Card "Bạn muốn chọn loại ghế nào?" vào giao diện
     * Giữa Card 2 (Khu vực) và Card 3 (Ưu tiên) mà KHÔNG cần sửa XML
     */
    private fun injectAndSetupSeatTypeCard() {
        val card2 = binding.optCenter.parent?.parent as? View ?: return
        val parentLayout = card2.parent as? LinearLayout ?: return
        val card2Index = parentLayout.indexOfChild(card2)

        val density = resources.displayMetrics.density

        val cardView = CardView(requireContext()).apply {
            radius = density * 16
            cardElevation = density * 1
            setCardBackgroundColor(Color.WHITE)
            useCompatPadding = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (density * 14).toInt()
            }
        }

        val cardContent = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val pad = (density * 16).toInt()
            setPadding(pad, pad, pad, pad)
        }

        val titleTv = TextView(requireContext()).apply {
            text = "Bạn muốn chọn loại ghế nào?"
            setTextColor(Color.parseColor("#0F172A"))
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
        }

        val subtitleTv = TextView(requireContext()).apply {
            text = "Lọc theo loại ghế phù hợp với nhu cầu của bạn"
            setTextColor(Color.parseColor("#64748B"))
            textSize = 12f
            setPadding(0, (density * 2).toInt(), 0, 0)
        }

        val rowLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (density * 48).toInt()
            ).apply {
                topMargin = (density * 12).toInt()
            }
        }

        fun createTypeOption(text: String, isFirst: Boolean): Pair<LinearLayout, TextView> {
            val container = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.bg_rounded_icon)
                isClickable = true
                isFocusable = true
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                ).apply {
                    if (!isFirst) {
                        marginStart = (density * 8).toInt()
                    }
                }
            }
            val tv = TextView(requireContext()).apply {
                this.text = text
                textSize = 13f
                typeface = Typeface.DEFAULT_BOLD
            }
            container.addView(tv)
            return Pair(container, tv)
        }

        val (optAll, tvAll) = createTypeOption("Tất cả", true)
        val (optStandard, tvStandard) = createTypeOption("Ghế thường", false)
        val (optVip, tvVip) = createTypeOption("Ghế VIP", false)

        rowLayout.addView(optAll)
        rowLayout.addView(optStandard)
        rowLayout.addView(optVip)

        cardContent.addView(titleTv)
        cardContent.addView(subtitleTv)
        cardContent.addView(rowLayout)
        cardView.addView(cardContent)

        val options = listOf(
            Triple(optAll, tvAll, SeatTypePreference.ANY),
            Triple(optStandard, tvStandard, SeatTypePreference.STANDARD),
            Triple(optVip, tvVip, SeatTypePreference.VIP)
        )

        fun highlightType(selected: SeatTypePreference) {
            selectedSeatType = selected
            for ((container, tv, type) in options) {
                if (type == selected) {
                    container.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EDE9FE"))
                    tv.setTextColor(Color.parseColor("#312E81"))
                } else {
                    container.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
                    tv.setTextColor(Color.parseColor("#64748B"))
                }
            }
        }

        highlightType(SeatTypePreference.ANY)

        for ((container, _, type) in options) {
            container.setOnClickListener {
                highlightType(type)
            }
        }

        parentLayout.addView(cardView, card2Index + 1)
    }

    private fun setupExtraPreferences() {
        binding.extraAvoidCorner.setOnClickListener {
            isAvoidCorner = !isAvoidCorner
            binding.checkAvoidCorner.backgroundTintList = ColorStateList.valueOf(
                if (isAvoidCorner) Color.parseColor("#5850EC") else Color.parseColor("#CBD5E1")
            )
        }

        binding.extraAvoidFrontRow.setOnClickListener {
            isAvoidFrontRow = !isAvoidFrontRow
            binding.checkAvoidFrontRow.backgroundTintList = ColorStateList.valueOf(
                if (isAvoidFrontRow) Color.parseColor("#5850EC") else Color.parseColor("#CBD5E1")
            )
        }
    }

    private fun setupNavigationButtons() {
        binding.btnFindSeats.setOnClickListener {
            val preference = SeatPreference(
                numberOfPeople = peopleCount,
                preferredZone = selectedZone,
                preferredSeatType = selectedSeatType,
                avoidCorner = isAvoidCorner,
                avoidFrontRows = isAvoidFrontRow
            )

            val allSeats = MockSeatRepository.getSeats()
            val suggestion = SmartSeatAlgorithm.findBestSeats(preference, allSeats)

            if (!suggestion.isSuccess || suggestion.seats.isEmpty()) {
                showCineMessage(suggestion.reason)
                return@setOnClickListener
            }

            showCineMessage(
                message = "Đã tìm thấy ${suggestion.seats.size} ghế phù hợp",
                subtitle = "Độ tương thích: ${suggestion.matchPercentage}%"
            )

            navigateToSeatSelection(
                useSmartSuggestion = true,
                preference = preference,
                suggestion = suggestion
            )
        }

        binding.btnManualSelect.setOnClickListener {
            val preference = SeatPreference(
                numberOfPeople = peopleCount,
                preferredZone = selectedZone,
                preferredSeatType = selectedSeatType,
                avoidCorner = isAvoidCorner,
                avoidFrontRows = isAvoidFrontRow
            )
            navigateToSeatSelection(
                useSmartSuggestion = false,
                preference = preference,
                suggestion = null
            )
        }
    }

    private fun navigateToSeatSelection(
        useSmartSuggestion: Boolean,
        preference: SeatPreference,
        suggestion: SeatSuggestion?
    ) {
        val args = bundleOf(
            SeatSelectionFragment.ARG_USE_SMART_SUGGESTION to useSmartSuggestion,
            SeatSelectionFragment.ARG_SEAT_PREFERENCE to preference,
            SeatSelectionFragment.ARG_SEAT_SUGGESTION to suggestion,
            SeatSelectionFragment.ARG_PEOPLE_COUNT to preference.numberOfPeople,
            SeatSelectionFragment.ARG_SELECTED_ZONE to preference.preferredZone.displayName
        )

        val movieTitle = arguments?.getString(MovieDetailFragment.ARG_MOVIE_TITLE)
        if (!movieTitle.isNullOrBlank()) {
            args.putString(MovieDetailFragment.ARG_MOVIE_TITLE, movieTitle)
        }

        try {
            findNavController().navigate(R.id.seatSelectionFragment, args)
        } catch (e: Exception) {
            showCineMessage("Chuyển sang sơ đồ chọn ghế")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}