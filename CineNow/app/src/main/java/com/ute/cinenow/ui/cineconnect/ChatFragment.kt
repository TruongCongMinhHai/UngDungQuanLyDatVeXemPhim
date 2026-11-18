package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderActions()
        setupQuickReplies()
        setupMessageSending()
        setupMovieInvitationActions()
    }

    private fun setupHeaderActions() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnQuickInvite.setOnClickListener {
            Toast.makeText(requireContext(), "Mở bảng chọn suất chiếu để gửi lời mời cho Thảo Vy", Toast.LENGTH_SHORT).show()
        }

        binding.btnMoreOptions.setOnClickListener {
            Toast.makeText(requireContext(), "Tùy chọn trò chuyện (Tắt thông báo, Chặn, Xóa lịch sử)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupQuickReplies() {
        binding.chipQuick1.setOnClickListener {
            binding.etMessageInput.setText("Mình đặt combo bắp nước luôn nhé?")
        }

        binding.chipQuick2.setOnClickListener {
            binding.etMessageInput.setText("Hẹn gặp ở sảnh trước")
        }
    }

    private fun setupMessageSending() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                Toast.makeText(requireContext(), "Đã gửi: $messageText", Toast.LENGTH_SHORT).show()
                binding.etMessageInput.text.clear()
                // TODO: Thêm tin nhắn mới vào danh sách chat Adapter
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập nội dung tin nhắn", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAddAttachment.setOnClickListener {
            Toast.makeText(requireContext(), "Đính kèm hình ảnh hoặc vé xem phim", Toast.LENGTH_SHORT).show()
        }

        binding.btnEmoji.setOnClickListener {
            Toast.makeText(requireContext(), "Mở bảng biểu tượng cảm xúc (Emoji)", Toast.LENGTH_SHORT).show()
        }

        binding.btnMovieInvite.setOnClickListener {
            Toast.makeText(requireContext(), "Gửi thiệp mời đặt vé xem phim nhanh", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupMovieInvitationActions() {
        // Xử lý nút Chấp nhận lời mời xem phim trong khung chat
        binding.btnAcceptInvite.setOnClickListener {
            Toast.makeText(requireContext(), "Đã chấp nhận lời mời! Tiến hành giữ ghế và thanh toán.", Toast.LENGTH_SHORT).show()
            binding.btnAcceptInvite.text = "✓ Đã chấp nhận"
            binding.btnAcceptInvite.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#10B981"))
            binding.btnRejectInvite.visibility = View.GONE
        }

        // Xử lý nút Từ chối lời mời xem phim
        binding.btnRejectInvite.setOnClickListener {
            Toast.makeText(requireContext(), "Đã từ chối lời mời xem phim", Toast.LENGTH_SHORT).show()
            binding.cardMovieInvite.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}