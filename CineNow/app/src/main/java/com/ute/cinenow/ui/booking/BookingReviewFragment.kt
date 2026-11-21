package com.ute.cinenow.ui.booking

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentBookingReviewBinding
import com.ute.cinenow.ui.movie.MovieDetailFragment
import com.ute.cinenow.ui.movie.MovieDetailSamples
import com.ute.cinenow.utils.showCineMessage
import java.text.NumberFormat
import java.util.Locale

class BookingReviewFragment : Fragment() {

    private var _binding: FragmentBookingReviewBinding? = null
    private val binding get() = _binding!!

    private var movieTitle: String = "Hành Tinh Cát 2"
    private var voucherCode: String = "CINEMAX20K"
    private var voucherDiscount: Long = 20_000L
    private var paymentMethod: String = PaymentConfirmBottomSheet.METHOD_EWALLET
    private var baseSubtotal: Long = 510_000L
    private val memberDiscount: Long = 45_000L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val passedTitle = arguments?.getString(MovieDetailFragment.ARG_MOVIE_TITLE)
        val movie = MovieDetailSamples.find(passedTitle)
        movieTitle = movie.title
        binding.imgMoviePoster.setImageResource(movie.posterRes)

        // Nhận danh sách ghế và tổng tiền từ màn SeatSelectionFragment
        val selectedSeats = arguments?.getStringArrayList("arg_selected_seats")
        val passedPrice = arguments?.getLong("arg_total_price", 0L) ?: 0L
        if (passedPrice > 0L) {
            baseSubtotal = passedPrice
        }
        if (!selectedSeats.isNullOrEmpty()) {
            binding.tvTicketSeats.text = selectedSeats.joinToString(", ")
        }

