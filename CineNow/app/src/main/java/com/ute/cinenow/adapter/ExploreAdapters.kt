package com.ute.cinenow.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ute.cinenow.R
import com.ute.cinenow.databinding.ItemExploreMovieBinding
import com.ute.cinenow.ui.movie.ExploreMovie
import com.ute.cinenow.ui.movie.MovieStatus
import com.ute.cinenow.utils.ReminderStore
import com.ute.cinenow.utils.applyReminderState
import java.util.Locale

class ExploreMovieAdapter(
    private val onDetailClick: (ExploreMovie) -> Unit,
    private val onBookClick: (ExploreMovie) -> Unit,
    private val onReminderChanged: (movie: ExploreMovie, isOn: Boolean) -> Unit
) : ListAdapter<ExploreMovie, ExploreMovieAdapter.ViewHolder>(DIFF) {

    class ViewHolder(val binding: ItemExploreMovieBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemExploreMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val b = holder.binding
        val ctx = b.root.context

        b.ivPoster.setImageResource(item.posterRes)
        b.tvAge.text = item.ageRating
        b.tvAge.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(ctx, ageBadgeColor(item.ageRating))
        )
        b.tvRating.text = String.format(Locale.US, "%.1f", item.rating)
        b.tvDuration.text = "${item.durationMin} phút"
        b.tvTitle.text = item.title
        b.tvGenre.text = item.genres.joinToString(", ")

        // Phim sắp chiếu: nút "Nhắc tôi" / "Đã nhắc"; phim đang chiếu: nút "Đặt vé"
        val isSoon = item.status == MovieStatus.SOON
        if (isSoon) {
            b.btnBook.applyReminderState(ReminderStore.isReminded(item.title))
        } else {
            applyBookStyle(b.btnBook)
        }

        b.root.setOnClickListener { onDetailClick(item) }
        b.btnDetail.setOnClickListener { onDetailClick(item) }
        b.btnBook.setOnClickListener {
            if (isSoon) {
                val turnOn = !ReminderStore.isReminded(item.title)
                ReminderStore.set(item.title, turnOn)
                b.btnBook.applyReminderState(turnOn)
                onReminderChanged(item, turnOn)
            } else {
                onBookClick(item)
            }
        }
    }

    // Dùng khi bấm HOÀN TÁC: vẽ lại nút của đúng phim đó
    fun refresh(title: String) {
        val index = currentList.indexOfFirst { it.title == title }
        if (index >= 0) notifyItemChanged(index)
    }

    // Trả nút về kiểu "Đặt vé" (cần vì ViewHolder được tái sử dụng)
    private fun applyBookStyle(button: MaterialButton) {
        val ctx = button.context
        button.text = "Đặt vé"
        button.setIconResource(R.drawable.ic_ticket)
        button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.cinenow_primary))
        val white = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.cinenow_on_primary))
        button.setTextColor(white)
        button.iconTint = white
    }

    // Màu nhãn độ tuổi: P xanh lá, T13 xanh dương, T16 cam, T18 đỏ
    private fun ageBadgeColor(age: String): Int = when (age) {
        "P" -> R.color.cinenow_success
        "T13" -> R.color.cinenow_info
        "T16" -> R.color.cinenow_warning
        else -> R.color.cinenow_error
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ExploreMovie>() {
            override fun areItemsTheSame(a: ExploreMovie, b: ExploreMovie) = a.title == b.title
            override fun areContentsTheSame(a: ExploreMovie, b: ExploreMovie) = a == b
        }
    }
}