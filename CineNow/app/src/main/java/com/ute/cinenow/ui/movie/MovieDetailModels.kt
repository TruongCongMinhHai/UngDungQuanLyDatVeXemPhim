package com.ute.cinenow.ui.movie

import com.ute.cinenow.R

// Diễn viên / đạo diễn. avatarRes = 0 nghĩa là chưa có ảnh, sẽ hiện chữ cái đầu
data class CastMember(
    val name: String,
    val role: String,
    val avatarRes: Int = 0
)

data class MovieReview(
    val id: Int,
    val userName: String,
    val timeAgo: String,
    val cinema: String,
    val score: Double,
    val content: String
)

// Dữ liệu giả lập cho màn Chi tiết phim (chưa nối dữ liệu thật)
data class MovieDetail(
    val title: String,
    val originalTitle: String,
    val ageRating: String,
    val banner: String,
    val posterRes: Int,
    val rating: Double,
    val ratingCount: Int,
    val recommendPercent: Int,
    val durationMin: Int,
    val language: String,
    val format: String,
    val cast: List<CastMember>,
    val reviewCountLabel: String,
    val reviews: List<MovieReview>,
    val minPrice: Int
)

object MovieDetailSamples {

    // Viết gọn cho một bình luận
    private fun rv(id: Int, user: String, time: String, cinema: String, score: Double, content: String) =
        MovieReview(id, user, time, cinema, score, content)

    // Phim chưa có thông tin diễn viên thì hiện tạm dòng này
    private val unknownCast = listOf(
        CastMember("Đạo diễn", "Đang cập nhật"),
        CastMember("Diễn viên chính", "Đang cập nhật"),
        CastMember("Diễn viên phụ", "Đang cập nhật")
    )

    private val dune = MovieDetail(
        title = "Hành Tinh Cát 2",
        originalTitle = "Dune: Part Two (2024)",
        ageRating = "T16",
        banner = "Chiếu rạp toàn quốc • IMAX 2D",
        posterRes = R.drawable.poster_hanh_tinh_cat_2,
        rating = 9.1,
        ratingCount = 14280,
        recommendPercent = 98,
        durationMin = 166,
        language = "Phụ đề Việt",
        format = "IMAX / 2D",
        cast = listOf(
            CastMember("Denis Villeneuve", "Đạo diễn"),
            CastMember("Timothée Chalamet", "Paul Atreides"),
            CastMember("Zendaya", "Chani"),
            CastMember("Rebecca Ferguson", "Lady Jessica")
        ),
        reviewCountLabel = "14k+",
        reviews = listOf(
            rv(1, "Minh Nhật", "2 giờ trước", "Rạp CGV Sư Vạn Hạnh", 10.0,
                "Âm thanh Hans Zimmer dập rung cả ghế IMAX, hình ảnh cát bụi sa mạc mãn nhãn tới từng khung hình. Phải đi xem rạp màn to mới thấy hết độ hoành tráng!"),
            rv(2, "Hoàng Anh", "Hôm qua", "Rạp Lotte Landmark", 9.5,
                "Tạo hình Feyd-Rautha của Austin Butler quá xuất sắc. Nhịp phim nửa sau nghẹt thở, đi xem cả nhóm bạn ai cũng tấm tắc khen."),
            rv(3, "Thùy Linh", "3 ngày trước", "Rạp Galaxy Nguyễn Du", 9.0,
                "Thời lượng gần 3 tiếng nhưng trôi qua rất nhanh vì cốt truyện lôi cuốn. Tính năng rủ nhóm bình chọn rạp của app này cực tiện lợi luôn!"),
            rv(4, "Quốc Bảo", "4 ngày trước", "Rạp CGV Vincom", 9.5,
                "Cảnh cưỡi sâu cát là khoảnh khắc đáng nhớ nhất. Xem IMAX là quyết định đúng đắn."),
            rv(5, "Ngọc Mai", "5 ngày trước", "Rạp BHD Phạm Ngọc Thạch", 8.5,
                "Phim hay nhưng nên xem phần 1 trước để hiểu bối cảnh. Hình ảnh thì khỏi bàn."),
            rv(6, "Đức Thịnh", "1 tuần trước", "Rạp Lotte Landmark", 9.0,
                "Nhạc phim quá cuốn, ra khỏi rạp rồi vẫn còn nghe văng vẳng trong đầu."),
            rv(7, "Lan Phương", "1 tuần trước", "Rạp Galaxy Nguyễn Du", 8.0,
                "Đoạn giữa hơi dài nhưng cái kết xứng đáng với sự chờ đợi. Mong có phần 3 sớm.")
        ),
        minPrice = 95000
    )

