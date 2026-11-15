package com.ute.cinenow.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gõ vào ô thì xóa thông báo lỗi
        binding.etIdentifier.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank()) binding.tilIdentifier.error = null
        }
        binding.etPassword.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank()) binding.tilPassword.error = null
        }

        binding.btnLogin.setOnClickListener {
            if (validateInput()) {
                // Dữ liệu giả lập: chưa xác thực, chỉ chuyển sang Trang chủ
                findNavController().navigate(
                    R.id.homeFragment,
                    null,
                    navOptions {
                        popUpTo(R.id.loginFragment) { inclusive = true }
                    }
                )
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }

    // Chỉ kiểm tra không để trống, chưa kiểm tra tài khoản thật
    private fun validateInput(): Boolean {
        var valid = true

        if (binding.etIdentifier.text.isNullOrBlank()) {
            binding.tilIdentifier.error = "Vui lòng nhập email hoặc số điện thoại"
            valid = false
        } else {
            binding.tilIdentifier.error = null
        }

        if (binding.etPassword.text.isNullOrBlank()) {
            binding.tilPassword.error = "Vui lòng nhập mật khẩu"
            valid = false
        } else {
            binding.tilPassword.error = null
        }

        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}