package com.ute.cinenow.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ute.cinenow.R
import com.ute.cinenow.adapter.HomeCineMatchAdapter
import com.ute.cinenow.adapter.HomeHotMovieAdapter
import com.ute.cinenow.adapter.HomeUpcomingAdapter
import com.ute.cinenow.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLists()
        setupClicks()
    }

    private fun setupLists() {
        binding.rvCineMatch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCineMatch.adapter = HomeCineMatchAdapter(
            items = sampleCineMatch(),
            onItemClick = { navigate(R.id.movieDetailFragment) },
            onBookClick = { navigate(R.id.showtimeFragment) }
        )

        binding.rvHotMovies.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvHotMovies.adapter = HomeHotMovieAdapter(
            items = sampleHotMovies(),
            onItemClick = { navigate(R.id.movieDetailFragment) },
            onBookClick = { navigate(R.id.showtimeFragment) }
        )

        binding.rvUpcoming.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcoming.adapter = HomeUpcomingAdapter(
            items = sampleUpcoming(),
            onItemClick = { navigate(R.id.movieDetailFragment) },
            onRemindClick = { toast("Đã bật nhắc nhở cho \"${it.title}\"") }
        )
    }

    private fun setupClicks() {
        binding.btnProfile.setOnClickListener { navigate(R.id.profileFragment) }
        binding.btnNotification.setOnClickListener { toast("Chưa có thông báo mới") }
        binding.btnLocation.setOnClickListener { toast("Chọn thành phố (sẽ làm sau)") }

        binding.cardSearch.setOnClickListener { navigate(R.id.exploreFragment) }
        binding.btnCreateRoom.setOnClickListener { navigate(R.id.createRoomFragment) }
        binding.btnExploreConnect.setOnClickListener { navigate(R.id.cineConnectFragment) }
        binding.chipMood.setOnClickListener { navigate(R.id.cineMatchPreferencesFragment) }

        binding.btnSeeAllHot.setOnClickListener { navigate(R.id.exploreFragment) }
        binding.btnSeeSchedule.setOnClickListener { navigate(R.id.exploreFragment) }
    }

    private fun navigate(destinationId: Int) = findNavController().navigate(destinationId)

    private fun toast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    // ---------- Dữ liệu giả lập ----------
    private fun sampleCineMatch() = listOf(
        CineMatchMovie(
            title = "Hành Tinh Cát 2",
            description = "Khớp thể loại: Hành động + Nhóm 3 người",
            matchPercent = 95, rating = "9.1", duration = "166 phút", format = "IMAX",
            price = "Từ 110.000đ", ageRating = "T16", highlighted = true
        ),
        CineMatchMovie(
            title = "Gia Tài Của Ngoại",
            description = "Khớp tâm trạng: Xúc động + Cuối tuần",
            matchPercent = 92, rating = "9.3", duration = "120 phút", format = "2D Phụ đề",
            price = "Từ 85.000đ", ageRating = "P", highlighted = false
        )
    )

    private fun sampleHotMovies() = listOf(
        HotMovie("Mai: Kỷ Nguyên Mới", "Tâm lý, Tình cảm", "125 phút", "8.8", "T18"),
        HotMovie("Hành Tinh Cát 2", "Khoa học viễn tưởng", "166 phút", "9.1", "T16"),
        HotMovie("Gia Tài Của Ngoại", "Gia đình, Tâm lý", "120 phút", "9.3", "P")
    )

    private fun sampleUpcoming() = listOf(
        UpcomingMovie("Deadpool & Wolverine", "Hành động, Hài hước", "26/07/2025"),
        UpcomingMovie("Những Mảnh Ghép Cảm Xúc", "Hoạt hình, Phiêu lưu", "14/08/2025")
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}