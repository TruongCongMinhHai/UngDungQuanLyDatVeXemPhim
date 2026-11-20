package com.ute.cinenow.ui.cinematch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ute.cinenow.R
import com.ute.cinenow.adapter.CineMatchGenreAdapter
import com.ute.cinenow.adapter.CineMatchOptionAdapter
import com.ute.cinenow.databinding.FragmentCinematchPreferencesBinding
import com.ute.cinenow.utils.CineMatchStore
import com.ute.cinenow.utils.showCineMessage

class CineMatchPreferencesFragment : Fragment() {

    private var _binding: FragmentCinematchPreferencesBinding? = null
    private val binding get() = _binding!!

    private lateinit var genreAdapter: CineMatchGenreAdapter
    private lateinit var moodAdapter: CineMatchOptionAdapter
    private lateinit var groupAdapter: CineMatchOptionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCinematchPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLists()
        setupActions()
        // Đang sửa (đi từ màn Kết quả sang) thì nút đổi thành "Cập nhật"
        if (isEditingFromResult()) binding.btnDiscover.text = "Cập nhật gợi ý"
    }

    // True khi màn đứng ngay dưới trong back stack là màn Kết quả (tức bấm "Sửa" mà tới đây)
    private fun isEditingFromResult(): Boolean =
        findNavController().previousBackStackEntry?.destination?.id == R.id.cineMatchResultFragment

    private fun setupLists() {
        // Đã có sở thích lưu thì hiện lại; lần đầu dùng mặc định giống thiết kế
        val saved = CineMatchStore.hasSaved
        val initialGenres = if (saved) CineMatchStore.genreIds else setOf("scifi", "action")
        val initialMood = if (saved) CineMatchStore.moodId else "excited"
        val initialGroup = if (saved) CineMatchStore.groupId else "friends"

        // Thể loại: chọn nhiều, tối đa MAX_GENRES. Chỉ đổi giao diện, CHƯA lưu.
        genreAdapter = CineMatchGenreAdapter(
            items = CineMatchOptions.genres,
            initialSelected = initialGenres,
            maxSelect = MAX_GENRES,
            onSelectionChanged = { updateGenreCount(it.size) },
            onLimitReached = { showCineMessage("Chỉ được chọn tối đa $MAX_GENRES thể loại") }
        )
        setupGrid(binding.rvGenres, genreAdapter)
        updateGenreCount(genreAdapter.selectedCount())

        // Tâm trạng và quy mô cùng đi: chỉ chọn 1 (cũng chưa lưu)
        moodAdapter = CineMatchOptionAdapter(
            CineMatchOptions.moods, initialSelectedId = initialMood, onSelected = { }
        )
        setupGrid(binding.rvMoods, moodAdapter)

        groupAdapter = CineMatchOptionAdapter(
            CineMatchOptions.groups, initialSelectedId = initialGroup, onSelected = { }
        )
        setupGrid(binding.rvGroups, groupAdapter)
    }

    private fun setupGrid(list: RecyclerView, listAdapter: RecyclerView.Adapter<*>) {
        list.layoutManager = GridLayoutManager(requireContext(), 2)
        list.adapter = listAdapter
        list.itemAnimator = null // tránh nhấp nháy khi đổi màu thẻ
    }

    private fun setupActions() {
        // Nút quay lại và nút hồ sơ do CineHeader tự xử lý (navigateUp / mở profileFragment).
        // Bấm Back mà chưa nhấn nút xác nhận thì không lưu gì: kết quả gợi ý cũ vẫn nguyên.
        binding.btnDiscover.setOnClickListener { discover() }
        binding.btnReset.setOnClickListener { resetAll() }
    }

    private fun discover() {
        val genres = genreAdapter.selectedItems()
        val mood = moodAdapter.selectedItem()
        val group = groupAdapter.selectedItem()

        when {
            genres.isEmpty() -> showCineMessage("Hãy chọn ít nhất 1 thể loại yêu thích")
            mood == null -> showCineMessage("Hãy chọn tâm trạng hôm nay")
            group == null -> showCineMessage("Hãy chọn quy mô cùng đi")
            else -> {
                // Chỉ lưu sở thích tại đây (khi bấm Xác nhận / Cập nhật)
                CineMatchStore.save(
                    genres = genres.map { it.id }.toSet(),
                    mood = mood.id,
                    group = group.id
                )

                val nav = findNavController()
                if (isEditingFromResult()) {
                    // Quy tắc 4: về lại màn Kết quả, màn đó tự tính lại từ sở thích vừa lưu
                    nav.popBackStack()
                } else {
                    // Quy tắc 1: lần đầu sang màn Kết quả và bỏ màn này khỏi back stack
                    nav.navigate(
                        R.id.cineMatchResultFragment,
                        null,
                        navOptions {
                            popUpTo(R.id.cineMatchPreferencesFragment) { inclusive = true }
                        }
                    )
                }
            }
        }
    }

    // Chỉ xóa lựa chọn trên giao diện, không đụng vào sở thích đã lưu
    private fun resetAll() {
        genreAdapter.clear()
        moodAdapter.clear()
        groupAdapter.clear()
        updateGenreCount(0)
        showCineMessage("Đã đặt lại lựa chọn")
    }

    private fun updateGenreCount(count: Int) {
        binding.tvGenreCount.text = "Đã chọn $count/$MAX_GENRES"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val MAX_GENRES = 3

        // Màn Kết quả giờ đọc thẳng từ CineMatchStore nên 3 khóa này không còn dùng.
        // Giữ lại để không lỗi nếu file nào khác còn nhắc tới.
        const val ARG_GENRES = "cinematch_genres"
        const val ARG_MOOD = "cinematch_mood"
        const val ARG_GROUP = "cinematch_group"
    }
}