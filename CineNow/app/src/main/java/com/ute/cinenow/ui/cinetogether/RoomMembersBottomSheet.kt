package com.ute.cinenow.ui.cinetogether

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ute.cinenow.databinding.BottomSheetRoomMembersBinding
import com.ute.cinenow.utils.showCineMessage

class RoomMembersBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetRoomMembersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetRoomMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        // Nút đóng Bottom Sheet
        binding.btnCloseSheet.setOnClickListener {
            dismiss()
        }

        // Sao chép mã PIN mời bạn bè
        binding.btnCopyInvitePin.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("CineTogether Invite PIN", "889966")
            clipboard.setPrimaryClip(clip)
            showCineMessage("Đã sao chép mã mời: 889 966")
        }

        // Nút Mời ngay tại vị trí còn trống
        binding.btnInviteFriend.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val inviteMessage = "Vào CineNow cùng xem phim với nhóm mình nhé! Mã phòng: 889966"
            val clip = ClipData.newPlainText("CineTogether Invite", inviteMessage)
            clipboard.setPrimaryClip(clip)
            showCineMessage("Đã sao chép tin nhắn mời bạn bè vào Clipboard")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}