package com.ute.cinenow.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Chuyển sang màn Chỉnh sửa hồ sơ (#27)
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.editProfileFragment)
        }

        // Chuyển sang màn Lịch sử đặt vé (#25)
        binding.itemTicketHistory.setOnClickListener {
            findNavController().navigate(R.id.ticketHistoryFragment)
        }

        // Chuyển sang màn Đổi mật khẩu (#28)
        binding.itemChangePassword.setOnClickListener {
            findNavController().navigate(R.id.changePasswordFragment)
        }

        // Chuyển sang tab Đi cùng nhóm (#19)
        binding.itemCineTogether.setOnClickListener {
            findNavController().navigate(R.id.cineTogetherFragment)
        }

        // Đăng xuất: quay về màn Đăng nhập (#1)
        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}