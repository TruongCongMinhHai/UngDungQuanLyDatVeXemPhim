package com.ute.cinenow.ui.cineconnect

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentCineConnectBinding
import com.ute.cinenow.utils.showCineMessage

class CineConnectFragment : Fragment() {

    private var _binding: FragmentCineConnectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

    private fun setupHeader() {
        binding.header.onBackClick = {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupTopActions() {
        binding.tvViewAllRequests.setOnClickListener {
            showCineMessage("Đang mở danh sách lời mời kết bạn")
        }
        binding.tvViewAllFriends.setOnClickListener {
            showCineMessage("Đang mở danh sách bạn bè trực tuyến")
        }
    }

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
            showCineMessage("Mở bộ lọc nâng cao")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}