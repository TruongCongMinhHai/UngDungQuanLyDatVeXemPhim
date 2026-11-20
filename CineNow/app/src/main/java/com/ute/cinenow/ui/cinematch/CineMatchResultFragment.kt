package com.ute.cinenow.ui.cinematch

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ute.cinenow.R
import com.ute.cinenow.adapter.CineMatchResultAdapter
import com.ute.cinenow.databinding.FragmentCinematchResultBinding
import com.ute.cinenow.databinding.ItemCmReasonBinding
import com.ute.cinenow.ui.movie.MovieDetailFragment
import com.ute.cinenow.utils.CineMatchStore
import java.util.Locale
import androidx.core.view.isVisible

class CineMatchResultFragment : Fragment() {

    private var _binding: FragmentCinematchResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCinematchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View được tạo lại mỗi khi quay về màn này, nên kết quả luôn tính từ sở thích đang lưu:
        // chưa bấm Cập nhật thì ra kết quả cũ, đã Cập nhật thì ra kết quả mới.
        val prefs = readPrefs()
        val results = CineMatchEngine.recommend(prefs)

        binding.tvSummary.text = CineMatchEngine.summary(prefs)
        // Có thể loại đã chọn mà kho chưa có phim thì báo cho người dùng biết
        val notice = CineMatchEngine.genreNotice(prefs)
        binding.tvGenreNotice.isVisible = notice != null
        binding.tvGenreNotice.text = notice
        binding.btnEdit.setOnClickListener { goToPreferences() }

        bindTop(results.first())
        bindOthers(results.drop(1).take(OTHER_COUNT))
    }

    // Chỉ đọc từ CineMatchStore (không đọc arguments nữa, vì arguments cũ sẽ đè sở thích mới)
    private fun readPrefs(): CineMatchPrefs =
        CineMatchStore.toPrefsOrNull() ?: CineMatchEngine.DEFAULT_PREFS

    private fun bindTop(result: MatchResult) {
        val movie = result.movie
        val ctx = requireContext()

        binding.ivTopPoster.setImageResource(movie.posterRes)
        binding.tvTopAge.text = movie.ageRating
        binding.tvTopAge.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(ctx, ageBadgeColor(movie.ageRating))
        )
        binding.tvTopDuration.text = "${movie.durationMin} phút"
        binding.tvTopRating.text = String.format(Locale.US, "%.1f/10", movie.rating)
        binding.tvTopPercent.text = "${result.percent}%"
        binding.tvTopTitle.text = movie.title
        binding.tvTopSubtitle.text =
            listOfNotNull(movie.originalTitle, movie.genreLabel).joinToString(" • ")

        // Lý do gợi ý được tạo theo từng phim và từng lựa chọn
        binding.layoutReasons.removeAllViews()
        result.reasons.forEach { reason ->
            val row = ItemCmReasonBinding.inflate(layoutInflater, binding.layoutReasons, false)
            row.tvReasonTitle.text = reason.title
            row.tvReasonDetail.text = reason.detail
            binding.layoutReasons.addView(row.root)
        }

        binding.btnTopDetail.setOnClickListener { openDetail(movie) }
        binding.btnTopBook.setOnClickListener { findNavController().navigate(R.id.showtimeFragment) }
        binding.btnTopVote.setOnClickListener { findNavController().navigate(R.id.createRoomFragment) }
    }

    private fun bindOthers(others: List<MatchResult>) {
        binding.rvOthers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOthers.adapter = CineMatchResultAdapter(
            items = others,
            onDetailClick = { openDetail(it.movie) },
            onBookClick = { findNavController().navigate(R.id.showtimeFragment) }
        )
    }

    private fun openDetail(movie: MatchMovie) {
        findNavController().navigate(
            R.id.movieDetailFragment,
            bundleOf(MovieDetailFragment.ARG_MOVIE_TITLE to movie.detailTitle)
        )
    }

    // Quy tắc 2: "Sửa" mở màn Chọn sở thích CHỒNG LÊN màn Kết quả (không pop màn Kết quả),
    // để nút Back ở màn Chọn sở thích quay về đúng màn Kết quả cũ (quy tắc 3).
    private fun goToPreferences() {
        findNavController().navigate(R.id.cineMatchPreferencesFragment)
    }

    // Màu nhãn độ tuổi: P xanh lá, T13 xanh dương, T16 cam, T18 đỏ
    private fun ageBadgeColor(age: String): Int = when (age) {
        "P" -> R.color.cinenow_success
        "T13" -> R.color.cinenow_info
        "T16" -> R.color.cinenow_warning
        else -> R.color.cinenow_error
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val OTHER_COUNT = 3
    }
}