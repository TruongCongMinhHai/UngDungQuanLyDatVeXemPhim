package com.ute.cinenow.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentBookingSuccessBinding
import com.ute.cinenow.ui.movie.MovieDetailFragment
import com.ute.cinenow.ui.movie.MovieDetailSamples
import com.ute.cinenow.utils.showCineMessage

class BookingSuccessFragment : Fragment() {

    private var _binding: FragmentBookingSuccessBinding? = null
    private val binding get() = _binding!!

    private var movieTitle: String = "Hành Tinh Cát 2"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val passedTitle = arguments?.getString(MovieDetailFragment.ARG_MOVIE_TITLE)
        val movie = MovieDetailSamples.find(passedTitle)
        movieTitle = movie.title

        binding.imgSuccessPoster.setImageResource(movie.posterRes)
        binding.imgQrCode.setImageResource(R.drawable.ma_qr)

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnViewETicket.setOnClickListener {
            showCineMessage("Mở chi tiết vé điện tử", subtitle = movieTitle)
            findNavController().navigate(
                R.id.ticketDetailFragment,
                bundleOf(MovieDetailFragment.ARG_MOVIE_TITLE to movieTitle)
            )
        }

        binding.btnBackToHome.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}