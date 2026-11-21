package com.ute.cinenow.ui.cinetogether

import android.content.res.ColorStateList
import android.graphics.Color
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

    private var selectedTime = "Cuối tuần này"
    private var selectedMemberCount = "4 người"
    private var isAutoSplitBill = true

    private var selectedCinema = "CGV Landmark 81 (Bình Thạnh)"
    private val cinemaAreas = arrayOf(
        "CGV Landmark 81 (Bình Thạnh)",
        "CGV Vincom Center Đồng Khởi (Quận 1)",
        "CGV Sư Vạn Hạnh (Quận 10)",
        "CGV Giga Mall Thủ Đức (TP. Thủ Đức)",
        "CGV Crescent Mall (Quận 7)",
        "Cinestar Sinh Viên (TP. Thủ Đức - Dĩ An)",
        "Cinestar Quốc Thanh (Quận 1)",
        "Lotte Cinema Cantavil (TP. Thủ Đức)",
        "Lotte Cinema Nowzone (Quận 5)",
        "Galaxy Cinema Nguyễn Du (Quận 1)",
        "Galaxy Cinema Tân Bình",
        "Galaxy Cinema Quang Trung (Gò Vấp)",
        "BHD Star Thảo Điền (TP. Thủ Đức)",
        "BHD Star 3/2 (Quận 10)",
        "Beta Cinemas Quang Trung (Gò Vấp)",
        "Mega GS Cao Thắng (Quận 3)",
        "Toàn TP. Hồ Chí Minh (Tất cả cụm rạp)",
        "Toàn Hà Nội (Tất cả cụm rạp)",
        "Toàn Đà Nẵng (Tất cả cụm rạp)"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInteractiveSelectors()
        setupCinemaSelector()
        setupActions()
        // Thiết lập trạng thái ban đầu của Switch
        updateSplitBillCardVisual(binding.switchSplitBill.isChecked)
    }

    private fun setupCinemaSelector() {
        // Gán danh sách rạp vào dropdown
        binding.actvCinema.setSimpleItems(cinemaAreas)

        // Đặt giá trị mặc định ban đầu
        binding.actvCinema.setText(selectedCinema, false)

        // Khi người dùng chạm chọn một rạp từ danh sách
        binding.actvCinema.setOnItemClickListener { _, _, position, _ ->
            selectedCinema = cinemaAreas[position]
        }

        // Chạm vào ô sẽ xổ danh sách lựa chọn ra ngay
        binding.actvCinema.setOnClickListener {
            binding.actvCinema.showDropDown()
        }
    }

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
        binding.switchSplitBill.setOnCheckedChangeListener { _, isChecked ->
            isAutoSplitBill = isChecked
            updateSplitBillCardVisual(isChecked)
            if (isChecked) {
                showCineMessage("Đã bật tự động chia đều tiền vé qua ví điện tử")
            } else {
                showCineMessage("Đã tắt tính năng tự động chia tiền vé")
            }
        }

        binding.cardSplitBill.setOnClickListener {
            binding.switchSplitBill.toggle()
        }
    }

    private fun updateSplitBillCardVisual(isChecked: Boolean) {
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.cinenow_primary)
        val outlineColor = ContextCompat.getColor(requireContext(), R.color.cinenow_outline)
        val containerColor = ContextCompat.getColor(requireContext(), R.color.cinenow_primary_container)
        val whiteColor = ContextCompat.getColor(requireContext(), R.color.cinenow_white)
        val offTrackColor = Color.parseColor("#CBD5E1")
        val offThumbColor = Color.parseColor("#94A3B8")

        if (isChecked) {
            // Khi BẬT: viền tím, nền tím nhạt, switch tím
            binding.cardSplitBill.strokeColor = primaryColor
            binding.cardSplitBill.strokeWidth = 3
            binding.cardSplitBill.setCardBackgroundColor(ColorStateList.valueOf(containerColor))
            binding.switchSplitBill.trackTintList = ColorStateList.valueOf(primaryColor)
            binding.switchSplitBill.thumbTintList = ColorStateList.valueOf(whiteColor)
        } else {
            // Khi TẮT: viền xám, nền trắng, switch xám tắt hoàn toàn
            binding.cardSplitBill.strokeColor = outlineColor
            binding.cardSplitBill.strokeWidth = 2
            binding.cardSplitBill.setCardBackgroundColor(ColorStateList.valueOf(whiteColor))
            binding.switchSplitBill.trackTintList = ColorStateList.valueOf(offTrackColor)
            binding.switchSplitBill.thumbTintList = ColorStateList.valueOf(offThumbColor)
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
        binding.header.onBackClick = {
            if (!findNavController().popBackStack()) {
                findNavController().navigate(R.id.cineTogetherFragment)
            }
        }

        binding.etRoomName.doOnTextChanged { _, _, _, _ ->
            binding.tilRoomName.error = null
        }

        binding.btnSubmitCreateRoom.setOnClickListener {
            it.animatePress {
                val roomName = binding.etRoomName.text?.toString()?.trim().orEmpty()
                if (roomName.isEmpty()) {
                    binding.tilRoomName.error = "Vui lòng nhập tên cho phòng xem phim"
                    binding.etRoomName.requestFocus()
                    return@animatePress
                }

                val randomPin = (100000..999999).random()
                showCineMessage("Tạo phòng '$roomName' thành công! Mã PIN: $randomPin")
                findNavController().navigate(R.id.votingRoomFragment)
            }
        }

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