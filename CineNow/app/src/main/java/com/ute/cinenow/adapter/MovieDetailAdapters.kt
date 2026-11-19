package com.ute.cinenow.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ute.cinenow.databinding.ItemMovieCastBinding
import com.ute.cinenow.databinding.ItemMovieReviewBinding
import com.ute.cinenow.ui.movie.CastMember
import com.ute.cinenow.ui.movie.MovieReview
import java.text.Normalizer
import java.util.Locale

// Lấy chữ cái đầu của họ và tên: "Minh Nhật" -> "MN"
private fun String.initials(): String =
    trim().split(" ").filter { it.isNotEmpty() }
        .let { parts ->
            when {
                parts.isEmpty() -> ""
                parts.size == 1 -> parts[0].take(1)
                else -> "${parts.first().first()}${parts.last().first()}"
            }
        }.uppercase()

// "Timothée Chalamet" -> "cast_timothee_chalamet" (tên file ảnh trong res/drawable)
private fun String.castImageName(): String {
    val plain = Normalizer.normalize(trim().lowercase(Locale.ROOT), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .replace('đ', 'd')
    return "cast_" + plain.replace(Regex("[^a-z0-9]+"), "_").trim('_')
}

class CastAdapter : ListAdapter<CastMember, CastAdapter.ViewHolder>(DIFF) {

    class ViewHolder(val binding: ItemMovieCastBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemMovieCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val b = holder.binding
        b.tvName.text = item.name
        b.tvRole.text = item.role

        // Ưu tiên avatarRes; không có thì tìm ảnh drawable tên cast_<ten_khong_dau>.
        // Chưa có file ảnh (id = 0) hoặc ảnh lỗi thì hiện chữ viết tắt.
        val ctx = b.root.context
        val resId = if (item.avatarRes != 0) item.avatarRes
        else ctx.resources.getIdentifier(item.name.castImageName(), "drawable", ctx.packageName)

        // ViewHolder được tái sử dụng nên phải xóa ảnh cũ trước
        b.ivAvatar.setImageDrawable(null)
        if (resId != 0) b.ivAvatar.setImageResource(resId)

        b.tvInitials.text = item.name.initials()
        b.tvInitials.isVisible = b.ivAvatar.drawable == null
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CastMember>() {
            override fun areItemsTheSame(a: CastMember, b: CastMember) = a.name == b.name
            override fun areContentsTheSame(a: CastMember, b: CastMember) = a == b
        }
    }
}

class ReviewAdapter : ListAdapter<MovieReview, ReviewAdapter.ViewHolder>(DIFF) {

    class ViewHolder(val binding: ItemMovieReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemMovieReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val b = holder.binding
        b.tvInitials.text = item.userName.initials()
        b.tvName.text = item.userName
        b.tvMeta.text = "${item.timeAgo} • ${item.cinema}"
        // 10.0 -> "10/10", 9.5 -> "9.5/10"
        val score = if (item.score % 1.0 == 0.0) item.score.toInt().toString()
        else String.format(Locale.US, "%.1f", item.score)
        b.tvScore.text = "$score/10"
        b.tvContent.text = item.content
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<MovieReview>() {
            override fun areItemsTheSame(a: MovieReview, b: MovieReview) = a.id == b.id
            override fun areContentsTheSame(a: MovieReview, b: MovieReview) = a == b
        }
    }
}