package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentFriendRequestsBinding

class FriendRequestsFragment : Fragment() {

    private var _binding: FragmentFriendRequestsBinding? = null
    private val binding get() = _binding!!

    // Biến trạng thái tab hiện tại: true = Đã nhận, false = Đã gửi
    private var isReceivedTab = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupTabs()
        setupRequestActions()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupTabs() {
        // Mặc định ban đầu hiển thị tab "Đã nhận"
        showReceivedState()

        binding.tabReceived.setOnClickListener {
            if (!isReceivedTab) {
                isReceivedTab = true
                showReceivedState()
                Toast.makeText(requireContext(), "Đã chuyển sang tab Lời mời đã nhận", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tabSent.setOnClickListener {
            if (isReceivedTab) {
                isReceivedTab = false
                showSentState()
                Toast.makeText(requireContext(), "Đã chuyển sang tab Lời mời đã gửi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showReceivedState() {
        // Cập nhật style nút Tab
        binding.tabReceived.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
        binding.tabReceived.setTextColor(Color.WHITE)

        binding.tabSent.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        binding.tabSent.setTextColor(Color.parseColor("#64748B"))

        // Hiển thị danh sách card "Đã nhận" (Ví dụ: Thảo Ly, Tuấn Kiệt)
        binding.cardRequest1.visibility = View.VISIBLE
        binding.cardRequest2.visibility = View.VISIBLE
    }

    private fun showSentState() {
        // Cập nhật style nút Tab
        binding.tabSent.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
        binding.tabSent.setTextColor(Color.WHITE)

        binding.tabReceived.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        binding.tabReceived.setTextColor(Color.parseColor("#64748B"))

        // Ẩn các card "Đã nhận" đi để nhường chỗ hiển thị danh sách "Đã gửi"
        // (Sau này khi có Adapter dữ liệu riêng cho tab Đã gửi, bạn sẽ load vào đây)
        binding.cardRequest1.visibility = View.GONE
        binding.cardRequest2.visibility = View.GONE
    }

    private fun setupRequestActions() {
        // --- Xử lý cho Thảo Ly (Card 1) ---
        binding.btnAccept1.setOnClickListener {
            Toast.makeText(requireContext(), "Đã chấp nhận kết bạn với Nguyễn Thảo Ly", Toast.LENGTH_SHORT).show()
            binding.cardRequest1.visibility = View.GONE
        }

        binding.btnReject1.setOnClickListener {
            Toast.makeText(requireContext(), "Đã từ chối lời mời của Nguyễn Thảo Ly", Toast.LENGTH_SHORT).show()
            binding.cardRequest1.visibility = View.GONE
        }

        // --- Xử lý cho Tuấn Kiệt (Card 2) ---
        binding.btnAccept2.setOnClickListener {
            Toast.makeText(requireContext(), "Đã chấp nhận kết bạn với Đặng Tuấn Kiệt", Toast.LENGTH_SHORT).show()
            binding.cardRequest2.visibility = View.GONE
        }

        binding.btnReject2.setOnClickListener {
            Toast.makeText(requireContext(), "Đã từ chối lời mời của Đặng Tuấn Kiệt", Toast.LENGTH_SHORT).show()
            binding.cardRequest2.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}