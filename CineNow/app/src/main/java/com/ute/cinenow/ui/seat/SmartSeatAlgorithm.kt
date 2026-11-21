package com.ute.cinenow.ui.seat

import kotlin.math.abs

/**
 * Thuật toán SmartSeat độc lập với Android UI
 * Thang điểm 100:
 * - Đúng khu vực mong muốn: max 40đ
 * - Gần trung tâm màn hình: max 30đ
 * - Không sát góc: max 15đ
 * - Không quá gần màn hình: max 15đ
 */
object SmartSeatAlgorithm {

    private data class ScoredCandidate(
        val seats: List<Seat>,
        val score: Int,
        val colOffset: Double,
        val unmet: List<String>
    )

    fun findBestSeats(preference: SeatPreference, allSeats: List<Seat>): SeatSuggestion {
        val peopleCount = preference.numberOfPeople.coerceIn(1, 6)

        // 1. Lọc ghế AVAILABLE
        val availableSeats = allSeats.filter { it.status == SeatStatus.AVAILABLE }

        // 2. Lọc theo loại ghế bắt buộc
        val candidatePool = when (preference.preferredSeatType) {
            SeatTypePreference.VIP -> availableSeats.filter { it.seatType == SeatType.VIP }
            SeatTypePreference.STANDARD -> availableSeats.filter { it.seatType == SeatType.STANDARD }
            SeatTypePreference.ANY -> availableSeats
        }

        // 3. Tìm các nhóm N ghế liền kề
        val candidateGroups = if (peopleCount == 1) {
            candidatePool.map { listOf(it) }
        } else {
            findConsecutiveSeatGroups(candidatePool, peopleCount, preference.preferredSeatType)
        }

        // Nếu không có nhóm ghế thỏa mãn
        if (candidateGroups.isEmpty()) {
            val typeText = when (preference.preferredSeatType) {
                SeatTypePreference.VIP -> "ghế VIP"
                SeatTypePreference.STANDARD -> "ghế thường"
                SeatTypePreference.ANY -> "ghế"
            }
            return SeatSuggestion(
                seats = emptyList(),
                matchScore = 0,
                matchPercentage = 0,
                reason = "Không còn đủ $peopleCount $typeText liền nhau phù hợp yêu cầu. Bạn có thể chọn khu vực khác hoặc tự chọn ghế trên sơ đồ.",
                unmetCriteria = listOf("Không đủ ghế trống liền nhau thỏa mãn số lượng và loại ghế"),
                totalPrice = 0L,
                isSuccess = false
            )
        }

        // 4. Chấm điểm từng ứng viên
        val scoredList = candidateGroups.map { group ->
            scoreCandidate(group, preference)
        }

        // 5. Chọn ứng viên có điểm cao nhất (ưu tiên độ cân đối trung tâm)
        val bestCandidate = scoredList.minWithOrNull(
            compareByDescending<ScoredCandidate> { it.score }
                .thenBy { it.colOffset }
                .thenBy { candidateEyeLevelPriority(it.seats.first().row) }
        ) ?: return SeatSuggestion(isSuccess = false, reason = "Không tìm thấy phương án phù hợp")

        val matchPercent = bestCandidate.score.coerceIn(0, 100)
        val selectedSeats = bestCandidate.seats
        val totalPrice = selectedSeats.sumOf { it.price }
        val reasonText = buildReasonText(bestCandidate, preference)

        return SeatSuggestion(
            seats = selectedSeats,
            matchScore = bestCandidate.score,
            matchPercentage = matchPercent,
            reason = reasonText,
            unmetCriteria = bestCandidate.unmet,
            totalPrice = totalPrice,
            isSuccess = true
        )
    }

    private fun findConsecutiveSeatGroups(
        pool: List<Seat>,
        peopleCount: Int,
        seatTypePref: SeatTypePreference
    ): List<List<Seat>> {
        val groups = mutableListOf<List<Seat>>()
        val byRow = pool.groupBy { it.row }

        for ((_, rowSeats) in byRow) {
            val sorted = rowSeats.sortedBy { it.number }
            if (sorted.size < peopleCount) continue

            for (i in 0..(sorted.size - peopleCount)) {
                val sub = sorted.subList(i, i + peopleCount)

                // Kiểm tra liền kề
                var isConsecutive = true
                for (j in 0 until sub.size - 1) {
                    val current = sub[j]
                    val next = sub[j + 1]
                    if (next.number != current.number + 1 || next.adjacencyGroup != current.adjacencyGroup) {
                        isConsecutive = false
                        break
                    }
                }

                if (!isConsecutive) continue

                // Nếu người dùng chọn riêng một loại ghế thì không được trộn
                if (seatTypePref == SeatTypePreference.VIP && sub.any { it.seatType != SeatType.VIP }) {
                    continue
                }
                if (seatTypePref == SeatTypePreference.STANDARD && sub.any { it.seatType != SeatType.STANDARD }) {
                    continue
                }

                groups.add(sub)
            }
        }
        return groups
    }

