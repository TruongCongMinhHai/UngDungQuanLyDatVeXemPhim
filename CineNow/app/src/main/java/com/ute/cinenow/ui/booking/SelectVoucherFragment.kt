package com.ute.cinenow.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentSelectVoucherBinding

class SelectVoucherFragment : Fragment() {

    private var _binding: FragmentSelectVoucherBinding? = null
    private val binding get() = _binding!!

    // Trạng thái mã đang được chọn (mặc định ban đầu chọn CINEMAX20K giảm 20.000đ)
    private var selectedVoucherCode: String? = "CINEMAX20K"
    private var currentDiscountAmount = 20_000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupVoucherInteractions()
        updateVoucherUIStates()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnConfirmVoucher.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Đã áp dụng mã thành công! Tiết kiệm: ${binding.tvTotalSavings.text}",
                Toast.LENGTH_SHORT
            ).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupVoucherInteractions() {
        // Nút áp dụng mã thủ công
        binding.btnApplyCode.setOnClickListener {
            val code = binding.etPromoCode.text.toString().trim().uppercase()
            if (code.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập mã khuyến mãi!", Toast.LENGTH_SHORT).show()
            } else if (code == "CINEMAX20K" || code == "CINEVIP15" || code == "COMBOMAX30") {
                selectedVoucherCode = code
                currentDiscountAmount = when (code) {
                    "CINEMAX20K" -> 20_000
                    "CINEVIP15" -> 35_000
                    else -> 30_000
                }
                updateVoucherUIStates()
                Toast.makeText(requireContext(), "Áp dụng mã $code thành công!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Mã giảm giá không hợp lệ hoặc hết hạn!", Toast.LENGTH_SHORT).show()
            }
        }

        // Voucher 1: CINEMAX20K
        binding.btnActionVoucher1.setOnClickListener {
            if (selectedVoucherCode == "CINEMAX20K") {
                selectedVoucherCode = null
                currentDiscountAmount = 0
            } else {
                selectedVoucherCode = "CINEMAX20K"
                currentDiscountAmount = 20_000
            }
            updateVoucherUIStates()
        }

        // Voucher 2: CINEVIP15
        binding.btnActionVoucher2.setOnClickListener {
            if (selectedVoucherCode == "CINEVIP15") {
                selectedVoucherCode = null
                currentDiscountAmount = 0
            } else {
                selectedVoucherCode = "CINEVIP15"
                currentDiscountAmount = 35_000
            }
            updateVoucherUIStates()
        }

        // Voucher 3: COMBOMAX30
        binding.btnActionVoucher3.setOnClickListener {
            if (selectedVoucherCode == "COMBOMAX30") {
                selectedVoucherCode = null
                currentDiscountAmount = 0
            } else {
                selectedVoucherCode = "COMBOMAX30"
                currentDiscountAmount = 30_000
            }
            updateVoucherUIStates()
        }
    }

    private fun updateVoucherUIStates() {
        // Cập nhật trạng thái Card 1 (CINEMAX20K)
        if (selectedVoucherCode == "CINEMAX20K") {
            binding.btnActionVoucher1.text = "Bỏ chọn"
            binding.btnActionVoucher1.setBackgroundColor(android.graphics.Color.parseColor("#F1F5F9"))
        } else {
            binding.btnActionVoucher1.text = "Áp dụng"
        }

        // Cập nhật trạng thái Card 2 (CINEVIP15)
        if (selectedVoucherCode == "CINEVIP15") {
            binding.btnActionVoucher2.text = "Bỏ chọn"
        } else {
            binding.btnActionVoucher2.text = "Áp dụng"
        }

        // Cập nhật trạng thái Card 3 (COMBOMAX30)
        if (selectedVoucherCode == "COMBOMAX30") {
            binding.btnActionVoucher3.text = "Bỏ chọn"
        } else {
            binding.btnActionVoucher3.text = "Áp dụng"
        }

        // Cập nhật tổng tiền tiết kiệm ở đáy
        val formattedSavings = String.format("%,d đ", currentDiscountAmount).replace(',', '.')
        binding.tvTotalSavings.text = formattedSavings
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}