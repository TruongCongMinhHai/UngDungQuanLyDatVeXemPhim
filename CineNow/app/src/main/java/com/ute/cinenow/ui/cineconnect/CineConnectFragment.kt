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
        binding.tvViewAllRequests.setOnClickListener {
            findNavController().navigate(R.id.friendRequestsFragment)
        }

        binding.tvViewAllFriends.setOnClickListener {
            findNavController().navigate(R.id.friendsFragment)
        }
    }

    private fun setupOnlineFriendsClick() {
        val rootScroll = binding.root.getChildAt(1) as? ViewGroup
        val mainContent = rootScroll?.getChildAt(0) as? LinearLayout ?: return

        for (i in 0 until mainContent.childCount) {
            val item = mainContent.getChildAt(i)
            if (item is LinearLayout && item.childCount >= 2) {
                val headerRow = item.getChildAt(0) as? LinearLayout
                val firstTv = headerRow?.getChildAt(1) as? TextView
                if (firstTv?.text?.contains("Bạn bè của bạn", ignoreCase = true) == true) {
                    val friendsRow = item.getChildAt(1) as? LinearLayout ?: continue

                    for (fIndex in 0 until friendsRow.childCount) {
                        val friendCol = friendsRow.getChildAt(fIndex) as? LinearLayout ?: continue
                        val nameTv = friendCol.getChildAt(1) as? TextView ?: continue
                        val friendName = nameTv.text.toString().trim()

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
                    break
                }
            }
        }
    }

    private fun setupSearchAndFilter() {
        val rootScroll = binding.root.getChildAt(1) as? ViewGroup
        val mainContent = rootScroll?.getChildAt(0) as? LinearLayout ?: return

        for (i in 0 until mainContent.childCount) {
            val section = mainContent.getChildAt(i)
            if (section is LinearLayout) {
                val headerRow = section.getChildAt(0) as? LinearLayout
                if (headerRow != null) {
                    val titleTv = headerRow.getChildAt(1) as? TextView
                    val seeAllTv = headerRow.getChildAt(2) as? TextView
                    if (titleTv?.text?.contains("Khám phá bạn bè", ignoreCase = true) == true) {
                        seeAllTv?.isClickable = true
                        seeAllTv?.setOnClickListener {
                            findNavController().navigate(R.id.matchedPeopleFragment)
                        }
                    }
                }

                for (subIdx in 0 until section.childCount) {
                    val sub = section.getChildAt(subIdx)
                    if (sub is HorizontalScrollView) {
                        val chipContainer = sub.getChildAt(0) as? LinearLayout ?: continue
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
                }
            }
        }

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

    private fun setupDiscoveredUserCards() {
        val rootScroll = binding.root.getChildAt(1) as? ViewGroup
        val mainContent = rootScroll?.getChildAt(0) as? LinearLayout ?: return

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

                    // QUY TẮC: NẾU ĐÃ LÀ BẠN BÈ (NGUYỄN THẢO LY) THÌ ẨN HOÀN TOÀN CARD KHỎI MỤC GỢI Ý KẾT BẠN
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

        traverseCards(mainContent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}