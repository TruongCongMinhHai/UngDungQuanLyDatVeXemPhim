package com.ute.cinenow.utils

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.ViewCineHeaderBinding

/**
 * Header chung của CineNow: [nút quay lại] + logo + "CineNow" / tên màn + [nút hồ sơ].
 *
 * Kích thước cố định cho mọi màn: cao 64dp, rộng hết màn hình, lề trái phải 16dp (@dimen/screen_padding),
 * nền cinenow_background, đổ bóng 2dp. Màn nào cũng dùng đúng một kiểu nên không còn lệch giữa các người làm.
 *
 * Dùng trong XML (đặt NGOÀI vùng cuộn, ở trên cùng màn hình):
 *
 *     <com.ute.cinenow.utils.CineHeader
 *         android:id="@+id/header"
 *         android:layout_width="match_parent"
 *         android:layout_height="wrap_content"
 *         app:headerTitle="Chọn rạp và suất chiếu"
 *         app:headerShowBack="true" />
 *
 * Mặc định: nút quay lại gọi navigateUp(), nút hồ sơ mở profileFragment. Cần xử lý riêng thì gán trong Kotlin:
 *
 *     binding.header.onBackClick = { ... }
 *     binding.header.title = "Phòng của tôi"
 */
class CineHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewCineHeaderBinding.inflate(LayoutInflater.from(context), this)

    /** Tên màn hình, hiện ở dòng dưới chữ "CineNow". */
    var title: CharSequence?
        get() = binding.tvHeaderTitle.text
        set(value) {
            binding.tvHeaderTitle.text = value
        }

    /** Hiện nút quay lại bên trái. Mặc định tắt (màn tab như Trang chủ không cần). */
    var showBack: Boolean
        get() = binding.btnHeaderBack.isVisible
        set(value) {
            binding.btnHeaderBack.isVisible = value
        }

    /** Hiện nút hồ sơ bên phải. Mặc định bật; màn Cá nhân nên tắt. */
    var showProfile: Boolean
        get() = binding.btnHeaderProfile.isVisible
        set(value) {
            binding.btnHeaderProfile.isVisible = value
        }

    /** Việc làm khi bấm nút quay lại. Mặc định: về màn trước. */
    var onBackClick: () -> Unit = { findNavController().navigateUp() }

    /** Việc làm khi bấm nút hồ sơ. Mặc định: mở màn Cá nhân. */
    var onProfileClick: () -> Unit = { findNavController().navigate(R.id.profileFragment) }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setBackgroundColor(ContextCompat.getColor(context, R.color.cinenow_background))
        elevation = ELEVATION_DP * resources.displayMetrics.density

        val horizontalPadding = resources.getDimensionPixelSize(R.dimen.screen_padding)
        setPaddingRelative(horizontalPadding, 0, horizontalPadding, 0)

        context.withStyledAttributes(attrs, R.styleable.CineHeader) {
            title = getString(R.styleable.CineHeader_headerTitle)
            showBack = getBoolean(R.styleable.CineHeader_headerShowBack, false)
            showProfile = getBoolean(R.styleable.CineHeader_headerShowProfile, true)
        }

        binding.btnHeaderBack.setOnClickListener { onBackClick() }
        binding.btnHeaderProfile.setOnClickListener { onProfileClick() }
    }

    // Ép chiều cao 64dp và chiều rộng hết khung cha, dù XML của màn có ghi layout_height thế nào.
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY)
        val height = MeasureSpec.makeMeasureSpec(
            (HEIGHT_DP * resources.displayMetrics.density).toInt(), MeasureSpec.EXACTLY
        )
        super.onMeasure(width, height)
    }

    private companion object {
        const val HEIGHT_DP = 64
        const val ELEVATION_DP = 2
    }
}