package com.ute.cinenow.ui.movie

import com.ute.cinenow.R

enum class MovieStatus { NOW, SOON }

// Dữ liệu giả lập cho màn Khám phá (chưa nối dữ liệu thật)
data class ExploreMovie(
    val title: String,
    val genres: List<String>,
    val durationMin: Int,
    val rating: Double,
    val ageRating: String,
    val formats: List<String>,
    val status: MovieStatus,
    val posterRes: Int = R.drawable.bg_poster_placeholder
)