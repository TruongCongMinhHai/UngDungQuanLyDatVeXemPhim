package com.ute.cinenow.ui.cinematch

import androidx.annotation.DrawableRes
import com.ute.cinenow.R
import kotlin.math.min
import kotlin.math.roundToInt

// Lựa chọn của người dùng ở màn Chọn sở thích (tất cả theo id trong CineMatchOptions)
data class CineMatchPrefs(
    val genreIds: Set<String>,
    val moodId: String,
    val groupId: String
)

// Một phim trong "kho" gợi ý, kèm đặc điểm để chấm điểm
data class MatchMovie(
    val title: String,
    val originalTitle: String?,
    val detailTitle: String,          // tên dùng để mở màn Chi tiết phim (khớp bảng tra byTitle)
    val genreIds: Set<String>,        // thuộc các id thể loại trong CineMatchOptions.genres
    val genreLabel: String,           // thể loại hiển thị
    val moodIds: Set<String>,         // hợp với các tâm trạng nào
    val groupIds: Set<String>,        // hợp với các quy mô cùng đi nào
    val rating: Double,
    val durationMin: Int,
    val ageRating: String,
    @DrawableRes val posterRes: Int,
    val tag: String,                  // dòng ngắn trong danh sách "Có thể bạn cũng thích"
    val genreNote: String,
    val moodNote: String,
    val groupNote: String,
    val ratingNote: String
)

data class MatchReason(val title: String, val detail: String)

data class MatchResult(
    val movie: MatchMovie,
    val percent: Int,
    val reasons: List<MatchReason>
)

/**
 * Thuật toán gợi ý (dữ liệu giả lập, chạy trong máy):
 *   điểm = 50% độ khớp thể loại + 30% khớp tâm trạng + 20% khớp quy mô cùng đi
 *   phần trăm hiển thị = 60 + điểm * 35  (khớp hoàn toàn = 95%)
 * Cùng phần trăm thì phim điểm đánh giá cao hơn đứng trước.
 */
object CineMatchEngine {

    // Lựa chọn mặc định (giống thiết kế), dùng khi chưa có dữ liệu nào
    val DEFAULT_PREFS = CineMatchPrefs(
        genreIds = setOf("scifi", "action"),
        moodId = "excited",
        groupId = "friends"
    )

    private val genreTitles = CineMatchOptions.genres.associate { it.id to it.title }

    private val moodShort = mapOf(
        "excited" to "Hào hứng",
        "relaxed" to "Thư giãn",
        "thrilled" to "Bất ngờ",
        "moved" to "Cảm động"
    )
    private val groupSummary = mapOf(
        "friends" to "Đi xem nhóm",
        "couple" to "Đi xem cặp đôi",
        "solo" to "Đi một mình",
        "family" to "Đi xem gia đình"
    )
    private val groupInline = mapOf(
        "friends" to "nhóm bạn",
        "couple" to "cặp đôi",
        "solo" to "một mình",
        "family" to "gia đình"
    )

    // Dòng tóm tắt, ví dụ: "Khoa học viễn tưởng • Hào hứng • Đi xem nhóm"
    fun summary(prefs: CineMatchPrefs): String {
        val genres = CineMatchOptions.genres
            .filter { it.id in prefs.genreIds }
            .joinToString(", ") { it.title }
        return listOf(
            genres,
            moodShort[prefs.moodId].orEmpty(),
            groupSummary[prefs.groupId].orEmpty()
        ).filter { it.isNotEmpty() }.joinToString(" • ")
    }

    // Báo khi có thể loại người dùng chọn mà kho phim chưa có phim nào
    fun genreNotice(prefs: CineMatchPrefs): String? {
        val missing = CineMatchOptions.genres.filter { opt ->
            opt.id in prefs.genreIds && movies.none { opt.id in it.genreIds }
        }
        if (missing.isEmpty()) return null

        val names = missing.joinToString(", ") { it.title }
        return if (missing.size == prefs.genreIds.size) {
            "Chưa có phim đang chiếu thuộc thể loại bạn chọn ($names). " +
                    "CineNow gợi ý theo tâm trạng và quy mô cùng đi."
        } else {
            "Chưa có phim đang chiếu thuộc thể loại: $names. " +
                    "Kết quả được tính theo các thể loại còn lại, tâm trạng và quy mô cùng đi."
        }
    }

    // Trả về toàn bộ phim đã chấm điểm, xếp từ phù hợp nhất đến ít phù hợp nhất
    fun recommend(prefs: CineMatchPrefs): List<MatchResult> =
        movies.map { score(it, prefs) }
            .sortedWith(
                compareByDescending<MatchResult> { it.percent }
                    .thenByDescending { it.movie.rating }
                    .thenBy { it.movie.title }
            )

