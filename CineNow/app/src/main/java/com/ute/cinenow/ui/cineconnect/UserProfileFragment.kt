package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentUserProfileBinding
import com.ute.cinenow.utils.showCineMessage

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private var currentUserName = "Trần Bảo Long"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserDataFromArguments()
        setupHeader()
        setupActions()
    }

    override fun onResume() {
        super.onResume()
        updateFriendButtonUI()
    }

    // Lỗi 6, 7, 8: Load toàn bộ dữ liệu từ CineConnectStateManager duy nhất
    private fun loadUserDataFromArguments() {
        val passedName = arguments?.getString("user_name")
        if (!passedName.isNullOrBlank()) {
            currentUserName = passedName
        }

        val user = CineConnectStateManager.getUser(currentUserName)
        binding.tvUserName.text = user.name
        binding.tvUserHandle.text = user.handle
        binding.ivAvatar.setImageResource(user.avatarRes)
        binding.header.title = "Hồ sơ: ${user.name}"

        // Điểm số và rạp thường ghé
        val container = binding.root
        findTextViewByPartial(container, "vé đã xem")?.text = "Thành viên thân thiết • ${user.watchedTickets} vé đã xem"
        findTextViewByPartial(container, "CineNow")?.text = user.favoriteCinema
        findTextViewByPartial(container, "Độ khớp gu điện ảnh")?.let {
            val parent = it.parent as? ViewGroup
            (parent?.getChildAt(0) as? TextView)?.text = "${user.matchPercent}%"
        }
    }

    private fun findTextViewByPartial(parent: ViewGroup, partial: String): TextView? {
        for (i in 0 until parent.childCount) {
            val v = parent.getChildAt(i)
            if (v is TextView && v.text.contains(partial, ignoreCase = true)) {
                return v
            } else if (v is ViewGroup) {
                val found = findTextViewByPartial(v, partial)
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

    // Lỗi 11: Nút kết bạn màu tím (#5850EC), hiện chú thích "Chỉ nhắn tin được sau khi cả hai đồng ý kết bạn."
    private fun updateFriendButtonUI() {
        val isAlreadyFriend = CineConnectStateManager.isFriend(currentUserName)
        val isSent = CineConnectStateManager.isRequestSent(currentUserName)

        val noticeView = binding.root.findViewById<TextView>(R.id.tvUserName)?.let {
            findTextViewByPartial(binding.root as ViewGroup, "Chỉ có thể trò chuyện")
        }

        when {
            isAlreadyFriend -> {
                binding.btnSendFriendRequest.text = "Nhắn tin cho bạn bè"
                binding.btnSendFriendRequest.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
                noticeView?.visibility = View.GONE
            }
            isSent -> {
                binding.btnSendFriendRequest.text = "Đã gửi lời mời kết bạn"
                binding.btnSendFriendRequest.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#94A3B8"))
                noticeView?.visibility = View.VISIBLE
                noticeView?.text = "Chỉ nhắn tin được sau khi cả hai đồng ý kết bạn."
            }
            else -> {
                binding.btnSendFriendRequest.text = "Kết bạn"
                binding.btnSendFriendRequest.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
                noticeView?.visibility = View.VISIBLE
                noticeView?.text = "Chỉ nhắn tin được sau khi cả hai đồng ý kết bạn."
            }
        }
    }

    private fun setupActions() {
        binding.btnSendFriendRequest.setOnClickListener {
            val isAlreadyFriend = CineConnectStateManager.isFriend(currentUserName)
            val isSent = CineConnectStateManager.isRequestSent(currentUserName)

            when {
                isAlreadyFriend -> {
                    findNavController().navigate(
                        R.id.chatFragment,
                        bundleOf("friend_name" to currentUserName)
                    )
                }
                !isSent -> {
                    CineConnectStateManager.sendFriendRequest(currentUserName)
                    updateFriendButtonUI()
                    showCineMessage(
                        message = "Đã gửi lời mời kết bạn tới $currentUserName",
                        actionLabel = "HOÀN TÁC",
                        onAction = {
                            CineConnectStateManager.cancelFriendRequest(currentUserName)
                            updateFriendButtonUI()
                            showCineMessage("Đã hủy lời mời kết bạn tới $currentUserName")
                        }
                    )
                }
                else -> {
                    CineConnectStateManager.cancelFriendRequest(currentUserName)
                    updateFriendButtonUI()
                    showCineMessage("Đã hủy lời mời kết bạn tới $currentUserName")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}