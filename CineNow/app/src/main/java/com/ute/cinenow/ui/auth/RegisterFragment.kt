package com.ute.cinenow.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ute.cinenow.databinding.FragmentRegisterBinding
import com.ute.cinenow.utils.showCineMessage

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gõ dữ liệu hợp lệ vào ô đang báo lỗi thì lỗi tự mất
        clearErrorWhenValid(binding.etFullName, binding.tilFullName) { fullNameError() }
        clearErrorWhenValid(binding.etPhone, binding.tilPhone) { phoneError() }
        clearErrorWhenValid(binding.etEmail, binding.tilEmail) { emailError() }
        clearErrorWhenValid(binding.etPassword, binding.tilPassword) { passwordError() }
        clearErrorWhenValid(binding.etConfirmPassword, binding.tilConfirmPassword) { confirmError() }

        // Đổi mật khẩu thì kiểm tra lại ô xác nhận
        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            if (binding.tilConfirmPassword.error != null && confirmError() == null) {
                binding.tilConfirmPassword.error = null
            }
        }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.tvLogin.setOnClickListener { findNavController().popBackStack() }

        binding.btnRegister.setOnClickListener {
            if (validateAll()) {
                // Dữ liệu giả lập: chưa tạo tài khoản thật, quay về màn Đăng nhập
                showCineMessage("Tạo tài khoản thành công")
                findNavController().popBackStack()
            }
        }
    }

    // ----- Kiểm tra từng ô: trả về nội dung lỗi, hoặc null nếu hợp lệ -----

    private fun fullNameError(): String? =
        if (binding.etFullName.text.isNullOrBlank()) "Vui lòng nhập họ và tên" else null

    private fun phoneError(): String? {
        val phone = binding.etPhone.text.toString().trim()
        return when {
            phone.isEmpty() -> "Vui lòng nhập số điện thoại"
            !Regex("^(0|\\+84)\\d{9}$").matches(phone) -> "Số điện thoại không hợp lệ"
            else -> null
        }
    }

    private fun emailError(): String? {
        val email = binding.etEmail.text.toString().trim()
        return when {
            email.isEmpty() -> "Vui lòng nhập email"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không hợp lệ"
            else -> null
        }
    }

    private fun passwordError(): String? {
        val password = binding.etPassword.text.toString()
        return when {
            password.isEmpty() -> "Vui lòng nhập mật khẩu"
            password.length < 8 -> "Mật khẩu tối thiểu 8 ký tự"
            else -> null
        }
    }

    private fun confirmError(): String? {
        val confirm = binding.etConfirmPassword.text.toString()
        return when {
            confirm.isEmpty() -> "Vui lòng nhập lại mật khẩu"
            confirm != binding.etPassword.text.toString() -> "Mật khẩu xác nhận không khớp"
            else -> null
        }
    }

    // Kiểm tra cả 5 ô, hiện lỗi ở tất cả ô sai (không dừng ở ô đầu tiên)
    private fun validateAll(): Boolean {
        binding.tilFullName.error = fullNameError()
        binding.tilPhone.error = phoneError()
        binding.tilEmail.error = emailError()
        binding.tilPassword.error = passwordError()
        binding.tilConfirmPassword.error = confirmError()

        return listOf(
            binding.tilFullName, binding.tilPhone, binding.tilEmail,
            binding.tilPassword, binding.tilConfirmPassword
        ).all { it.error == null }
    }

    private fun clearErrorWhenValid(
        editText: TextInputEditText,
        layout: TextInputLayout,
        validator: () -> String?
    ) {
        editText.doOnTextChanged { _, _, _, _ ->
            if (layout.error != null && validator() == null) layout.error = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}