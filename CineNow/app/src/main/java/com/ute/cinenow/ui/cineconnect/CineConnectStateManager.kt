package com.ute.cinenow.ui.cineconnect

import com.ute.cinenow.R

object CineConnectStateManager {

    enum class ConnectionStatus {
        NOT_CONNECTED,
        REQUEST_SENT,
        REQUEST_RECEIVED,
        FRIEND
    }

    data class CineUser(
        val id: String,
        val name: String,
        val handle: String,
        val age: Int,
        val avatarRes: Int = R.drawable.ic_person,
        val favoriteCinema: String,
        val watchedTickets: Int,
        val genres: List<String>,
        val matchPercent: Int,
        val genreScore: Int,
        val formatScore: Int,
        val timeScore: Int,
        var status: ConnectionStatus,
        val bio: String,
        val isOnline: Boolean = false,
        val onlineStatusText: String = "Hoạt động gần đây",
        val favoriteMovies: List<String> = listOf("Hành Tinh Cát 2", "Oppenheimer", "Kẻ Trộm Giấc Mơ")
    )

    data class ChatMessage(
        val senderName: String,
        val message: String,
        val time: String,
        val isMe: Boolean,
        val isMovieInvite: Boolean = false,
        val movieTitle: String? = null,
        val movieShowtime: String? = null,
        val movieCinema: String? = null
    )

    // Master list: Nguồn duy nhất cho mọi màn hình
    private val allUsers = mutableMapOf<String, CineUser>()

    // Chat history per user
    private val chatHistories = mutableMapOf<String, MutableList<ChatMessage>>()

    init {
        resetDefaultData()
    }

