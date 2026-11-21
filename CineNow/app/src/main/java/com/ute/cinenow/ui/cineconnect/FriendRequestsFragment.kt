package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColorInt
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentFriendRequestsBinding
import com.ute.cinenow.utils.showCineMessage

class FriendRequestsFragment : Fragment() {

    private var _binding: FragmentFriendRequestsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setupTabs()
        setupRequestActions()
        setupProfileShortcuts()
    }

    override fun onResume() {
        super.onResume()
        updateBadgesAndVisibility()
        renderDynamicSentRequests()
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            val popped = try { findNavController().popBackStack() } catch (_: Exception) { false }
            if (!popped) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun updateBadgesAndVisibility() {
        val remainingReceived = CineConnectStateManager.pendingReceivedRequests
        binding.tvReceivedBadge.text = remainingReceived.size.toString()
        binding.cardRequest1.visibility = if (remainingReceived.contains("Nguyễn Thảo Ly")) View.VISIBLE else View.GONE
        binding.cardRequest2.visibility = if (remainingReceived.contains("Đặng Tuấn Kiệt")) View.VISIBLE else View.GONE

        val sentList = CineConnectStateManager.sentRequestList
        binding.tvSentBadge.text = sentList.size.toString()
    }

    // Hiển thị danh sách động tất cả lời mời đã gửi (Trần Bảo Long, Bảo Huy, Thảo Vy...)
    private fun renderDynamicSentRequests() {
        val sentList = CineConnectStateManager.sentRequestList
        val sentContainer = binding.layoutSentContainer

        // Ẩn thẻ tĩnh mặc định trong XML để hiển thị danh sách đồng bộ từ Manager
        binding.cardSentRequest1.visibility = View.GONE

        // Xóa các card đã render động trước đó (giữ lại cardSentRequest1 tĩnh của xml)
        val viewsToRemove = mutableListOf<View>()
        for (i in 0 until sentContainer.childCount) {
            val child = sentContainer.getChildAt(i)
            if (child.id != R.id.cardSentRequest1) {
                viewsToRemove.add(child)
            }
        }
        viewsToRemove.forEach { sentContainer.removeView(it) }

        // Render từng người có trong sentRequestList
        for (personName in sentList) {
            val density = resources.displayMetrics.density

            val card = CardView(requireContext()).apply {
                radius = 20f * density
                cardElevation = 1f * density
                setCardBackgroundColor(Color.WHITE)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (12 * density).toInt()
                }
                layoutParams = lp
            }

            val cardContent = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                val p = (16 * density).toInt()
                setPadding(p, p, p, p)
            }

            // Hàng trên: Avatar tròn + Thông tin tên
            val topRow = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val avatarCard = CardView(requireContext()).apply {
                radius = 24f * density
                cardElevation = 0f
                setCardBackgroundColor(Color.parseColor("#E2E8F0"))
                val lp = LinearLayout.LayoutParams((48 * density).toInt(), (48 * density).toInt())
                layoutParams = lp
            }

            val avatarImg = ImageView(requireContext()).apply {
                setImageResource(R.drawable.ic_person)
                imageTintList = ColorStateList.valueOf(Color.parseColor("#64748B"))
                val pad = (8 * density).toInt()
                setPadding(pad, pad, pad, pad)
            }
            avatarCard.addView(avatarImg)

            val infoCol = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    marginStart = (12 * density).toInt()
                }
                layoutParams = lp
            }

            val tvName = TextView(requireContext()).apply {
                text = personName
                textSize = 14.5f
                setTextColor(Color.parseColor("#0F172A"))
                setTypeface(null, Typeface.BOLD)
            }

            val tvSub = TextView(requireContext()).apply {
                text = when (personName) {
                    "Trần Bảo Long" -> "Đã gửi vừa xong • 92% Khớp gu • Landmark 81"
                    "Bảo Huy & 1 người khác" -> "Đã gửi tại CineNow Landmark 81"
                    "Phạm Thảo Vy" -> "Đã gửi 2 giờ trước • 95% Khớp gu"
                    else -> "Đã gửi lời mời kết bạn"
                }
                textSize = 11.5f
                setTextColor(Color.parseColor("#64748B"))
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (2 * density).toInt()
                }
                layoutParams = lp
            }

            infoCol.addView(tvName)
            infoCol.addView(tvSub)

            topRow.addView(avatarCard)
            topRow.addView(infoCol)

            // Nút Hủy lời mời đã gửi
            val btnCancel = MaterialButton(requireContext()).apply {
                text = "Hủy lời mời đã gửi"
                textSize = 12f
                isAllCaps = false
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#334155"))
                backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F1F5F9"))
                cornerRadius = (14 * density).toInt()
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (42 * density).toInt()
                ).apply {
                    topMargin = (12 * density).toInt()
                }
                layoutParams = lp

                setOnClickListener {
                    CineConnectStateManager.cancelFriendRequest(personName)
                    updateBadgesAndVisibility()
                    renderDynamicSentRequests()
                    showCineMessage("Đã hủy lời mời kết bạn gửi tới $personName")
                }
            }

            cardContent.addView(topRow)
            cardContent.addView(btnCancel)
            card.addView(cardContent)

            // Cho phép bấm vào thẻ để xem hồ sơ cá nhân
            card.setOnClickListener {
                findNavController().navigate(
                    R.id.userProfileFragment,
                    bundleOf("user_name" to personName)
                )
            }

            sentContainer.addView(card)
        }
    }

    private fun setupTabs() {
        val activeTabBg = "#FFFFFF".toColorInt()
        val transparentBg = "#00000000".toColorInt()
        val activeText = "#4F46E5".toColorInt()
        val inactiveText = "#475569".toColorInt()
        val activeBadgeBg = "#5B3DF5".toColorInt()
        val inactiveBadgeBg = "#E2E8F0".toColorInt()

        binding.tabReceived.setOnClickListener {
            binding.tabReceived.backgroundTintList = ColorStateList.valueOf(activeTabBg)
            binding.tvTabReceivedTitle.setTextColor(activeText)
            binding.tvReceivedBadge.backgroundTintList = ColorStateList.valueOf(activeBadgeBg)
            binding.tvReceivedBadge.setTextColor("#FFFFFF".toColorInt())

            binding.tabSent.backgroundTintList = ColorStateList.valueOf(transparentBg)
            binding.tvTabSentTitle.setTextColor(inactiveText)
            binding.tvSentBadge.backgroundTintList = ColorStateList.valueOf(inactiveBadgeBg)
            binding.tvSentBadge.setTextColor(inactiveText)

            binding.layoutReceivedContainer.visibility = View.VISIBLE
            binding.layoutSentContainer.visibility = View.GONE
        }

        binding.tabSent.setOnClickListener {
            binding.tabSent.backgroundTintList = ColorStateList.valueOf(activeTabBg)
            binding.tvTabSentTitle.setTextColor(activeText)
            binding.tvSentBadge.backgroundTintList = ColorStateList.valueOf(activeBadgeBg)
            binding.tvSentBadge.setTextColor("#FFFFFF".toColorInt())

            binding.tabReceived.backgroundTintList = ColorStateList.valueOf(transparentBg)
            binding.tvTabReceivedTitle.setTextColor(inactiveText)
            binding.tvReceivedBadge.backgroundTintList = ColorStateList.valueOf(inactiveBadgeBg)
            binding.tvReceivedBadge.setTextColor(inactiveText)

            binding.layoutReceivedContainer.visibility = View.GONE
            binding.layoutSentContainer.visibility = View.VISIBLE
            renderDynamicSentRequests()
        }
    }

    private fun setupProfileShortcuts() {
        binding.cardRequest1.setOnClickListener {
            findNavController().navigate(
                R.id.userProfileFragment,
                bundleOf("user_name" to "Nguyễn Thảo Ly")
            )
        }
        binding.cardRequest2.setOnClickListener {
            findNavController().navigate(
                R.id.userProfileFragment,
                bundleOf("user_name" to "Đặng Tuấn Kiệt")
            )
        }
    }

    private fun setupRequestActions() {
        val items = listOf(
            Triple(binding.cardRequest1, binding.btnAccept1, binding.btnDecline1) to "Nguyễn Thảo Ly",
            Triple(binding.cardRequest2, binding.btnAccept2, binding.btnDecline2) to "Đặng Tuấn Kiệt"
        )

        for ((controls, name) in items) {
            val (_, btnAccept, btnDecline) = controls

            btnAccept.setOnClickListener {
                CineConnectStateManager.acceptFriendRequest(name)
                updateBadgesAndVisibility()

                showCineMessage(
                    message = "Đã đồng ý kết bạn với $name",
                    subtitle = "$name đã trở thành bạn bè và được chuyển vào danh sách Bạn bè!",
                    actionLabel = "XEM BẠN BÈ",
                    onAction = {
                        findNavController().navigate(R.id.friendsFragment)
                    }
                )
            }

            btnDecline.setOnClickListener {
                CineConnectStateManager.declineFriendRequest(name)
                updateBadgesAndVisibility()

                showCineMessage(
                    message = "Đã từ chối lời mời kết bạn của $name",
                    actionLabel = "HOÀN TÁC",
                    onAction = {
                        if (!CineConnectStateManager.pendingReceivedRequests.contains(name)) {
                            CineConnectStateManager.pendingReceivedRequests.add(name)
                        }
                        updateBadgesAndVisibility()
                        showCineMessage("Đã khôi phục lời mời kết bạn của $name")
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