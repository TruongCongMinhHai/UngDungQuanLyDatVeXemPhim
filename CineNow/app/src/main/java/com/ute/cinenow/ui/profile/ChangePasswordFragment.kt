package com.ute.cinenow.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.databinding.FragmentChangePasswordBinding

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

        // Nút mũi tên quay lại
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Nút Hủy: quay lại trang trước
        binding.btnCancelChange.setOnClickListener {
            findNavController().popBackStack()
        }

        // Nút Cập nhật mật khẩu: mô phỏng đổi mật khẩu thành công và quay lại
        binding.btnUpdatePassword.setOnClickListener {
            val currentPass = binding.etCurrentPassword.text?.toString().orEmpty()
            val newPass = binding.etNewPassword.text?.toString().orEmpty()
            val confirmPass = binding.etConfirmPassword.text?.toString().orEmpty()

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ các trường!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(requireContext(), "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}