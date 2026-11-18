package com.ute.cinenow.ui.seat

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentSeatSelectionBinding

class SeatSelectionFragment : Fragment() {

    private var _binding: FragmentSeatSelectionBinding? = null
    private val binding get() = _binding!!

    // Lưu danh sách các ghế đang được chọn (Mặc định gợi ý F5, F6, F7)
    private val selectedSeats = mutableSetOf("F5", "F6", "F7")
    private val seatPrice = 165_000 // Giá vé 1 ghế IMAX

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeatSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupSeatClickListeners()
        updateCheckoutSummary()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnResetSeats.setOnClickListener {
            selectedSeats.clear()
            selectedSeats.addAll(listOf("F5", "F6", "F7"))
            refreshSeatUI()
            Toast.makeText(requireContext(), "Đã khôi phục ghế gợi ý SmartSeat", Toast.LENGTH_SHORT).show()
        }

        binding.btnContinueCheckout.setOnClickListener {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn ít nhất 1 ghế!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Thành công! Đang chuyển sang thanh toán ${selectedSeats.joinToString()}...",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupSeatClickListeners() {
        // Danh sách các ghế người dùng có thể bấm chọn/hủy chọn thủ công
        val seatMap = mapOf(
            "F5" to binding.seatF5, "F6" to binding.seatF6, "F7" to binding.seatF7,
            "F1" to binding.seatF1, "F2" to binding.seatF2, "F3" to binding.seatF3, "F8" to binding.seatF8, "F9" to binding.seatF9, "F10" to binding.seatF10,
            "C1" to binding.seatC1, "C2" to binding.seatC2, "C3" to binding.seatC3, "C4" to binding.seatC4, "C6" to binding.seatC6, "C7" to binding.seatC7
        )

        seatMap.forEach { (seatName, seatView) ->
            seatView.setOnClickListener {
                if (selectedSeats.contains(seatName)) {
                    // Nếu ghế đang chọn thì bỏ chọn
                    selectedSeats.remove(seatName)
                } else {
                    // Nếu chưa chọn thì thêm vào
                    selectedSeats.add(seatName)
                }
                refreshSeatUI()
            }
        }
    }

    private fun refreshSeatUI() {
        val seatMap = mapOf(
            "F5" to binding.seatF5, "F6" to binding.seatF6, "F7" to binding.seatF7,
            "F1" to binding.seatF1, "F2" to binding.seatF2, "F3" to binding.seatF3, "F8" to binding.seatF8, "F9" to binding.seatF9, "F10" to binding.seatF10,
            "C1" to binding.seatC1, "C2" to binding.seatC2, "C3" to binding.seatC3, "C4" to binding.seatC4, "C6" to binding.seatC6, "C7" to binding.seatC7
        )

        seatMap.forEach { (seatName, seatView) ->
            if (selectedSeats.contains(seatName)) {
                seatView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
                seatView.setTextColor(Color.WHITE)
            } else {
                seatView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F3F0FF"))
                seatView.setTextColor(Color.parseColor("#312E81"))
            }
        }

        updateCheckoutSummary()
    }

    private fun updateCheckoutSummary() {
        if (selectedSeats.isEmpty()) {
            binding.tvSelectedSeatSummary.text = "• Chưa chọn ghế nào"
            binding.tvSmartSeatReason.text = "⚠️ Vui lòng chọn ghế trên sơ đồ phòng chiếu."
            binding.tvTotalPrice.text = "0đ"
        } else {
            val sortedSeats = selectedSeats.sorted().joinToString(", ")
            binding.tvSelectedSeatSummary.text = "• Đang chọn: $sortedSeats"
            binding.tvSmartSeatReason.text = "✓ Thỏa mãn: ${selectedSeats.size} ghế đã chọn tại phòng chiếu IMAX."

            val totalPrice = selectedSeats.size * seatPrice
            val formattedPrice = String.format("%,dđ", totalPrice).replace(',', '.')
            binding.tvTotalPrice.text = formattedPrice
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}