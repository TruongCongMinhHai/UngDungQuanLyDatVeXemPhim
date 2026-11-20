package com.ute.cinenow.ui.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentTicketsBinding
import com.ute.cinenow.utils.showCineMessage

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

    private fun setupClickListeners() {
        // Tab Vé sắp xem
        binding.btnTabUpcoming.setOnClickListener {
            it.animatePress {
                showCineMessage("Bạn đang xem danh sách vé sắp chiếu")
            }
        }

        // Tab Lịch sử -> Chuyển sang màn Lịch sử vé (#25)
        binding.btnTabHistory.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.ticketHistoryFragment)
            }
        }

        // Nút Xem tất cả lịch sử
        binding.tvViewAllHistory.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.ticketHistoryFragment)
            }
        }

        // Bấm vé Hero -> Dune: Hành Tinh Cát 2
        val openHeroTicketDetail: (View) -> Unit = { view ->
            view.animatePress {
                val bundle = bundleOf("ticket_key" to "dune")
                findNavController().navigate(R.id.ticketDetailFragment, bundle)
            }
        }
        binding.btnViewHeroTicket.setOnClickListener(openHeroTicketDetail)
        binding.cardHeroTicket.setOnClickListener(openHeroTicketDetail)

        // Bấm vé gần đây 1 -> Phim Mai
        binding.cardRecentTicket1.setOnClickListener {
            it.animatePress {
                val bundle = bundleOf("ticket_key" to "mai")
                findNavController().navigate(R.id.ticketDetailFragment, bundle)
            }
        }

        // Bấm vé gần đây 2 -> Gia Tài Của Ngoại
        binding.cardRecentTicket2.setOnClickListener {
            it.animatePress {
                val bundle = bundleOf("ticket_key" to "ngoai")
                findNavController().navigate(R.id.ticketDetailFragment, bundle)
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