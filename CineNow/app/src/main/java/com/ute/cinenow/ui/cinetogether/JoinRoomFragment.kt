package com.ute.cinenow.ui.cinetogether

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentJoinRoomBinding
import com.ute.cinenow.utils.showCineMessage

class JoinRoomFragment : Fragment() {

    private var _binding: FragmentJoinRoomBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActions()
    }

    private fun setupActions() {
        // Nút back trên CineHeader
        binding.header.onBackClick = {
            if (!findNavController().popBackStack()) {
                findNavController().navigate(R.id.cineTogetherFragment)
            }
        }

        // Tự động xóa lỗi khi người dùng gõ mã PIN
        binding.etPinCode.doOnTextChanged { _, _, _, _ ->
            binding.tilPinCode.error = null
        }

        // Nút Dán nhanh từ bộ nhớ tạm Clipboard
        binding.btnPastePin.setOnClickListener {
            it.animatePress {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = clipboard.primaryClip
                if (clipData != null && clipData.itemCount > 0) {
                    val pastedText = clipData.getItemAt(0).text?.toString()?.trim().orEmpty()
                    if (pastedText.isNotEmpty()) {
                        binding.etPinCode.setText(pastedText)
                        showCineMessage("Đã dán mã PIN từ bộ nhớ tạm")
                    } else {
                        showCineMessage("Bộ nhớ tạm đang trống")
                    }
                } else {
                    showCineMessage("Bộ nhớ tạm đang trống")
                }
            }
        }

        // Chạm vào phòng gợi ý -> Điền sẵn mã PIN "CT-8821"
        binding.cardSuggestedRoom.setOnClickListener {
            it.animatePress {
                binding.etPinCode.setText("CT-8821")
                showCineMessage("Đã chọn phòng 'UIT Movie Weekend'")
            }
        }

        // Nút Vào phòng bình chọn
        binding.btnSubmitJoinRoom.setOnClickListener {
            it.animatePress {
                val pin = binding.etPinCode.text?.toString()?.trim().orEmpty()
                if (pin.isEmpty()) {
                    binding.tilPinCode.error = "Vui lòng nhập mã PIN phòng"
                    return@animatePress
                }

                val formattedPin = if (pin.startsWith("CT-", ignoreCase = true) || pin.startsWith("#")) pin else "CT-$pin"
                showCineMessage("Tham gia phòng $formattedPin thành công!")

                // Chuyển thẳng sang Màn #22: Phòng xem phim và bình chọn (VotingRoomFragment)
                findNavController().navigate(R.id.votingRoomFragment)
            }
        }

        // Nút Chưa có phòng? Tạo phòng mới -> Chuyển sang Màn #20
        binding.btnGoCreateRoom.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.createRoomFragment)
            }
        }

        // Nút Hủy
        binding.btnCancel.setOnClickListener {
            it.animatePress {
                findNavController().popBackStack()
            }
        }
    }

    private fun View.animatePress(action: () -> Unit) {
        animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).withEndAction {
            animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).withEndAction {
                action()
            }.start()
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}