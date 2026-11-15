package com.ute.cinenow.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentResetPasswordBinding

class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gõ dữ liệu hợp lệ vào ô đang báo lỗi thì lỗi tự mất
        binding.etNewPassword.doOnTextChanged { _, _, _, _ ->
            if (binding.tilNewPassword.error != null && newPasswordError() == null) {
                binding.tilNewPassword.error = null
            }
            // Đổi mật khẩu mới thì kiểm tra lại ô xác nhận
            if (binding.tilConfirmPassword.error != null && confirmError() == null) {
                binding.tilConfirmPassword.error = null
            }
        }
        binding.etConfirmPassword.doOnTextChanged { _, _, _, _ ->
            if (binding.tilConfirmPassword.error != null && confirmError() == null) {
                binding.tilConfirmPassword.error = null
            }
        }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnSave.setOnClickListener { submit() }

        // Bấm nút Xong trên bàn phím cũng giống bấm Lưu
        binding.etConfirmPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                true
            } else {
                false
            }
        }
    }

    private fun submit() {
        binding.tilNewPassword.error = newPasswordError()
        binding.tilConfirmPassword.error = confirmError()

        if (binding.tilNewPassword.error == null && binding.tilConfirmPassword.error == null) {
            // Dữ liệu giả lập: chưa đổi mật khẩu thật, hiện hộp thoại thành công
            findNavController().navigate(R.id.resetSuccessDialog)
        }
    }

    // Trả về nội dung lỗi, hoặc null nếu hợp lệ
    private fun newPasswordError(): String? {
        val password = binding.etNewPassword.text.toString()
        return when {
            password.isEmpty() -> "Vui lòng nhập mật khẩu mới"
            password.length < 8 -> "Mật khẩu tối thiểu 8 ký tự"
            !password.any { it.isLetter() } || !password.any { it.isDigit() } ->
                "Mật khẩu cần có cả chữ cái và chữ số"
            else -> null
        }
    }

    private fun confirmError(): String? {
        val confirm = binding.etConfirmPassword.text.toString()
        return when {
            confirm.isEmpty() -> "Vui lòng nhập lại mật khẩu mới"
            confirm != binding.etNewPassword.text.toString() -> "Mật khẩu xác nhận không khớp"
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}