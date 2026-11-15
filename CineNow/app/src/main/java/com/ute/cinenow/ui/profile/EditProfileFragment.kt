package com.ute.cinenow.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nút mũi tên quay lại
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Nút Hủy: quay về trang trước
        binding.btnCancelEdit.setOnClickListener {
            findNavController().popBackStack()
        }

        // Nút Lưu thay đổi: thông báo giả lập và quay về trang cá nhân
        binding.btnSaveProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Đã lưu thông tin hồ sơ thành công!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}