package com.ute.cinenow.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private var selectedDateLabel: String = "Hôm nay (25/10)"
    private var selectedCinemaShort: String = "Landmark 81"
    private var selectedTime: String = "16:45"
    private var selectedFormat: String = "IMAX LASER"

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
        setupClickListeners()
    }

    private fun bindMovieInfo(title: String, posterRes: Int) {
        binding.imgMoviePoster.setImageResource(posterRes)
        binding.tvSelectedSummary.text = "$selectedCinemaShort • $selectedTime • $selectedDateLabel"
        binding.header.title = "Chọn suất chiếu"
    }

    private fun updateSummaryText() {
        binding.tvSelectedSummary.text = "$selectedCinemaShort • $selectedTime • $selectedDateLabel"
    }

    private fun setupClickListeners() {
        binding.day24.setOnClickListener {
            selectedDateLabel = "Hôm nay (24/10)"
            updateSummaryText()
            showCineMessage("Đã chọn ngày chiếu", subtitle = "$selectedMovieTitle • $selectedDateLabel")
        }

        binding.day25.setOnClickListener {
            selectedDateLabel = "Ngày mai (25/10)"
            updateSummaryText()
            showCineMessage("Đã chọn ngày chiếu", subtitle = "$selectedMovieTitle • $selectedDateLabel")
        }

        binding.day26.setOnClickListener {
            selectedDateLabel = "Cuối tuần (26/10)"
            updateSummaryText()
            showCineMessage("Đã chọn ngày chiếu", subtitle = "$selectedMovieTitle • $selectedDateLabel")
        }

        binding.day27.setOnClickListener {
            selectedDateLabel = "Chủ nhật (27/10)"
            updateSummaryText()
            showCineMessage("Đã chọn ngày chiếu", subtitle = "$selectedMovieTitle • $selectedDateLabel")
        }

        binding.day28.setOnClickListener {
            selectedDateLabel = "Thứ hai (28/10)"
            updateSummaryText()
            showCineMessage("Đã chọn ngày chiếu", subtitle = "$selectedMovieTitle • $selectedDateLabel")
        }

        binding.slot1645.setOnClickListener {
            selectedCinemaShort = "Landmark 81"
            selectedTime = "16:45"
            selectedFormat = "IMAX LASER"
            updateSummaryText()
            showCineMessage("Đã chọn suất chiếu", subtitle = "$selectedMovieTitle • $selectedTime")
        }

        binding.btnContinueSeat.setOnClickListener {
            showCineMessage(
                message = "Đã chọn suất chiếu",
                subtitle = "$selectedMovieTitle • $selectedTime ($selectedCinemaShort)"
            )
            findNavController().navigate(
                R.id.seatSelectionFragment,
                bundleOf(
                    MovieDetailFragment.ARG_MOVIE_TITLE to selectedMovieTitle,
                    "showtime" to selectedTime,
                    "cinema" to selectedCinemaShort,
                    "dateLabel" to selectedDateLabel,
                    "format" to selectedFormat
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}