package com.ute.cinenow.ui.cinetogether

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentVotingRoomBinding
import com.ute.cinenow.utils.showCineMessage

class VotingRoomFragment : Fragment() {

    private var _binding: FragmentVotingRoomBinding? = null
    private val binding get() = _binding!!

    // Trạng thái vote hiện tại của user (1: Dune 2, 2: Mai, 3: Gia tài của ngoại)
    private var myVotedMovieId = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVotingRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupListeners()
        updateVoteUI()
    }

    private fun setupHeader() {
        // Sử dụng thuộc tính onBackClick chuẩn của CineHeader
        binding.header.onBackClick = {
            findNavController().navigateUp()
        }
    }

    private fun setupListeners() {
        // Sao chép mã PIN phòng
        binding.btnCopyPin.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("CineTogether Room PIN", "889966")
            clipboard.setPrimaryClip(clip)
            showCineMessage("Đã sao chép mã phòng: 889 966")
        }

        // Mở Màn #23: RoomMembersBottomSheet (Thành viên trong phòng)
        binding.btnViewMembers.setOnClickListener {
            findNavController().navigate(R.id.roomMembersBottomSheet)
        }
        binding.btnOpenMembers.setOnClickListener {
            findNavController().navigate(R.id.roomMembersBottomSheet)
        }

        // Mở Màn #29: ConfirmVoteBottomSheet (Xác nhận chốt phim)
        binding.btnConfirmVote.setOnClickListener {
            findNavController().navigate(R.id.confirmVoteBottomSheet)
        }

        // Tương tác đổi phiếu bầu
        binding.btnVoteMovie1.setOnClickListener {
            if (myVotedMovieId != 1) {
                myVotedMovieId = 1
                updateVoteUI()
                showCineMessage("Bạn đã bình chọn cho Dune: Hành Tinh Cát 2")
            }
        }

        binding.btnVoteMovie2.setOnClickListener {
            if (myVotedMovieId != 2) {
                myVotedMovieId = 2
                updateVoteUI()
                showCineMessage("Bạn đã bình chọn cho Mai")
            }
        }

        binding.btnVoteMovie3.setOnClickListener {
            if (myVotedMovieId != 3) {
                myVotedMovieId = 3
                updateVoteUI()
                showCineMessage("Bạn đã bình chọn cho Gia Tài Của Ngoại")
            }
        }
    }

    private fun updateVoteUI() {
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.cinenow_primary)
        val outlineColor = ContextCompat.getColor(requireContext(), R.color.cinenow_outline)
        val whiteColor = ContextCompat.getColor(requireContext(), R.color.cinenow_white)

        // Phim 1 (Dune 2)
        if (myVotedMovieId == 1) {
            binding.cardMovie1.strokeColor = primaryColor
            binding.cardMovie1.strokeWidth = 4
            binding.btnVoteMovie1.text = "Đã bình chọn ✓"
            binding.btnVoteMovie1.backgroundTintList = ColorStateList.valueOf(primaryColor)
            binding.btnVoteMovie1.setTextColor(whiteColor)
            binding.progressMovie1.progress = 67
            binding.tvVotesMovie1.text = "2 phiếu (67%) • Bạn, Hải đã chọn"
        } else {
            binding.cardMovie1.strokeColor = outlineColor
            binding.cardMovie1.strokeWidth = 2
            binding.btnVoteMovie1.text = "Bình chọn"
            binding.btnVoteMovie1.backgroundTintList = ColorStateList.valueOf(whiteColor)
            binding.btnVoteMovie1.setTextColor(primaryColor)
            binding.progressMovie1.progress = 33
            binding.tvVotesMovie1.text = "1 phiếu (33%) • Hải đã chọn"
        }

        // Phim 2 (Mai)
        if (myVotedMovieId == 2) {
            binding.cardMovie2.strokeColor = primaryColor
            binding.cardMovie2.strokeWidth = 4
            binding.btnVoteMovie2.text = "Đã bình chọn ✓"
            binding.btnVoteMovie2.backgroundTintList = ColorStateList.valueOf(primaryColor)
            binding.btnVoteMovie2.setTextColor(whiteColor)
            binding.progressMovie2.progress = 67
            binding.tvVotesMovie2.text = "2 phiếu (67%) • Bạn, Nguyên đã chọn"
        } else {
            binding.cardMovie2.strokeColor = outlineColor
            binding.cardMovie2.strokeWidth = 2
            binding.btnVoteMovie2.text = "Bình chọn"
            binding.btnVoteMovie2.backgroundTintList = ColorStateList.valueOf(whiteColor)
            binding.btnVoteMovie2.setTextColor(primaryColor)
            binding.progressMovie2.progress = 33
            binding.tvVotesMovie2.text = "1 phiếu (33%) • Nguyên đã chọn"
        }

        // Phim 3 (Gia Tài Của Ngoại)
        if (myVotedMovieId == 3) {
            binding.cardMovie3.strokeColor = primaryColor
            binding.cardMovie3.strokeWidth = 4
            binding.btnVoteMovie3.text = "Đã bình chọn ✓"
            binding.btnVoteMovie3.backgroundTintList = ColorStateList.valueOf(primaryColor)
            binding.btnVoteMovie3.setTextColor(whiteColor)
            binding.progressMovie3.progress = 33
            binding.tvVotesMovie3.text = "1 phiếu (33%) • Bạn đã chọn"
        } else {
            binding.cardMovie3.strokeColor = outlineColor
            binding.cardMovie3.strokeWidth = 2
            binding.btnVoteMovie3.text = "Bình chọn"
            binding.btnVoteMovie3.backgroundTintList = ColorStateList.valueOf(whiteColor)
            binding.btnVoteMovie3.setTextColor(primaryColor)
            binding.progressMovie3.progress = 0
            binding.tvVotesMovie3.text = "0 phiếu (0%)"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}