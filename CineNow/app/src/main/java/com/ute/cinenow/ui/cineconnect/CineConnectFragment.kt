package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentCineConnectBinding
import com.ute.cinenow.utils.showCineMessage

class CineConnectFragment : Fragment() {

    private var _binding: FragmentCineConnectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCineConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setupTopActions()
        setupOnlineFriendsClick()
        setupSearchAndFilter()
    }

    override fun onResume() {
        super.onResume()
        updateRequestsBadgeText()
        setupDiscoveredUserCards()
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            val popped = try { findNavController().popBackStack() } catch (_: Exception) { false }
            if (!popped) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupTopActions() {
        // Cho phép bấm "Xem tất cả" hoặc cả hàng để mở Lời mời kết bạn
        binding.tvViewAllRequests.setOnClickListener {
            findNavController().navigate(R.id.friendRequestsFragment)
        }
        (binding.tvViewAllRequests.parent as? View)?.setOnClickListener {
            findNavController().navigate(R.id.friendRequestsFragment)
        }

        // Cho phép bấm "Xem tất cả" hoặc cả hàng để mở Danh sách bạn bè
        binding.tvViewAllFriends.setOnClickListener {
            findNavController().navigate(R.id.friendsFragment)
        }
        (binding.tvViewAllFriends.parent as? View)?.setOnClickListener {
            findNavController().navigate(R.id.friendsFragment)
        }
    }

    // Cập nhật số lượng lời mời kết bạn động theo trạng thái thực tế
    private fun updateRequestsBadgeText() {
        val count = CineConnectStateManager.pendingReceivedRequests.size
        fun scanForRequestsText(v: View) {
            if (v is TextView && v.text.toString().contains("Lời mời kết bạn", ignoreCase = true)) {
                v.text = "Lời mời kết bạn ($count)"
                return
            }
            if (v is ViewGroup) {
                for (i in 0 until v.childCount) {
                    scanForRequestsText(v.getChildAt(i))
                }
            }
        }
        scanForRequestsText(binding.root)
    }

    // Xử lý chạm vào danh sách bạn bè Online ở đầu trang
    private fun setupOnlineFriendsClick() {
        fun scanFriendsSection(view: View) {
            if (view is TextView && view.text.toString().trim() == "Bạn bè của bạn") {
                val parentSection = view.parent?.parent as? ViewGroup ?: return
                // Tìm hàng ngang chứa các avatar bạn bè
                for (i in 0 until parentSection.childCount) {
                    val child = parentSection.getChildAt(i)
                    if (child is LinearLayout && child != view.parent) {
                        for (colIdx in 0 until child.childCount) {
                            val friendCol = child.getChildAt(colIdx) as? LinearLayout ?: continue
                            // Tìm TextView tên bạn
                            var friendName = ""
                            for (t in 0 until friendCol.childCount) {
                                val item = friendCol.getChildAt(t)
                                if (item is TextView && item.text.isNotEmpty() && !item.text.contains("Online") && !item.text.contains("phút")) {
                                    friendName = item.text.toString().trim()
                                    break
                                }
                            }

                            if (friendName.isNotEmpty()) {
                                friendCol.isClickable = true
                                friendCol.isFocusable = true
                                friendCol.setOnClickListener {
                                    if (friendName.contains("Thêm", ignoreCase = true)) {
                                        findNavController().navigate(R.id.matchedPeopleFragment)
                                    } else {
                                        findNavController().navigate(
                                            R.id.chatFragment,
                                            bundleOf("friend_name" to friendName)
                                        )
                                    }
                                }
                            }
                        }
                        break
                    }
                }
                return
            }
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    scanFriendsSection(view.getChildAt(i))
                }
            }
        }
        scanFriendsSection(binding.root)
    }

    private fun setupSearchAndFilter() {
        // Tìm và gắn sự kiện cho nút "Xem tất cả" trong mục Khám phá bạn bè
        fun scanExploreHeader(v: View) {
            if (v is TextView && v.text.toString().trim() == "Khám phá bạn bè") {
                val headerRow = v.parent as? ViewGroup
                if (headerRow != null) {
                    for (i in 0 until headerRow.childCount) {
                        val child = headerRow.getChildAt(i)
                        if (child is TextView && child.text.toString().contains("Xem tất cả", ignoreCase = true)) {
                            child.isClickable = true
                            child.setOnClickListener {
                                findNavController().navigate(R.id.matchedPeopleFragment)
                            }
                        }
                    }
                }
            }
            if (v is ViewGroup) {
                for (i in 0 until v.childCount) {
                    scanExploreHeader(v.getChildAt(i))
                }
            }
        }
        scanExploreHeader(binding.root)

        // Tìm và gắn sự kiện cho các Chip lọc
        fun scanChips(v: View) {
            if (v is HorizontalScrollView) {
                val chipContainer = v.getChildAt(0) as? LinearLayout
                if (chipContainer != null) {
                    val chips = mutableListOf<TextView>()
                    for (c in 0 until chipContainer.childCount) {
                        val chip = chipContainer.getChildAt(c) as? TextView ?: continue
                        chips.add(chip)
                    }
                    for (chip in chips) {
                        chip.isClickable = true
                        chip.setOnClickListener {
                            val text = chip.text.toString().trim()
                            for (other in chips) {
                                val isCur = (other == chip)
                                other.backgroundTintList = ColorStateList.valueOf(
                                    if (isCur) Color.parseColor("#5850EC") else Color.WHITE
                                )
                                other.setTextColor(if (isCur) Color.WHITE else Color.parseColor("#475569"))
                            }
                            showCineMessage("Bộ lọc", subtitle = "Đang xem: $text")
                        }
                    }
                }
                return
            }
            if (v is ViewGroup) {
                for (i in 0 until v.childCount) {
                    scanChips(v.getChildAt(i))
                }
            }
        }
        scanChips(binding.root)

        // Ô tìm kiếm
        binding.etSearchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim()
                if (keyword.isNotEmpty()) {
                    binding.btnFilter.text = "Tìm kiếm"
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnFilter.setOnClickListener {
            val kw = binding.etSearchUser.text.toString().trim()
            findNavController().navigate(
                R.id.matchedPeopleFragment,
                bundleOf("search_keyword" to kw)
            )
        }
    }

    // Quản lý các thẻ gợi ý bạn bè khám phá (Nguyễn Thảo Ly, Trần Bảo Long)
    private fun setupDiscoveredUserCards() {
        fun traverseCards(view: View) {
            if (view is CardView) {
                var memberName = ""
                var profileBtn: MaterialButton? = null
                var actionBtn: MaterialButton? = null

                fun scanInner(v: View) {
                    if (v is TextView && memberName.isEmpty()) {
                        val text = v.text.toString().trim()
                        if (text == "Nguyễn Thảo Ly" || text == "Trần Bảo Long") {
                            memberName = text
                        }
                    }
                    if (v is MaterialButton) {
                        val btnText = v.text.toString().trim()
                        if (btnText.contains("Xem hồ sơ", ignoreCase = true)) {
                            profileBtn = v
                        } else if (btnText.contains("Kết bạn", ignoreCase = true) ||
                            btnText.contains("Đã gửi", ignoreCase = true) ||
                            btnText.contains("Nhắn tin", ignoreCase = true)
                        ) {
                            actionBtn = v
                        }
                    }
                    if (v is ViewGroup) {
                        for (k in 0 until v.childCount) {
                            scanInner(v.getChildAt(k))
                        }
                    }
                }

                scanInner(view)

                if (memberName.isNotEmpty()) {
                    val finalName = memberName

                    // Nếu đã là bạn bè thì ẩn hoàn toàn thẻ khỏi mục gợi ý kết bạn
                    if (CineConnectStateManager.isFriend(finalName)) {
                        view.visibility = View.GONE
                        return
                    } else {
                        view.visibility = View.VISIBLE
                    }

                    fun refreshBtnUI() {
                        val isSent = CineConnectStateManager.isRequestSent(finalName)
                        if (isSent) {
                            actionBtn?.text = "Đã gửi lời mời"
                            actionBtn?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#94A3B8"))
                        } else {
                            actionBtn?.text = "Kết bạn"
                            actionBtn?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
                        }
                    }

                    refreshBtnUI()

                    profileBtn?.setOnClickListener {
                        findNavController().navigate(
                            R.id.userProfileFragment,
                            bundleOf("user_name" to finalName)
                        )
                    }

                    actionBtn?.setOnClickListener {
                        val isSent = CineConnectStateManager.isRequestSent(finalName)
                        if (!isSent) {
                            CineConnectStateManager.sendFriendRequest(finalName)
                            refreshBtnUI()
                            showCineMessage(
                                message = "Đã gửi lời mời kết bạn tới $finalName",
                                actionLabel = "HOÀN TÁC",
                                onAction = {
                                    CineConnectStateManager.cancelFriendRequest(finalName)
                                    refreshBtnUI()
                                    showCineMessage("Đã hủy lời mời kết bạn tới $finalName")
                                }
                            )
                        } else {
                            CineConnectStateManager.cancelFriendRequest(finalName)
                            refreshBtnUI()
                            showCineMessage("Đã hủy lời mời kết bạn tới $finalName")
                        }
                    }
                }
            }

            if (view is ViewGroup) {
                for (idx in 0 until view.childCount) {
                    traverseCards(view.getChildAt(idx))
                }
            }
        }

        traverseCards(binding.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}