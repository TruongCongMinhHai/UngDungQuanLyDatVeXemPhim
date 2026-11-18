package com.ute.cinenow.ui.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentTicketHistoryBinding

class TicketHistoryFragment : Fragment() {

    private var _binding: FragmentTicketHistoryBinding? = null
    private val binding get() = _binding!!

    // Enum quản lý 3 trạng thái bộ lọc
    private enum class FilterStatus { ALL, USED, CANCELED }
    private var currentFilter = FilterStatus.ALL

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabNavigation()
        setupFilterChips()
        setupLiveSearch()
        setupDetailNavigation()
    }

    /**
     * Chuyển tab giữa Vé sắp xem và Lịch sử
     */
    private fun setupTabNavigation() {
        // Tab Vé sắp xem -> Quay lại màn TicketsFragment (#24)
        binding.btnTabUpcoming.setOnClickListener {
            it.animatePress {
                if (!findNavController().popBackStack(R.id.ticketsFragment, false)) {
                    findNavController().navigate(R.id.ticketsFragment)
                }
            }
        }

        // Tab Lịch sử (đang chọn)
        binding.btnTabHistory.setOnClickListener {
            it.animatePress {
                Toast.makeText(requireContext(), "Bạn đang ở màn hình Lịch sử vé", Toast.LENGTH_SHORT).show()
            }
        }

        // Avatar góc phải -> Chuyển sang màn Cá nhân (#26)
        binding.imgTopAvatar.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.profileFragment)
            }
        }
    }

    /**
     * Xử lý click chọn bộ lọc: Tất cả / Đã sử dụng / Đã hủy
     */
    private fun setupFilterChips() {
        binding.chipFilterAll.setOnClickListener {
            it.animatePress {
                currentFilter = FilterStatus.ALL
                updateFilterUI()
                applyFilterAndSearch()
            }
        }

        binding.chipFilterUsed.setOnClickListener {
            it.animatePress {
                currentFilter = FilterStatus.USED
                updateFilterUI()
                applyFilterAndSearch()
            }
        }

        binding.chipFilterCanceled.setOnClickListener {
            it.animatePress {
                currentFilter = FilterStatus.CANCELED
                updateFilterUI()
                applyFilterAndSearch()
            }
        }
    }

    private fun updateFilterUI() {
        val chips = listOf(
            binding.chipFilterAll to FilterStatus.ALL,
            binding.chipFilterUsed to FilterStatus.USED,
            binding.chipFilterCanceled to FilterStatus.CANCELED
        )

        for ((chip, status) in chips) {
            val isSelected = (status == currentFilter)
            if (isSelected) {
                chip.setBackgroundResource(R.drawable.bg_rounded_icon)
                chip.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.cinenow_primary)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.cinenow_white))
            } else {
                chip.setBackgroundResource(R.drawable.bg_rounded_icon)
                chip.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.cinenow_white)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.cinenow_text_secondary))
            }
        }
    }

    /**
     * Tìm kiếm thời gian thực theo tên phim hoặc mã vé
     */
    private fun setupLiveSearch() {
        binding.etSearchTicket.doOnTextChanged { _, _, _, _ ->
            applyFilterAndSearch()
        }
    }

    private fun applyFilterAndSearch() {
        val query = binding.etSearchTicket.text?.toString()?.trim()?.lowercase().orEmpty()

        // Vé 1: Mai (Đã sử dụng, CN-773192)
        val matchFilter1 = (currentFilter == FilterStatus.ALL || currentFilter == FilterStatus.USED)
        val matchQuery1 = query.isEmpty() || "mai".contains(query) || "cn-773192".lowercase().contains(query)
        binding.cardHistoryTicket1.visibility = if (matchFilter1 && matchQuery1) View.VISIBLE else View.GONE

        // Vé 2: Gia Tài Của Ngoại (Đã sử dụng, CN-551048)
        val matchFilter2 = (currentFilter == FilterStatus.ALL || currentFilter == FilterStatus.USED)
        val matchQuery2 = query.isEmpty() || "gia tài".contains(query) || "ngoại".contains(query) || "cn-551048".lowercase().contains(query)
        binding.cardHistoryTicket2.visibility = if (matchFilter2 && matchQuery2) View.VISIBLE else View.GONE

        // Vé 3: Hành Tinh Cát 2 (Đã hủy, CN-894215)
        val matchFilter3 = (currentFilter == FilterStatus.ALL || currentFilter == FilterStatus.CANCELED)
        val matchQuery3 = query.isEmpty() || "hành tinh cát".contains(query) || "dune".contains(query) || "cn-894215".lowercase().contains(query)
        binding.cardHistoryTicket3.visibility = if (matchFilter3 && matchQuery3) View.VISIBLE else View.GONE
    }

    /**
     * Điều hướng sang màn Chi tiết vé khi click vào từng vé
     */
    private fun setupDetailNavigation() {
        val openTicketDetail: (View) -> Unit = { view ->
            view.animatePress {
                findNavController().navigate(R.id.ticketDetailFragment)
            }
        }

        // Vé 1
        binding.btnViewDetail1.setOnClickListener(openTicketDetail)
        binding.cardHistoryTicket1.setOnClickListener(openTicketDetail)

        // Vé 2
        binding.btnViewDetail2.setOnClickListener(openTicketDetail)
        binding.cardHistoryTicket2.setOnClickListener(openTicketDetail)

        // Vé 3
        binding.btnViewDetail3.setOnClickListener(openTicketDetail)
        binding.cardHistoryTicket3.setOnClickListener(openTicketDetail)
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