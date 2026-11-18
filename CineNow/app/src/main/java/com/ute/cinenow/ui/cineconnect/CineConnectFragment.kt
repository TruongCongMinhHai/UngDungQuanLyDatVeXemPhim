package com.ute.cinenow.ui.cineconnect

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentCineConnectBinding

class CineConnectFragment : Fragment() {

    private var _binding: FragmentCineConnectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCineConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupTopActions()
        setupSearchAndFilter()
    }

    // 1. Xử lý nút Back trên thanh Header
    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    // 2. Xử lý các nút điều hướng nhanh phía trên
    private fun setupTopActions() {
        binding.tvViewAllRequests.setOnClickListener {
            Toast.makeText(requireContext(), "Đang mở danh sách lời mời kết bạn", Toast.LENGTH_SHORT).show()
        }

        binding.tvViewAllFriends.setOnClickListener {
            Toast.makeText(requireContext(), "Đang mở danh sách bạn bè trực tuyến", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Xử lý tìm kiếm và bộ lọc
    private fun setupSearchAndFilter() {
        binding.etSearchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim()
                if (keyword.isNotEmpty()) {
                    // Xử lý lọc danh sách theo từ khóa tìm kiếm
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnFilter.setOnClickListener {
            Toast.makeText(requireContext(), "Mở bộ lọc nâng cao", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}