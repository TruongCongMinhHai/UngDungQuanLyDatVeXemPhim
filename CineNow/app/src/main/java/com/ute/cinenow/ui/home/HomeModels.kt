package com.ute.cinenow.ui.home

import com.ute.cinenow.R

// Dữ liệu giả lập cho màn Trang chủ (chưa nối dữ liệu thật).
// posterRes: để ảnh poster thật vào res/drawable rồi đổi thành R.drawable.ten_anh

data class CineMatchMovie(
    val title: String,
    val description: String,
    val matchPercent: Int,
    val rating: String,
    val duration: String,
    val format: String,
    val price: String,
    val ageRating: String,
    val highlighted: Boolean,
    val posterRes: Int = R.drawable.bg_poster_placeholder
)

data class HotMovie(
    val title: String,
    val genre: String,
    val duration: String,
    val rating: String,
    val ageRating: String,
    val posterRes: Int = R.drawable.bg_poster_placeholder
)

data class UpcomingMovie(
    val title: String,
    val genre: String,
    val releaseDate: String,
    val posterRes: Int = R.drawable.bg_poster_placeholder
)