    private val oppenheimer = MovieDetail(
        title = "Oppenheimer",
        originalTitle = "Oppenheimer (2023)",
        ageRating = "T18",
        banner = "Chiếu rạp toàn quốc • IMAX 2D",
        posterRes = R.drawable.poster_oppenheimer,
        rating = 9.0,
        ratingCount = 21530,
        recommendPercent = 96,
        durationMin = 180,
        language = "Phụ đề Việt",
        format = "IMAX / 2D",
        cast = listOf(
            CastMember("Christopher Nolan", "Đạo diễn"),
            CastMember("Cillian Murphy", "J. R. Oppenheimer"),
            CastMember("Emily Blunt", "Kitty Oppenheimer"),
            CastMember("Robert Downey Jr.", "Lewis Strauss")
        ),
        reviewCountLabel = "21k+",
        reviews = listOf(
            rv(1, "Quốc Bảo", "5 giờ trước", "Rạp CGV Vincom", 9.5,
                "Diễn xuất của Cillian Murphy quá đỉnh. Ba tiếng không hề dài, cảnh thử bom khiến cả rạp nín thở."),
            rv(2, "Ngọc Mai", "Hôm qua", "Rạp Lotte Landmark", 9.0,
                "Phim nhiều thoại nhưng cuốn từ đầu đến cuối. Nên xem IMAX để cảm nhận trọn vẹn âm thanh."),
            rv(3, "Minh Nhật", "2 ngày trước", "Rạp CGV Sư Vạn Hạnh", 9.0,
                "Cách Nolan chia hai tuyến thời gian đen trắng và màu rất thông minh, xem xong muốn xem lại ngay."),
            rv(4, "Thùy Linh", "4 ngày trước", "Rạp Galaxy Nguyễn Du", 8.5,
                "Hơi nặng đô về lịch sử, nhưng dàn diễn viên phụ đều diễn rất tốt."),
            rv(5, "Hoàng Anh", "1 tuần trước", "Rạp BHD Phạm Ngọc Thạch", 9.5,
                "Phim đáng xem nhất năm với mình. Khoảnh khắc im lặng sau vụ nổ ám ảnh thật sự.")
        ),
        minPrice = 95000
    )

    private val giaTai = MovieDetail(
        title = "Gia Tài Của Ngoại",
        originalTitle = "Gia Tài Của Ngoại (2024)",
        ageRating = "P",
        banner = "Chiếu rạp toàn quốc • 2D Phụ đề",
        posterRes = R.drawable.poster_gia_tai_cua_ngoai,
        rating = 9.3,
        ratingCount = 9870,
        recommendPercent = 97,
        durationMin = 120,
        language = "Phụ đề Việt",
        format = "2D",
        cast = unknownCast,
        reviewCountLabel = "9k+",
        reviews = listOf(
            rv(1, "Bảo Châu", "3 giờ trước", "Rạp CGV Vincom", 9.5,
                "Xem mà khóc suốt nửa sau phim. Nhớ bà ngoại của mình quá, nên rủ cả nhà đi xem."),
            rv(2, "Gia Huy", "Hôm qua", "Rạp Lotte Landmark", 9.0,
                "Câu chuyện giản dị nhưng chạm đúng cảm xúc. Phim rất phù hợp đi cùng gia đình."),
            rv(3, "Lan Phương", "2 ngày trước", "Rạp Galaxy Nguyễn Du", 9.5,
                "Nhớ mang theo khăn giấy. Diễn xuất tự nhiên, không hề gượng ép."),
            rv(4, "Đức Thịnh", "5 ngày trước", "Rạp BHD Phạm Ngọc Thạch", 8.5,
                "Nhịp phim chậm nhưng ấm áp, xem xong thấy muốn về thăm ông bà ngay.")
        ),
        minPrice = 85000
    )

