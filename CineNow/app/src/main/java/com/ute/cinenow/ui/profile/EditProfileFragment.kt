package com.ute.cinenow.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentEditProfileBinding
import com.ute.cinenow.utils.showCineMessage

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGenreChips()
        setupLiveInputPreview()
        setupClickListeners()
    }

    /**
     * Tương tác chọn thể loại phim
     */
    private fun setupGenreChips() {
        val genreChips = listOf(
            binding.chipSciFi,
            binding.chipAction,
            binding.chipAdventure,
            binding.chipAnimation,
            binding.chipHorror,
            binding.chipDrama
        )

        val defaultSelectedIds = setOf(
            binding.chipSciFi.id,
            binding.chipAction.id,
            binding.chipAdventure.id
        )

        genreChips.forEach { chip ->
            val initialSelected = chip.id in defaultSelectedIds
            applyChipVisual(chip, initialSelected)

            chip.setOnClickListener {
                it.animatePress {
                    val currentlySelected = chip.tag as? Boolean ?: false
                    val currentCount = genreChips.count { (it.tag as? Boolean) == true }

                    if (!currentlySelected && currentCount >= 5) {
                        showCineMessage("Bạn chỉ được chọn tối đa 5 thể loại phim")
                    } else {
                        val nextState = !currentlySelected
                        applyChipVisual(chip, nextState)
                        updateGenreCount(genreChips)
                    }
                }
            }
        }

        updateGenreCount(genreChips)
    }

    private fun applyChipVisual(chip: Chip, isSelected: Boolean) {
        chip.tag = isSelected
        if (isSelected) {
            chip.setChipBackgroundColorResource(R.color.cinenow_primary)
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.cinenow_white))
            val cleanText = chip.text.toString().removePrefix("+ ").trim()
            chip.text = cleanText
        } else {
            chip.setChipBackgroundColorResource(R.color.cinenow_surface_container_low)
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.cinenow_text_primary))
            val cleanText = chip.text.toString().removePrefix("+ ").trim()
            chip.text = "+ $cleanText"
        }
    }

    private fun updateGenreCount(chips: List<Chip>) {
        val count = chips.count { (it.tag as? Boolean) == true }
        binding.tvGenreCount.text = "$count/5 đã chọn"
    }

    private fun setupLiveInputPreview() {
        binding.etFullName.doOnTextChanged { text, _, _, _ ->
            binding.tvProfileNamePreview.text =
                if (text.isNullOrBlank()) "Họ và tên" else text.toString().trim()
        }
    }

    /**
     * Sự kiện click cho các nút chức năng
     */
    private fun setupClickListeners() {
        // Nút quay lại đã được CineHeader tự động xử lý (findNavController().navigateUp())

        // Đổi ảnh đại diện
        val avatarClickAction: (View) -> Unit = { view ->
            view.animatePress {
                showCineMessage("Mở thư viện chọn ảnh đại diện mới")
            }
        }
        binding.btnChangeAvatar.setOnClickListener(avatarClickAction)
        binding.imgLargeAvatar.setOnClickListener(avatarClickAction)

        // Nút Lưu thay đổi
        binding.btnSaveProfile.setOnClickListener {
            it.animatePress {
                val fullName = binding.etFullName.text?.toString()?.trim().orEmpty()
                val phone = binding.etPhone.text?.toString()?.trim().orEmpty()

                if (fullName.isEmpty()) {
                    binding.tilFullName.error = "Họ và tên không được để trống"
                    return@animatePress
                } else {
                    binding.tilFullName.error = null
                }

                if (phone.isEmpty()) {
                    binding.tilPhone.error = "Số điện thoại không được để trống"
                    return@animatePress
                } else {
                    binding.tilPhone.error = null
                }

                showCineMessage("Đã lưu thông tin hồ sơ thành công")
                findNavController().popBackStack()
            }
        }

        // Nút Hủy
        binding.btnCancelEdit.setOnClickListener {
            it.animatePress {
                findNavController().popBackStack()
            }
        }
    }

    private fun View.animatePress(action: () -> Unit) {
        animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).withEndAction {
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