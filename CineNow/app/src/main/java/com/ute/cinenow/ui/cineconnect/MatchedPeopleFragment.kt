package com.ute.cinenow.ui.cinematch

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.ute.cinenow.databinding.FragmentMatchedPeopleBinding

class MatchedPeopleFragment : Fragment() {

    private var _binding: FragmentMatchedPeopleBinding? = null
    private val binding get() = _binding!!

    // Danh sách tên 6 thành viên khớp với giao diện XML
    private val memberNames = listOf(
        "Trần Bảo Long",
        "Nguyễn Thảo Ly",
        "Đặng Tuấn Kiệt",
        "Lê Hoàng Minh",
        "Phạm Quỳnh Nga",
        "Hoàng Hữu Tài"
    )

    // Mảng lưu trạng thái kết bạn cho từng thành viên
    private val friendStates = BooleanArray(6) { false }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchedPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupSearchAndFilter()
        setupAllMemberActions()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSearchAndFilter() {
        // 1. Xử lý tìm kiếm gõ chữ
        binding.etSearchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim().lowercase()
                if (keyword.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Đang tìm kiếm: $keyword", Toast.LENGTH_SHORT).show()
                    // Tại đây bạn có thể bổ sung logic lọc danh sách hiển thị card phù hợp
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 2. Nút bộ lọc nâng cao
        binding.btnFilter.setOnClickListener {
            Toast.makeText(requireContext(), "Mở bộ lọc nâng cao theo gu điện ảnh", Toast.LENGTH_SHORT).show()
        }

        // 3. Xử lý chọn thành phố qua PopupMenu khi bấm vào chip khu vực
        binding.chipLocation.setOnClickListener { view ->
            val popup = PopupMenu(requireContext(), view)
            popup.menu.add("📍 Tất cả khu vực")
            popup.menu.add("🏙️ TP.HCM")
            popup.menu.add("🏖️ Đà Nẵng")
            popup.menu.add("🏛️ Hà Nội")

            popup.setOnMenuItemClickListener { item ->
                val selectedCity = item.title.toString()
                binding.chipLocation.text = selectedCity
                Toast.makeText(requireContext(), "Đã lọc theo: $selectedCity", Toast.LENGTH_SHORT).show()
                true
            }
            popup.show()
        }
    }

    private fun setupAllMemberActions() {
        // Ánh xạ danh sách nút xem hồ sơ của 6 thành viên
        val viewProfileButtons = listOf(
            binding.btnViewProfile1, binding.btnViewProfile2,
            binding.btnViewProfile3, binding.btnViewProfile4,
            binding.btnViewProfile5, binding.btnViewProfile6
        )

        // Ánh xạ danh sách nút kết bạn của 6 thành viên
        val addFriendButtons = listOf(
            binding.btnAddFriend1, binding.btnAddFriend2,
            binding.btnAddFriend3, binding.btnAddFriend4,
            binding.btnAddFriend5, binding.btnAddFriend6
        )

        // Gán sự kiện vòng lặp cho tất cả các thành viên
        for (i in memberNames.indices) {
            viewProfileButtons[i].setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Đang mở hồ sơ chi tiết của ${memberNames[i]}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            addFriendButtons[i].setOnClickListener {
                friendStates[i] = !friendStates[i]
                updateFriendButtonState(addFriendButtons[i], friendStates[i], memberNames[i])
            }
        }
    }

    private fun updateFriendButtonState(button: Button, isAdded: Boolean, name: String) {
        if (isAdded) {
            button.text = "✓ Đã gửi lời mời"
            // Dùng backgroundTintList để giữ nguyên bo góc chuẩn Material Button
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#10B981")) // Xanh lá thành công
            Toast.makeText(requireContext(), "Đã gửi lời mời kết bạn đến $name", Toast.LENGTH_SHORT).show()
        } else {
            button.text = "👤+ Kết bạn"
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5850EC")) // Tím chủ đạo CineNow
            Toast.makeText(requireContext(), "Đã hủy lời mời kết bạn với $name", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}