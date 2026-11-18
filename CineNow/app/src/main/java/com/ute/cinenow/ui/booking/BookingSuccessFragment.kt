package com.ute.cinenow.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentBookingSuccessBinding

class BookingSuccessFragment : Fragment() {

    private var _binding: FragmentBookingSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionButtons()
    }

    private fun setupActionButtons() {
        binding.btnViewETicket.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Đang mở vé điện tử mã #CN-88921-UIT...",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnBackToHome.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Đang quay về trang chủ CineNow",
                Toast.LENGTH_SHORT
            ).show()
            // Hoặc thực hiện điều hướng về trang chủ/activity chính ở đây
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}