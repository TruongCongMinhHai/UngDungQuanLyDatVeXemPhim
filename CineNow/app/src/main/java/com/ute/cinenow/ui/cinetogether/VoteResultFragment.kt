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

        bindWinningMovieData()
        setupHeader()
        setupListeners()
    }

    private fun bindWinningMovieData() {
        val winningMovieKey = arguments?.getString("winning_movie") ?: "dune"
        val groupName = arguments?.getString("group_name") ?: "UIT Movie Weekend"

        // Cập nhật tên nhóm trên banner
        binding.tvResultGroupBanner.text = "Nhóm $groupName đã chọn được bộ phim ưng ý nhất"

        // Cập nhật thông tin chi tiết phim
        when (winningMovieKey) {
            "mai" -> {
                binding.imgResultPoster.setImageResource(R.drawable.poster_mai)
                binding.tvResultMovieTitle.text = "Mai"
                binding.tvResultMovieSubtitle.text = "Tâm lý, Tình cảm • 131 phút"
                binding.tvResultRatingFormat.text = "8.2 ★  |  Rạp 2D Phụ đề"
                binding.tvResultVoteStats.text = "2/3 thành viên vote (67%)"
                binding.tvResultCinema.text = "CGV Landmark 81"
                binding.tvResultShowtime.text = "Tối nay - 20:15"
                binding.tvResultTotalPrice.text = "285.000 đ"
                binding.tvResultPerPerson.text = "95.000 đ / người (MoMo)"
            }
            "ngoai" -> {
                binding.imgResultPoster.setImageResource(R.drawable.poster_gia_tai_cua_ngoai)
                binding.tvResultMovieTitle.text = "Gia Tài Của Ngoại"
                binding.tvResultMovieSubtitle.text = "Gia đình, Chính kịch • 125 phút"
                binding.tvResultRatingFormat.text = "8.5 ★  |  Rạp 2D Lồng tiếng"
                binding.tvResultVoteStats.text = "Hòa phiếu • Trưởng phòng quyết định"
                binding.tvResultCinema.text = "CGV Landmark 81"
                binding.tvResultShowtime.text = "Tối nay - 18:00"
                binding.tvResultTotalPrice.text = "300.000 đ"
                binding.tvResultPerPerson.text = "100.000 đ / người (MoMo)"
            }
            else -> {
                binding.imgResultPoster.setImageResource(R.drawable.poster_hanh_tinh_cat_2)
                binding.tvResultMovieTitle.text = "Dune: Hành Tinh Cát 2"
                binding.tvResultMovieSubtitle.text = "Khoa học viễn tưởng • 166 phút"
                binding.tvResultRatingFormat.text = "8.9 ★  |  Rạp IMAX 2D"
                binding.tvResultVoteStats.text = "2/3 thành viên vote (67%)"
                binding.tvResultCinema.text = "CGV Landmark 81"
                binding.tvResultShowtime.text = "Tối nay - 19:30"
                binding.tvResultTotalPrice.text = "360.000 đ"
                binding.tvResultPerPerson.text = "120.000 đ / người (MoMo)"
            }
        }
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            findNavController().navigateUp()
        }
    }

    private fun setupListeners() {
        binding.btnProceedBooking.setOnClickListener {
            showCineMessage("Chuyển sang tìm ghế phù hợp cho nhóm...")
            // Điều hướng sang giao diện Thiết lập SmartSeat (Tìm ghế phù hợp)
            findNavController().navigate(R.id.smartSeatSetupFragment)
        }

        binding.btnBackToCineTogether.setOnClickListener {
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