    fun resetDefaultData() {
        allUsers.clear()
        chatHistories.clear()

        val list = listOf(
            CineUser(
                id = "user_01",
                name = "Minh Quân",
                handle = "minhquan_cine",
                age = 24,
                avatarRes = R.drawable.ic_person,
                favoriteCinema = "CineNow Bitexco Q1",
                watchedTickets = 32,
                genres = listOf("Khoa học viễn tưởng", "Hành động", "IMAX"),
                matchPercent = 96,
                genreScore = 98,
                formatScore = 95,
                timeScore = 95,
                status = ConnectionStatus.FRIEND,
                bio = "Mê phim viễn tưởng và rạp IMAX. Thích thảo luận cốt truyện sâu sau phim.",
                isOnline = true,
                onlineStatusText = "Đang hoạt động"
            ),
            CineUser(
                id = "user_02",
                name = "Thảo Vy",
                handle = "thaovy_film",
                age = 23,
                avatarRes = R.drawable.ic_person,
                favoriteCinema = "CineNow Landmark 81",
                watchedTickets = 19,
                genres = listOf("Tình cảm", "Hoạt hình", "Ghibli"),
                matchPercent = 92,
                genreScore = 90,
                formatScore = 92,
                timeScore = 94,
                status = ConnectionStatus.FRIEND,
                bio = "Thích phim tình cảm nhẹ nhàng, hoạt hình cuối tuần cùng bạn bè.",
                isOnline = true,
                onlineStatusText = "Đang hoạt động"
            ),
            CineUser(
                id = "user_03",
                name = "Hoàng Nam",
                handle = "hoangnam_imax",
                age = 25,
                avatarRes = R.drawable.ic_person,
                favoriteCinema = "CineNow Nguyễn Huệ",
                watchedTickets = 28,
                genres = listOf("IMAX Fan", "Hành động", "Phiêu lưu"),
                matchPercent = 88,
                genreScore = 88,
                formatScore = 90,
                timeScore = 86,
                status = ConnectionStatus.FRIEND,
                bio = "IMAX Fan chính hiệu. Rảnh các tối Thứ 6 và ngày cuối tuần.",
                isOnline = true,
                onlineStatusText = "Đang hoạt động"
            ),
            CineUser(
                id = "user_04",
                name = "Phương Linh",
                handle = "phuonglinh_horror",
                age = 21,
                avatarRes = R.drawable.ic_person,
                favoriteCinema = "CineNow Landmark 81",
                watchedTickets = 15,
                genres = listOf("Kinh dị", "Giật gân", "Trinh thám"),
                matchPercent = 85,
                genreScore = 86,
                formatScore = 84,
                timeScore = 85,
                status = ConnectionStatus.FRIEND,
                bio = "Tìm cạ cứng xem phim kinh dị giật gân, không sợ ma!",
                isOnline = false,
                onlineStatusText = "Hoạt động 2 giờ trước"
            ),
            CineUser(
                id = "user_05",
                name = "Vũ Mai Anh",
                handle = "maianh_vu",
                age = 22,
                avatarRes = R.drawable.ic_person,
                favoriteCinema = "CineNow Hai Bà Trưng",
                watchedTickets = 21,
                genres = listOf("Tâm lý", "Âm nhạc", "Lãng mạn"),
                matchPercent = 87,
                genreScore = 89,
                formatScore = 85,
                timeScore = 87,
                status = ConnectionStatus.FRIEND,
                bio = "Yêu âm nhạc trong phim và các tác phẩm độc lập sâu sắc.",
                isOnline = true,
                onlineStatusText = "15 phút trước"
            ),
            CineUser(
                id = "user_06",
                name = "Bảo Minh",
                handle = "baominh_cinema",
                age = 26,
                avatarRes = R.drawable.ic_person,
                favoriteCinema = "CineNow Bitexco Q1",
                watchedTickets = 30,
                genres = listOf("Hành động", "Kịch tính", "Sci-Fi"),
                matchPercent = 83,
                genreScore = 85,
                formatScore = 82,
                timeScore = 82,
                status = ConnectionStatus.FRIEND,
                bio = "Xem phim giải trí xả stress sau giờ làm việc căng thẳng.",
                isOnline = false,
                onlineStatusText = "Hôm qua"
            ),
            CineUser(
                id = "user_07",
                name = "Nguyễn Thảo Ly",
                handle = "thaoly_cinema",
                age = 22,
                avatarRes = R.drawable.ic_person,
                favoriteCinema = "CineNow Landmark 81",
                watchedTickets = 18,
                genres = listOf("Khoa học viễn tưởng", "Anime", "Tâm lý"),
                matchPercent = 94,
                genreScore = 95,
                formatScore = 93,
                timeScore = 94,
                status = ConnectionStatus.REQUEST_RECEIVED,
                bio = "Mê phim viễn tưởng & rạp IMAX cuối tuần. Thích thảo luận cốt truyện sau phim!",
                isOnline = true,
                onlineStatusText = "Đang hoạt động"
            ),
            CineUser(
                id = "user_08",
                name = "Đặng Tuấn Kiệt",
                handle = "tuankiet_cine",
                age = 26,
                avatarRes = R.drawable.ic_person,
                favoriteCinema = "CineNow Bitexco Q1",
                watchedTickets = 27,
                genres = listOf("Hành động", "Phiêu lưu", "Marvel"),
                matchPercent = 89,
                genreScore = 90,
                formatScore = 88,
                timeScore = 89,
                status = ConnectionStatus.REQUEST_RECEIVED,
                bio = "Thường xem phim bom tấn hành động rạp Bitexco, tìm bạn đi xem cùng để rủ nhau bàn luận kỹ xảo.",
                isOnline = true,
                onlineStatusText = "Đang hoạt động"
            ),
            CineUser(
                id = "user_09",
                name = "Phạm Thảo Vy",
                handle = "phamthaovy_cgv",
                age = 23,
                avatarRes = R.drawable.ic_person,
                favoriteCinema = "CineNow Hai Bà Trưng",
                watchedTickets = 14,
                genres = listOf("Hài hước", "Gia đình", "Hoạt hình"),
                matchPercent = 81,
                genreScore = 82,
                formatScore = 80,
                timeScore = 81,
                status = ConnectionStatus.REQUEST_SENT,
                bio = "Xem phim nhẹ nhàng vui vẻ xả stress cuối tuần.",
                isOnline = false,
                onlineStatusText = "Hoạt động 1 ngày trước"
            ),
            CineUser(
                id = "user_10",
                name = "Trần Bảo Long",
                handle = "baolong_cinema",
                age = 25,
                avatarRes = R.drawable.ic_person,
                favoriteCinema = "CineNow Landmark 81",
                watchedTickets = 24,
                genres = listOf("Khoa học viễn tưởng", "IMAX", "4DX"),
                matchPercent = 96,
                genreScore = 98,
                formatScore = 94,
                timeScore = 92,
                status = ConnectionStatus.NOT_CONNECTED,
                bio = "Thích xem phim cuối tuần ở phòng chiếu IMAX, mê phim viễn tưởng và thảo luận chi tiết plot twist sau phim.",
                isOnline = true,
                onlineStatusText = "Đang hoạt động"
            ),
            CineUser(
                id = "user_11",
                name = "Lê Hoàng Minh",
                handle = "hoangminh_imax",
                age = 27,
                avatarRes = R.drawable.ic_person,
                favoriteCinema = "CineNow Landmark 81",
                watchedTickets = 35,
                genres = listOf("Tâm lý", "Giật gân", "Nolan"),
                matchPercent = 90,
                genreScore = 92,
                formatScore = 90,
                timeScore = 88,
                status = ConnectionStatus.NOT_CONNECTED,
                bio = "Tín đồ phim của đạo diễn Christopher Nolan và Denis Villeneuve.",
                isOnline = false,
                onlineStatusText = "Hoạt động 3 giờ trước"
            )
        )

        list.forEach { allUsers[it.name] = it }

        // Khởi tạo lịch sử chat mẫu cho từng bạn bè (không dùng chung)
        chatHistories["Thảo Vy"] = mutableListOf(
            ChatMessage("Thảo Vy", "Chào bạn! Cuối tuần này rảnh không, mình thấy phim Hành Tinh Cát 2 đang hot quá nè!", "14:22", false),
            ChatMessage("Tôi", "Mình cũng đang muốn xem lại bản IMAX nè, bạn đã mua vé chưa?", "14:25", true),
            ChatMessage("Thảo Vy", "Chưa nè, hay tụi mình đi chung suất tối thứ 7 đi! Bạn chọn rạp thử xem?", "14:26", false),
            ChatMessage("Thảo Vy", "Lời mời xem phim", "14:28", false, isMovieInvite = true, movieTitle = "Hành Tinh Cát: Phần 2", movieShowtime = "19:30 • Thứ Bảy, 20/04", movieCinema = "CineNow Nguyễn Huệ • Rạp 03")
        )

        chatHistories["Hoàng Nam"] = mutableListOf(
            ChatMessage("Hoàng Nam", "Ê bạn ơi, Godzilla x Kong xem ở rạp IMAX Landmark 81 phê lắm!", "10:15", false),
            ChatMessage("Tôi", "Thế hả Nam? Tối thứ 6 này có suất chiếu nào đẹp không?", "10:20", true)
        )

        chatHistories["Minh Quân"] = mutableListOf(
            ChatMessage("Minh Quân", "Hôm qua vừa xem Dune 2 về, kỹ xảo đỉnh cao thực sự!", "09:00", false),
            ChatMessage("Tôi", "Đoạn sa mạc nhìn choáng ngợp thật sự Quân ạ.", "09:05", true)
        )

        chatHistories["Vũ Mai Anh"] = mutableListOf(
            ChatMessage("Vũ Mai Anh", "Chào bạn, phim Mai có suất chiếu tối nay ở Hai Bà Trưng nè.", "16:00", false)
        )

        chatHistories["Bảo Minh"] = mutableListOf(
            ChatMessage("Bảo Minh", "Cuối tuần này rảnh làm chầu bắp nước xem phim bom tấn nhé!", "11:30", false)
        )

        chatHistories["Phương Linh"] = mutableListOf(
            ChatMessage("Phương Linh", "Phim kinh dị mới ra rạp ghê lắm, đi xem chung cho đỡ sợ không?", "20:00", false)
        )
    }

