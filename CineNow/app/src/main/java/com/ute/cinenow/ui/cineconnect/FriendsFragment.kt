package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.ute.cinenow.databinding.FragmentFriendsBinding
import com.ute.cinenow.utils.showCineMessage

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setupSearchAndChips()
        setupFriendCards()
        setupBottomBar()
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSearchAndChips() {
        val allFriendCards: List<Pair<View, String>> = listOf(
            Pair(binding.cardFriend1, "minh quân khoa học viễn tưởng hành động hành tinh cát 2"),
            Pair(binding.cardFriend2, "thảo vy tình cảm hoạt hình ghibli mai"),
            Pair(binding.cardFriend3, "hoàng nam imax fan định dạng lớn thứ 6 cuối tuần"),
            Pair(binding.cardFriend4, "phương linh kinh dị giật gân trinh thám")
        )

        val onlineCards: List<View> = listOf(
            binding.cardFriend1,
            binding.cardFriend2,
            binding.cardFriend3
        )

        binding.etSearchFriends.doAfterTextChanged { editable ->
            val keyword = editable?.toString()?.trim()?.lowercase().orEmpty()
            for (item in allFriendCards) {
                val card = item.first
                val content = item.second
                card.visibility = if (keyword.isEmpty() || content.contains(keyword)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }

        binding.btnFriendFilter.setOnClickListener {
            showCineMessage("Đang lọc danh sách bạn bè xem phim")
        }

        binding.chipAllFriends.setOnClickListener {
            binding.etSearchFriends.setText("")
            binding.chipAllFriends.backgroundTintList = ColorStateList.valueOf("#5B3DF5".toColorInt())
            binding.chipOnlineFriends.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            binding.chipSameTaste.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            for (item in allFriendCards) {
                item.first.visibility = View.VISIBLE
            }
            showCineMessage("Hiển thị tất cả bạn bè (18)")
        }

        binding.chipOnlineFriends.setOnClickListener {
            binding.chipOnlineFriends.backgroundTintList = ColorStateList.valueOf("#EEF2FF".toColorInt())
            binding.chipAllFriends.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            binding.chipSameTaste.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            for (item in allFriendCards) {
                item.first.visibility = if (onlineCards.contains(item.first)) View.VISIBLE else View.GONE
            }
            showCineMessage("Đang hiển thị bạn bè online")
        }

        binding.chipSameTaste.setOnClickListener {
            binding.chipSameTaste.backgroundTintList = ColorStateList.valueOf("#EEF2FF".toColorInt())
            binding.chipAllFriends.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            binding.chipOnlineFriends.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            for (item in allFriendCards) {
                item.first.visibility = View.VISIBLE
            }
            showCineMessage("Đã sắp xếp theo độ khớp gu điện ảnh")
        }
    }

    private fun setupFriendCards() {
        val chatButtons: List<Pair<View, String>> = listOf(
            Pair(binding.btnChatFriend1, "Minh Quân"),
            Pair(binding.btnChatFriend2, "Thảo Vy"),
            Pair(binding.btnChatFriend3, "Hoàng Nam"),
            Pair(binding.btnChatFriend4, "Phương Linh")
        )

        for (item in chatButtons) {
            val btnChat = item.first
            val friendName = item.second
            btnChat.setOnClickListener {
                showCineMessage("Mở khung trò chuyện với $friendName")
            }
        }

        val inviteButtons: List<Pair<MaterialButton, String>> = listOf(
            Pair(binding.btnInviteFriend1, "Minh Quân"),
            Pair(binding.btnInviteFriend2, "Thảo Vy"),
            Pair(binding.btnInviteFriend3, "Hoàng Nam"),
            Pair(binding.btnInviteFriend4, "Phương Linh")
        )

        for (item in inviteButtons) {
            val btnInvite = item.first
            val friendName = item.second
            btnInvite.setOnClickListener {
                showCineMessage(
                    message = "Đã gửi lời mời xem phim tới $friendName",
                    actionLabel = "HOÀN TÁC",
                    onAction = {
                        showCineMessage("Đã thu hồi lời mời xem phim gửi tới $friendName")
                    }
                )
            }
        }

        var connectedNearby = false
        binding.btnNearbyConnect.setOnClickListener {
            connectedNearby = !connectedNearby
            if (connectedNearby) {
                binding.btnNearbyConnect.text = "Đã gửi"
                showCineMessage(
                    message = "Đã gửi lời mời kết bạn tại CineNow Landmark 81",
                    actionLabel = "HOÀN TÁC",
                    onAction = {
                        connectedNearby = false
                        binding.btnNearbyConnect.text = "Kết bạn"
                        showCineMessage("Đã hủy lời mời kết bạn gần rạp")
                    }
                )
            } else {
                binding.btnNearbyConnect.text = "Kết bạn"
                showCineMessage("Đã hủy lời mời kết bạn gần rạp")
            }
        }

        binding.btnFriendRequestsBadge.setOnClickListener {
            showCineMessage("Mở danh sách lời mời kết bạn")
        }
    }

    private fun setupBottomBar() {
        binding.navTabFriends.setOnClickListener {
            showCineMessage("Đang ở tab Bạn bè")
        }
        binding.navTabChat.setOnClickListener {
            showCineMessage("Chuyển sang tab Trò chuyện")
        }
        binding.navTabInviteMovie.setOnClickListener {
            showCineMessage("Chuyển sang tab Mời phim")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}