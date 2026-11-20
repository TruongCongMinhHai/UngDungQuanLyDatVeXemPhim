package com.ute.cinenow.ui.cineconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentChatBinding
import com.ute.cinenow.utils.showCineMessage

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setupTopInviteAndSuggestions()
        setupInviteCardActions()
        setupMessageSending()
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupTopInviteAndSuggestions() {
        binding.btnTopInviteMovie.setOnClickListener {
            showCineMessage("Mở màn hình chọn phim mời Thảo Vy")
        }

        binding.chipSuggestCombo.setOnClickListener {
            binding.etMessageInput.setText("Minh đặt combo bắp nước luôn nhé?")
        }

        binding.chipSuggestLobby.setOnClickListener {
            binding.etMessageInput.setText("Hẹn gặp ở sảnh trước 15 phút")
        }

        binding.btnAddMedia.setOnClickListener {
            showCineMessage("Đính kèm lịch chiếu hoặc rạp yêu thích")
        }
    }

    private fun setupInviteCardActions() {
        binding.btnAcceptInvite.setOnClickListener {
            binding.btnAcceptInvite.text = "Đã chấp nhận"
            showCineMessage(
                message = "Đã chấp nhận lời mời xem Hành Tinh Cát: Phần 2 (19:30)",
                actionLabel = "HOÀN TÁC",
                onAction = {
                    binding.btnAcceptInvite.text = "Chấp nhận"
                    showCineMessage("Đã hoàn tác trạng thái chấp nhận lời mời")
                }
            )
        }

        binding.btnDeclineInvite.setOnClickListener {
            binding.cardMovieInviteInChat.visibility = View.GONE
            showCineMessage(
                message = "Đã từ chối lời mời xem phim từ Thảo Vy",
                actionLabel = "HOÀN TÁC",
                onAction = {
                    binding.cardMovieInviteInChat.visibility = View.VISIBLE
                    showCineMessage("Đã khôi phục thẻ lời mời xem phim")
                }
            )
        }
    }

    private fun setupMessageSending() {
        binding.btnSendMessage.setOnClickListener {
            val content = binding.etMessageInput.text?.toString()?.trim().orEmpty()
            if (content.isEmpty()) {
                showCineMessage("Vui lòng nhập nội dung tin nhắn")
                return@setOnClickListener
            }

            appendSentMessage(content)
            binding.etMessageInput.setText("")
            binding.scrollChatMessages.post {
                binding.scrollChatMessages.fullScroll(View.FOCUS_DOWN)
            }
        }
    }

    private fun appendSentMessage(text: String) {
        val bubble = TextView(requireContext()).apply {
            this.text = text
            setTextColor("#FFFFFF".toColorInt())
            textSize = 12f
            setBackgroundResource(com.ute.cinenow.R.drawable.bg_rounded_icon)
            backgroundTintList = android.content.res.ColorStateList.valueOf("#5B3DF5".toColorInt())
            setPadding(28, 22, 28, 22)
        }

        val params = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 16
            marginStart = 120
            gravity = android.view.Gravity.END
        }

        binding.layoutChatMessages.addView(bubble, params)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}