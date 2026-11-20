package com.ute.cinenow.ui.cineconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentUserProfileBinding
import com.ute.cinenow.utils.showCineMessage

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private var isFriendRequestSent = false

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
        setupHeader()
        setupActions()
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupActions() {
        val targetName = binding.tvUserName.text.toString()

        binding.tvAlgorithmBadge.setOnClickListener {
            showCineMessage("Độ khớp gu tính theo thể loại, định dạng rạp và khung giờ")
        }

        binding.btnSendFriendRequest.setOnClickListener {
            val previousState = isFriendRequestSent
            isFriendRequestSent = !previousState
            binding.btnSendFriendRequest.text = if (isFriendRequestSent) {
                "Đã gửi lời mời kết bạn"
            } else {
                "Gửi lời mời kết bạn"
            }

            if (isFriendRequestSent) {
                showCineMessage(
                    message = "Đã gửi lời mời kết bạn tới $targetName",
                    actionLabel = "HOÀN TÁC",
                    onAction = {
                        isFriendRequestSent = previousState
                        binding.btnSendFriendRequest.text = "Gửi lời mời kết bạn"
                        showCineMessage("Đã hủy lời mời kết bạn tới $targetName")
                    }
                )
            } else {
                showCineMessage(
                    message = "Đã hủy lời mời kết bạn tới $targetName",
                    actionLabel = "HOÀN TÁC",
                    onAction = {
                        isFriendRequestSent = true
                        binding.btnSendFriendRequest.text = "Đã gửi lời mời kết bạn"
                        showCineMessage("Đã gửi lại lời mời kết bạn tới $targetName")
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}