package com.ute.cinenow.ui.cineconnect

object CineConnectStateManager {

    // Danh sách những người đã gửi lời mời (lưu vết vĩnh viễn trong phiên)
    private val sentFriendRequests = mutableSetOf<String>()

    // Danh sách lời mời đã gửi hiển thị ở tab "Đã gửi" của FriendRequestsFragment
    val sentRequestList = mutableListOf(
        "Phạm Thảo Vy"
    )

    // Danh sách lời mời kết bạn đang chờ nhận
    val pendingReceivedRequests = mutableListOf(
        "Nguyễn Thảo Ly",
        "Đặng Tuấn Kiệt"
    )

    // Danh sách bạn bè chính thức
    data class FriendItem(
        val name: String,
        val role: String,
        val subtitle: String,
        val isOnline: Boolean,
        val matchPercent: Int
    )

    val friendList = mutableListOf(
        FriendItem("Minh Quân", "Khoa học viễn tưởng", "Vừa xem: Hành Tinh Cát 2", true, 96),
        FriendItem("Thảo Vy", "Tình cảm, Hoạt hình", "Đang tìm bạn đi xem: Mai", true, 92),
        FriendItem("Hoàng Nam", "IMAX Fan", "Rảnh các tối Thứ 6 & Cuối tuần", true, 88),
        FriendItem("Phương Linh", "Kinh dị, Giật gân", "Hoạt động 2 giờ trước", false, 85)
    )

    fun isFriend(userName: String): Boolean {
        return friendList.any { it.name.equals(userName, ignoreCase = true) }
    }

    fun isRequestSent(userName: String): Boolean {
        if (isFriend(userName)) return false
        return sentFriendRequests.contains(userName)
    }

    fun sendFriendRequest(userName: String) {
        sentFriendRequests.add(userName)
        if (!sentRequestList.contains(userName)) {
            sentRequestList.add(0, userName)
        }
    }

    fun cancelFriendRequest(userName: String) {
        sentFriendRequests.remove(userName)
        sentRequestList.remove(userName)
    }

    fun acceptFriendRequest(userName: String) {
        pendingReceivedRequests.remove(userName)
        sentFriendRequests.remove(userName)
        sentRequestList.remove(userName)
        if (!isFriend(userName)) {
            friendList.add(
                0,
                FriendItem(
                    name = userName,
                    role = if (userName.contains("Ly")) "Anime, Tâm lý" else "Hành động, Marvel",
                    subtitle = "Vừa kết bạn mới",
                    isOnline = true,
                    matchPercent = 90
                )
            )
        }
    }

    fun declineFriendRequest(userName: String) {
        pendingReceivedRequests.remove(userName)
    }
}