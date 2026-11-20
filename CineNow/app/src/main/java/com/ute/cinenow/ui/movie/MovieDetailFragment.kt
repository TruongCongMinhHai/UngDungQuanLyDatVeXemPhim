package com.ute.cinenow.ui.movie

import android.content.res.ColorStateList
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ute.cinenow.R
import com.ute.cinenow.adapter.CastAdapter
import com.ute.cinenow.adapter.ReviewAdapter
import com.ute.cinenow.databinding.FragmentMovieDetailBinding
import com.ute.cinenow.utils.showCineMessage
import java.util.Locale

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var movie: MovieDetail
    private val castAdapter = CastAdapter()
    private val reviewAdapter = ReviewAdapter()

    // false: chỉ hiện vài bình luận đầu; true: hiện toàn bộ bình luận
    private var reviewsExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        movie = MovieDetailSamples.find(arguments?.getString(ARG_MOVIE_TITLE))
        setupLists()
        bindMovie()
        setupActions()
    }

    private fun setupLists() {
        binding.rvCast.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCast.adapter = castAdapter

        binding.rvReviews.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReviews.adapter = reviewAdapter
    }

    private fun bindMovie() = with(binding) {
        ivPoster.setImageResource(movie.posterRes)
        ivPoster.cropWithFocus(POSTER_FOCUS_Y)
        tvAge.text = movie.ageRating
        tvAge.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), ageBadgeColor(movie.ageRating))
        )
        tvBanner.text = movie.banner
        tvTitle.text = movie.title
        tvOriginalTitle.text = movie.originalTitle

        tvScore.text = String.format(Locale.US, "%.1f", movie.rating)
        tvRatingCount.text = "${movie.ratingCount.formatVi()} lượt đánh giá xác thực"
        tvRecommend.text = "${movie.recommendPercent}% đề xuất"

        tvDuration.text = "${movie.durationMin} phút"
        tvLanguage.text = movie.language
        tvFormat.text = movie.format

        tvCastCount.text = "${movie.cast.size} nhân vật chính"
        castAdapter.submitList(movie.cast)

        tvReviewCount.text = movie.reviewCountLabel
        renderReviews()

        tvPrice.text = "${movie.minPrice.formatVi()}đ"
    }

    private fun setupActions() = with(binding) {
        btnBook.setOnClickListener { findNavController().navigate(R.id.showtimeFragment) }

        // Tạm thời chỉ báo, chưa có trailer
        btnPlay.setOnClickListener {
            showCineMessage("Trailer sắp ra mắt", subtitle = movie.title)
        }

        // Bấm "Xem tất cả" để mở thêm bình luận ngay bên dưới, bấm lần nữa để thu gọn
        btnSeeAll.setOnClickListener {
            reviewsExpanded = !reviewsExpanded
            renderReviews()
        }
    }

    // Hiện bình luận theo trạng thái thu gọn / mở rộng. Phim chưa có bình luận thì ẩn cả mục
    private fun renderReviews() = with(binding) {
        val all = movie.reviews
        val hasReviews = all.isNotEmpty()
        val canExpand = all.size > PREVIEW_REVIEW_COUNT

        layoutReviewHeader.isVisible = hasReviews
        rvReviews.isVisible = hasReviews
        btnSeeAll.isVisible = canExpand
        btnSeeAll.text = if (reviewsExpanded) "Thu gọn" else "Xem tất cả"

        reviewAdapter.submitList(
            if (reviewsExpanded || !canExpand) all else all.take(PREVIEW_REVIEW_COUNT)
        )
    }

    // Giống centerCrop (phủ kín khung, không kéo giãn) nhưng cho chọn vị trí cắt theo chiều dọc.
    // focusY: 0f = giữ phần trên của ảnh, 0.5f = chính giữa (như centerCrop), 1f = giữ phần dưới.
    // ImageView phải đặt android:scaleType="matrix".
    private fun ImageView.cropWithFocus(focusY: Float) {
        doOnLayout {
            val d = drawable ?: return@doOnLayout
            val dw = d.intrinsicWidth.toFloat()
            val dh = d.intrinsicHeight.toFloat()
            if (dw <= 0f || dh <= 0f) return@doOnLayout

            val vw = (width - paddingLeft - paddingRight).toFloat()
            val vh = (height - paddingTop - paddingBottom).toFloat()
            val scale = maxOf(vw / dw, vh / dh)          // cùng một tỉ lệ cho cả hai chiều
            val dx = (vw - dw * scale) * 0.5f            // chiều ngang luôn căn giữa
            val dy = (vh - dh * scale) * focusY          // chiều dọc theo focusY

            imageMatrix = Matrix().apply {
                setScale(scale, scale)
                postTranslate(dx + paddingLeft, dy + paddingTop)
            }
        }
    }

    // 95000 -> "95.000"
    private fun Int.formatVi(): String = String.format(Locale("vi", "VN"), "%,d", this)

    // Cùng bảng màu nhãn độ tuổi với màn Khám phá
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
        // Key truyền tên phim khi điều hướng sang màn này
        const val ARG_MOVIE_TITLE = "movieTitle"

        // Số bình luận hiện sẵn khi chưa bấm "Xem tất cả"
        private const val PREVIEW_REVIEW_COUNT = 3

        // Vị trí cắt ảnh poster: số càng nhỏ càng giữ nhiều phần phía trên (mặt nhân vật)
        private const val POSTER_FOCUS_Y = 0.2f
    }
}