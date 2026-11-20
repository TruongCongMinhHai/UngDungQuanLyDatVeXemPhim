package com.ute.cinenow.utils

import com.ute.cinenow.ui.cinematch.CineMatchPrefs

/**
 * Nhớ lựa chọn ở màn "Chọn sở thích" (theo id) để:
 *  - Bấm "Sửa" ở màn kết quả quay lại vẫn thấy các thẻ đã chọn.
 *  - Màn kết quả có dữ liệu dự phòng nếu không nhận được gì qua arguments.
 * Tạm lưu trong bộ nhớ (mất khi tắt hẳn app). Khi có dữ liệu thật sẽ thay bằng Room/Firebase.
 */
object CineMatchStore {

    var hasSaved: Boolean = false
        private set

    var genreIds: Set<String> = emptySet()
        private set

    var moodId: String? = null
        private set

    var groupId: String? = null
        private set

    fun save(genres: Set<String>, mood: String?, group: String?) {
        genreIds = genres.toSet()
        moodId = mood
        groupId = group
        hasSaved = true
    }

    // Trả về null nếu chưa chọn đủ (thể loại, tâm trạng, quy mô)
    fun toPrefsOrNull(): CineMatchPrefs? {
        val mood = moodId
        val group = groupId
        return if (genreIds.isNotEmpty() && mood != null && group != null) {
            CineMatchPrefs(genreIds, mood, group)
        } else {
            null
        }
    }
}