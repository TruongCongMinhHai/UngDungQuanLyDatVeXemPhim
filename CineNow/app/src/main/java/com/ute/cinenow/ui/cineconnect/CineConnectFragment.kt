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
        // 1. Bấm "Xem tất cả" hoặc cả hàng để mở Lời mời kết bạn
        binding.tvViewAllRequests.setOnClickListener {
            findNavController().navigate(R.id.friendRequestsFragment)
        }
        (binding.tvViewAllRequests.parent as? View)?.setOnClickListener {
            findNavController().navigate(R.id.friendRequestsFragment)
        }
        // 2. Bấm "Xem tất cả" hoặc cả hàng để mở Danh sách bạn bè
        binding.tvViewAllFriends.setOnClickListener {
            findNavController().navigate(R.id.friendsFragment)
        }
        (binding.tvViewAllFriends.parent as? View)?.setOnClickListener {
            findNavController().navigate(R.id.friendsFragment)
        }
        // 3. Bấm "Xem tất cả" bên phải "Khám phá bạn bè" -> Nhảy qua trang Người phù hợp
        binding.tvViewAllExplore.setOnClickListener {
            findNavController().navigate(R.id.matchedPeopleFragment)
        }
        // Cho phép bấm cả cụm text + icon mũi tên bên cạnh
        (binding.tvViewAllExplore.parent as? View)?.setOnClickListener {
            findNavController().navigate(R.id.matchedPeopleFragment)
        }
    }

    // Cập nhật số lượng lời mời kết bạn chính xác theo dữ liệu thực tế
    private fun updateRequestsBadgeText() {
        // Lấy số lượng lời mời đang chờ nhận từ CineConnectStateManager
        val count = CineConnectStateManager.getReceivedRequests().size
        binding.tvFriendRequestsTitle.text = "Lời mời kết bạn ($count)"
    }

    // Lỗi 1: Bấm Hoàng Nam, Vũ Mai Anh, Bảo Minh mở đúng chat của người đó
    private fun setupOnlineFriendsClick() {
        fun scanFriendsSection(view: View) {
            if (view is TextView && view.text.toString().trim() == "Bạn bè của bạn") {
                val parentSection = view.parent?.parent as? ViewGroup ?: return
                for (i in 0 until parentSection.childCount) {
                    val child = parentSection.getChildAt(i)
                    if (child is LinearLayout && child != view.parent) {
                        for (colIdx in 0 until child.childCount) {
                            val friendCol = child.getChildAt(colIdx) as? LinearLayout ?: continue
                            var friendName = ""
                            for (t in 0 until friendCol.childCount) {
                                val item = friendCol.getChildAt(t)
                                if (item is TextView && item.text.isNotEmpty() &&
                                    !item.text.contains("Online") && !item.text.contains("phút") && !item.text.contains("Hôm qua")) {
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
        binding.btnFilter.setOnClickListener {
            val kw = binding.etSearchUser.text.toString().trim()
            findNavController().navigate(
                R.id.matchedPeopleFragment,
                bundleOf("search_keyword" to kw)
            )
        }
    }

    // Lỗi 5, 6, 7, 8: Thẻ người dùng đồng bộ rạp, % khớp gu và trạng thái bạn bè
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
                    val user = CineConnectStateManager.getUser(finalName)

                    fun refreshBtnUI() {
                        when (user.status) {
                            CineConnectStateManager.ConnectionStatus.FRIEND -> {
                                actionBtn?.text = "Nhắn tin"
                                actionBtn?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
                            }
                            CineConnectStateManager.ConnectionStatus.REQUEST_SENT -> {
                                actionBtn?.text = "Đã gửi lời mời"
                                actionBtn?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#94A3B8"))
                            }
                            else -> {
                                actionBtn?.text = "Kết bạn"
                                actionBtn?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC"))
                            }
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
                        if (CineConnectStateManager.isFriend(finalName)) {
                            findNavController().navigate(
                                R.id.chatFragment,
                                bundleOf("friend_name" to finalName)
                            )
                        } else if (!CineConnectStateManager.isRequestSent(finalName)) {
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