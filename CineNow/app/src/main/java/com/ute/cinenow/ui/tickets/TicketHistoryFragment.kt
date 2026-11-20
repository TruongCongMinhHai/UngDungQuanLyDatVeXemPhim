package com.ute.cinenow.ui.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentTicketHistoryBinding
import com.ute.cinenow.utils.showCineMessage

class TicketHistoryFragment : Fragment() {

    private var _binding: FragmentTicketHistoryBinding? = null
    private val binding get() = _binding!!

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

    private fun setupTabNavigation() {
        binding.header.onBackClick = {
            if (!findNavController().popBackStack()) {
                findNavController().navigate(R.id.ticketsFragment)
            }
        }

        binding.btnTabUpcoming.setOnClickListener {
            it.animatePress {
                if (!findNavController().popBackStack(R.id.ticketsFragment, false)) {
                    findNavController().navigate(R.id.ticketsFragment)
                }
            }
        }

        binding.btnTabHistory.setOnClickListener {
            it.animatePress {
                showCineMessage("Bạn đang ở màn hình Lịch sử vé")
            }
        }
    }

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

    private fun setupLiveSearch() {
        binding.etSearchTicket.doOnTextChanged { _, _, _, _ ->
            applyFilterAndSearch()
        }
    }

    private fun applyFilterAndSearch() {
        val query = binding.etSearchTicket.text?.toString()?.trim()?.lowercase().orEmpty()

        val matchFilter1 = (currentFilter == FilterStatus.ALL || currentFilter == FilterStatus.USED)
        val matchQuery1 = query.isEmpty() || "mai".contains(query) || "cn-773192".lowercase().contains(query)
        binding.cardHistoryTicket1.visibility = if (matchFilter1 && matchQuery1) View.VISIBLE else View.GONE

        val matchFilter2 = (currentFilter == FilterStatus.ALL || currentFilter == FilterStatus.USED)
        val matchQuery2 = query.isEmpty() || "gia tài".contains(query) || "ngoại".contains(query) || "cn-551048".lowercase().contains(query)
        binding.cardHistoryTicket2.visibility = if (matchFilter2 && matchQuery2) View.VISIBLE else View.GONE

        val matchFilter3 = (currentFilter == FilterStatus.ALL || currentFilter == FilterStatus.CANCELED)
        val matchQuery3 = query.isEmpty() || "hành tinh cát".contains(query) || "dune".contains(query) || "cn-894215".lowercase().contains(query)
        binding.cardHistoryTicket3.visibility = if (matchFilter3 && matchQuery3) View.VISIBLE else View.GONE
    }

    private fun setupDetailNavigation() {
        // Vé 1: Mai
        val openTicketMai: (View) -> Unit = { view ->
            view.animatePress {
                val bundle = bundleOf("ticket_key" to "mai")
                findNavController().navigate(R.id.ticketDetailFragment, bundle)
            }
        }
        binding.btnViewDetail1.setOnClickListener(openTicketMai)
        binding.cardHistoryTicket1.setOnClickListener(openTicketMai)

        // Vé 2: Gia Tài Của Ngoại
        val openTicketNgoai: (View) -> Unit = { view ->
            view.animatePress {
                val bundle = bundleOf("ticket_key" to "ngoai")
                findNavController().navigate(R.id.ticketDetailFragment, bundle)
            }
        }
        binding.btnViewDetail2.setOnClickListener(openTicketNgoai)
        binding.cardHistoryTicket2.setOnClickListener(openTicketNgoai)

        // Vé 3: Dune 2
        val openTicketDune: (View) -> Unit = { view ->
            view.animatePress {
                val bundle = bundleOf("ticket_key" to "dune")
                findNavController().navigate(R.id.ticketDetailFragment, bundle)
            }
        }
        binding.btnViewDetail3.setOnClickListener(openTicketDune)
        binding.cardHistoryTicket3.setOnClickListener(openTicketDune)
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