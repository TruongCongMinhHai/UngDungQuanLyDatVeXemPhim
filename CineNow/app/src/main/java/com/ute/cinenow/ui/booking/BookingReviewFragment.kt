package com.ute.cinenow.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private var paymentMethod: String = "Ví điện tử"
    private val baseSubtotal: Long = 510_000L
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

        observeVoucherResult()
        updatePaymentSummary()
        setupPaymentSelection()
        setupActions()
    }

    private fun observeVoucherResult() {
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

    private fun setupPaymentSelection() {
        binding.optEWallet.setOnClickListener {
            paymentMethod = "Ví điện tử"
            binding.radioEWallet.backgroundTintList =
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#5850EC"))
            binding.radioBankCard.backgroundTintList =
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#CBD5E1"))
            showCineMessage("Đã chọn phương thức thanh toán", subtitle = paymentMethod)
        }

        binding.optBankCard.setOnClickListener {
            paymentMethod = "Thẻ ngân hàng"
            binding.radioBankCard.backgroundTintList =
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#5850EC"))
            binding.radioEWallet.backgroundTintList =
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#CBD5E1"))
            showCineMessage("Đã chọn phương thức thanh toán", subtitle = paymentMethod)
        }
    }

    private fun setupActions() {
        binding.btnViewAllVouchers.setOnClickListener {
            findNavController().navigate(
                R.id.selectVoucherFragment,
                bundleOf(MovieDetailFragment.ARG_MOVIE_TITLE to movieTitle)
            )
        }

        binding.btnChangeVoucher.setOnClickListener {
            findNavController().navigate(
                R.id.selectVoucherFragment,
                bundleOf(MovieDetailFragment.ARG_MOVIE_TITLE to movieTitle)
            )
        }

        binding.btnBookNow.setOnClickListener {
            showCineMessage("Đang xử lý đặt vé", subtitle = "$movieTitle • $paymentMethod")
            findNavController().navigate(
                R.id.bookingSuccessFragment,
                bundleOf(MovieDetailFragment.ARG_MOVIE_TITLE to movieTitle)
            )
        }

        binding.btnChangeSeats.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}