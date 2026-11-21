package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentFriendsBinding
import com.ute.cinenow.utils.showCineMessage

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    // Lưu trữ các thẻ bạn bè được thêm động (khi vừa chấp nhận kết bạn)
    private val dynamicFriendCards = mutableListOf<Pair<View, String>>()

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

    override fun onResume() {
        super.onResume()
        updateNearbyButtonState()
        renderNewlyAddedFriends()
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    // Hiển thị các bạn bè mới được chấp nhận từ CineConnectStateManager
    private fun renderNewlyAddedFriends() {
        val container = binding.cardFriend1.parent as? ViewGroup ?: return

        // Xóa các thẻ động cũ trước khi render lại để không bị trùng lặp
        for (item in dynamicFriendCards) {
            container.removeView(item.first)
        }
        dynamicFriendCards.clear()

        val defaultNames = listOf("Minh Quân", "Thảo Vy", "Hoàng Nam", "Phương Linh")
        val newFriends = CineConnectStateManager.friendList.filter { friend ->
            !defaultNames.any { it.equals(friend.name, ignoreCase = true) }
        }

        val density = resources.displayMetrics.density
        val baseIndex = container.indexOfChild(binding.cardFriend1)

        for ((offset, friend) in newFriends.withIndex()) {
            val card = createFriendCardView(friend, density)
            val searchKey = "${friend.name} ${friend.role} ${friend.subtitle}".lowercase()
            dynamicFriendCards.add(Pair(card, searchKey))
            container.addView(card, baseIndex + offset)
        }
    }

    private fun createFriendCardView(friend: CineConnectStateManager.FriendItem, density: Float): View {
        val dp = { value: Int -> (value * density).toInt() }

        val card = CardView(requireContext()).apply {
            radius = 20f * density
            cardElevation = 1f * density
            setCardBackgroundColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(12)
            }
        }

        val content = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val p = dp(15)
            setPadding(p, p, p, p)
        }

        // Hàng 1: Avatar + Tên + Badge khớp gu
        val topRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val avatarFrame = FrameLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(dp(46), dp(46))
        }
        val avatarCard = CardView(requireContext()).apply {
            radius = 23f * density
            cardElevation = 0f
            setCardBackgroundColor(Color.parseColor("#E2E8F0"))
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
        val avatarImg = ImageView(requireContext()).apply {
            setImageResource(R.drawable.ic_person)
            imageTintList = ColorStateList.valueOf(Color.parseColor("#475569"))
            setPadding(dp(7), dp(7), dp(7), dp(7))
        }
        avatarCard.addView(avatarImg)
        avatarFrame.addView(avatarCard)

        if (friend.isOnline) {
            val dot = View(requireContext()).apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_camera_circle)
                backgroundTintList = ColorStateList.valueOf(Color.parseColor("#10B981"))
                layoutParams = FrameLayout.LayoutParams(dp(11), dp(11)).apply {
                    gravity = Gravity.BOTTOM or Gravity.END
                }
            }
            avatarFrame.addView(dot)
        }

        val nameCol = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = dp(10)
            }
        }
        val tvName = TextView(requireContext()).apply {
            text = friend.name
            textSize = 14f
            setTextColor(Color.parseColor("#0F172A"))
            setTypeface(null, Typeface.BOLD)
        }
        val tvRole = TextView(requireContext()).apply {
            text = "Thích: ${friend.role}"
            textSize = 11f
            setTextColor(Color.parseColor("#64748B"))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(2)
            }
        }
        nameCol.addView(tvName)
        nameCol.addView(tvRole)

        val tvMatch = TextView(requireContext()).apply {
            text = "Khớp gu: ${friend.matchPercent}%"
            textSize = 10f
            setTextColor(Color.parseColor("#4F46E5"))
            setTypeface(null, Typeface.BOLD)
            setBackgroundResource(R.drawable.bg_rounded_icon)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EEF2FF"))
            setPadding(dp(8), dp(3), dp(8), dp(3))
        }

        topRow.addView(avatarFrame)
        topRow.addView(nameCol)
        topRow.addView(tvMatch)

        // Hàng 2: Trạng thái (Vừa kết bạn mới)
        val statusRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundResource(R.drawable.bg_rounded_icon)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
            setPadding(dp(10), dp(6), dp(10), dp(6))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(10)
            }
        }
        val iconTicket = ImageView(requireContext()).apply {
            setImageResource(R.drawable.ic_ticket)
            imageTintList = ColorStateList.valueOf(Color.parseColor("#4F46E5"))
            layoutParams = LinearLayout.LayoutParams(dp(13), dp(13))
        }
        val tvStatus = TextView(requireContext()).apply {
            text = friend.subtitle
            textSize = 11f
            setTextColor(Color.parseColor("#0F172A"))
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                marginStart = dp(6)
            }
        }
        statusRow.addView(iconTicket)
        statusRow.addView(tvStatus)

        // Hàng 3: Nút Nhắn tin & Mời xem phim
        val actionRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(12)
            }
        }

        val btnChat = ImageButton(requireContext()).apply {
            setImageResource(R.drawable.ic_auth_mail_filled)
            imageTintList = ColorStateList.valueOf(Color.parseColor("#475569"))
            setBackgroundResource(R.drawable.bg_rounded_icon)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F1F5F9"))
            scaleType = ImageView.ScaleType.FIT_CENTER
            setPadding(dp(10), dp(10), dp(10), dp(10))
            layoutParams = LinearLayout.LayoutParams(dp(42), dp(40)).apply {
                marginEnd = dp(8)
            }
            setOnClickListener {
                findNavController().navigate(
                    R.id.chatFragment,
                    bundleOf("friend_name" to friend.name)
                )
            }
        }

        val btnInvite = MaterialButton(requireContext()).apply {
            text = "Mời xem phim"
            isAllCaps = false
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5B3DF5"))
            cornerRadius = dp(12)
            layoutParams = LinearLayout.LayoutParams(0, dp(40), 1f)
            setOnClickListener {
                findNavController().navigate(
                    R.id.inviteMovieFragment,
                    bundleOf("friend_name" to friend.name)
                )
            }
        }

        actionRow.addView(btnChat)
        actionRow.addView(btnInvite)

        content.addView(topRow)
        content.addView(statusRow)
        content.addView(actionRow)
        card.addView(content)

        return card
    }

    private fun setupSearchAndChips() {
        val staticFriendCards: List<Pair<View, String>> = listOf(
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
            val allCards = dynamicFriendCards + staticFriendCards
            for (item in allCards) {
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
            val allCards = dynamicFriendCards + staticFriendCards
            for (item in allCards) {
                item.first.visibility = View.VISIBLE
            }
            showCineMessage("Hiển thị tất cả bạn bè")
        }

        binding.chipOnlineFriends.setOnClickListener {
            binding.chipOnlineFriends.backgroundTintList = ColorStateList.valueOf("#EEF2FF".toColorInt())
            binding.chipAllFriends.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            binding.chipSameTaste.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            val allCards = dynamicFriendCards + staticFriendCards
            for (item in allCards) {
                val isOnline = onlineCards.contains(item.first) || dynamicFriendCards.any { it.first == item.first }
                item.first.visibility = if (isOnline) View.VISIBLE else View.GONE
            }
            showCineMessage("Đang hiển thị bạn bè online")
        }

        binding.chipSameTaste.setOnClickListener {
            binding.chipSameTaste.backgroundTintList = ColorStateList.valueOf("#EEF2FF".toColorInt())
            binding.chipAllFriends.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            binding.chipOnlineFriends.backgroundTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
            val allCards = dynamicFriendCards + staticFriendCards
            for (item in allCards) {
                item.first.visibility = View.VISIBLE
            }
            showCineMessage("Đã sắp xếp theo độ khớp gu điện ảnh")
        }
    }

    private fun updateNearbyButtonState() {
        val targetName = "Trần Bảo Long"
        val isSent = CineConnectStateManager.isRequestSent(targetName)
        if (isSent) {
            binding.btnNearbyConnect.text = "Đã gửi"
            binding.btnNearbyConnect.backgroundTintList = ColorStateList.valueOf("#94A3B8".toColorInt())
        } else {
            binding.btnNearbyConnect.text = "Kết bạn"
            binding.btnNearbyConnect.backgroundTintList = ColorStateList.valueOf("#5850EC".toColorInt())
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
                findNavController().navigate(
                    R.id.chatFragment,
                    bundleOf("friend_name" to friendName)
                )
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
                findNavController().navigate(
                    R.id.inviteMovieFragment,
                    bundleOf("friend_name" to friendName)
                )
            }
        }

        val nearbyTarget = "Trần Bảo Long"
        updateNearbyButtonState()

        binding.btnNearbyConnect.setOnClickListener {
            val isSent = CineConnectStateManager.isRequestSent(nearbyTarget)
            if (!isSent) {
                CineConnectStateManager.sendFriendRequest(nearbyTarget)
                binding.btnNearbyConnect.text = "Đã gửi"
                binding.btnNearbyConnect.backgroundTintList = ColorStateList.valueOf("#94A3B8".toColorInt())
                showCineMessage(
                    message = "Đã gửi lời mời kết bạn tới $nearbyTarget",
                    actionLabel = "HOÀN TÁC",
                    onAction = {
                        CineConnectStateManager.cancelFriendRequest(nearbyTarget)
                        binding.btnNearbyConnect.text = "Kết bạn"
                        binding.btnNearbyConnect.backgroundTintList = ColorStateList.valueOf("#5850EC".toColorInt())
                        showCineMessage("Đã hủy lời mời kết bạn tới $nearbyTarget")
                    }
                )
            } else {
                CineConnectStateManager.cancelFriendRequest(nearbyTarget)
                binding.btnNearbyConnect.text = "Kết bạn"
                binding.btnNearbyConnect.backgroundTintList = ColorStateList.valueOf("#5850EC".toColorInt())
                showCineMessage("Đã hủy lời mời kết bạn tới $nearbyTarget")
            }
        }

        binding.btnFriendRequestsBadge.setOnClickListener {
            try {
                findNavController().navigate(R.id.friendRequestsFragment)
            } catch (e: Exception) {
                showCineMessage("Mở danh sách lời mời kết bạn")
            }
        }
    }

    private fun setupBottomBar() {
        binding.navTabFriends.setOnClickListener {
            showCineMessage("Đang ở tab Bạn bè")
        }
        binding.navTabChat.setOnClickListener {
            try {
                findNavController().navigate(R.id.chatFragment)
            } catch (e: Exception) {
                showCineMessage("Chuyển sang tab Trò chuyện")
            }
        }
        binding.navTabInviteMovie.setOnClickListener {
            try {
                findNavController().navigate(R.id.inviteMovieFragment)
            } catch (e: Exception) {
                showCineMessage("Chuyển sang tab Mời phim")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}