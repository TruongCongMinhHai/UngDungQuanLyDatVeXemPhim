package com.ute.cinenow.utils

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.ute.cinenow.R

/**
 * Lưu trạng thái "Nhắc tôi" của từng phim (theo tên phim), dùng chung cho Trang chủ và Khám phá.
 * Tạm lưu trong bộ nhớ (mất khi tắt hẳn app). Khi có dữ liệu thật sẽ thay bằng Room/Firebase.
 */
object ReminderStore {
    private val reminded = mutableSetOf<String>()

    fun isReminded(title: String): Boolean = title in reminded

    fun set(title: String, isOn: Boolean) {
        if (isOn) reminded.add(title) else reminded.remove(title)
    }
}

/**
 * Đổi giao diện nút theo trạng thái nhắc nhở.
 *  - Chưa bật: "Nhắc tôi", icon chuông, nền tím nhạt, chữ và icon tím.
 *  - Đã bật:   "Đã nhắc", icon chuông có dấu tích, nền tím đậm, chữ và icon trắng.
 */
fun MaterialButton.applyReminderState(isOn: Boolean) {
    text = if (isOn) "Đã nhắc" else "Nhắc tôi"
    setIconResource(if (isOn) R.drawable.ic_home_bell_check else R.drawable.ic_home_notifications)

    val background = if (isOn) R.color.cinenow_accent else R.color.cinenow_accent_soft
    val foreground = if (isOn) R.color.cinenow_white else R.color.cinenow_accent

    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, background))
    val fg = ColorStateList.valueOf(ContextCompat.getColor(context, foreground))
    setTextColor(fg)
    iconTint = fg
}