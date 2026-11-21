package com.ute.cinenow.ui.booking

/** Định dạng chiếu. chipLabel = chữ trên thẻ lọc, title = tên hiển thị trong thẻ rạp. */
enum class ShowFormat(val chipLabel: String, val title: String) {
    STANDARD_2D("2D", "Standard 2D"),
    IMAX_LASER("IMAX Laser", "IMAX Laser"),
    FOUR_DX("4DX", "4DX"),
    SCREEN_X("ScreenX", "ScreenX")
}

/** Khu vực trong danh sách chọn (nút "Bình Thạnh & TP. Thủ Đức"). */
data class ShowArea(val id: String, val label: String)

/** Một suất chiếu. seatsLeft = 0 nghĩa là hết vé; note (nếu có) hiện thay cho số ghế, ví dụ "Suất khuya". */
data class Showtime(val time: String, val seatsLeft: Int, val note: String? = null)

/** Các suất chiếu của một định dạng tại một rạp. */
data class ShowGroup(val format: ShowFormat, val language: String, val times: List<Showtime>)

data class ShowCinema(
    val id: String,
    val areaId: String,
    val name: String,
    val shortName: String,
    val badge: String?,
    val distance: String?,
    val address: String,
    val features: List<String>,
    val priceRange: String,
    val groups: List<ShowGroup>
)

/**
 * Dữ liệu mẫu cho màn Chọn suất chiếu.
 * Muốn thêm rạp/khu vực: thêm vào [areas] và [cinemas], màn hình tự cập nhật.
 */
object ShowtimeSamples {

    const val DEFAULT_AREA_ID = "hcm_binh_thanh"

    // Suất được chọn sẵn khi mở màn hình (giống bản cũ: Landmark 81 • 16:45 • IMAX Laser)
    const val DEFAULT_CINEMA_ID = "landmark81"
    const val DEFAULT_TIME = "16:45"
    val DEFAULT_FORMAT = ShowFormat.IMAX_LASER

    val areas = listOf(
        ShowArea("hcm_binh_thanh", "Bình Thạnh & TP. Thủ Đức"),
        ShowArea("hcm_q1_q10", "Quận 1 & Quận 10"),
        ShowArea("hanoi", "Hà Nội"),
        ShowArea("danang", "Đà Nẵng"),
        ShowArea("haiphong", "Hải Phòng"),
        ShowArea("cantho", "Cần Thơ")
    )

    val defaultArea: ShowArea get() = areas.first { it.id == DEFAULT_AREA_ID }

    private const val SUB = "Phụ đề tiếng Việt"

    private fun t(time: String, seats: Int, note: String? = null) = Showtime(time, seats, note)