        observeVoucherResult()
        updatePaymentSummary()
        setupPaymentSelection()
        setupActions()
        observePaymentConfirm()
    }

    private fun observeVoucherResult() {
        // Nhận voucher qua savedStateHandle (Navigation Component)
        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<String>("selected_voucher_code")
            ?.observe(viewLifecycleOwner) { code ->
                voucherCode = code.orEmpty()
                updatePaymentSummary()
            }
        savedStateHandle?.getLiveData<Long>("selected_voucher_discount")
            ?.observe(viewLifecycleOwner) { discount ->
                voucherDiscount = discount ?: 0L
                updatePaymentSummary()
            }

        // Dự phòng nhận voucher qua FragmentResult
        parentFragmentManager.setFragmentResultListener("voucher_result", viewLifecycleOwner) { _, bundle ->
            val code = bundle.getString("selected_voucher_code", "")
            val discount = bundle.getLong("selected_voucher_discount", 0L)
            voucherCode = code
            voucherDiscount = discount
            updatePaymentSummary()
        }
    }

    private fun formatCurrencyVnd(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return "${formatter.format(amount)} đ"
    }

    private fun updatePaymentSummary() {
        val total = (baseSubtotal - memberDiscount - voucherDiscount).coerceAtLeast(0L)
        val totalText = formatCurrencyVnd(total)
        val discountText = formatCurrencyVnd(voucherDiscount)

        binding.tvOriginalPrice.text = formatCurrencyVnd(baseSubtotal)
        binding.tvFinalTotalPrice.text = totalText
        binding.btnBookNow.text = "Đặt vé ngay • $totalText"

        if (voucherCode.isNotEmpty() && voucherDiscount > 0L) {
            binding.rowVoucherDiscount.visibility = View.VISIBLE
            binding.tvVoucherLineName.text = "Voucher $voucherCode"
            binding.tvVoucherLineAmount.text = "-$discountText"
            binding.tvSelectedVoucherCode.text = voucherCode
            binding.tvSelectedVoucherDesc.text = "Giảm $discountText cho đơn hàng"
            binding.tvSavedNote.text = "Tiết kiệm thêm $discountText cho đơn này"
            binding.tvPromoCountBadge.text = "2 ưu đãi áp dụng"
        } else {
            binding.rowVoucherDiscount.visibility = View.GONE
            binding.tvSelectedVoucherCode.text = "Chưa chọn mã"
            binding.tvSelectedVoucherDesc.text = "Nhấn Đổi mã hoặc Xem tất cả ưu đãi"
            binding.tvSavedNote.text = "Chưa áp dụng mã giảm giá thêm"
            binding.tvPromoCountBadge.text = "1 ưu đãi áp dụng"
        }
    }

    // --- Chọn phương thức thanh toán: mục được chọn đổi màu (nền, viền, chấm tròn, icon) ---
    private fun setupPaymentSelection() {
        applyPaymentStyle()

        binding.optEWallet.setOnClickListener {
            selectPayment(PaymentConfirmBottomSheet.METHOD_EWALLET)
        }
        binding.optBankCard.setOnClickListener {
            selectPayment(PaymentConfirmBottomSheet.METHOD_BANK_CARD)
        }
    }

    private fun selectPayment(method: String) {
        if (paymentMethod == method) return
        paymentMethod = method
        applyPaymentStyle()
        showCineMessage("Đã chọn phương thức thanh toán", subtitle = paymentMethod)
    }

    private fun applyPaymentStyle() {
        val isEWallet = paymentMethod == PaymentConfirmBottomSheet.METHOD_EWALLET
        styleOption(binding.optEWallet, binding.radioEWallet, binding.imgEWallet, isEWallet)
        styleOption(binding.optBankCard, binding.radioBankCard, binding.imgBankCard, !isEWallet)
    }

    private fun styleOption(option: View, radio: View, icon: ImageView, selected: Boolean) {
        option.isSelected = selected   // bg_payment_option: nền tím nhạt + viền tím khi được chọn
        radio.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(if (selected) "#5850EC" else "#CBD5E1"))
        icon.imageTintList =
            ColorStateList.valueOf(Color.parseColor(if (selected) "#5850EC" else "#475569"))
    }

    // --- Màn hình Xác nhận thanh toán (Bottom Sheet) ---
    private fun currentTotal(): Long =
        (baseSubtotal - memberDiscount - voucherDiscount).coerceAtLeast(0L)

    private fun showPaymentConfirm() {
        if (childFragmentManager.findFragmentByTag(PaymentConfirmBottomSheet.TAG) != null) return
        PaymentConfirmBottomSheet.newInstance(paymentMethod, currentTotal())
            .show(childFragmentManager, PaymentConfirmBottomSheet.TAG)
    }

    private fun observePaymentConfirm() {
        childFragmentManager.setFragmentResultListener(
            PaymentConfirmBottomSheet.REQUEST_KEY, viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean(PaymentConfirmBottomSheet.RESULT_CONFIRMED, false)) {
                completeBooking()
            }
        }
    }

    private fun completeBooking() {
        showCineMessage("Đang xử lý đặt vé", subtitle = "$movieTitle • $paymentMethod")
        findNavController().navigate(
            R.id.bookingSuccessFragment,
            bundleOf(MovieDetailFragment.ARG_MOVIE_TITLE to movieTitle)
        )
    }

    private fun handleBackToSeats() {
        // 1. Thử pop qua Navigation Component
        val poppedNav = try { findNavController().popBackStack() } catch (_: Exception) { false }
        if (!poppedNav) {
            // 2. Thử pop qua FragmentManager
            val poppedFrag = try {
                if (parentFragmentManager.backStackEntryCount > 0) {
                    parentFragmentManager.popBackStack()
                    true
                } else false
            } catch (_: Exception) { false }

            // 3. Nếu cả 2 đều không pop được thì navigate trực tiếp về SeatSelectionFragment
            if (!poppedFrag) {
                try {
                    findNavController().navigate(R.id.seatSelectionFragment)
                } catch (_: Exception) {}
            }
        }
    }

    private fun setupActions() {
        binding.header.onBackClick = { handleBackToSeats() }

        val openVoucher = {
            val bundle = bundleOf(
                MovieDetailFragment.ARG_MOVIE_TITLE to movieTitle,
                "arg_subtotal" to baseSubtotal,
                "current_voucher_code" to voucherCode
            )
            findNavController().navigate(R.id.selectVoucherFragment, bundle)
        }

        binding.btnViewAllVouchers.setOnClickListener { openVoucher() }
        binding.btnChangeVoucher.setOnClickListener { openVoucher() }

        // Bấm "Đặt vé ngay" -> mở màn Xác nhận thanh toán
        binding.btnBookNow.setOnClickListener { showPaymentConfirm() }

        binding.btnChangeSeats.setOnClickListener {
            handleBackToSeats()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
