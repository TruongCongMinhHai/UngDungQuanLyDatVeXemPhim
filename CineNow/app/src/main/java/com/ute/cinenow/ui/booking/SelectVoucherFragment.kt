package com.ute.cinenow.ui.booking

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentSelectVoucherBinding
import com.ute.cinenow.ui.movie.MovieDetailFragment
import java.text.NumberFormat
import java.util.Locale

class SelectVoucherFragment : Fragment() {

    private var _binding: FragmentSelectVoucherBinding? = null
    private val binding get() = _binding!!

    private var selectedCode: String = "CINEMAX20K"
    private var discountAmount: Long = 20_000L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val passedCode = arguments?.getString("current_voucher_code")
        if (!passedCode.isNullOrEmpty()) {
            selectedCode = passedCode
            discountAmount = discountForCode(passedCode)
        }

        val passedSubtotal = arguments?.getLong("arg_subtotal", 465_000L) ?: 465_000L
        binding.tvOrderSubtotal.text = formatCurrencyVnd(passedSubtotal)

        updateSavingsDisplay()
        updateVoucherButtonsUI()
        setupListeners()
    }

    private fun discountForCode(code: String): Long {
        return when (code.uppercase(Locale.ROOT)) {
            "CINEMAX20K" -> 20_000L
            "CINEVIP15" -> 40_000L
            "COMBOMAX30" -> 30_000L
            else -> 20_000L
        }
    }

    private fun formatCurrencyVnd(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return "${formatter.format(amount)} đ"
    }

    private fun updateSavingsDisplay() {
        binding.tvTotalSavings.text = formatCurrencyVnd(discountAmount)
    }

    private fun updateVoucherButtonsUI() {
        val isV1 = (selectedCode == "CINEMAX20K" && discountAmount > 0L)
        binding.btnActionVoucher1.apply {
            text = if (isV1) "Bỏ chọn" else "Áp dụng"
            backgroundTintList = ColorStateList.valueOf(
                Color.parseColor(if (isV1) "#F1F5F9" else "#EEF2FF")
            )
            setTextColor(Color.parseColor(if (isV1) "#475569" else "#4F46E5"))
        }
        binding.indicatorVoucher1.visibility = if (isV1) View.VISIBLE else View.INVISIBLE
        binding.tagApplying1.visibility = if (isV1) View.VISIBLE else View.GONE

        val isV2 = (selectedCode == "CINEVIP15" && discountAmount > 0L)
        binding.btnActionVoucher2.apply {
            text = if (isV2) "Bỏ chọn" else "Áp dụng"
            backgroundTintList = ColorStateList.valueOf(
                Color.parseColor(if (isV2) "#F1F5F9" else "#EEF2FF")
            )
            setTextColor(Color.parseColor(if (isV2) "#475569" else "#4F46E5"))
        }

        val isV3 = (selectedCode == "COMBOMAX30" && discountAmount > 0L)
        binding.btnActionVoucher3.apply {
            text = if (isV3) "Bỏ chọn" else "Áp dụng"
            backgroundTintList = ColorStateList.valueOf(
                Color.parseColor(if (isV3) "#F1F5F9" else "#EEF2FF")
            )
            setTextColor(Color.parseColor(if (isV3) "#475569" else "#4F46E5"))
        }
    }

    /**
     * Hiển thị thông báo Snackbar nhưng neo trực tiếp vào `binding.cardBottomAction`
     * (thanh xác nhận đáy), đảm bảo thông báo nổi cao hơn và KHÔNG BAO GIỜ CHE MẤT "Xác nhận ưu đãi".
     * Hoàn toàn không cần sửa file CineMessage.kt.
     */
    private fun showElevatedMessage(
        message: String,
        subtitle: String? = null,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        val act = activity ?: return
        val density = resources.displayMetrics.density

        val text = SpannableStringBuilder(message)
        if (!subtitle.isNullOrBlank()) {
            text.setSpan(StyleSpan(Typeface.BOLD), 0, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.setSpan(AbsoluteSizeSpan(15, true), 0, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            val subtitleStart = text.length + 1
            text.append("\n").append(subtitle)
            text.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(act, R.color.cinenow_snackbar_subtitle)),
                subtitleStart, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text.setSpan(AbsoluteSizeSpan(13, true), subtitleStart, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val snackbar = Snackbar.make(binding.root, text, 3000)
        snackbar.view.background = ContextCompat.getDrawable(act, R.drawable.bg_cine_snackbar)
        snackbar.setTextColor(ContextCompat.getColor(act, R.color.cinenow_white))

        val textView = snackbar.view.findViewById<TextView>(
            com.google.android.material.R.id.snackbar_text
        )
        val logoSize = (28 * density).toInt()
        val logo = ContextCompat.getDrawable(act, R.drawable.ic_cinenow_logo)?.mutate()
        logo?.setBounds(0, 0, logoSize, logoSize)
        textView.setCompoundDrawablesRelative(logo, null, null, null)
        textView.compoundDrawablePadding = (12 * density).toInt()
        textView.maxLines = 3

        if (actionLabel != null && onAction != null) {
            snackbar.setAction(actionLabel) { onAction() }
            snackbar.setActionTextColor(ContextCompat.getColor(act, R.color.cinenow_snackbar_action))
        }

        // Neo ngay trên thanh "Xác nhận ưu đãi"
        snackbar.setAnchorView(binding.cardBottomAction)

        snackbar.show()
    }

    private fun setupListeners() {
        binding.header.onBackClick = {
            findNavController().popBackStack()
        }

        binding.btnApplyCode.setOnClickListener {
            val rawInput = binding.etPromoCode.text?.toString()?.trim().orEmpty()
            if (rawInput.isEmpty()) {
                showElevatedMessage("Vui lòng nhập mã ưu đãi")
                return@setOnClickListener
            }

            val normalizedCode = rawInput.uppercase(Locale.ROOT)
            selectedCode = normalizedCode
            discountAmount = discountForCode(normalizedCode)
            updateSavingsDisplay()
            updateVoucherButtonsUI()
            showElevatedMessage("Đã áp dụng ưu đãi", subtitle = selectedCode)
        }

        binding.btnActionVoucher1.setOnClickListener {
            if (selectedCode == "CINEMAX20K" && discountAmount > 0L) {
                val prevCode = selectedCode
                val prevDiscount = discountAmount
                selectedCode = ""
                discountAmount = 0L
                updateSavingsDisplay()
                updateVoucherButtonsUI()
                showElevatedMessage(
                    message = "Đã bỏ chọn ưu đãi",
                    subtitle = prevCode,
                    actionLabel = "HOÀN TÁC",
                    onAction = {
                        selectedCode = prevCode
                        discountAmount = prevDiscount
                        updateSavingsDisplay()
                        updateVoucherButtonsUI()
                    }
                )
            } else {
                selectedCode = "CINEMAX20K"
                discountAmount = 20_000L
                updateSavingsDisplay()
                updateVoucherButtonsUI()
                showElevatedMessage("Đã áp dụng ưu đãi", subtitle = selectedCode)
            }
        }

        binding.btnActionVoucher2.setOnClickListener {
            if (selectedCode == "CINEVIP15" && discountAmount > 0L) {
                selectedCode = ""
                discountAmount = 0L
                updateSavingsDisplay()
                updateVoucherButtonsUI()
                showElevatedMessage("Đã bỏ chọn ưu đãi", subtitle = "CINEVIP15")
            } else {
                selectedCode = "CINEVIP15"
                discountAmount = 40_000L
                updateSavingsDisplay()
                updateVoucherButtonsUI()
                showElevatedMessage("Đã áp dụng ưu đãi", subtitle = selectedCode)
            }
        }

        binding.btnActionVoucher3.setOnClickListener {
            if (selectedCode == "COMBOMAX30" && discountAmount > 0L) {
                selectedCode = ""
                discountAmount = 0L
                updateSavingsDisplay()
                updateVoucherButtonsUI()
                showElevatedMessage("Đã bỏ chọn ưu đãi", subtitle = "COMBOMAX30")
            } else {
                selectedCode = "COMBOMAX30"
                discountAmount = 30_000L
                updateSavingsDisplay()
                updateVoucherButtonsUI()
                showElevatedMessage("Đã áp dụng ưu đãi", subtitle = selectedCode)
            }
        }

        binding.btnConfirmVoucher.setOnClickListener {
            val movieTitle = arguments?.getString(MovieDetailFragment.ARG_MOVIE_TITLE) ?: "Hành Tinh Cát 2"

            // Gửi kết quả về qua Navigation Component savedStateHandle
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "selected_voucher_code",
                selectedCode
            )
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "selected_voucher_discount",
                discountAmount
            )
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                MovieDetailFragment.ARG_MOVIE_TITLE,
                movieTitle
            )

            // Dự phòng gửi thêm qua FragmentResult
            setFragmentResult(
                "voucher_result",
                bundleOf(
                    "selected_voucher_code" to selectedCode,
                    "selected_voucher_discount" to discountAmount
                )
            )

            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}