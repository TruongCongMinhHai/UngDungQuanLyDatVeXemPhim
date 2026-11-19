package com.ute.cinenow.ui.cinetogether

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ute.cinenow.R
import com.ute.cinenow.databinding.BottomSheetConfirmVoteBinding
import com.ute.cinenow.utils.showCineMessage

class ConfirmVoteBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetConfirmVoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetConfirmVoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        // Nút hủy / Tiếp tục bình chọn: Đóng Bottom Sheet
        binding.btnCancelConfirm.setOnClickListener {
            dismiss()
        }

        // Nút xác nhận chốt phim: Đóng sheet và điều hướng sang Màn #30 (VoteResultFragment)
        binding.btnConfirmAndCloseVote.setOnClickListener {
            showCineMessage("Đã chốt bình chọn phim: Dune: Hành Tinh Cát 2")
            dismiss()
            findNavController().navigate(R.id.voteResultFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}