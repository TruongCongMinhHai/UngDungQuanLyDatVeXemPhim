package com.ute.cinenow.ui.seat

/**
 * Mock data phòng chiếu số 03 (IMAX 2D)
 * - 8 hàng A -> H, mỗi hàng 10 ghế (tổng 80 ghế)
 * - GHẾ VIP: Chỉ gồm các ghế D4..D7, E4..E7, F4..F7, G4..G7 (Giá 210.000đ)
 * - GHẾ THƯỜNG: Tất cả các ghế còn lại (Giá 165.000đ)
 */
object MockSeatRepository {

    const val STANDARD_PRICE: Long = 165_000L
    const val VIP_PRICE: Long = 210_000L

    // Tập hợp 16 ghế VIP chính xác theo sơ đồ thiết kế
    private val VIP_SEAT_IDS = setOf(
        "D4", "D5", "D6", "D7",
        "E4", "E5", "E6", "E7",
        "F4", "F5", "F6", "F7",
        "G4", "G5", "G6", "G7"
    )

    // Ghế đã đặt (BOOKED) - màu xám có dấu ×
    private val BOOKED_SEAT_IDS = setOf(
        "A4", "A5", "B1", "B2", "B4", "B5", "B9",
        "C5", "D3", "D8", "E4", "E5", "E6",
        "F4", "G7"
    )

    // Ghế tạm khóa kỹ thuật (BLOCKED)
    private val BLOCKED_SEAT_IDS = setOf(
        "H10"
    )

    private val cachedSeats: List<Seat> by lazy {
        generateSeats()
    }

    fun getSeats(): List<Seat> = cachedSeats

    fun findSeat(seatId: String): Seat? {
        return cachedSeats.firstOrNull { it.seatId.equals(seatId, ignoreCase = true) }
    }

    fun isVipSeat(seatId: String): Boolean {
        return VIP_SEAT_IDS.contains(seatId)
    }

    private fun generateSeats(): List<Seat> {
        val rows = listOf("A", "B", "C", "D", "E", "F", "G", "H")
        val seatList = mutableListOf<Seat>()

        for ((rowIndex, rowLetter) in rows.withIndex()) {
            val positionY = rowIndex + 1

            for (col in 1..10) {
                val seatId = "$rowLetter$col"

                val isVip = VIP_SEAT_IDS.contains(seatId)
                val seatType = if (isVip) SeatType.VIP else SeatType.STANDARD
                val price = if (isVip) VIP_PRICE else STANDARD_PRICE

                val zone = when (rowLetter) {
                    "A", "B", "C" -> SeatZone.FRONT
                    "D", "E", "F" -> SeatZone.CENTER
                    "G" -> SeatZone.BACK
                    "H" -> SeatZone.COUPLE
                    else -> SeatZone.CENTER
                }

                val status = when {
                    BOOKED_SEAT_IDS.contains(seatId) -> SeatStatus.BOOKED
                    BLOCKED_SEAT_IDS.contains(seatId) -> SeatStatus.BLOCKED
                    else -> SeatStatus.AVAILABLE
                }

                seatList.add(
                    Seat(
                        seatId = seatId,
                        row = rowLetter,
                        number = col,
                        seatType = seatType,
                        status = status,
                        positionX = col,
                        positionY = positionY,
                        zone = zone,
                        adjacencyGroup = 1,
                        price = price
                    )
                )
            }
        }
        return seatList
    }
}