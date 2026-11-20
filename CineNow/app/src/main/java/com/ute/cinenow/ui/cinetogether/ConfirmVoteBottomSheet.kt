package com.ute.cinenow.ui.cinetogether

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ute.cinenow.R
import com.ute.cinenow.databinding.BottomSheetConfirmVoteBinding
import com.ute.cinenow.utils.showCineMessage

class ConfirmVoteBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetConfirmVoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetConfirmVoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindWinningMovieData()
        setupListeners()
    }

    /**
     * Nhận mã phim chiến thắng và hiển thị chính xác bộ phim được vote nhiều nhất
     */
    private fun bindWinningMovieData() {
        val winningMovieKey = arguments?.getString("winning_movie") ?: "dune"

        when (winningMovieKey) {
            "mai" -> {
                binding.imgConfirmPoster.setImageResource(R.drawable.poster_mai)
                binding.tvConfirmMovieTitle.text = "Mai"
                binding.tvConfirmMovieSubtitle.text = "Rạp 2D Phụ đề tiếng Việt"
                binding.tvConfirmCinema.text = "CGV Landmark 81"
                binding.tvConfirmShowtime.text = "Tối nay (20:15 - Phòng chiếu 03)"
                binding.tvConfirmVoteStats.text = "✓ 2/3 phiếu bình chọn (67%)"
                binding.tvConfirmPrice.text = "95.000 đ"
            }
            "ngoai" -> {
                binding.imgConfirmPoster.setImageResource(R.drawable.poster_gia_tai_cua_ngoai)
                binding.tvConfirmMovieTitle.text = "Gia Tài Của Ngoại"
                binding.tvConfirmMovieSubtitle.text = "Rạp 2D Lồng tiếng"
                binding.tvConfirmCinema.text = "CGV Landmark 81"
                binding.tvConfirmShowtime.text = "Tối nay (18:00 - Phòng chiếu 02)"
                binding.tvConfirmVoteStats.text = "✓ Hòa phiếu • Trưởng phòng quyết định"
                binding.tvConfirmPrice.text = "100.000 đ"
            }
            else -> {
                binding.imgConfirmPoster.setImageResource(R.drawable.poster_hanh_tinh_cat_2)
                binding.tvConfirmMovieTitle.text = "Dune: Hành Tinh Cát 2"
                binding.tvConfirmMovieSubtitle.text = "Rạp IMAX 2D • Phụ đề tiếng Việt"
                binding.tvConfirmCinema.text = "CGV Landmark 81"
                binding.tvConfirmShowtime.text = "Tối nay (19:30 - Phòng chiếu 01)"
                binding.tvConfirmVoteStats.text = "✓ 2/3 phiếu bình chọn (67%)"
                binding.tvConfirmPrice.text = "120.000 đ"
            }
        }
    }

    private fun setupListeners() {
        val winningMovieKey = arguments?.getString("winning_movie") ?: "dune"

        binding.btnCancelConfirm.setOnClickListener {
            dismiss()
        }

        binding.btnConfirmAndCloseVote.setOnClickListener {
            val movieName = when (winningMovieKey) {
                "mai" -> "Mai"
                "ngoai" -> "Gia Tài Của Ngoại"
                else -> "Dune: Hành Tinh Cát 2"
            }
            showCineMessage("Đã chốt bình chọn phim: $movieName")
            dismiss()

            // Chuyển sang Màn #30 kèm mã phim chiến thắng
            val bundle = bundleOf("winning_movie" to winningMovieKey)
            findNavController().navigate(R.id.voteResultFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}