    val cinemas = listOf(
        // ===== Bình Thạnh & TP. Thủ Đức =====
        ShowCinema(
            id = "landmark81", areaId = "hcm_binh_thanh",
            name = "CineNow Landmark 81", shortName = "Landmark 81",
            badge = "VIP", distance = "1.5 km",
            address = "Tầng B1, Vincom Landmark 81",
            features = listOf("IMAX Laser", "Dolby Atmos"),
            priceRange = "120.000 đ - 160.000 đ",
            groups = listOf(
                ShowGroup(
                    ShowFormat.IMAX_LASER, SUB,
                    listOf(t("09:30", 42), t("13:15", 18), t("16:45", 27), t("20:00", 6), t("22:30", 50, "Suất khuya"))
                ),
                ShowGroup(ShowFormat.SCREEN_X, SUB, listOf(t("11:00", 35), t("15:30", 20), t("19:15", 14)))
            )
        ),
        ShowCinema(
            id = "thuduc", areaId = "hcm_binh_thanh",
            name = "CineNow Thủ Đức", shortName = "Thủ Đức",
            badge = null, distance = "6.8 km",
            address = "Võ Văn Ngân, TP. Thủ Đức",
            features = listOf("Standard 2D", "4DX", "Phụ đề"),
            priceRange = "65.000 đ - 150.000 đ",
            groups = listOf(
                ShowGroup(
                    ShowFormat.STANDARD_2D, SUB,
                    listOf(t("11:15", 72), t("15:00", 45), t("19:30", 12), t("21:45", 0))
                ),
                ShowGroup(ShowFormat.FOUR_DX, SUB, listOf(t("12:30", 22), t("17:00", 15), t("20:45", 30)))
            )
        ),

        // ===== Quận 1 & Quận 10 =====
        ShowCinema(
            id = "vanhanh", areaId = "hcm_q1_q10",
            name = "CineNow Vạn Hạnh Mall", shortName = "Vạn Hạnh Mall",
            badge = null, distance = "3.2 km",
            address = "Tầng 6, Vạn Hạnh Mall, Q.10",
            features = listOf("Standard 2D", "Ghế Đôi Cuddle"),
            priceRange = "95.000 đ - 130.000 đ",
            groups = listOf(
                ShowGroup(
                    ShowFormat.STANDARD_2D, SUB,
                    listOf(t("10:00", 64), t("14:30", 51), t("18:00", 23), t("21:15", 38))
                )
            )
        ),
        ShowCinema(
            id = "saigoncentre", areaId = "hcm_q1_q10",
            name = "CineNow Saigon Centre", shortName = "Saigon Centre",
            badge = "VIP", distance = "4.1 km",
            address = "Tầng 5, Saigon Centre, Q.1",
            features = listOf("IMAX Laser", "4DX"),
            priceRange = "100.000 đ - 170.000 đ",
            groups = listOf(
                ShowGroup(
                    ShowFormat.IMAX_LASER, SUB,
                    listOf(t("09:00", 30), t("12:30", 44), t("16:00", 16), t("19:15", 0), t("22:00", 25, "Suất khuya"))
                ),
                ShowGroup(
                    ShowFormat.FOUR_DX, SUB,
                    listOf(t("10:30", 20), t("14:15", 12), t("18:45", 5), t("21:30", 18))
                )
            )
        ),

        // ===== Hà Nội =====
        ShowCinema(
            id = "royalcity", areaId = "hanoi",
            name = "CineNow Royal City", shortName = "Royal City",
            badge = "VIP", distance = null,
            address = "Tầng B2, Vincom Royal City, Q. Thanh Xuân",
            features = listOf("IMAX Laser", "Dolby Atmos"),
            priceRange = "110.000 đ - 150.000 đ",
            groups = listOf(
                ShowGroup(
                    ShowFormat.IMAX_LASER, SUB,
                    listOf(t("09:45", 38), t("13:00", 26), t("16:30", 7), t("20:15", 33))
                ),
                ShowGroup(
                    ShowFormat.STANDARD_2D, SUB,
                    listOf(t("10:30", 60), t("14:00", 48), t("18:00", 22), t("21:00", 35))
                )
            )
        ),
        ShowCinema(
            id = "aeonlongbien", areaId = "hanoi",
            name = "CineNow Aeon Long Biên", shortName = "Aeon Long Biên",
            badge = null, distance = null,
            address = "Tầng 3, Aeon Mall Long Biên, Q. Long Biên",
            features = listOf("Standard 2D", "ScreenX"),
            priceRange = "80.000 đ - 120.000 đ",
            groups = listOf(
                ShowGroup(
                    ShowFormat.STANDARD_2D, SUB,
                    listOf(t("09:30", 70), t("13:45", 52), t("17:30", 28), t("21:30", 0))
                ),
                ShowGroup(ShowFormat.SCREEN_X, SUB, listOf(t("11:15", 24), t("15:45", 17), t("19:45", 10)))
            )
        ),

        // ===== Đà Nẵng =====
        ShowCinema(
            id = "ngoquyen", areaId = "danang",
            name = "CineNow Vincom Ngô Quyền", shortName = "Vincom Ngô Quyền",
            badge = null, distance = null,
            address = "Tầng 4, Vincom Plaza Ngô Quyền, Q. Sơn Trà",
            features = listOf("Standard 2D", "4DX"),
            priceRange = "75.000 đ - 140.000 đ",
            groups = listOf(
                ShowGroup(
                    ShowFormat.STANDARD_2D, SUB,
                    listOf(t("09:15", 58), t("13:30", 40), t("17:45", 19), t("20:30", 33))
                ),
                ShowGroup(
                    ShowFormat.FOUR_DX, SUB,
                    listOf(t("11:00", 16), t("15:15", 9), t("19:00", 0), t("21:45", 24))
                )
            )
        ),
        ShowCinema(
            id = "indochina", areaId = "danang",
            name = "CineNow Indochina Riverside", shortName = "Indochina Riverside",
            badge = "VIP", distance = null,
            address = "Tầng 3, Indochina Riverside Mall, Q. Hải Châu",
            features = listOf("IMAX Laser", "Dolby Atmos"),
            priceRange = "100.000 đ - 160.000 đ",
            groups = listOf(
                ShowGroup(
                    ShowFormat.IMAX_LASER, SUB,
                    listOf(t("10:00", 36), t("14:00", 25), t("17:30", 7), t("20:45", 29))
                ),
                ShowGroup(
                    ShowFormat.STANDARD_2D, SUB,
                    listOf(t("09:30", 52), t("12:45", 44), t("16:15", 31), t("22:15", 40, "Suất khuya"))
                )
            )
        ),
        ShowCinema(
            id = "lottemartdn", areaId = "danang",
            name = "CineNow Lotte Mart Đà Nẵng", shortName = "Lotte Mart Đà Nẵng",
            badge = null, distance = null,
            address = "Tầng 3, Lotte Mart, Q. Cẩm Lệ",
            features = listOf("Standard 2D", "ScreenX"),
            priceRange = "70.000 đ - 120.000 đ",
            groups = listOf(
                ShowGroup(
                    ShowFormat.STANDARD_2D, SUB,
                    listOf(t("10:15", 68), t("14:45", 55), t("18:15", 27), t("21:00", 42))
                ),
                ShowGroup(ShowFormat.SCREEN_X, SUB, listOf(t("12:00", 21), t("16:30", 13), t("20:00", 8)))
            )
        ),

        // ===== Hải Phòng =====
        ShowCinema(
            id = "aeonhp", areaId = "haiphong",
            name = "CineNow Aeon Mall Hải Phòng", shortName = "Aeon Hải Phòng",
            badge = null, distance = null,
            address = "Tầng 3, Aeon Mall Hải Phòng, Q. Lê Chân",
            features = listOf("Standard 2D", "IMAX Laser"),
            priceRange = "80.000 đ - 140.000 đ",
            groups = listOf(
                ShowGroup(
                    ShowFormat.STANDARD_2D, SUB,
                    listOf(t("09:45", 66), t("13:15", 47), t("17:00", 30), t("20:30", 21))
                ),
                ShowGroup(ShowFormat.IMAX_LASER, SUB, listOf(t("11:30", 28), t("15:30", 19), t("19:45", 4)))
            )
        ),

        // ===== Cần Thơ =====
        ShowCinema(
            id = "sensecity", areaId = "cantho",
            name = "CineNow Sense City", shortName = "Sense City",
            badge = null, distance = null,
            address = "Tầng 4, Sense City, Q. Ninh Kiều",
            features = listOf("Standard 2D", "4DX"),
            priceRange = "65.000 đ - 130.000 đ",
            groups = listOf(
                ShowGroup(
                    ShowFormat.STANDARD_2D, SUB,
                    listOf(t("10:00", 60), t("14:00", 44), t("18:15", 26), t("21:30", 37))
                ),
                ShowGroup(ShowFormat.FOUR_DX, SUB, listOf(t("12:15", 18), t("16:45", 11), t("20:15", 15)))
            )
        )
    )

    /** Rạp thuộc khu vực [areaId]; nếu [format] != null chỉ giữ lại các suất đúng định dạng đó. */
    fun cinemasFor(areaId: String, format: ShowFormat?): List<Pair<ShowCinema, List<ShowGroup>>> =
        cinemas.filter { it.areaId == areaId }.mapNotNull { cinema ->
            val groups = if (format == null) cinema.groups else cinema.groups.filter { it.format == format }
            if (groups.isEmpty()) null else cinema to groups
        }
}
