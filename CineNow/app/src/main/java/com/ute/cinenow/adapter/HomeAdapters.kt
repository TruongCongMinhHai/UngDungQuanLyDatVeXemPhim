package com.ute.cinenow.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ute.cinenow.R
import com.ute.cinenow.databinding.ItemHomeCinematchBinding
import com.ute.cinenow.databinding.ItemHomeHotMovieBinding
import com.ute.cinenow.databinding.ItemHomeUpcomingBinding
import com.ute.cinenow.ui.home.CineMatchMovie
import com.ute.cinenow.ui.home.HotMovie
import com.ute.cinenow.ui.home.UpcomingMovie

private fun Context.tint(@ColorRes id: Int): ColorStateList =
    ColorStateList.valueOf(ContextCompat.getColor(this, id))

// Màu nhãn độ tuổi: P xanh lá, T13 xanh dương, T16 cam, T18 đỏ
private fun ageBadgeColor(age: String): Int = when (age) {
    "P" -> R.color.cinenow_success
    "T13" -> R.color.cinenow_info
    "T16" -> R.color.cinenow_warning
    else -> R.color.cinenow_error
}

// ---------------- CineMatch Hôm Nay ----------------
class HomeCineMatchAdapter(
    private val items: List<CineMatchMovie>,
    private val onItemClick: (CineMatchMovie) -> Unit,
    private val onBookClick: (CineMatchMovie) -> Unit
) : RecyclerView.Adapter<HomeCineMatchAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHomeCinematchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemHomeCinematchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val b = holder.binding
        val ctx = b.root.context

        b.ivPoster.setImageResource(item.posterRes)
        b.tvAge.text = item.ageRating
        b.tvAge.backgroundTintList = ctx.tint(ageBadgeColor(item.ageRating))
        b.tvTitle.text = item.title
        b.tvDesc.text = item.description
        b.tvRating.text = item.rating
        b.tvDuration.text = item.duration
        b.tvFormat.text = item.format
        b.tvPrice.text = item.price
        b.tvMatch.text = "Điểm khớp: ${item.matchPercent}%"

        // Nút "Chọn suất": tất cả các thẻ đều dùng kiểu tím đặc
        b.btnBook.backgroundTintList = ctx.tint(R.color.cinenow_primary)
        b.btnBook.setTextColor(ContextCompat.getColor(ctx, R.color.cinenow_on_primary))
        b.btnBook.iconTint = ctx.tint(R.color.cinenow_on_primary)

        // Chip điểm khớp: thẻ đầu nổi bật (xanh lá), các thẻ sau dịu hơn (tím nhạt)
        if (item.highlighted) {
            b.chipMatch.backgroundTintList = ctx.tint(R.color.cinenow_success_container)
            b.tvMatch.setTextColor(ContextCompat.getColor(ctx, R.color.cinenow_on_success_container))
            b.ivMatch.imageTintList = ctx.tint(R.color.cinenow_on_success_container)
        } else {
            b.chipMatch.backgroundTintList = ctx.tint(R.color.cinenow_secondary_container)
            b.tvMatch.setTextColor(ContextCompat.getColor(ctx, R.color.cinenow_primary_variant))
            b.ivMatch.imageTintList = ctx.tint(R.color.cinenow_primary_variant)
        }

        b.root.setOnClickListener { onItemClick(item) }
        b.btnBook.setOnClickListener { onBookClick(item) }
    }
}

// ---------------- Phim Đang Chiếu Hot ----------------
class HomeHotMovieAdapter(
    private val items: List<HotMovie>,
    private val onItemClick: (HotMovie) -> Unit,
    private val onBookClick: (HotMovie) -> Unit
) : RecyclerView.Adapter<HomeHotMovieAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHomeHotMovieBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemHomeHotMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val b = holder.binding
        val ctx = b.root.context

        b.ivPoster.setImageResource(item.posterRes)
        b.tvAge.text = item.ageRating
        b.tvAge.backgroundTintList = ctx.tint(ageBadgeColor(item.ageRating))
        b.tvRating.text = item.rating
        b.tvTitle.text = item.title
        b.tvGenre.text = item.genre
        b.tvDuration.text = item.duration

        b.root.setOnClickListener { onItemClick(item) }
        b.btnBook.setOnClickListener { onBookClick(item) }
    }
}

// ---------------- Phim Sắp Chiếu ----------------
class HomeUpcomingAdapter(
    private val items: List<UpcomingMovie>,
    private val onItemClick: (UpcomingMovie) -> Unit,
    private val onRemindClick: (UpcomingMovie) -> Unit
) : RecyclerView.Adapter<HomeUpcomingAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHomeUpcomingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemHomeUpcomingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val b = holder.binding

        b.ivPoster.setImageResource(item.posterRes)
        b.tvTitle.text = item.title
        b.tvGenre.text = item.genre
        b.tvDate.text = "Khởi chiếu: ${item.releaseDate}"

        b.root.setOnClickListener { onItemClick(item) }
        b.btnRemind.setOnClickListener { onRemindClick(item) }
    }
}