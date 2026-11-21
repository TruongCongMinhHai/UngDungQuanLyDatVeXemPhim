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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentVotingRoomBinding
import com.ute.cinenow.utils.showCineMessage

class VotingRoomFragment : Fragment() {

    private var _binding: FragmentVotingRoomBinding? = null
    private val binding get() = _binding!!

    // Trạng thái bình chọn của Trưởng phòng (User): 1: Dune 2, 2: Mai, 3: Ngoại
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

        // Mở Màn #29: ConfirmVoteBottomSheet (Truyền mã phim chiến thắng)
        binding.btnConfirmVote.setOnClickListener {
            val winningKey = getWinningMovieKey()
            val bundle = bundleOf("winning_movie" to winningKey)
            findNavController().navigate(R.id.confirmVoteBottomSheet, bundle)
        }

        // Bấm chọn Phim 1: Dune 2
        binding.btnVoteMovie1.setOnClickListener {
            if (myVotedMovieId != 1) {
                myVotedMovieId = 1
                updateVoteUI()
                showCineMessage("Bạn đã bình chọn cho Dune: Hành Tinh Cát 2")
            }
        }

        // Bấm chọn Phim 2: Mai
        binding.btnVoteMovie2.setOnClickListener {
            if (myVotedMovieId != 2) {
                myVotedMovieId = 2
                updateVoteUI()
                showCineMessage("Bạn đã bình chọn cho Mai (Phim đang dẫn đầu!)")
            }
        }

        // Bấm chọn Phim 3: Gia Tài Của Ngoại
        binding.btnVoteMovie3.setOnClickListener {
            if (myVotedMovieId != 3) {
                myVotedMovieId = 3
                updateVoteUI()
                showCineMessage("Hòa phiếu! Lựa chọn của bạn (Trưởng phòng) sẽ quyết định.")
            }
        }
    }

    /**
     * Xác định phim chiến thắng dựa trên quy tắc:
     * - Phim có nhiều phiếu bầu hơn.
     * - Nếu hòa phiếu (mỗi phim 1 phiếu), lựa chọn của Trưởng phòng (myVotedMovieId) quyết định.
     */
    private fun getWinningMovieKey(): String {
        return when (myVotedMovieId) {
            2 -> "mai"      // Mai 2 phiếu (Bạn + Nguyên), Dune 1 phiếu -> Mai thắng
            3 -> "ngoai"    // Hòa (Dune 1, Mai 1, Ngoại 1) -> Trưởng phòng chọn Ngoại -> Ngoại thắng
            else -> "dune"  // Dune 2 phiếu (Bạn + Hải), Mai 1 phiếu -> Dune thắng
        }
    }

    private fun updateVoteUI() {
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.cinenow_primary)
        val outlineColor = ContextCompat.getColor(requireContext(), R.color.cinenow_outline)
        val whiteColor = ContextCompat.getColor(requireContext(), R.color.cinenow_white)

        // Phim 1: Dune 2
        if (myVotedMovieId == 1) {
            binding.cardMovie1.strokeColor = primaryColor
            binding.cardMovie1.strokeWidth = 4
            binding.btnVoteMovie1.text = "Đã bình chọn ✓"
            binding.btnVoteMovie1.backgroundTintList = ColorStateList.valueOf(primaryColor)
            binding.btnVoteMovie1.setTextColor(whiteColor)
            binding.progressMovie1.progress = 67
            binding.tvVotesMovie1.text = "2 phiếu (67%) • Bạn, Hải đã chọn"
            binding.tvLeaderMovie1.visibility = View.VISIBLE
        } else {
            binding.cardMovie1.strokeColor = outlineColor
            binding.cardMovie1.strokeWidth = 2
            binding.btnVoteMovie1.text = "Bình chọn"
            binding.btnVoteMovie1.backgroundTintList = ColorStateList.valueOf(whiteColor)
            binding.btnVoteMovie1.setTextColor(primaryColor)
            binding.progressMovie1.progress = 33
            binding.tvVotesMovie1.text = "1 phiếu (33%) • Hải đã chọn"
            binding.tvLeaderMovie1.visibility = View.GONE
        }

        // Phim 2: Mai
        if (myVotedMovieId == 2) {
            binding.cardMovie2.strokeColor = primaryColor
            binding.cardMovie2.strokeWidth = 4
            binding.btnVoteMovie2.text = "Đã bình chọn ✓"
            binding.btnVoteMovie2.backgroundTintList = ColorStateList.valueOf(primaryColor)
            binding.btnVoteMovie2.setTextColor(whiteColor)
            binding.progressMovie2.progress = 67
            binding.tvVotesMovie2.text = "2 phiếu (67%) • Bạn, Nguyên đã chọn"
            binding.tvLeaderMovie2.visibility = View.VISIBLE
        } else {
            binding.cardMovie2.strokeColor = outlineColor
            binding.cardMovie2.strokeWidth = 2
            binding.btnVoteMovie2.text = "Bình chọn"
            binding.btnVoteMovie2.backgroundTintList = ColorStateList.valueOf(whiteColor)
            binding.btnVoteMovie2.setTextColor(primaryColor)
            binding.progressMovie2.progress = 33
            binding.tvVotesMovie2.text = "1 phiếu (33%) • Nguyên đã chọn"
            binding.tvLeaderMovie2.visibility = View.GONE
        }

        // Phim 3: Gia Tài Của Ngoại
        if (myVotedMovieId == 3) {
            binding.cardMovie3.strokeColor = primaryColor
            binding.cardMovie3.strokeWidth = 4
            binding.btnVoteMovie3.text = "Đã bình chọn ✓"
            binding.btnVoteMovie3.backgroundTintList = ColorStateList.valueOf(primaryColor)
            binding.btnVoteMovie3.setTextColor(whiteColor)
            binding.progressMovie3.progress = 33
            binding.tvVotesMovie3.text = "1 phiếu (33%) • Bạn đã chọn"
            // Khi hòa phiếu (1-1-1), hiển thị badge ưu tiên trưởng phòng
            binding.tvLeaderMovie3.visibility = View.VISIBLE
            binding.tvLeaderMovie3.text = "Trưởng phòng chọn"
        } else {
            binding.cardMovie3.strokeColor = outlineColor
            binding.cardMovie3.strokeWidth = 2
            binding.btnVoteMovie3.text = "Bình chọn"
            binding.btnVoteMovie3.backgroundTintList = ColorStateList.valueOf(whiteColor)
            binding.btnVoteMovie3.setTextColor(primaryColor)
            binding.progressMovie3.progress = 0
            binding.tvVotesMovie3.text = "0 phiếu (0%)"
            binding.tvLeaderMovie3.visibility = View.GONE
        }

        // Cập nhật text trên nút chốt phim
        val winningMovieName = when (myVotedMovieId) {
            2 -> "Mai (2/3 vote)"
            3 -> "Gia Tài Của Ngoại (Hòa - Quyết định)"
            else -> "Dune 2 (2/3 vote)"
        }
        binding.btnConfirmVote.text = "Chốt phim: $winningMovieName"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}