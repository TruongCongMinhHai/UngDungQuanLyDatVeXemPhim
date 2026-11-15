package com.ute.cinenow.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.DialogResetSuccessBinding

class ResetSuccessDialog : DialogFragment() {

    private var _binding: DialogResetSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogResetSuccessBinding.inflate(inflater, container, false)
        // Chỉ đóng bằng nút "Quay lại đăng nhập", không đóng khi chạm ra ngoài hoặc bấm Back
        isCancelable = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackToLogin.setOnClickListener {
            val nav = findNavController()
            // Xóa Quên mật khẩu → Xác minh → Đặt lại mật khẩu khỏi back stack, về lại Đăng nhập
            if (!nav.popBackStack(R.id.loginFragment, false)) {
                nav.navigate(R.id.loginFragment)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // Rộng hết chiều ngang (lề 20dp do layout tạo), nền trong suốt để thấy góc bo
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(android.R.color.transparent)
            setDimAmount(0.5f)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}