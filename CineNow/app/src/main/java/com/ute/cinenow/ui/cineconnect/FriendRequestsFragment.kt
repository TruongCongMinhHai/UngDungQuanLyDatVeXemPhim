package com.ute.cinenow.ui.cineconnect

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentFriendRequestsBinding
import com.ute.cinenow.utils.showCineMessage

class FriendRequestsFragment : Fragment() {

    private var _binding: FragmentFriendRequestsBinding? = null
    private val binding get() = _binding!!

    private var receivedCount = 2
    private var sentCount = 1

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
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun updateBadges() {
        binding.tvReceivedBadge.text = receivedCount.toString()
        binding.tvSentBadge.text = sentCount.toString()
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
        }
    }

    private fun setupRequestActions() {
        val items = listOf(
            Triple(binding.cardRequest1, binding.btnAccept1, binding.btnDecline1) to "Nguyễn Thảo Ly",
            Triple(binding.cardRequest2, binding.btnAccept2, binding.btnDecline2) to "Đặng Tuấn Kiệt"
        )

        for ((controls, name) in items) {
            val (card, btnAccept, btnDecline) = controls

            btnAccept.setOnClickListener {
                card.visibility = View.GONE
                receivedCount = (receivedCount - 1).coerceAtLeast(0)
                updateBadges()

                showCineMessage(
                    message = "Đã chấp nhận lời mời kết bạn của $name",
                    actionLabel = "HOÀN TÁC",
                    onAction = {
                        card.visibility = View.VISIBLE
                        receivedCount += 1
                        updateBadges()
                        showCineMessage("Đã khôi phục lời mời kết bạn của $name")
                    }
                )
            }

            btnDecline.setOnClickListener {
                card.visibility = View.GONE
                receivedCount = (receivedCount - 1).coerceAtLeast(0)
                updateBadges()

                showCineMessage(
                    message = "Đã từ chối lời mời kết bạn của $name",
                    actionLabel = "HOÀN TÁC",
                    onAction = {
                        card.visibility = View.VISIBLE
                        receivedCount += 1
                        updateBadges()
                        showCineMessage("Đã khôi phục lời mời kết bạn của $name")
                    }
                )
            }
        }

        binding.btnCancelSent1.setOnClickListener {
            binding.cardSentRequest1.visibility = View.GONE
            sentCount = (sentCount - 1).coerceAtLeast(0)
            updateBadges()

            showCineMessage(
                message = "Đã hủy lời mời kết bạn gửi tới Phạm Thảo Vy",
                actionLabel = "HOÀN TÁC",
                onAction = {
                    binding.cardSentRequest1.visibility = View.VISIBLE
                    sentCount += 1
                    updateBadges()
                    showCineMessage("Đã khôi phục lời mời đã gửi")
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}