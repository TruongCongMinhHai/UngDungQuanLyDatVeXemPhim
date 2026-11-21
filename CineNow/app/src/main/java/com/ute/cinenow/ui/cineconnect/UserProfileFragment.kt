package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private fun loadUserDataFromArguments() {
        val passedName = arguments?.getString("user_name")
        if (!passedName.isNullOrBlank()) {
            currentUserName = passedName
            binding.tvUserName.text = passedName
            binding.tvUserHandle.text = when (passedName) {
                "Nguyễn Thảo Ly" -> "thaoly_cinema"
                "Đặng Tuấn Kiệt" -> "tuankiet_cine"
                "Lê Hoàng Minh" -> "hoangminh_imax"
                else -> "baolong_cinema"
            }
            binding.header.title = "Hồ sơ: $passedName"
        }
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            val popped = try { findNavController().popBackStack() } catch (_: Exception) { false }
            if (!popped) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun updateFriendButtonUI() {
        val isAlreadyFriend = CineConnectStateManager.isFriend(currentUserName)
        val isSent = CineConnectStateManager.isRequestSent(currentUserName)

        when {
            isAlreadyFriend -> {
                binding.btnSendFriendRequest.text = "Nhắn tin cho bạn bè"
                binding.btnSendFriendRequest.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#10B981"))
            }
            isSent -> {
                binding.btnSendFriendRequest.text = "Đã gửi lời mời kết bạn"
                binding.btnSendFriendRequest.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#94A3B8"))
            }
            else -> {
                binding.btnSendFriendRequest.text = "Gửi lời mời kết bạn"
                binding.btnSendFriendRequest.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
            }
        }
    }

    private fun setupActions() {
        binding.tvAlgorithmBadge.setOnClickListener {
            showCineMessage("Độ khớp gu 92%", subtitle = "Tính theo thể loại, định dạng rạp và khung giờ ưa thích")
        }

        binding.btnSendFriendRequest.setOnClickListener {
            val isAlreadyFriend = CineConnectStateManager.isFriend(currentUserName)
            val isSent = CineConnectStateManager.isRequestSent(currentUserName)

            when {
                isAlreadyFriend -> {
                    // Đã là bạn bè -> Mở ngay màn hình chat
                    findNavController().navigate(
                        R.id.chatFragment,
                        bundleOf("friend_name" to currentUserName)
                    )
                }
                !isSent -> {
                    // Chưa gửi -> Gửi lời mời kết bạn
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
                    // Đang là "Đã gửi lời mời kết bạn" bấm lần nữa -> Hủy và hiện lại "Gửi lời mời kết bạn"
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