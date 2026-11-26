package com.ute.cinenow.ui.cineconnect

import android.app.AlertDialog
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

    // Lỗi 5: Cập nhật badge tab Đã nhận và Đã gửi động
    private fun updateBadgesAndVisibility() {
        val remainingReceived = CineConnectStateManager.getReceivedRequests()
        binding.tvReceivedBadge.text = remainingReceived.size.toString()
        binding.cardRequest1.visibility = if (remainingReceived.any { it.name == "Nguyễn Thảo Ly" }) View.VISIBLE else View.GONE
        binding.cardRequest2.visibility = if (remainingReceived.any { it.name == "Đặng Tuấn Kiệt" }) View.VISIBLE else View.GONE

        val sentList = CineConnectStateManager.getSentRequests()
        binding.tvSentBadge.text = sentList.size.toString()
    }

    private fun renderDynamicSentRequests() {
        val sentList = CineConnectStateManager.getSentRequests()
        val sentContainer = binding.layoutSentContainer
        binding.cardSentRequest1.visibility = View.GONE

        val viewsToRemove = mutableListOf<View>()
        for (i in 0 until sentContainer.childCount) {
            val child = sentContainer.getChildAt(i)
            if (child.id != R.id.cardSentRequest1) {
                viewsToRemove.add(child)
            }
        }
        viewsToRemove.forEach { sentContainer.removeView(it) }

        for (user in sentList) {
            val density = resources.displayMetrics.density

            val card = CardView(requireContext()).apply {
                radius = 20f * density
                cardElevation = 1f * density
                setCardBackgroundColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (12 * density).toInt()
                }
            }

            val cardContent = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                val p = (16 * density).toInt()
                setPadding(p, p, p, p)
            }

            val topRow = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val avatarCard = CardView(requireContext()).apply {
                radius = 24f * density
                cardElevation = 0f
                setCardBackgroundColor(Color.parseColor("#E2E8F0"))
                layoutParams = LinearLayout.LayoutParams((48 * density).toInt(), (48 * density).toInt())
            }

            val avatarImg = ImageView(requireContext()).apply {
                setImageResource(user.avatarRes)
                imageTintList = null
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            avatarCard.addView(avatarImg)

            val infoCol = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    marginStart = (12 * density).toInt()
                }
            }

            val tvName = TextView(requireContext()).apply {
                text = user.name
                textSize = 14.5f
                setTextColor(Color.parseColor("#0F172A"))
                setTypeface(null, Typeface.BOLD)
            }

            val tvSub = TextView(requireContext()).apply {
                text = "Đã gửi • ${user.matchPercent}% Khớp gu • ${user.favoriteCinema}"
                textSize = 11.5f
                setTextColor(Color.parseColor("#64748B"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (2 * density).toInt()
                }
            }

            infoCol.addView(tvName)
            infoCol.addView(tvSub)

            topRow.addView(avatarCard)
            topRow.addView(infoCol)

            // Lỗi 14: Hiện hộp thoại xác nhận khi bấm "Hủy lời mời đã gửi"
            val btnCancel = MaterialButton(requireContext()).apply {
                text = "Hủy lời mời đã gửi"
                textSize = 12f
                isAllCaps = false
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#334155"))
                backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F1F5F9"))
                cornerRadius = (14 * density).toInt()
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (42 * density).toInt()
                ).apply {
                    topMargin = (12 * density).toInt()
                }

                setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Hủy lời mời")
                        .setMessage("Bạn có chắc chắn muốn hủy lời mời kết bạn gửi tới ${user.name}?")
                        .setPositiveButton("Hủy lời mời") { _, _ ->
                            CineConnectStateManager.cancelFriendRequest(user.name)
                            updateBadgesAndVisibility()
                            renderDynamicSentRequests()
                            showCineMessage(
                                message = "Đã hủy lời mời kết bạn gửi tới ${user.name}",
                                actionLabel = "HOÀN TÁC",
                                onAction = {
                                    CineConnectStateManager.sendFriendRequest(user.name)
                                    updateBadgesAndVisibility()
                                    renderDynamicSentRequests()
                                }
                            )
                        }
                        .setNegativeButton("Đóng", null)
                        .show()
                }
            }

            cardContent.addView(topRow)
            cardContent.addView(btnCancel)
            card.addView(cardContent)

            card.setOnClickListener {
                findNavController().navigate(
                    R.id.userProfileFragment,
                    bundleOf("user_name" to user.name)
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

    // Lỗi 5 & 14: Xác nhận khi từ chối, cập nhật tức thì danh sách bạn bè
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
                    subtitle = "$name đã có trong Bạn bè của bạn!",
                    actionLabel = "XEM BẠN BÈ",
                    onAction = {
                        findNavController().navigate(R.id.friendsFragment)
                    }
                )
            }

            btnDecline.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Từ chối lời mời")
                    .setMessage("Bạn có chắc chắn muốn từ chối lời mời kết bạn của $name?")
                    .setPositiveButton("Từ chối") { _, _ ->
                        CineConnectStateManager.declineFriendRequest(name)
                        updateBadgesAndVisibility()

                        showCineMessage(
                            message = "Đã từ chối lời mời kết bạn của $name",
                            actionLabel = "HOÀN TÁC",
                            onAction = {
                                CineConnectStateManager.getUser(name).status = CineConnectStateManager.ConnectionStatus.REQUEST_RECEIVED
                                updateBadgesAndVisibility()
                                showCineMessage("Đã khôi phục lời mời kết bạn của $name")
                            }
                        )
                    }
                    .setNegativeButton("Hủy", null)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}