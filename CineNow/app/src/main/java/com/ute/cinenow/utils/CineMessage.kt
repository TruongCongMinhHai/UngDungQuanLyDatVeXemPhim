package com.ute.cinenow.utils

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.ute.cinenow.R

/**
 * Thông báo ngắn kèm logo CineNow ở bên trái, hiện ở phía dưới màn hình.
 * Nếu thanh điều hướng dưới đang hiện thì thông báo nằm ngay phía trên nó.
 *
 * Cách dùng trong Fragment:  showCineMessage("Nội dung thông báo")
 */
fun Activity.showCineMessage(message: String) {
    val content = findViewById<View>(android.R.id.content) ?: return
    val density = resources.displayMetrics.density

    val snackbar = Snackbar.make(content, message, Snackbar.LENGTH_SHORT)
    snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.cinenow_text_primary))
    snackbar.setTextColor(ContextCompat.getColor(this, R.color.cinenow_white))

    // Gắn logo vào bên trái dòng chữ
    val textView = snackbar.view.findViewById<TextView>(
        com.google.android.material.R.id.snackbar_text
    )
    val logoSize = (28 * density).toInt()
    val logo = ContextCompat.getDrawable(this, R.drawable.ic_cinenow_logo)?.mutate()
    logo?.setBounds(0, 0, logoSize, logoSize)
    textView.setCompoundDrawablesRelative(logo, null, null, null)
    textView.compoundDrawablePadding = (12 * density).toInt()
    textView.maxLines = 3

    // Đặt thông báo phía trên bottom nav nếu nó đang hiện
    val bottomNav = findViewById<View>(R.id.bottom_nav)
    if (bottomNav != null && bottomNav.visibility == View.VISIBLE) {
        snackbar.setAnchorView(bottomNav)
    }

    snackbar.show()
}

fun Fragment.showCineMessage(message: String) {
    requireActivity().showCineMessage(message)
}