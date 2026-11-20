package com.ute.cinenow.ui.cinematch

import androidx.annotation.DrawableRes
import com.ute.cinenow.R

// Một lựa chọn trên màn Chọn sở thích (thể loại, tâm trạng hoặc quy mô cùng đi)
data class PrefOption(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    @DrawableRes val iconRes: Int
)

// Danh sách lựa chọn cố định cho màn CineMatch
object CineMatchOptions {

    val genres = listOf(
        PrefOption("scifi", "Khoa học viễn tưởng", iconRes = R.drawable.ic_cm_sparkle),
        PrefOption("action", "Hành động kịch tính", iconRes = R.drawable.ic_home_bolt),
        PrefOption("comedy", "Hài hước vui nhộn", iconRes = R.drawable.ic_cm_mood),
        PrefOption("horror", "Kinh dị rùng rợn", iconRes = R.drawable.ic_cm_moon),
        PrefOption("family_drama", "Tâm lý gia đình", iconRes = R.drawable.ic_home_people),
        PrefOption("animation", "Hoạt hình phiêu lưu", iconRes = R.drawable.ic_cm_explore)
    )

    val moods = listOf(
        PrefOption("excited", "Hào hứng, bùng nổ", "Hành động, kỹ xảo", R.drawable.ic_home_bolt),
        PrefOption("relaxed", "Nhẹ nhàng, thư giãn", "Hài hước, tình cảm", R.drawable.ic_cm_eco),
        PrefOption("thrilled", "Bất ngờ, lôi cuốn", "Hồi hộp, plot twist", R.drawable.ic_cm_bulb),
        PrefOption("moved", "Cảm động, sâu lắng", "Gia đình, tâm lý", R.drawable.ic_cm_heart)
    )

    val groups = listOf(
        PrefOption("friends", "Nhóm bạn", "Từ 3 người trở lên", R.drawable.ic_home_people),
        PrefOption("couple", "Cặp đôi", "2 người lãng mạn", R.drawable.ic_cm_heart),
        PrefOption("solo", "Đi một mình", "Solo chill trọn vẹn", R.drawable.ic_cm_person),
        PrefOption("family", "Gia đình", "Mọi lứa tuổi (P)", R.drawable.ic_cm_home)
    )
}