    fun getUser(name: String): CineUser {
        return allUsers[name] ?: CineUser(
            id = "user_default_${name.hashCode()}",
            name = name,
            handle = name.lowercase().replace(" ", "_"),
            age = 22,
            avatarRes = R.drawable.ic_person,
            favoriteCinema = "CineNow Landmark 81",
            watchedTickets = 10,
            genres = listOf("Điện ảnh"),
            matchPercent = 85,
            genreScore = 85,
            formatScore = 85,
            timeScore = 85,
            status = ConnectionStatus.NOT_CONNECTED,
            bio = "Người yêu điện ảnh tại CineNow."
        )
    }

    fun getAllUsers(): List<CineUser> = allUsers.values.toList()

    fun getFriends(): List<CineUser> = allUsers.values.filter { it.status == ConnectionStatus.FRIEND }

    fun getReceivedRequests(): List<CineUser> = allUsers.values.filter { it.status == ConnectionStatus.REQUEST_RECEIVED }

    fun getSentRequests(): List<CineUser> = allUsers.values.filter { it.status == ConnectionStatus.REQUEST_SENT }

    fun isFriend(userName: String): Boolean {
        return getUser(userName).status == ConnectionStatus.FRIEND
    }

    fun isRequestSent(userName: String): Boolean {
        return getUser(userName).status == ConnectionStatus.REQUEST_SENT
    }

    fun isRequestReceived(userName: String): Boolean {
        return getUser(userName).status == ConnectionStatus.REQUEST_RECEIVED
    }

    fun sendFriendRequest(userName: String) {
        val user = getUser(userName)
        if (user.status == ConnectionStatus.REQUEST_RECEIVED) {
            // Cả hai cùng gửi lời mời -> Tự động thành bạn bè
            user.status = ConnectionStatus.FRIEND
        } else if (user.status == ConnectionStatus.NOT_CONNECTED) {
            user.status = ConnectionStatus.REQUEST_SENT
        }
        allUsers[user.name] = user
    }

    fun cancelFriendRequest(userName: String) {
        val user = getUser(userName)
        if (user.status == ConnectionStatus.REQUEST_SENT) {
            user.status = ConnectionStatus.NOT_CONNECTED
            allUsers[user.name] = user
        }
    }

    fun acceptFriendRequest(userName: String) {
        val user = getUser(userName)
        user.status = ConnectionStatus.FRIEND
        allUsers[user.name] = user
    }

    fun declineFriendRequest(userName: String) {
        val user = getUser(userName)
        user.status = ConnectionStatus.NOT_CONNECTED
        allUsers[user.name] = user
    }

    // Chat management
    fun getMessages(userName: String): MutableList<ChatMessage> {
        return chatHistories.getOrPut(userName) { mutableListOf() }
    }

    fun addMessage(userName: String, message: ChatMessage) {
        getMessages(userName).add(message)
    }
}