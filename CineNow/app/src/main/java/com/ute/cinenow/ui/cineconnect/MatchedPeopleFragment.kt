package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentMatchedPeopleBinding
import com.ute.cinenow.utils.showCineMessage

class MatchedPeopleFragment : Fragment() {

    private var _binding: FragmentMatchedPeopleBinding? = null
    private val binding get() = _binding!!

    private val memberData = listOf(
        "Trần Bảo Long",
        "Nguyễn Thảo Ly",
        "Đặng Tuấn Kiệt",
        "Lê Hoàng Minh"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchedPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setupSearchAndFilter()
        setupMemberActions()

        val initialKw = arguments?.getString("search_keyword")
        if (!initialKw.isNullOrBlank()) {
            binding.etSearchPeople.setText(initialKw)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshAllAddButtons()
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            val popped = try { findNavController().popBackStack() } catch (_: Exception) { false }
            if (!popped) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun refreshAllAddButtons() {
        val addButtons = listOf(
            binding.btnAddFriend1 to memberData[0],
            binding.btnAddFriend2 to memberData[1],
            binding.btnAddFriend3 to memberData[2],
            binding.btnAddFriend4 to memberData[3]
        )
        for ((btn, name) in addButtons) {
            val isFriend = CineConnectStateManager.isFriend(name)
            val isSent = CineConnectStateManager.isRequestSent(name)

            when {
                isFriend -> {
                    btn.text = "Nhắn tin"
                    btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#10B981"))
                }
                isSent -> {
                    btn.text = "Đã gửi lời mời"
                    btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#94A3B8"))
                }
                else -> {
                    btn.text = "Kết bạn"
                    btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
                }
            }
        }
    }

    private fun setupSearchAndFilter() {
        val memberCards: List<Pair<View, String>> = listOf(
            Pair(binding.cardMember1, "trần bảo long cinenow landmark 81 imax khoa học viễn tưởng 4dx"),
            Pair(binding.cardMember2, "nguyễn thảo ly cinenow hai bà trưng anime tâm lý ghibli"),
            Pair(binding.cardMember3, "đặng tuấn kiệt cinenow bitexco q1 hành động phiêu lưu marvel"),
            Pair(binding.cardMember4, "lê hoàng minh cinenow landmark 81 tâm lý giật gân nolan")
        )

        // Hiển thị ban đầu đúng số lượng thành viên thực tế (4 thành viên)
        binding.tvFoundCount.text = "Tìm thấy ${memberCards.size} thành viên"

        binding.etSearchPeople.doAfterTextChanged { editable ->
            val keyword = editable?.toString()?.trim()?.lowercase().orEmpty()
            var visibleCount = 0

            for (item in memberCards) {
                val card = item.first
                val searchContent = item.second
                val matched = keyword.isEmpty() || searchContent.contains(keyword)
                card.visibility = if (matched) View.VISIBLE else View.GONE
                if (matched) visibleCount++
            }

            // Đếm động chính xác theo số thẻ hiển thị
            binding.tvFoundCount.text = when {
                visibleCount == 0 -> "Không tìm thấy thành viên phù hợp"
                else -> "Tìm thấy $visibleCount thành viên"
            }
        }

        binding.btnFilterPeople.setOnClickListener {
            showCineMessage("Bộ lọc", subtitle = "Đang lọc thành viên phù hợp tại khu vực TP.HCM")
        }

        // Đồng bộ màu nền và màu chữ cho Chip để luôn dễ đọc
        val chips = listOf(binding.chipAll, binding.chipArea, binding.chipSciFi)
        fun highlightChip(target: View) {
            for (c in chips) {
                val isSelected = (c == target)
                c.backgroundTintList = ColorStateList.valueOf(
                    if (isSelected) Color.parseColor("#5B3DF5") else Color.WHITE
                )
                val textColor = if (isSelected) Color.WHITE else Color.parseColor("#334155")
                if (c is TextView) {
                    c.setTextColor(textColor)
                } else if (c is LinearLayout) {
                    val icon = c.getChildAt(0) as? ImageView
                    val tv = c.getChildAt(1) as? TextView
                    icon?.imageTintList = ColorStateList.valueOf(if (isSelected) Color.WHITE else Color.parseColor("#475569"))
                    tv?.setTextColor(textColor)
                }
            }
        }

        binding.chipAll.setOnClickListener {
            highlightChip(binding.chipAll)
            binding.etSearchPeople.setText("")
            showCineMessage("Hiển thị tất cả thành viên phù hợp")
        }

        binding.chipArea.setOnClickListener {
            highlightChip(binding.chipArea)
            binding.etSearchPeople.setText("TP.HCM")
            showCineMessage("Khu vực hiện tại: TP.HCM")
        }

        binding.chipSciFi.setOnClickListener {
            highlightChip(binding.chipSciFi)
            binding.etSearchPeople.setText("Khoa học viễn tưởng")
        }
    }

    private fun setupMemberActions() {
        val profileButtons = listOf(
            binding.btnViewProfile1 to memberData[0],
            binding.btnViewProfile2 to memberData[1],
            binding.btnViewProfile3 to memberData[2],
            binding.btnViewProfile4 to memberData[3]
        )

        for ((btnProfile, memberName) in profileButtons) {
            btnProfile.setOnClickListener {
                findNavController().navigate(
                    R.id.userProfileFragment,
                    bundleOf("user_name" to memberName)
                )
            }
        }

        val addButtons = listOf(
            binding.btnAddFriend1 to memberData[0],
            binding.btnAddFriend2 to memberData[1],
            binding.btnAddFriend3 to memberData[2],
            binding.btnAddFriend4 to memberData[3]
        )

        for ((btnAdd, memberName) in addButtons) {
            btnAdd.setOnClickListener {
                if (CineConnectStateManager.isFriend(memberName)) {
                    // Đã là bạn bè -> Mở chat
                    findNavController().navigate(
                        R.id.chatFragment,
                        bundleOf("friend_name" to memberName)
                    )
                } else if (!CineConnectStateManager.isRequestSent(memberName)) {
                    // Chưa gửi -> Gửi lời mời
                    CineConnectStateManager.sendFriendRequest(memberName)
                    refreshAllAddButtons()
                    showCineMessage(
                        message = "Đã gửi lời mời kết bạn tới $memberName",
                        actionLabel = "HOÀN TÁC",
                        onAction = {
                            CineConnectStateManager.cancelFriendRequest(memberName)
                            refreshAllAddButtons()
                            showCineMessage("Đã hủy lời mời kết bạn tới $memberName")
                        }
                    )
                } else {
                    // Bấm vào nút "Đã gửi lời mời" -> Hủy lời mời
                    CineConnectStateManager.cancelFriendRequest(memberName)
                    refreshAllAddButtons()
                    showCineMessage("Đã hủy lời mời kết bạn tới $memberName")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}