package com.ute.cinenow.ui.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentTicketsBinding

class TicketsFragment : Fragment() {

    private var _binding: FragmentTicketsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    /**
     * Cấu hình sự kiện click và điều hướng cho màn Vé của tôi
     */
    private fun setupClickListeners() {
        // 1. Bấm Avatar góc phải -> Chuyển sang màn Cá nhân (#26)
        binding.imgTopAvatar.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.profileFragment)
            }
        }

        // 2. Tab Vé sắp xem (đang chọn)
        binding.btnTabUpcoming.setOnClickListener {
            it.animatePress {
                Toast.makeText(requireContext(), "Bạn đang xem danh sách vé sắp chiếu", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Tab Lịch sử -> Chuyển sang màn Lịch sử vé (#25)
        binding.btnTabHistory.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.ticketHistoryFragment)
            }
        }

        // 4. Bấm nút Xem chi tiết hoặc bấm vào vé Hero -> Chuyển sang Chi tiết vé & Mã QR (#16)
        val openHeroTicketDetail: (View) -> Unit = { view ->
            view.animatePress {
                findNavController().navigate(R.id.ticketDetailFragment)
            }
        }
        binding.btnViewHeroTicket.setOnClickListener(openHeroTicketDetail)
        binding.cardHeroTicket.setOnClickListener(openHeroTicketDetail)

        // 5. Bấm vé gần đây 1 (Mai: Kỳ Nguyên Mới) -> Chuyển sang Chi tiết vé (#16)
        binding.cardRecentTicket1.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.ticketDetailFragment)
            }
        }

        // 6. Bấm vé gần đây 2 (Gia Tài Của Ngoại) -> Chuyển sang Chi tiết vé (#16)
        binding.cardRecentTicket2.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.ticketDetailFragment)
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