package com.ute.cinenow.ui.cinetogether

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentVoteResultBinding
import com.ute.cinenow.utils.showCineMessage

class VoteResultFragment : Fragment() {

    private var _binding: FragmentVoteResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoteResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupListeners()
    }

    private fun setupHeader() {
        // Nút back trên CineHeader
        binding.header.onBackClick = {
            findNavController().navigateUp()
        }
    }

    private fun setupListeners() {
        // Tiến hành giữ chỗ & Đặt vé
        binding.btnProceedBooking.setOnClickListener {
            showCineMessage("Đang chuyển sang bước giữ 3 ghế tại CGV Landmark 81...")
            findNavController().navigate(R.id.seatSelectionFragment)
        }

        // Quay lại màn hình tổng quan CineTogether
        binding.btnBackToCineTogether.setOnClickListener {
            // Quay về CineTogetherFragment trên backstack
            if (!findNavController().popBackStack(R.id.cineTogetherFragment, false)) {
                findNavController().navigate(R.id.cineTogetherFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}