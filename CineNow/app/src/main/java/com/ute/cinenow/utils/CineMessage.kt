package com.ute.cinenow.utils

import android.app.Activity
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.ute.cinenow.R

private const val MESSAGE_DURATION_MS = 3500

/**
 * Thông báo kiểu CineNow: nền #28243A bo góc 12dp, logo bên trái, tự tắt sau ~3,5 giây.
 * Nằm ngay phía trên Bottom Navigation nếu thanh này đang hiện.
 *
 * Dùng đơn giản:      showCineMessage("Tạo tài khoản thành công")
 * Dùng có dòng phụ:   showCineMessage("Đã bật nhắc nhở", subtitle = "Tên phim",
 *                                     actionLabel = "HOÀN TÁC", onAction = { ... })
 */
fun Activity.showCineMessage(
    message: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    val content = findViewById<View>(android.R.id.content) ?: return
    val density = resources.displayMetrics.density

    // Dòng chính (trắng, đậm nếu có dòng phụ) + dòng phụ (#D6D1E7, nhỏ hơn)
    val text = SpannableStringBuilder(message)
    if (!subtitle.isNullOrBlank()) {
        text.setSpan(StyleSpan(Typeface.BOLD), 0, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(AbsoluteSizeSpan(15, true), 0, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val subtitleStart = text.length + 1
        text.append("\n").append(subtitle)
        text.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.cinenow_snackbar_subtitle)),
            subtitleStart, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text.setSpan(AbsoluteSizeSpan(13, true), subtitleStart, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    val snackbar = Snackbar.make(content, text, MESSAGE_DURATION_MS)
    snackbar.view.background = ContextCompat.getDrawable(this, R.drawable.bg_cine_snackbar)
    snackbar.setTextColor(ContextCompat.getColor(this, R.color.cinenow_white))

    // Logo CineNow bên trái dòng chữ
    val textView = snackbar.view.findViewById<TextView>(
        com.google.android.material.R.id.snackbar_text
    )
    val logoSize = (28 * density).toInt()
    val logo = ContextCompat.getDrawable(this, R.drawable.ic_cinenow_logo)?.mutate()
    logo?.setBounds(0, 0, logoSize, logoSize)
    textView.setCompoundDrawablesRelative(logo, null, null, null)
    textView.compoundDrawablePadding = (12 * density).toInt()
    textView.maxLines = 3

    // Nút hành động (ví dụ HOÀN TÁC), màu #B9AEFF
    if (actionLabel != null && onAction != null) {
        snackbar.setAction(actionLabel) { onAction() }
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.cinenow_snackbar_action))
    }

    // Neo phía trên Bottom Navigation để không che hoặc chồng lên nó
    val bottomNav = findViewById<View>(R.id.bottom_nav)
    if (bottomNav != null && bottomNav.visibility == View.VISIBLE) {
        snackbar.setAnchorView(bottomNav)
    }

    snackbar.show()
}

fun Fragment.showCineMessage(
    message: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    requireActivity().showCineMessage(message, subtitle, actionLabel, onAction)
}