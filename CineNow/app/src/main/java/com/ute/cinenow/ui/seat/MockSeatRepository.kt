package com.ute.cinenow.ui.seat

/**
 * Mock data phòng chiếu số 03 (IMAX 2D)
 * - 8 hàng A -> H, mỗi hàng 10 ghế (tổng 80 ghế)
 * - Hàng A, B, C: Ghế Thường (165.000đ) - Zone: FRONT
 * - Hàng D, E, F: Ghế VIP (210.000đ) - Zone: CENTER
 * - Hàng G: Ghế VIP (210.000đ) - Zone: BACK
 * - Hàng H: Ghế Thường / Ghế đôi (165.000đ) - Zone: COUPLE / BACK
 */
object MockSeatRepository {

    const val STANDARD_PRICE: Long = 165_000L
    const val VIP_PRICE: Long = 210_000L

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

    private fun generateSeats(): List<Seat> {
        val rows = listOf("A", "B", "C", "D", "E", "F", "G", "H")
        val seatList = mutableListOf<Seat>()

        for ((rowIndex, rowLetter) in rows.withIndex()) {
            val positionY = rowIndex + 1

            val (seatType, price, zone) = when (rowLetter) {
                "A", "B", "C" -> Triple(SeatType.STANDARD, STANDARD_PRICE, SeatZone.FRONT)
                "D", "E", "F" -> Triple(SeatType.VIP, VIP_PRICE, SeatZone.CENTER)
                "G" -> Triple(SeatType.VIP, VIP_PRICE, SeatZone.BACK)
                "H" -> Triple(SeatType.STANDARD, STANDARD_PRICE, SeatZone.COUPLE)
                else -> Triple(SeatType.STANDARD, STANDARD_PRICE, SeatZone.CENTER)
            }

            for (col in 1..10) {
                val seatId = "$rowLetter$col"

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