package com.ute.cinenow.ui.cinetogether

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.ute.cinenow.R
import com.ute.cinenow.databinding.BottomSheetRoomMembersBinding

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

        // Sao chép mã PIN mời bạn bè -> Hiển thị thông báo nổi trên Bottom Sheet
        binding.btnCopyInvitePin.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("CineTogether Invite PIN", "889966")
            clipboard.setPrimaryClip(clip)
            showSheetMessage("Đã sao chép mã mời: 889 966")
        }

        // Nút Mời ngay tại vị trí còn trống
        binding.btnInviteFriend.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val inviteMessage = "Vào CineNow cùng xem phim với nhóm mình nhé! Mã phòng: 889966"
            val clip = ClipData.newPlainText("CineTogether Invite", inviteMessage)
            clipboard.setPrimaryClip(clip)
            showSheetMessage("Đã sao chép tin nhắn mời bạn bè vào Clipboard")
        }
    }

    /**
     * Hiển thị thông báo Snackbar nổi trực tiếp TRÊN giao diện Bottom Sheet,
     * neo phía trên nút Đóng để không bị che khuất.
     */
    private fun showSheetMessage(message: String) {
        val density = resources.displayMetrics.density
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        snackbar.view.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_cine_snackbar)
        snackbar.setTextColor(ContextCompat.getColor(requireContext(), R.color.cinenow_white))

        // Gắn icon logo CineNow
        val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        val logoSize = (24 * density).toInt()
        val logo = ContextCompat.getDrawable(requireContext(), R.drawable.ic_cinenow_logo)?.mutate()
        logo?.setBounds(0, 0, logoSize, logoSize)
        textView?.setCompoundDrawablesRelative(logo, null, null, null)
        textView?.compoundDrawablePadding = (10 * density).toInt()

        // Neo nổi ngay phía trên nút Đóng
        snackbar.anchorView = binding.btnCloseSheet
        snackbar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}