    private val mai = MovieDetail(
        title = "Mai",
        originalTitle = "Mai (2024)",
        ageRating = "T18",
        banner = "Chiếu rạp toàn quốc • 2D",
        posterRes = R.drawable.poster_mai,
        rating = 8.2,
        ratingCount = 12400,
        recommendPercent = 88,
        durationMin = 131,
        language = "Tiếng Việt",
        format = "2D",
        cast = listOf(
            CastMember("Trấn Thành", "Đạo diễn"),
            CastMember("Phương Anh Đào", "Mai"),
            CastMember("Tuấn Trần", "Dương")
        ),
        reviewCountLabel = "12k+",
        reviews = listOf(
            rv(1, "Ngọc Mai", "1 giờ trước", "Rạp CGV Sư Vạn Hạnh", 8.5,
                "Câu chuyện tình cảm có nhiều lớp cảm xúc, diễn viên chính diễn rất tròn vai."),
            rv(2, "Minh Nhật", "Hôm qua", "Rạp Lotte Landmark", 8.0,
                "Phần đầu hơi nhẹ nhàng, nửa sau mới thực sự cuốn. Âm nhạc rất hợp."),
            rv(3, "Thùy Linh", "3 ngày trước", "Rạp Galaxy Nguyễn Du", 8.5,
                "Phim làm mình suy nghĩ nhiều về lựa chọn trong tình yêu."),
            rv(4, "Quốc Bảo", "1 tuần trước", "Rạp BHD Phạm Ngọc Thạch", 7.5,
                "Có vài đoạn kéo dài, nhưng nhìn chung vẫn đáng đi xem cùng người yêu.")
        ),
        minPrice = 80000
    )

    private val keTromGiacMo = MovieDetail(
        title = "Kẻ Trộm Giấc Mơ",
        originalTitle = "Inception (2010)",
        ageRating = "T16",
        banner = "Chiếu lại • IMAX 2D",
        posterRes = R.drawable.poster_ke_trom_giac_mo,
        rating = 8.9,
        ratingCount = 18650,
        recommendPercent = 95,
        durationMin = 148,
        language = "Phụ đề Việt",
        format = "IMAX / 2D",
        cast = listOf(
            CastMember("Christopher Nolan", "Đạo diễn"),
            CastMember("Leonardo DiCaprio", "Dom Cobb"),
            CastMember("Joseph Gordon-Levitt", "Arthur"),
            CastMember("Elliot Page", "Ariadne")
        ),
        reviewCountLabel = "18k+",
        reviews = listOf(
            rv(1, "Đức Thịnh", "4 giờ trước", "Rạp CGV Vincom", 9.5,
                "Xem lại trên màn IMAX mới thấy hết độ choáng của cảnh hành lang xoay. Kịch bản vẫn đỉnh sau bao năm."),
            rv(2, "Hoàng Anh", "Hôm qua", "Rạp Lotte Landmark", 9.0,
                "Cái kết mở khiến cả nhóm cãi nhau suốt buổi cà phê sau phim."),
            rv(3, "Lan Phương", "3 ngày trước", "Rạp Galaxy Nguyễn Du", 8.5,
                "Phải tập trung cao độ mới theo kịp, nhưng xem xong rất đã."),
            rv(4, "Gia Huy", "1 tuần trước", "Rạp CGV Sư Vạn Hạnh", 9.0,
                "Nhạc nền của Hans Zimmer làm không khí căng thẳng hơn hẳn.")
        ),
        minPrice = 95000
    )

    private val vungDatLinhHon = MovieDetail(
        title = "Vùng Đất Linh Hồn",
        originalTitle = "Spirited Away (2001)",
        ageRating = "P",
        banner = "Chiếu rạp toàn quốc • 2D Phụ đề",
        posterRes = R.drawable.poster_vung_dat_linh_hon,
        rating = 8.7,
        ratingCount = 11200,
        recommendPercent = 94,
        durationMin = 125,
        language = "Phụ đề Việt",
        format = "2D / 3D",
        cast = listOf(
            CastMember("Hayao Miyazaki", "Đạo diễn"),
            CastMember("Rumi Hiiragi", "Chihiro"),
            CastMember("Miyu Irino", "Haku"),
            CastMember("Mari Natsuki", "Yubaba")
        ),
        reviewCountLabel = "11k+",
        reviews = listOf(
            rv(1, "Bảo Châu", "2 giờ trước", "Rạp Lotte Landmark", 9.0,
                "Xem lại vẫn thấy bồi hồi như lần đầu. Hình ảnh vẽ tay đẹp như tranh."),
            rv(2, "Ngọc Mai", "Hôm qua", "Rạp CGV Vincom", 9.0,
                "Phim hoạt hình mà người lớn xem còn thấm hơn trẻ con."),
            rv(3, "Minh Nhật", "4 ngày trước", "Rạp Galaxy Nguyễn Du", 8.5,
                "Thế giới linh hồn được dựng rất sáng tạo, nhạc của Joe Hisaishi tuyệt vời."),
            rv(4, "Quốc Bảo", "1 tuần trước", "Rạp BHD Phạm Ngọc Thạch", 8.0,
                "Hợp để đi cùng gia đình, bọn trẻ nhà mình xem rất say sưa.")
        ),
        minPrice = 75000
    )

