package com.ute.cinenow.ui.booking

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentBookingReviewBinding

class BookingReviewFragment : Fragment() {

    private var _binding: FragmentBookingReviewBinding? = null
    private val binding get() = _binding!!

    // Giá gốc sau khi trừ mã thành viên (-10%): 405.000 + 105.000 - 45.000 = 465.000đ
    private val subTotalAfterMember = 465_000
    private var currentVoucherIndex = 0
    private var selectedPaymentMethod = "Ví điện tử"

    private data class VoucherItem(val code: String, val desc: String, val discount: Int)

    private val vouchers = listOf(
        VoucherItem("CINEMAX20K", "Giảm 20.000 đ cho đơn từ 300.000đ", 20_000),
        VoucherItem("IMAX50K", "Giảm 50.000 đ cho vé định dạng IMAX", 50_000),
        VoucherItem("NEWUSER30K", "Ưu đãi thành viên mới giảm 30.000đ", 30_000)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gạch ngang giá gốc
        binding.tvOriginalPrice.paintFlags =
            binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        setupHeader()
        setupVoucherSwitcher()
        setupPaymentMethods()
        setupActionButtons()
        updateTotalPriceUI()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupVoucherSwitcher() {
        val switchAction = View.OnClickListener {
            currentVoucherIndex = (currentVoucherIndex + 1) % vouchers.size
            val v = vouchers[currentVoucherIndex]

            binding.tvSelectedVoucherCode.text = v.code
            binding.tvSelectedVoucherDesc.text = v.desc
            binding.tvVoucherLineName.text = "🎟️ Voucher ${v.code}"

            val formattedDiscount = String.format("-%,d đ", v.discount).replace(',', '.')
            binding.tvVoucherLineAmount.text = formattedDiscount
            binding.tvSavedNote.text = "✓ Tiết kiệm thêm ${String.format("%,d đ", v.discount).replace(',', '.')} cho đơn này"

            updateTotalPriceUI()
            Toast.makeText(requireContext(), "Đã áp dụng mã ${v.code}", Toast.LENGTH_SHORT).show()
        }

        binding.btnChangeVoucher.setOnClickListener(switchAction)
        binding.btnViewAllVouchers.setOnClickListener(switchAction)
    }

    private fun setupPaymentMethods() {
        binding.optEWallet.setOnClickListener {
            selectedPaymentMethod = "Ví điện tử"
            binding.optEWallet.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F5F3FF"))
            binding.radioEWallet.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))

            binding.optBankCard.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
            binding.radioBankCard.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
        }

        binding.optBankCard.setOnClickListener {
            selectedPaymentMethod = "Thẻ ngân hàng"
            binding.optBankCard.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F5F3FF"))
            binding.radioBankCard.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))

            binding.optEWallet.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
            binding.radioEWallet.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
        }
    }

    private fun updateTotalPriceUI() {
        val currentDiscount = vouchers[currentVoucherIndex].discount
        val finalTotal = subTotalAfterMember - currentDiscount
        val formattedTotal = String.format("%,d đ", finalTotal).replace(',', '.')

        binding.tvFinalTotalPrice.text = formattedTotal
        binding.btnBookNow.text = "✓  Đặt vé ngay • $formattedTotal"
    }

    private fun setupActionButtons() {
        binding.btnChangeSeats.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnBookNow.setOnClickListener {
            val totalStr = binding.tvFinalTotalPrice.text
            Toast.makeText(
                requireContext(),
                "Đặt vé thành công ($totalStr qua $selectedPaymentMethod)!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}