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
        setupNearbyCinema()
    }

    override fun onResume() {
        super.onResume()
        renderAllFriends()
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    // Lỗi 4: Xử lý thẻ "Gần rạp của bạn" - Mở danh sách người cùng rạp, KHÔNG gửi lời mời
    private fun setupNearbyCinema() {
        binding.btnNearbyConnect.text = "Xem người cùng rạp"
        binding.btnNearbyConnect.setOnClickListener {
            findNavController().navigate(
                R.id.matchedPeopleFragment,
                bundleOf("search_keyword" to "Landmark 81")
            )
        }
    }

    // Lỗi 5, 8, 12: Hiển thị đồng nhất toàn bộ bạn bè từ CineConnectStateManager
    private fun renderAllFriends() {
        val friends = CineConnectStateManager.getFriends()
        val totalFriendsCount = friends.size
        binding.header.title = "Bạn bè xem phim ($totalFriendsCount)"

        val container = binding.cardFriend1.parent as? ViewGroup ?: return
        for (item in dynamicFriendCards) {
            container.removeView(item.first)
        }
        dynamicFriendCards.clear()

        // Ẩn 4 thẻ mẫu tĩnh trong XML để render dữ liệu chuẩn từ CineConnectStateManager
        binding.cardFriend1.visibility = View.GONE
        binding.cardFriend2.visibility = View.GONE
        binding.cardFriend3.visibility = View.GONE
        binding.cardFriend4.visibility = View.GONE

        val density = resources.displayMetrics.density
        val baseIndex = container.indexOfChild(binding.cardFriend1)

        for ((idx, friend) in friends.withIndex()) {
            val card = createFriendCardView(friend, density)
            val searchKey = "${friend.name} ${friend.genres.joinToString(" ")} ${friend.favoriteCinema}".lowercase()
            dynamicFriendCards.add(Pair(card, searchKey))
            container.addView(card, baseIndex + idx)
        }
    }

    private fun createFriendCardView(friend: CineConnectStateManager.CineUser, density: Float): View {
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
            setImageResource(friend.avatarRes)
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
            text = "Rạp: ${friend.favoriteCinema}"
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

        // Hàng 2: Trạng thái & Gu phim
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
            text = "Thích: ${friend.genres.joinToString(", ")}"
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
        binding.etSearchFriends.doAfterTextChanged { editable ->
            val keyword = editable?.toString()?.trim()?.lowercase().orEmpty()
            for (item in dynamicFriendCards) {
                val card = item.first
                val content = item.second
                card.visibility = if (keyword.isEmpty() || content.contains(keyword)) View.VISIBLE else View.GONE
            }
        }

        binding.chipAllFriends.setOnClickListener {
            binding.etSearchFriends.setText("")
            for (item in dynamicFriendCards) item.first.visibility = View.VISIBLE
        }

        binding.chipOnlineFriends.setOnClickListener {
            val friends = CineConnectStateManager.getFriends()
            for ((index, item) in dynamicFriendCards.withIndex()) {
                val isOnline = if (index < friends.size) friends[index].isOnline else false
                item.first.visibility = if (isOnline) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupFriendCards() {
        binding.btnFriendRequestsBadge.setOnClickListener {
            findNavController().navigate(R.id.friendRequestsFragment)
        }
    }

    private fun setupBottomBar() {
        binding.navTabFriends.setOnClickListener {
            showCineMessage("Đang ở tab Bạn bè")
        }
        binding.navTabChat.setOnClickListener {
            findNavController().navigate(R.id.chatFragment)
        }
        binding.navTabInviteMovie.setOnClickListener {
            findNavController().navigate(R.id.inviteMovieFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}