package com.ute.cinenow.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ute.cinenow.R
import com.ute.cinenow.databinding.ItemCinematchResultBinding
import com.ute.cinenow.ui.cinematch.MatchResult
import java.util.Locale

// Danh sách "Có thể bạn cũng thích" ở màn kết quả CineMatch
class CineMatchResultAdapter(
    private val items: List<MatchResult>,
    private val onDetailClick: (MatchResult) -> Unit,
    private val onBookClick: (MatchResult) -> Unit
) : RecyclerView.Adapter<CineMatchResultAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCinematchResultBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemCinematchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val movie = item.movie
        val b = holder.binding
        val ctx = b.root.context

        b.ivPoster.setImageResource(movie.posterRes)
        b.tvAge.text = movie.ageRating
        b.tvAge.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(ctx, ageBadgeColor(movie.ageRating))
        )
        b.tvTitle.text = movie.title
        b.tvRating.text = String.format(Locale.US, "%.1f", movie.rating)
        b.tvMeta.text = "${movie.genreLabel} • ${movie.durationMin} phút"
        b.tvPercent.text = "${item.percent}% phù hợp"
        b.tvTag.text = movie.tag

        b.root.setOnClickListener { onDetailClick(item) }
        b.btnDetail.setOnClickListener { onDetailClick(item) }
        b.btnBook.setOnClickListener { onBookClick(item) }
    }

    // Màu nhãn độ tuổi: P xanh lá, T13 xanh dương, T16 cam, T18 đỏ
    private fun ageBadgeColor(age: String): Int = when (age) {
        "P" -> R.color.cinenow_success
        "T13" -> R.color.cinenow_info
        "T16" -> R.color.cinenow_warning
        else -> R.color.cinenow_error
    }
}