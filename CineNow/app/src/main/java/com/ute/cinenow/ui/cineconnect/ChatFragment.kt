package com.ute.cinenow.ui.cineconnect

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentChatBinding
import com.ute.cinenow.utils.showCineMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var friendName: String = "Thảo Vy"

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

        val passedFriend = arguments?.getString("friend_name") ?: arguments?.getString("user_name")
        if (!passedFriend.isNullOrBlank()) {
            friendName = passedFriend
        }

        binding.header.title = "Trò chuyện"
        loadFriendProfile()
        setupHeader()
        setupTopInviteAndSuggestions()
        setupInviteCardActions()
        setupMessageSending()
        renderChatHistory()
    }

    private fun loadFriendProfile() {
        val user = CineConnectStateManager.getUser(friendName)
        // Đồng bộ avatar, tên, trạng thái, % khớp gu theo đúng friendName
        binding.ivChatUserAvatar.setImageResource(user.avatarRes)
        binding.ivChatUserAvatar.imageTintList = null
        binding.ivChatUserAvatar.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
        binding.topUserBar.findViewById<TextView>(R.id.header)?.text = "Trò chuyện"

        // Quét các text view trong topUserBar
        findTextViewByText(binding.topUserBar, "Thảo Vy")?.text = user.name
        findTextViewByText(binding.topUserBar, "Đang hoạt động")?.text =
            "${user.onlineStatusText} • ${user.matchPercent}% khớp gu"

        // Cập nhật avatar trong các tin nhắn nhận
        fun updateBubblesAvatar(view: View) {
            if (view is android.widget.ImageView && view.contentDescription == "Thảo Vy") {
                view.setImageResource(user.avatarRes)
                view.imageTintList = null
                view.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            } else if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    updateBubblesAvatar(view.getChildAt(i))
                }
            }
        }
        updateBubblesAvatar(binding.layoutChatMessages)

        // Điểm tương thích gu phim banner
        val matchBanner = binding.layoutChatMessages.getChildAt(0)
        if (matchBanner is ViewGroup) {
            for (i in 0 until matchBanner.childCount) {
                val child = matchBanner.getChildAt(i)
                if (child is TextView && child.text.contains("Điểm tương thích")) {
                    child.text = "Điểm tương thích gu phim: ${user.matchPercent}% (${user.genres.joinToString(", ")})"
                    break
                }
            }
        }
    }

    private fun findTextViewByText(parent: ViewGroup, partial: String): TextView? {
        for (i in 0 until parent.childCount) {
            val v = parent.getChildAt(i)
            if (v is TextView && v.text.contains(partial, ignoreCase = true)) {
                return v
            } else if (v is ViewGroup) {
                val found = findTextViewByText(v, partial)
                if (found != null) return found
            }
        }
        return null
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            val popped = try { findNavController().popBackStack() } catch (_: Exception) { false }
            if (!popped) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupTopInviteAndSuggestions() {
        binding.btnTopInviteMovie.setOnClickListener {
            findNavController().navigate(
                R.id.inviteMovieFragment,
                bundleOf("friend_name" to friendName)
            )
        }

        binding.chipSuggestCombo.setOnClickListener {
            binding.etMessageInput.setText("Mình đặt combo bắp nước luôn nhé?")
        }

        binding.chipSuggestLobby.setOnClickListener {
            binding.etMessageInput.setText("Hẹn gặp ở sảnh rạp trước 15 phút!")
        }

        binding.btnAddMedia.setOnClickListener {
            showCineMessage("Đính kèm lịch chiếu", subtitle = "Đang mở danh sách suất chiếu của bạn")
        }

        binding.btnMovieShortcut.setOnClickListener {
            findNavController().navigate(
                R.id.inviteMovieFragment,
                bundleOf("friend_name" to friendName)
            )
        }
    }

    private fun setupInviteCardActions() {
        binding.btnAcceptInvite.setOnClickListener {
            binding.btnAcceptInvite.text = "Đã chấp nhận"
            binding.btnAcceptInvite.isEnabled = false
            binding.btnDeclineInvite.visibility = View.GONE
            showCineMessage(
                message = "Đã đồng ý xem Hành Tinh Cát 2 cùng $friendName",
                subtitle = "Suất chiếu đã được đồng bộ vào lịch rảnh!",
                actionLabel = "ĐẶT VÉ NGAY",
                onAction = {
                    findNavController().navigate(R.id.showtimeFragment)
                }
            )
        }

        // Lỗi 14: Hiện hộp xác nhận ngắn trước khi từ chối
        binding.btnDeclineInvite.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Từ chối lời mời")
                .setMessage("Bạn có chắc chắn muốn từ chối lời mời xem phim từ $friendName?")
                .setPositiveButton("Từ chối") { _, _ ->
                    binding.cardMovieInviteInChat.visibility = View.GONE
                    showCineMessage(
                        message = "Đã từ chối lời mời xem phim từ $friendName",
                        actionLabel = "HOÀN TÁC",
                        onAction = {
                            binding.cardMovieInviteInChat.visibility = View.VISIBLE
                            showCineMessage("Đã khôi phục lời mời xem phim từ $friendName")
                        }
                    )
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    private fun setupMessageSending() {
        binding.btnSendMessage.setOnClickListener {
            val content = binding.etMessageInput.text?.toString()?.trim().orEmpty()
            if (content.isEmpty()) {
                showCineMessage("Vui lòng nhập nội dung tin nhắn")
                return@setOnClickListener
            }

            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val msg = CineConnectStateManager.ChatMessage(
                senderName = "Tôi",
                message = content,
                time = currentTime,
                isMe = true
            )
            CineConnectStateManager.addMessage(friendName, msg)

            appendSentMessage(content, currentTime)
            binding.etMessageInput.setText("")
            binding.scrollChatMessages.post {
                binding.scrollChatMessages.fullScroll(View.FOCUS_DOWN)
            }
        }
    }

    private fun renderChatHistory() {
        val messages = CineConnectStateManager.getMessages(friendName)
        // Nếu là bạn khác Thảo Vy, ẩn thẻ mời mặc định ban đầu nếu người đó chưa có lời mời
        val hasInvite = messages.any { it.isMovieInvite }
        binding.cardMovieInviteInChat.visibility = if (hasInvite || friendName == "Thảo Vy") View.VISIBLE else View.GONE

        // Cập nhật tên trong các tin nhắn mẫu hiển thị
        for (i in 0 until binding.layoutChatMessages.childCount) {
            val child = binding.layoutChatMessages.getChildAt(i)
            if (child is ViewGroup) {
                // Thay đổi các bubble chat cho khớp với từng friendName
            }
        }
    }

    private fun appendSentMessage(text: String, time: String) {
        val bubble = TextView(requireContext()).apply {
            this.text = text
            setTextColor("#FFFFFF".toColorInt())
            textSize = 12f
            setBackgroundResource(R.drawable.bg_rounded_icon)
            backgroundTintList = ColorStateList.valueOf("#5B3DF5".toColorInt())
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