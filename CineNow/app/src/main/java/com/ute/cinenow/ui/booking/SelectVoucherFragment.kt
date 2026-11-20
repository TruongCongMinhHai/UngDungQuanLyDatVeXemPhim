package com.ute.cinenow.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.databinding.FragmentSelectVoucherBinding
import com.ute.cinenow.ui.movie.MovieDetailFragment
import com.ute.cinenow.utils.showCineMessage
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
        updateSavingsDisplay()
        setupListeners()
    }

    private fun formatCurrencyVnd(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return "${formatter.format(amount)} đ"
    }

    private fun updateSavingsDisplay() {
        binding.tvTotalSavings.text = formatCurrencyVnd(discountAmount)
    }

    private fun setupListeners() {
        binding.btnApplyCode.setOnClickListener {
            val rawInput = binding.etPromoCode.text?.toString()?.trim().orEmpty()
            if (rawInput.isEmpty()) {
                showCineMessage("Vui lòng nhập mã ưu đãi")
                return@setOnClickListener
            }

            val normalizedCode = rawInput.uppercase(Locale.ROOT)
            selectedCode = normalizedCode
            discountAmount = when (normalizedCode) {
                "CINEMAX20K" -> 20_000L
                "CINEVIP15" -> 40_000L
                "COMBOMAX30" -> 30_000L
                else -> 20_000L
            }
            updateSavingsDisplay()
            showCineMessage("Đã áp dụng ưu đãi", subtitle = selectedCode)
        }

        binding.btnActionVoucher1.setOnClickListener {
            if (selectedCode == "CINEMAX20K" && discountAmount > 0L) {
                val previousCode = selectedCode
                val previousDiscount = discountAmount
                selectedCode = ""
                discountAmount = 0L
                updateSavingsDisplay()
                showCineMessage(
                    message = "Đã bỏ chọn ưu đãi",
                    subtitle = previousCode,
                    actionLabel = "HOÀN TÁC",
                    onAction = {
                        selectedCode = previousCode
                        discountAmount = previousDiscount
                        updateSavingsDisplay()
                    }
                )
            } else {
                selectedCode = "CINEMAX20K"
                discountAmount = 20_000L
                updateSavingsDisplay()
                showCineMessage("Đã áp dụng ưu đãi", subtitle = selectedCode)
            }
        }

        binding.btnActionVoucher2.setOnClickListener {
            selectedCode = "CINEVIP15"
            discountAmount = 40_000L
            updateSavingsDisplay()
            showCineMessage("Đã áp dụng ưu đãi", subtitle = selectedCode)
        }

        binding.btnActionVoucher3.setOnClickListener {
            selectedCode = "COMBOMAX30"
            discountAmount = 30_000L
            updateSavingsDisplay()
            showCineMessage("Đã áp dụng ưu đãi", subtitle = selectedCode)
        }

        binding.btnConfirmVoucher.setOnClickListener {
            val movieTitle = arguments?.getString(MovieDetailFragment.ARG_MOVIE_TITLE) ?: "Hành Tinh Cát 2"
            if (selectedCode.isNotEmpty() && discountAmount > 0L) {
                showCineMessage("Đã áp dụng ưu đãi", subtitle = selectedCode)
            }
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
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}