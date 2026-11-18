package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private var isFriendRequested = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupActions()
    }

    private fun setupHeader() {
        // Nút quay lại màn hình trước
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnMore.setOnClickListener {
            Toast.makeText(requireContext(), "Tùy chọn khác (Báo cáo / Chặn)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupActions() {
        // Xử lý nút Gửi lời mời kết bạn với hiệu ứng thay đổi trạng thái mượt mà
        binding.btnAddFriend.setOnClickListener {
            isFriendRequested = !isFriendRequested
            if (isFriendRequested) {
                binding.btnAddFriend.text = "✓ Đã gửi lời mời kết bạn"
                binding.btnAddFriend.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#10B981")) // Xanh thành công
                Toast.makeText(requireContext(), "Đã gửi lời mời kết bạn đến Trần Bảo Long", Toast.LENGTH_SHORT).show()
            } else {
                binding.btnAddFriend.text = "👤+ Gửi lời mời kết bạn"
                binding.btnAddFriend.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC")) // Tím CineNow
                Toast.makeText(requireContext(), "Đã thu hồi lời mời kết bạn", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}