    private val thamHiemVuTru = MovieDetail(
        title = "Thám Hiểm Vũ Trụ",
        originalTitle = "Thám Hiểm Vũ Trụ (2024)",
        ageRating = "T13",
        banner = "Chiếu rạp toàn quốc • IMAX 3D",
        posterRes = R.drawable.poster_tham_hiem_vu_tru,
        rating = 8.4,
        ratingCount = 7640,
        recommendPercent = 90,
        durationMin = 120,
        language = "Phụ đề Việt",
        format = "IMAX / 3D / 2D",
        cast = unknownCast,
        reviewCountLabel = "7k+",
        reviews = listOf(
            rv(1, "Gia Huy", "6 giờ trước", "Rạp CGV Sư Vạn Hạnh", 8.5,
                "Hiệu ứng 3D rất đã mắt, cảnh bay giữa các hành tinh làm mình thích nhất."),
            rv(2, "Đức Thịnh", "Hôm qua", "Rạp Lotte Landmark", 8.0,
                "Cốt truyện đơn giản nhưng hành động liên tục, không có chỗ nào chán."),
            rv(3, "Lan Phương", "3 ngày trước", "Rạp Galaxy Nguyễn Du", 8.5,
                "Rất hợp đi xem cuối tuần cùng nhóm bạn."),
            rv(4, "Thùy Linh", "5 ngày trước", "Rạp CGV Vincom", 8.0,
                "Âm thanh IMAX làm tăng kịch tính đáng kể.")
        ),
        minPrice = 90000
    )

    // Phim sắp chiếu: chưa có bình luận nên mục bình luận sẽ tự ẩn
    private val deadpool = MovieDetail(
        title = "Deadpool & Wolverine",
        originalTitle = "Deadpool & Wolverine (2024)",
        ageRating = "T18",
        banner = "Sắp chiếu • 26/07/2025",
        posterRes = R.drawable.poster_deadpool_wolverine,
        rating = 8.0,
        ratingCount = 3200,
        recommendPercent = 90,
        durationMin = 128,
        language = "Phụ đề Việt",
        format = "IMAX / 2D",
        cast = listOf(
            CastMember("Shawn Levy", "Đạo diễn"),
            CastMember("Ryan Reynolds", "Deadpool"),
            CastMember("Hugh Jackman", "Wolverine"),
            CastMember("Emma Corrin", "Cassandra Nova")
        ),
        reviewCountLabel = "",
        reviews = emptyList(),
        minPrice = 100000
    )

    private val manhGhep = MovieDetail(
        title = "Những Mảnh Ghép Cảm Xúc",
        originalTitle = "Inside Out 2 (2024)",
        ageRating = "P",
        banner = "Sắp chiếu • 14/08/2025",
        posterRes = R.drawable.poster_nhung_manh_ghep_cam_xuc,
        rating = 8.3,
        ratingCount = 2800,
        recommendPercent = 92,
        durationMin = 96,
        language = "Lồng tiếng Việt",
        format = "2D / 3D",
        cast = listOf(
            CastMember("Kelsey Mann", "Đạo diễn"),
            CastMember("Amy Poehler", "Joy"),
            CastMember("Maya Hawke", "Anxiety"),
            CastMember("Phyllis Smith", "Sadness")
        ),
        reviewCountLabel = "",
        reviews = emptyList(),
        minPrice = 75000
    )

    // Tên phim ở các màn khác nhau có thể khác nhau, nên gom về cùng một phim
    private val byTitle: Map<String, MovieDetail> = mapOf(
        "Dune: Hành Tinh Cát" to dune,
        "Hành Tinh Cát 2" to dune,
        "Oppenheimer" to oppenheimer,
        "Gia Tài Của Ngoại" to giaTai,
        "Mai" to mai,
        "Mai: Kỷ Nguyên Mới" to mai,
        "Kẻ Trộm Giấc Mơ" to keTromGiacMo,
        "Vùng Đất Linh Hồn" to vungDatLinhHon,
        "Thám Hiểm Vũ Trụ" to thamHiemVuTru,
        "Deadpool & Wolverine" to deadpool,
        "Những Mảnh Ghép Cảm Xúc" to manhGhep
    )

    // Không tìm thấy tên phim thì tạm dùng phim Dune
    fun find(title: String?): MovieDetail = byTitle[title] ?: dune
}