    private fun scoreCandidate(seats: List<Seat>, preference: SeatPreference): ScoredCandidate {
        val unmet = mutableListOf<String>()

        // 1. Đúng khu vực mong muốn (Tối đa 40 điểm)
        val firstSeat = seats.first()
        val zoneScore = when {
            preference.preferredZone == SeatZone.ALL -> 40
            firstSeat.zone == preference.preferredZone -> 40
            preference.preferredZone == SeatZone.CENTER && (firstSeat.zone == SeatZone.BACK || firstSeat.zone == SeatZone.FRONT) -> {
                unmet.add("Khu vực ${firstSeat.zone.displayName} thay vì ${preference.preferredZone.displayName}")
                20
            }
            preference.preferredZone == SeatZone.COUPLE && firstSeat.zone == SeatZone.BACK -> {
                unmet.add("Hàng ghế sau thay vì khu vực ghế đôi")
                25
            }
            else -> {
                unmet.add("Đổi sang khu vực ${firstSeat.zone.displayName}")
                10
            }
        }

        // 2. Vị trí gần trung tâm màn hình (Tối đa 30 điểm)
        val avgCol = (seats.first().positionX + seats.last().positionX) / 2.0
        val colOffset = abs(avgCol - 5.5)

        val colScore = when {
            colOffset <= 0.6 -> 20
            colOffset <= 1.5 -> 16
            colOffset <= 2.5 -> 12
            colOffset <= 3.5 -> 7
            else -> 3
        }

        val rowScore = when (firstSeat.row) {
            "F", "E" -> 10
            "D", "G" -> 8
            "C", "H" -> 6
            "B" -> 4
            "A" -> 2
            else -> 5
        }
        val centerScore = colScore + rowScore

        // 3. Không sát góc (Tối đa 15 điểm)
        val hasCornerSeat = seats.any { it.positionX <= 2 || it.positionX >= 9 }
        val cornerScore = if (!hasCornerSeat) {
            15
        } else {
            if (preference.avoidCorner) {
                unmet.add("Chưa tránh được vị trí sát góc do số ghế trống trung tâm có hạn")
                0
            } else {
                8
            }
        }

        // 4. Không quá gần màn hình (Tối đa 15 điểm)
        val isFrontRow = seats.any { it.row in listOf("A", "B") }
        val frontScore = if (!isFrontRow) {
            15
        } else {
            if (preference.avoidFrontRows) {
                unmet.add("Chưa tránh được hàng ghế gần màn hình do các hàng sau đã kín chỗ")
                0
            } else {
                6
            }
        }

        val totalScore = (zoneScore + centerScore + cornerScore + frontScore).coerceIn(0, 100)

        return ScoredCandidate(
            seats = seats,
            score = totalScore,
            colOffset = colOffset,
            unmet = unmet
        )
    }

    private fun candidateEyeLevelPriority(row: String): Int {
        return when (row) {
            "F" -> 1
            "E" -> 2
            "D" -> 3
            "G" -> 4
            "C" -> 5
            "B" -> 6
            "H" -> 7
            "A" -> 8
            else -> 9
        }
    }

    private fun buildReasonText(candidate: ScoredCandidate, preference: SeatPreference): String {
        val count = candidate.seats.size
        val row = candidate.seats.first().row
        val seatTypes = candidate.seats.map { it.seatType }.distinct()
        val typeStr = when {
            seatTypes.size > 1 -> "VIP & Thường"
            seatTypes.first() == SeatType.VIP -> "ghế VIP"
            else -> "ghế thường"
        }

        val baseReason = "Thỏa mãn $count $typeStr liền nhau tại dãy $row (${candidate.seats.first().zone.displayName}), góc nhìn cân đối."
        return if (candidate.unmet.isNotEmpty()) {
            "$baseReason (Lưu ý: ${candidate.unmet.first()})"
        } else {
            baseReason
        }
    }
}