    private fun score(movie: MatchMovie, prefs: CineMatchPrefs): MatchResult {
        val matchedGenres = prefs.genreIds.intersect(movie.genreIds)
        val genreRatio =
            if (prefs.genreIds.isEmpty() || movie.genreIds.isEmpty()) 0.0
            else matchedGenres.size.toDouble() / min(prefs.genreIds.size, movie.genreIds.size)
        val moodOk = prefs.moodId in movie.moodIds
        val groupOk = prefs.groupId in movie.groupIds

        val raw = 0.5 * genreRatio +
                0.3 * (if (moodOk) 1.0 else 0.0) +
                0.2 * (if (groupOk) 1.0 else 0.0)
        val percent = (60 + raw * 35).roundToInt()

        return MatchResult(movie, percent, buildReasons(movie, prefs, matchedGenres, moodOk, groupOk))
    }

    // Luôn 3 lý do: thể loại, tâm trạng/quy mô, điểm đánh giá
    private fun buildReasons(
        movie: MatchMovie,
        prefs: CineMatchPrefs,
        matchedGenres: Set<String>,
        moodOk: Boolean,
        groupOk: Boolean
    ): List<MatchReason> {
        val genreReason = if (matchedGenres.isNotEmpty()) {
            val names = CineMatchOptions.genres
                .filter { it.id in matchedGenres }
                .joinToString(" + ") { genreTitles.getValue(it.id) }
            MatchReason("Đúng thể loại đã chọn: $names", movie.genreNote)
        } else {
            MatchReason(
                "Thể loại mới lạ: ${movie.genreLabel}",
                "Một lựa chọn ngoài các thể loại bạn đã chọn để khám phá thêm"
            )
        }

        val group = groupInline[prefs.groupId].orEmpty()
        val mood = moodShort[prefs.moodId].orEmpty()
        val moodGroupReason = when {
            moodOk && groupOk ->
                MatchReason("Hợp tâm trạng và phù hợp xem cùng $group", movie.groupNote)
            groupOk ->
                MatchReason("Phù hợp xem cùng $group", movie.groupNote)
            moodOk ->
                MatchReason("Hợp tâm trạng: $mood", movie.moodNote)
            else ->
                MatchReason(
                    "Cân bằng với lựa chọn của bạn",
                    "Điểm số được tính từ thể loại, tâm trạng và quy mô cùng đi"
                )
        }

        val ratingReason = if (movie.rating >= 8.5) {
            MatchReason("Điểm đánh giá cao: ${movie.rating}/10", movie.ratingNote)
        } else {
            MatchReason("Điểm đánh giá tốt: ${movie.rating}/10", movie.ratingNote)
        }

        return listOf(genreReason, moodGroupReason, ratingReason)
    }

