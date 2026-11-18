package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderActions()
        setupSearchAndFilters()
        setupFriendActions()
    }

    private fun setupHeaderActions() {
        binding.btnOpenFriendRequests.setOnClickListener {
            Toast.makeText(requireContext(), "Mở màn hình Lời mời kết bạn", Toast.LENGTH_SHORT).show()
            // TODO: Chuyển sang FriendRequestsFragment tại đây
        }
    }

    private fun setupSearchAndFilters() {
        binding.etSearchFriend.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim()
                if (keyword.isNotEmpty()) {
                    // Logic lọc danh sách bạn bè theo từ khóa
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipAllFriends.setOnClickListener {
            setActiveChip(isAll = true)
            Toast.makeText(requireContext(), "Hiển thị tất cả 18 bạn bè", Toast.LENGTH_SHORT).show()
        }

        binding.chipOnline.setOnClickListener {
            setActiveChip(isAll = false)
            Toast.makeText(requireContext(), "Hiển thị 6 bạn đang online", Toast.LENGTH_SHORT).show()
        }

        binding.chipNearby.setOnClickListener {
            Toast.makeText(requireContext(), "Lọc theo cụm rạp gần bạn", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setActiveChip(isAll: Boolean) {
        if (isAll) {
            binding.chipAllFriends.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
            binding.chipAllFriends.setTextColor(Color.WHITE)

            binding.chipOnline.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            binding.chipOnline.setTextColor(Color.parseColor("#475569"))
        } else {
            binding.chipOnline.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
            binding.chipOnline.setTextColor(Color.WHITE)

            binding.chipAllFriends.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            binding.chipAllFriends.setTextColor(Color.parseColor("#475569"))
        }
    }

    private fun setupFriendActions() {
        // --- Minh Quân ---
        binding.btnChat1.setOnClickListener {
            Toast.makeText(requireContext(), "Mở khung chat với Minh Quân", Toast.LENGTH_SHORT).show()
        }
        binding.btnInvite1.setOnClickListener {
            Toast.makeText(requireContext(), "Gửi lời mời xem phim đến Minh Quân", Toast.LENGTH_SHORT).show()
        }

        // --- Thảo Vy ---
        binding.btnChat2.setOnClickListener {
            Toast.makeText(requireContext(), "Mở khung chat với Thảo Vy", Toast.LENGTH_SHORT).show()
        }
        binding.btnInvite2.setOnClickListener {
            Toast.makeText(requireContext(), "Gửi lời mời xem phim đến Thảo Vy", Toast.LENGTH_SHORT).show()
        }

        // --- Hoàng Nam ---
        binding.btnChat3.setOnClickListener {
            Toast.makeText(requireContext(), "Mở khung chat với Hoàng Nam", Toast.LENGTH_SHORT).show()
        }
        binding.btnInvite3.setOnClickListener {
            Toast.makeText(requireContext(), "Gửi lời mời xem phim đến Hoàng Nam", Toast.LENGTH_SHORT).show()
        }

        // --- Gần rạp của bạn ---
        binding.btnConnectNearby.setOnClickListener {
            Toast.makeText(requireContext(), "Đã gửi yêu cầu kết nối cộng đồng rạp Landmark 81", Toast.LENGTH_SHORT).show()
            binding.btnConnectNearby.text = "✓ Đã kết nối"
            binding.btnConnectNearby.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#10B981"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}