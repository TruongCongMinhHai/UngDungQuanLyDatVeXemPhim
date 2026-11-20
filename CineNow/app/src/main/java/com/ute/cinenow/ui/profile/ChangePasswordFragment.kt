package com.ute.cinenow.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentChangePasswordBinding
import com.ute.cinenow.utils.showCineMessage

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLiveValidation()
        setupClickListeners()
    }

    private fun setupLiveValidation() {
        binding.etCurrentPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilCurrentPassword.error = null
        }
        binding.etNewPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilNewPassword.error = null
        }
        binding.etConfirmPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilConfirmPassword.error = null
        }
    }

    /**
     * Sự kiện click các nút và kiểm tra mật khẩu
     */
    private fun setupClickListeners() {
        // Nút quay lại đã được CineHeader tự động xử lý (findNavController().navigateUp())

        // Nút Hủy
        binding.btnCancelChange.setOnClickListener {
            it.animatePress {
                findNavController().popBackStack()
            }
        }

        // Nút Cập nhật mật khẩu
        binding.btnUpdatePassword.setOnClickListener {
            it.animatePress {
                validateAndSubmit()
            }
        }
    }

    private fun validateAndSubmit() {
        val currentPass = binding.etCurrentPassword.text?.toString()?.trim().orEmpty()
        val newPass = binding.etNewPassword.text?.toString()?.trim().orEmpty()
        val confirmPass = binding.etConfirmPassword.text?.toString()?.trim().orEmpty()

        var isValid = true

        if (currentPass.isEmpty()) {
            binding.tilCurrentPassword.error = "Vui lòng nhập mật khẩu hiện tại"
            binding.tilCurrentPassword.shake()
            isValid = false
        }

        if (newPass.isEmpty()) {
            binding.tilNewPassword.error = "Vui lòng nhập mật khẩu mới"
            binding.tilNewPassword.shake()
            isValid = false
        } else if (newPass.length < 8) {
            binding.tilNewPassword.error = "Mật khẩu mới phải có ít nhất 8 ký tự"
            binding.tilNewPassword.shake()
            isValid = false
        }

        if (confirmPass.isEmpty()) {
            binding.tilConfirmPassword.error = "Vui lòng xác nhận mật khẩu mới"
            binding.tilConfirmPassword.shake()
            isValid = false
        } else if (confirmPass != newPass) {
            binding.tilConfirmPassword.error = "Mật khẩu xác nhận không khớp"
            binding.tilConfirmPassword.shake()
            isValid = false
        }

        if (isValid) {
            showCineMessage("Đổi mật khẩu thành công")
            findNavController().popBackStack()
        }
    }

    private fun View.animatePress(action: () -> Unit) {
        animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).withEndAction {
            animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).withEndAction {
                action()
            }.start()
        }.start()
    }

    private fun View.shake() {
        animate()
            .translationXBy(20f)
            .setDuration(40)
            .withEndAction {
                animate()
                    .translationXBy(-40f)
                    .setDuration(40)
                    .withEndAction {
                        animate()
                            .translationXBy(20f)
                            .setDuration(40)
                            .start()
                    }.start()
            }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}