    // ---------- Kho phim giả lập (đều là phim đang chiếu, đã có poster trong drawable) ----------
    // Muốn thêm phim: thêm một MatchMovie vào danh sách này, không cần sửa chỗ khác.
    val movies = listOf(
        MatchMovie(
            title = "Hành Tinh Cát: Phần Hai",
            originalTitle = "Dune: Part Two",
            detailTitle = "Hành Tinh Cát 2",
            genreIds = setOf("scifi", "action"),
            genreLabel = "Khoa học viễn tưởng • Phiêu lưu • Hành động",
            moodIds = setOf("excited", "thrilled"),
            groupIds = setOf("friends", "couple"),
            rating = 8.9, durationMin = 166, ageRating = "T16",
            posterRes = R.drawable.poster_hanh_tinh_cat_2,
            tag = "Bom tấn viễn tưởng",
            genreNote = "Bối cảnh phiêu lưu vũ trụ kỳ vĩ, hình ảnh kỹ xảo mãn nhãn",
            moodNote = "Nhịp phim dồn dập, cảnh hành động hoành tráng",
            groupNote = "Không khí sôi nổi, trải nghiệm tốt nhất trên màn chiếu lớn",
            ratingNote = "Được cộng đồng yêu phim đánh giá xuất sắc"
        ),
        MatchMovie(
            title = "Kẻ Trộm Giấc Mơ",
            originalTitle = "Inception",
            detailTitle = "Kẻ Trộm Giấc Mơ",
            genreIds = setOf("scifi", "action"),
            genreLabel = "Giật gân • Khoa học viễn tưởng • Hành động",
            moodIds = setOf("thrilled"),
            groupIds = setOf("friends", "solo"),
            rating = 8.8, durationMin = 148, ageRating = "T16",
            posterRes = R.drawable.poster_ke_trom_giac_mo,
            tag = "Trí tuệ & kịch tính",
            genreNote = "Ý tưởng khoa học viễn tưởng độc đáo, cảnh hành động sáng tạo",
            moodNote = "Cốt truyện nhiều lớp, liên tục đảo chiều bất ngờ",
            groupNote = "Xem xong rất dễ cùng bạn bè bàn luận và đoán kết thúc",
            ratingNote = "Một trong những phim được yêu thích nhất mọi thời đại"
        ),
        MatchMovie(
            title = "Thám Hiểm Vũ Trụ",
            originalTitle = null,
            detailTitle = "Thám Hiểm Vũ Trụ",
            genreIds = setOf("scifi", "action"),
            genreLabel = "Khoa học viễn tưởng • Hành động",
            moodIds = setOf("excited"),
            groupIds = setOf("friends", "family"),
            rating = 8.4, durationMin = 120, ageRating = "T13",
            posterRes = R.drawable.poster_tham_hiem_vu_tru,
            tag = "Phiêu lưu ngoài không gian",
            genreNote = "Hành trình khám phá vũ trụ với nhiều pha hành động",
            moodNote = "Nhịp phim nhanh, hình ảnh rực rỡ",
            groupNote = "Nội dung vừa sức, cả nhóm cùng xem đều thấy cuốn",
            ratingNote = "Khán giả đánh giá cao phần hình ảnh và nhịp phim"
        ),
        MatchMovie(
            title = "Oppenheimer",
            originalTitle = null,
            detailTitle = "Oppenheimer",
            genreIds = emptySet(),
            genreLabel = "Tiểu sử • Lịch sử • Kịch tính",
            moodIds = setOf("thrilled", "moved"),
            groupIds = setOf("solo", "couple"),
            rating = 8.9, durationMin = 180, ageRating = "T18",
            posterRes = R.drawable.poster_oppenheimer,
            tag = "Kiệt tác điện ảnh",
            genreNote = "Bộ phim tiểu sử kịch tính về bước ngoặt của lịch sử",
            moodNote = "Cuốn hút và để lại nhiều suy ngẫm sau khi xem",
            groupNote = "Phù hợp để thưởng thức tập trung, trọn vẹn từng cảnh",
            ratingNote = "Được giới phê bình và khán giả đánh giá rất cao"
        ),
        MatchMovie(
            title = "Gia Tài Của Ngoại",
            originalTitle = null,
            detailTitle = "Gia Tài Của Ngoại",
            genreIds = setOf("family_drama"),
            genreLabel = "Gia đình • Tâm lý",
            moodIds = setOf("moved", "relaxed"),
            groupIds = setOf("family", "couple"),
            rating = 9.3, durationMin = 120, ageRating = "P",
            posterRes = R.drawable.poster_gia_tai_cua_ngoai,
            tag = "Ấm áp tình thân",
            genreNote = "Câu chuyện gia đình gần gũi, chạm đến cảm xúc người xem",
            moodNote = "Nhẹ nhàng, sâu lắng và nhiều cảm xúc",
            groupNote = "Phù hợp mọi lứa tuổi, cả nhà cùng xem",
            ratingNote = "Điểm đánh giá thuộc hàng cao nhất tại rạp hiện nay"
        ),
        MatchMovie(
            title = "Mai",
            originalTitle = null,
            detailTitle = "Mai",
            genreIds = setOf("family_drama"),
            genreLabel = "Tâm lý • Tình cảm",
            moodIds = setOf("moved", "relaxed"),
            groupIds = setOf("couple", "solo"),
            rating = 8.2, durationMin = 131, ageRating = "T18",
            posterRes = R.drawable.poster_mai,
            tag = "Xúc cảm & lắng đọng",
            genreNote = "Chuyện tình cảm và nội tâm nhân vật được khắc họa sâu sắc",
            moodNote = "Nhẹ nhàng nhưng lắng đọng, dễ chạm đến cảm xúc",
            groupNote = "Hợp cho buổi hẹn hò hoặc tự thưởng cho bản thân",
            ratingNote = "Được khán giả đón nhận nồng nhiệt"
        ),
        MatchMovie(
            title = "Vùng Đất Linh Hồn",
            originalTitle = "Spirited Away",
            detailTitle = "Vùng Đất Linh Hồn",
            genreIds = setOf("animation"),
            genreLabel = "Hoạt hình • Kỳ ảo • Phiêu lưu",
            moodIds = setOf("relaxed", "moved"),
            groupIds = setOf("family", "friends", "solo"),
            rating = 8.7, durationMin = 125, ageRating = "P",
            posterRes = R.drawable.poster_vung_dat_linh_hon,
            tag = "Phiêu lưu kỳ ảo",
            genreNote = "Thế giới hoạt hình kỳ ảo đầy màu sắc và trí tưởng tượng",
            moodNote = "Nhẹ nhàng, giàu cảm xúc và đẹp như một giấc mơ",
            groupNote = "Phù hợp mọi lứa tuổi và mọi kiểu đi xem",
            ratingNote = "Tác phẩm hoạt hình kinh điển được yêu thích"
        )
    )
}