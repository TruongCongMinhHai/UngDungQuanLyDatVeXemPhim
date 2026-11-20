package com.ute.cinenow.ui.cineconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.ute.cinenow.databinding.FragmentMatchedPeopleBinding
import com.ute.cinenow.utils.showCineMessage

class MatchedPeopleFragment : Fragment() {

    private var _binding: FragmentMatchedPeopleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchedPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setupSearchAndFilter()
        setupMemberActions()
    }

    private fun setupHeader() {
        binding.header.onBackClick = {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSearchAndFilter() {
        val memberCards: List<Pair<View, String>> = listOf(
            Pair(binding.cardMember1, "trần bảo long cinenow landmark 81 imax khoa học viễn tưởng 4dx"),
            Pair(binding.cardMember2, "nguyễn thảo ly cinenow hai bà trưng anime tâm lý ghibli"),
            Pair(binding.cardMember3, "đặng tuấn kiệt cinenow bitexco q1 hành động phiêu lưu marvel"),
            Pair(binding.cardMember4, "lê hoàng minh cinenow landmark 81 tâm lý giật gân nolan")
        )

        binding.etSearchPeople.doAfterTextChanged { editable ->
            val keyword = editable?.toString()?.trim()?.lowercase().orEmpty()
            var visibleCount = 0

            for (item in memberCards) {
                val card = item.first
                val searchContent = item.second
                val matched = keyword.isEmpty() || searchContent.contains(keyword)
                card.visibility = if (matched) View.VISIBLE else View.GONE
                if (matched) visibleCount++
            }

            binding.tvFoundCount.text = if (keyword.isEmpty()) {
                "Tìm thấy 6 thành viên"
            } else {
                "Tìm thấy $visibleCount thành viên"
            }
        }

        binding.btnFilterPeople.setOnClickListener {
            showCineMessage("Đang lọc thành viên phù hợp tại khu vực TP.HCM")
        }

        binding.chipAll.setOnClickListener {
            binding.etSearchPeople.setText("")
            showCineMessage("Hiển thị tất cả 6 thành viên phù hợp")
        }

        binding.chipArea.setOnClickListener {
            showCineMessage("Khu vực hiện tại: TP.HCM")
        }

        binding.chipSciFi.setOnClickListener {
            binding.etSearchPeople.setText("Khoa học viễn tưởng")
        }
    }

    private fun setupMemberActions() {
        val profileButtons: List<Pair<MaterialButton, String>> = listOf(
            Pair(binding.btnViewProfile1, "Trần Bảo Long"),
            Pair(binding.btnViewProfile2, "Nguyễn Thảo Ly"),
            Pair(binding.btnViewProfile3, "Đặng Tuấn Kiệt"),
            Pair(binding.btnViewProfile4, "Lê Hoàng Minh")
        )

        for (item in profileButtons) {
            val btnProfile = item.first
            val memberName = item.second
            btnProfile.setOnClickListener {
                showCineMessage("Đang mở hồ sơ của $memberName")
            }
        }

        val addFriendButtons: List<Pair<MaterialButton, String>> = listOf(
            Pair(binding.btnAddFriend1, "Trần Bảo Long"),
            Pair(binding.btnAddFriend2, "Nguyễn Thảo Ly"),
            Pair(binding.btnAddFriend3, "Đặng Tuấn Kiệt"),
            Pair(binding.btnAddFriend4, "Lê Hoàng Minh")
        )

        for (item in addFriendButtons) {
            val btnAdd = item.first
            val memberName = item.second
            var isSent = false

            btnAdd.setOnClickListener {
                if (!isSent) {
                    isSent = true
                    btnAdd.text = "Đã gửi lời mời"
                    showCineMessage(
                        message = "Đã gửi lời mời kết bạn tới $memberName",
                        actionLabel = "HOÀN TÁC",
                        onAction = {
                            isSent = false
                            btnAdd.text = "Kết bạn"
                            showCineMessage("Đã hủy lời mời kết bạn tới $memberName")
                        }
                    )
                } else {
                    isSent = false
                    btnAdd.text = "Kết bạn"
                    showCineMessage("Đã hủy lời mời kết bạn tới $memberName")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}