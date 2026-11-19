package com.ute.cinenow.ui.cinetogether

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentCreateRoomBinding
import com.ute.cinenow.utils.showCineMessage

class CreateRoomFragment : Fragment() {

    private var _binding: FragmentCreateRoomBinding? = null
    private val binding get() = _binding!!

    // Biến lưu trạng thái tùy chọn
    private var selectedTime = "Cuối tuần này"
    private var selectedMemberCount = "4 người"
    private var isAutoSplitBill = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInteractiveSelectors()
        setupActions()
    }

    /**
     * Xử lý tương tác chọn Thời gian, Số lượng thành viên, và Bật/Tắt chia tiền
     */
    private fun setupInteractiveSelectors() {
        // 1. Tương tác chọn Thời gian dự kiến
        val timeChips = listOf(
            binding.chipTimeWeekend to "Cuối tuần này",
            binding.chipTimeTonight to "Tối nay (sau 19h)",
            binding.chipTimeTomorrow to "Ngày mai"
        )
        timeChips.forEach { (chip, text) ->
            chip.setOnClickListener {
                it.animatePress {
                    selectedTime = text
                    updateChipsVisual(timeChips.map { it.first }, chip)
                }
            }
        }

        // 2. Tương tác chọn Số lượng thành viên tối đa
        val memberChips = listOf(
            binding.chipMember2 to "2 người",
            binding.chipMember4 to "4 người",
            binding.chipMember6 to "6 người",
            binding.chipMember8 to "8+ người"
        )
        memberChips.forEach { (chip, text) ->
            chip.setOnClickListener {
                it.animatePress {
                    selectedMemberCount = text
                    updateChipsVisual(memberChips.map { it.first }, chip)
                }
            }
        }

        // 3. Tương tác Bật / Tắt tính năng Chia tiền vé tự động
        binding.cardSplitBill.setOnClickListener {
            it.animatePress {
                isAutoSplitBill = !isAutoSplitBill
                if (isAutoSplitBill) {
                    binding.ivCheckSplitBill.setImageResource(R.drawable.ic_auth_check_circle)
                    binding.ivCheckSplitBill.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.cinenow_primary)
                    showCineMessage("Đã bật chia đều chi phí tự động")
                } else {
                    binding.ivCheckSplitBill.setImageResource(R.drawable.ic_auth_shield_outline)
                    binding.ivCheckSplitBill.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.cinenow_text_tertiary)
                    showCineMessage("Đã tắt chia tiền tự động")
                }
            }
        }
    }

    private fun updateChipsVisual(allChips: List<TextView>, selectedChip: TextView) {
        allChips.forEach { chip ->
            if (chip == selectedChip) {
                chip.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.cinenow_primary)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.cinenow_white))
                chip.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                chip.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.cinenow_surface_container_low)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.cinenow_text_primary))
                chip.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }
    }

    private fun setupActions() {
        // Nút back trên CineHeader
        binding.header.onBackClick = {
            if (!findNavController().popBackStack()) {
                findNavController().navigate(R.id.cineTogetherFragment)
            }
        }

        // Tự động xóa lỗi khi người dùng gõ tên phòng
        binding.etRoomName.doOnTextChanged { _, _, _, _ ->
            binding.tilRoomName.error = null
        }

        // Nút Tạo phòng & Lấy mã PIN
        binding.btnSubmitCreateRoom.setOnClickListener {
            it.animatePress {
                val roomName = binding.etRoomName.text?.toString()?.trim().orEmpty()
                if (roomName.isEmpty()) {
                    binding.tilRoomName.error = "Vui lòng nhập tên cho phòng xem phim"
                    return@animatePress
                }

                // Sinh mã PIN ngẫu nhiên 4 số
                val randomPin = (1000..9999).random()
                showCineMessage("Tạo phòng '$roomName' ($selectedMemberCount) thành công! Mã PIN: #CT-$randomPin")

                // Chuyển thẳng sang Màn #22: Phòng xem phim và bình chọn (VotingRoomFragment)
                findNavController().navigate(R.id.votingRoomFragment)
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