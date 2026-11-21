package com.ute.cinenow.ui.booking

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ute.cinenow.R
import com.ute.cinenow.databinding.BottomSheetPaymentConfirmBinding
import java.text.NumberFormat
import java.util.Locale

/**
 * Màn hình "Xác nhận thanh toán" (Bottom Sheet) – hiện khi bấm "Đặt vé ngay" ở màn Xác nhận đặt vé.
 * Hiển thị đúng phương thức người dùng đã chọn (Ví điện tử / Thẻ ngân hàng) và tổng tiền.
 * Bấm "Xác nhận thanh toán" thì gửi kết quả [RESULT_CONFIRMED] về màn gọi nó.
 */
class PaymentConfirmBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPaymentConfirmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPaymentConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val method = arguments?.getString(ARG_METHOD) ?: METHOD_EWALLET
        val total = arguments?.getLong(ARG_TOTAL, 0L) ?: 0L
        val amount = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(total)

        // Phương thức thanh toán: hiện đúng theo lựa chọn ở màn trước
        binding.tvMethodValue.text = method
        binding.imgMethodIcon.setImageResource(
            if (method == METHOD_BANK_CARD) R.drawable.ic_booking_card else R.drawable.ic_booking_wallet
        )

        // Số tiền to, ký hiệu "đ" nhỏ hơn
        val amountText = SpannableString("$amount đ")
        amountText.setSpan(
            RelativeSizeSpan(0.5f), amount.length, amountText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvTotalAmount.text = amountText
        binding.btnConfirmPayment.text = "Xác nhận thanh toán · $amount đ"

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnBackEdit.setOnClickListener { dismiss() }
        binding.btnConfirmPayment.setOnClickListener {
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_CONFIRMED to true))
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        // Để nền bo góc của layout hiển thị đúng, và mở sheet đầy đủ ngay từ đầu
        (requireView().parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PaymentConfirmBottomSheet"
        const val REQUEST_KEY = "payment_confirm_request"
        const val RESULT_CONFIRMED = "payment_confirmed"

        const val METHOD_EWALLET = "Ví điện tử"
        const val METHOD_BANK_CARD = "Thẻ ngân hàng"

        private const val ARG_METHOD = "arg_payment_method"
        private const val ARG_TOTAL = "arg_payment_total"

        fun newInstance(method: String, total: Long) = PaymentConfirmBottomSheet().apply {
            arguments = bundleOf(ARG_METHOD to method, ARG_TOTAL to total)
        }
    }
}
