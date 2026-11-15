package com.ute.cinenow.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentForgotPasswordBinding

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gõ dữ liệu hợp lệ vào ô đang báo lỗi thì lỗi tự mất
        binding.etIdentifier.doOnTextChanged { _, _, _, _ ->
            if (binding.tilIdentifier.error != null && identifierError() == null) {
                binding.tilIdentifier.error = null
            }
        }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnContinue.setOnClickListener { submit() }

        // Bấm nút Xong trên bàn phím cũng giống bấm Tiếp tục
        binding.etIdentifier.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                true
            } else {
                false
            }
        }
    }

    private fun submit() {
        val error = identifierError()
        binding.tilIdentifier.error = error
        if (error == null) {
            // Dữ liệu giả lập: chưa gửi mã thật, chỉ chuyển sang màn Xác minh
            findNavController().navigate(R.id.verifyAccountFragment)
        }
    }

    // Trả về nội dung lỗi, hoặc null nếu hợp lệ (email hoặc số điện thoại)
    private fun identifierError(): String? {
        val input = binding.etIdentifier.text.toString().trim()
        return when {
            input.isEmpty() -> "Vui lòng nhập email hoặc số điện thoại"
            Patterns.EMAIL_ADDRESS.matcher(input).matches() -> null
            Regex("^(0|\\+84)\\d{9}$").matches(input) -> null
            else -> "Email hoặc số điện thoại không hợp lệ"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}