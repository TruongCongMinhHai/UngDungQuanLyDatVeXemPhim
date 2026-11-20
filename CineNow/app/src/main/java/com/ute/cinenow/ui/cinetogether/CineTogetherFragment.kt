package com.ute.cinenow.ui.cinetogether

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
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

    private fun setupNavigation() {
        // 1. Nút Tạo phòng mới -> Màn #20
        binding.btnCreateRoom.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.createRoomFragment)
            }
        }

        // 2. Nút Tham gia phòng bằng mã PIN -> Màn #21
        binding.btnJoinRoom.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.joinRoomFragment)
            }
        }

        // 3. Bấm vào Card phòng đang mở hoặc Nút Vào phòng -> Màn #22
        val openVotingRoomAction: (View) -> Unit = { view ->
            view.animatePress {
                findNavController().navigate(R.id.votingRoomFragment)
            }
        }
        binding.btnEnterActiveRoom.setOnClickListener(openVotingRoomAction)
        binding.cardActiveRoom.setOnClickListener(openVotingRoomAction)

        // 4. Bấm vào Buổi xem gần đây (Hội bạn K17 UIT - Xem phim Mai)
        // -> Truyền chính xác mã phim Mai và tên nhóm sang Màn #30
        binding.cardRecentGroup.setOnClickListener {
            it.animatePress {
                val bundle = bundleOf(
                    "winning_movie" to "mai",
                    "group_name" to "Hội bạn K17 UIT"
                )
                findNavController().navigate(R.id.voteResultFragment, bundle)
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