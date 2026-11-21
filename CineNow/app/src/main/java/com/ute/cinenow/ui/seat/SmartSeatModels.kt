package com.ute.cinenow.ui.seat

import java.io.Serializable

/**
 * Phân loại hạng ghế
 */
enum class SeatType(val displayName: String) : Serializable {
    STANDARD("Ghế thường"),
    VIP("Ghế VIP")
}

/**
 * Trạng thái ghế trên sơ đồ
 */
enum class SeatStatus : Serializable {
    AVAILABLE,  // Ghế trống có thể đặt
    BOOKED,     // Ghế đã có người mua
    BLOCKED     // Ghế bị khóa kỹ thuật / không khả dụng
}

/**
 * Khu vực ghế ngồi
 */
enum class SeatZone(val displayName: String) : Serializable {
    CENTER("Khu vực trung tâm"),
    FRONT("Gần màn hình"),
    BACK("Hàng ghế phía sau"),
    COUPLE("Ghế đôi"),
    ALL("Tất cả khu vực");

    companion object {
        fun fromDisplayName(name: String?): SeatZone {
            if (name == null) return CENTER
            return values().firstOrNull { it.displayName.equals(name, ignoreCase = true) }
                ?: when {
                    name.contains("trung tâm", true) -> CENTER
                    name.contains("màn hình", true) -> FRONT
                    name.contains("sau", true) -> BACK
                    name.contains("đôi", true) -> COUPLE
                    else -> CENTER
                }
        }
    }
}

/**
 * Lựa chọn ưu tiên loại ghế
 */
enum class SeatTypePreference(val displayName: String) : Serializable {
    ANY("Tất cả"),
    STANDARD("Ghế thường"),
    VIP("Ghế VIP")
}

/**
 * Tiêu chí tìm kiếm của người dùng
 */
data class SeatPreference(
    val numberOfPeople: Int = 1,
    val preferredZone: SeatZone = SeatZone.CENTER,
    val preferredSeatType: SeatTypePreference = SeatTypePreference.ANY,
    val avoidCorner: Boolean = false,
    val avoidFrontRows: Boolean = false
) : Serializable

/**
 * Thực thể ghế ngồi
 */
data class Seat(
    val seatId: String,             // Ví dụ: "F5"
    val row: String,                // Ví dụ: "F"
    val number: Int,                // Số ghế: 5
    val seatType: SeatType,         // VIP hoặc STANDARD
    val status: SeatStatus,         // AVAILABLE, BOOKED, BLOCKED
    val positionX: Int,             // Vị trí cột (1..10)
    val positionY: Int,             // Vị trí hàng (1=A .. 8=H)
    val zone: SeatZone,             // Khu vực ghế
    val adjacencyGroup: Int = 1,    // Nhóm liền kề thực tế trong hàng
    val price: Long                 // Giá ghế niêm yết
) : Serializable

/**
 * Kết quả gợi ý từ thuật toán SmartSeat
 */
data class SeatSuggestion(
    val seats: List<Seat> = emptyList(),
    val matchScore: Int = 0,                    // Điểm thực tế trên thang 100
    val matchPercentage: Int = 0,               // % Tương thích tính từ điểm thực tế
    val reason: String = "",                    // Lý do gợi ý
    val unmetCriteria: List<String> = emptyList(), // Tiêu chí chưa đáp ứng nếu có
    val totalPrice: Long = 0L,                  // Tổng tiền của nhóm ghế
    val isSuccess: Boolean = true               // Trạng thái tìm kiếm
) : Serializable