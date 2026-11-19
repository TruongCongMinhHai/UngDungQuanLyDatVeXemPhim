package com.ute.cinenow.ui.cinematch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ute.cinenow.R
import com.ute.cinenow.adapter.CineMatchGenreAdapter
import com.ute.cinenow.adapter.CineMatchOptionAdapter
import com.ute.cinenow.databinding.FragmentCinematchPreferencesBinding
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
    }

    private fun setupLists() {
        // Thể loại: chọn nhiều, tối đa MAX_GENRES. Mặc định chọn sẵn 2 thể loại như thiết kế
        genreAdapter = CineMatchGenreAdapter(
            items = CineMatchOptions.genres,
            initialSelected = setOf("scifi", "action"),
            maxSelect = MAX_GENRES,
            onSelectionChanged = { updateGenreCount(it.size) },
            onLimitReached = { showCineMessage("Chỉ được chọn tối đa $MAX_GENRES thể loại") }
        )
        setupGrid(binding.rvGenres, genreAdapter)
        updateGenreCount(genreAdapter.selectedCount())

        // Tâm trạng và quy mô cùng đi: chỉ chọn 1, mặc định chọn mục đầu như thiết kế
        moodAdapter = CineMatchOptionAdapter(CineMatchOptions.moods, initialSelectedId = "excited")
        setupGrid(binding.rvMoods, moodAdapter)

        groupAdapter = CineMatchOptionAdapter(CineMatchOptions.groups, initialSelectedId = "friends")
        setupGrid(binding.rvGroups, groupAdapter)
    }

    private fun setupGrid(list: RecyclerView, listAdapter: RecyclerView.Adapter<*>) {
        list.layoutManager = GridLayoutManager(requireContext(), 2)
        list.adapter = listAdapter
        list.itemAnimator = null // tránh nhấp nháy khi đổi màu thẻ
    }

    private fun setupActions() {
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
                // Dữ liệu giả lập: chỉ chuyển lựa chọn sang màn kết quả, chưa có thuật toán gợi ý
                val args = Bundle().apply {
                    putStringArrayList(ARG_GENRES, ArrayList(genres.map { it.title }))
                    putString(ARG_MOOD, mood.title)
                    putString(ARG_GROUP, group.title)
                }
                findNavController().navigate(R.id.cineMatchResultFragment, args)
            }
        }
    }

    private fun resetAll() {
        genreAdapter.clear()
        moodAdapter.clear()
        groupAdapter.clear()
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

        // Khóa dữ liệu gửi sang màn kết quả CineMatch
        const val ARG_GENRES = "cinematch_genres"
        const val ARG_MOOD = "cinematch_mood"
        const val ARG_GROUP = "cinematch_group"
    }
}