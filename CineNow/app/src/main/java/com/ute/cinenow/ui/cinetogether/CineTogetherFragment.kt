package com.ute.cinenow.ui.cinetogether

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentCineTogetherBinding

class CineTogetherFragment : Fragment() {

    private var _binding: FragmentCineTogetherBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCineTogetherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
    }

    /**
     * Cấu hình toàn bộ luồng điều hướng của màn Đi cùng nhóm (#19)
     */
    private fun setupNavigation() {
        // 1. Nút Tạo phòng mới -> Chuyển sang Màn #20 (CreateRoomFragment)
        binding.btnCreateRoom.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.createRoomFragment)
            }
        }

        // 2. Nút Tham gia phòng bằng mã PIN -> Chuyển sang Màn #21 (JoinRoomFragment)
        binding.btnJoinRoom.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.joinRoomFragment)
            }
        }

        // 3. Bấm vào Card phòng đang mở hoặc Nút Vào phòng -> Chuyển sang Màn #22 (VotingRoomFragment)
        val openVotingRoomAction: (View) -> Unit = { view ->
            view.animatePress {
                findNavController().navigate(R.id.votingRoomFragment)
            }
        }
        binding.btnEnterActiveRoom.setOnClickListener(openVotingRoomAction)
        binding.cardActiveRoom.setOnClickListener(openVotingRoomAction)

        // 4. Bấm vào Buổi xem gần đây -> Xem kết quả chốt vé Màn #30 (VoteResultFragment)
        binding.cardRecentGroup.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.voteResultFragment)
            }
        }
    }

    // Hiệu ứng ấn nhả nút co nhún mượt mà
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