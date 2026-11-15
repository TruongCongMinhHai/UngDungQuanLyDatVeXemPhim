package com.ute.cinenow.ui.auth

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentVerifyAccountBinding
import com.ute.cinenow.databinding.ItemOtpCellBinding
import java.util.Locale

class VerifyAccountFragment : Fragment() {

    private var _binding: FragmentVerifyAccountBinding? = null
    private val binding get() = _binding!!

    private val cells = mutableListOf<ItemOtpCellBinding>()
    private var timer: CountDownTimer? = null
    private var canResend = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buildOtpCells()
        updateCells("")

        // Gõ số vào ô ẩn, 6 ô hiển thị cập nhật theo
        binding.etOtp.doOnTextChanged { text, _, _, _ ->
            binding.tvOtpError.visibility = View.GONE
            updateCells(text?.toString().orEmpty())
        }
        binding.etOtp.setOnFocusChangeListener { _, _ ->
            updateCells(binding.etOtp.text.toString())
        }

        // Bấm vào vùng 6 ô thì mở bàn phím
        binding.otpContainer.setOnClickListener { focusOtp() }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.tvResend.setOnClickListener {
            if (canResend) {
                // Dữ liệu giả lập: chưa gửi mã thật
                binding.etOtp.setText("")
                startTimer()
                Toast.makeText(requireContext(), "Đã gửi lại mã xác minh", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnVerify.setOnClickListener {
            if (binding.etOtp.text.toString().length < OTP_LENGTH) {
                binding.tvOtpError.visibility = View.VISIBLE
            } else {
                // Dữ liệu giả lập: chưa kiểm tra mã, mã nào đủ 6 số cũng qua
                findNavController().navigate(R.id.resetPasswordFragment)
            }
        }

        startTimer()
        binding.etOtp.post { focusOtp() }
    }

    // Tạo 6 ô hiển thị từ layout item_otp_cell
    private fun buildOtpCells() {
        repeat(OTP_LENGTH) {
            val cell = ItemOtpCellBinding.inflate(layoutInflater, binding.otpCells, false)
            binding.otpCells.addView(cell.root)
            cells.add(cell)
        }
    }

    private fun updateCells(code: String) {
        val activeIndex =
            if (binding.etOtp.hasFocus() && code.length < OTP_LENGTH) code.length else -1

        cells.forEachIndexed { index, cell ->
            cell.tvDigit.text = code.getOrNull(index)?.toString().orEmpty()
            cell.root.isSelected = index == activeIndex

            val dashColor =
                if (index < code.length || index == activeIndex) R.color.cinenow_primary
                else R.color.cinenow_outline_variant
            cell.vDash.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), dashColor))
        }
    }

    private fun focusOtp() {
        binding.etOtp.requestFocus()
        WindowCompat.getInsetsController(requireActivity().window, binding.etOtp)
            .show(WindowInsetsCompat.Type.ime())
    }

    private fun startTimer() {
        timer?.cancel()
        canResend = false
        binding.tvResend.alpha = 0.5f
        binding.tvTimer.visibility = View.VISIBLE

        timer = object : CountDownTimer(RESEND_SECONDS * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                _binding?.tvTimer?.text =
                    String.format(Locale.getDefault(), "(%02d:%02d)", seconds / 60, seconds % 60)
            }

            override fun onFinish() {
                canResend = true
                _binding?.let {
                    it.tvResend.alpha = 1f
                    it.tvTimer.visibility = View.GONE
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        cells.clear()
        _binding = null
    }

    companion object {
        private const val OTP_LENGTH = 6
        private const val RESEND_SECONDS = 59
    }
}