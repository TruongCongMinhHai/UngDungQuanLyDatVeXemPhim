package com.ute.cinenow.ui.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.ChipGroup
import com.ute.cinenow.R
import com.ute.cinenow.adapter.ExploreMovieAdapter
import com.ute.cinenow.databinding.FragmentExploreBinding
import com.ute.cinenow.utils.ReminderStore
import com.ute.cinenow.utils.showCineMessage

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private val allMovies = sampleMovies()
    private lateinit var adapter: ExploreMovieAdapter

    // Trạng thái bộ lọc hiện tại
    private var statusFilter = "ALL"      // ALL | NOW | SOON | IMAX
    private var genreFilter = ""          // "" = tất cả
    private var minRating = 0.0           // 0 = tất cả
    private var formatFilter = ""         // "" = tất cả
    private var sortMode = SortMode.DEFAULT

    private enum class SortMode { DEFAULT, RATING, NAME, DURATION }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupFilters()
        setupActions()
        applyFilters()
    }

    private fun setupList() {
        adapter = ExploreMovieAdapter(
            onDetailClick = { findNavController().navigate(R.id.movieDetailFragment) },
            onBookClick = { findNavController().navigate(R.id.showtimeFragment) },
            onReminderChanged = { movie, isOn ->
                showCineMessage(
                    message = if (isOn) "Đã bật nhắc nhở" else "Đã tắt nhắc nhở",
                    subtitle = movie.title,
                    actionLabel = "HOÀN TÁC",
                    onAction = {
                        // Khôi phục đúng trạng thái trước đó của phim này
                        ReminderStore.set(movie.title, !isOn)
                        adapter.refresh(movie.title)
                    }
                )
            }
        )
        binding.rvMovies.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMovies.adapter = adapter
    }

    private fun setupFilters() {
        binding.chipGroupStatus.setOnCheckedStateChangeListener { group, _ ->
            statusFilter = group.selectedTag() ?: "ALL"
            applyFilters()
        }
        binding.chipGroupGenre.setOnCheckedStateChangeListener { group, _ ->
            genreFilter = group.selectedTag().orEmpty()
            applyFilters()
        }
        binding.chipGroupRating.setOnCheckedStateChangeListener { group, _ ->
            minRating = group.selectedTag()?.toDoubleOrNull() ?: 0.0
            applyFilters()
        }
        binding.chipGroupFormat.setOnCheckedStateChangeListener { group, _ ->
            formatFilter = group.selectedTag().orEmpty()
            applyFilters()
        }

        binding.etSearch.doOnTextChanged { _, _, _, _ -> applyFilters() }
        binding.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                v.clearFocus()
                true
            } else {
                false
            }
        }
    }

    private fun setupActions() {
        binding.btnProfile.setOnClickListener { findNavController().navigate(R.id.profileFragment) }

        // Nút trong ô tìm kiếm: ẩn/hiện cả khung Bộ lọc
        binding.btnFilterToggle.setOnClickListener {
            binding.cardFilter.visibility =
                if (binding.cardFilter.isVisible) View.GONE else View.VISIBLE
        }

        // "Thu gọn": chỉ thu phần chip, giữ lại tiêu đề khung
        binding.btnCollapse.setOnClickListener {
            val expanded = binding.layoutFilterBody.isVisible
            binding.layoutFilterBody.visibility = if (expanded) View.GONE else View.VISIBLE
            binding.tvCollapse.text = if (expanded) "Mở rộng" else "Thu gọn"
        }

        binding.btnSort.setOnClickListener { showSortMenu() }

        // Đổi giữa lưới 2 cột và 1 cột
        binding.btnLayout.setOnClickListener {
            val manager = binding.rvMovies.layoutManager as GridLayoutManager
            manager.spanCount = if (manager.spanCount == 2) 1 else 2
        }
    }

    private fun showSortMenu() {
        val menu = PopupMenu(requireContext(), binding.btnSort)
        menu.menu.add(0, 0, 0, "Mặc định")
        menu.menu.add(0, 1, 1, "Điểm cao nhất")
        menu.menu.add(0, 2, 2, "Tên A - Z")
        menu.menu.add(0, 3, 3, "Thời lượng dài nhất")
        menu.setOnMenuItemClickListener {
            sortMode = SortMode.values()[it.itemId]
            applyFilters()
            true
        }
        menu.show()
    }

    // Lọc dữ liệu mẫu theo mọi điều kiện rồi đưa vào danh sách
    private fun applyFilters() {
        val query = binding.etSearch.text.toString().trim().lowercase()

        var result = allMovies.filter { movie ->
            val statusOk = when (statusFilter) {
                "NOW" -> movie.status == MovieStatus.NOW
                "SOON" -> movie.status == MovieStatus.SOON
                "IMAX" -> "IMAX" in movie.formats
                else -> true
            }
            val genreOk = genreFilter.isEmpty() || genreFilter in movie.genres
            val ratingOk = movie.rating >= minRating
            val formatOk = formatFilter.isEmpty() || formatFilter in movie.formats
            val queryOk = query.isEmpty() ||
                    movie.title.lowercase().contains(query) ||
                    movie.genres.any { it.lowercase().contains(query) }

            statusOk && genreOk && ratingOk && formatOk && queryOk
        }

        result = when (sortMode) {
            SortMode.DEFAULT -> result
            SortMode.RATING -> result.sortedByDescending { it.rating }
            SortMode.NAME -> result.sortedBy { it.title }
            SortMode.DURATION -> result.sortedByDescending { it.durationMin }
        }

        adapter.submitList(result)

        binding.tvResultTitle.text = when (statusFilter) {
            "NOW" -> "Phim đang chiếu"
            "SOON" -> "Phim sắp chiếu"
            "IMAX" -> "Phim IMAX"
            else -> "Tất cả phim"
        }
        binding.tvResultCount.text = "(${result.size} phim)"
        binding.tvFooter.text =
            if (result.isEmpty()) "Không tìm thấy phim phù hợp"
            else "Đã hiển thị tất cả suất chiếu hôm nay"
    }

    // Lấy nội dung android:tag của chip đang chọn trong nhóm
    private fun ChipGroup.selectedTag(): String? {
        val id = checkedChipId
        return if (id == View.NO_ID) null else findViewById<View>(id)?.tag as? String
    }

    // ---------- Dữ liệu giả lập ----------
    // Ảnh poster: bỏ file vào res/drawable rồi gọi R.drawable.<tên file không có đuôi>
    private fun sampleMovies() = listOf(
        ExploreMovie(
            "Dune: Hành Tinh Cát", listOf("Khoa học viễn tưởng", "Phiêu lưu"),
            166, 8.8, "T16", listOf("2D", "3D", "IMAX"), MovieStatus.NOW,
            R.drawable.poster_hanh_tinh_cat_2
        ),
        ExploreMovie(
            "Oppenheimer", listOf("Tiểu sử", "Lịch sử", "Kịch tính"),
            180, 9.0, "T18", listOf("2D", "IMAX"), MovieStatus.NOW,
            R.drawable.poster_oppenheimer
        ),
        ExploreMovie(
            "Mai", listOf("Tâm lý", "Tình cảm"),
            131, 8.2, "T18", listOf("2D"), MovieStatus.NOW,
            R.drawable.poster_mai
        ),
        ExploreMovie(
            "Kẻ Trộm Giấc Mơ", listOf("Hành động", "Giật gân"),
            148, 8.9, "T16", listOf("2D", "IMAX"), MovieStatus.NOW,
            R.drawable.poster_ke_trom_giac_mo
        ),
        ExploreMovie(
            "Vùng Đất Linh Hồn", listOf("Hoạt hình", "Kỳ ảo"),
            125, 8.7, "P", listOf("2D", "3D"), MovieStatus.NOW,
            R.drawable.poster_vung_dat_linh_hon
        ),
        ExploreMovie(
            "Thám Hiểm Vũ Trụ", listOf("Khoa học viễn tưởng", "Hành động"),
            120, 8.4, "T13", listOf("2D", "3D", "IMAX"), MovieStatus.NOW,
            R.drawable.poster_tham_hiem_vu_tru
        ),
        // 2 phim sắp chiếu (cùng với Trang chủ), xóa 2 dòng này nếu muốn danh sách chỉ có 6 phim
        ExploreMovie(
            "Deadpool & Wolverine", listOf("Hành động", "Hài"),
            128, 8.0, "T18", listOf("2D", "IMAX"), MovieStatus.SOON,
            R.drawable.poster_deadpool_wolverine
        ),
        ExploreMovie(
            "Những Mảnh Ghép Cảm Xúc", listOf("Hoạt hình", "Hài"),
            96, 8.3, "P", listOf("2D", "3D"), MovieStatus.SOON,
            R.drawable.poster_nhung_manh_ghep_cam_xuc
        )
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}