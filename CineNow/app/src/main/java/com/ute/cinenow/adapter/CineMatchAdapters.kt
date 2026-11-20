package com.ute.cinenow.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ute.cinenow.R
import com.ute.cinenow.databinding.ItemCmGenreBinding
import com.ute.cinenow.databinding.ItemCmOptionBinding
import com.ute.cinenow.ui.cinematch.PrefOption

private fun Context.color(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)
private fun Context.tint(@ColorRes id: Int): ColorStateList = ColorStateList.valueOf(color(id))

// ---------------- Thể loại: chọn nhiều, tối đa maxSelect ----------------
class CineMatchGenreAdapter(
    private val items: List<PrefOption>,
    initialSelected: Set<String>,
    private val maxSelect: Int,
    private val onSelectionChanged: (Set<String>) -> Unit,
    private val onLimitReached: () -> Unit
) : RecyclerView.Adapter<CineMatchGenreAdapter.ViewHolder>() {

    // Trạng thái chọn nằm ở adapter (không nằm trong ViewHolder) nên cuộn hay tái sử dụng không bị lệch
    private val selected = LinkedHashSet(initialSelected)

    class ViewHolder(val binding: ItemCmGenreBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemCmGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val b = holder.binding
        val ctx = b.root.context
        val isOn = item.id in selected

        b.ivIcon.setImageResource(item.iconRes)
        b.tvTitle.text = item.title

        b.card.setCardBackgroundColor(
            ctx.color(if (isOn) R.color.cinenow_accent else R.color.cinenow_surface_container_lowest)
        )
        val content = if (isOn) R.color.cinenow_white else R.color.cinenow_text_primary
        b.tvTitle.setTextColor(ctx.color(content))
        b.ivIcon.imageTintList = ctx.tint(content)

        b.ivTrailing.setImageResource(
            if (isOn) R.drawable.ic_auth_check_circle else R.drawable.ic_cm_add_circle
        )
        b.ivTrailing.imageTintList =
            ctx.tint(if (isOn) R.color.cinenow_white else R.color.cinenow_text_tertiary)

        b.card.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            toggle(items[pos].id, pos)
        }
    }

    private fun toggle(id: String, position: Int) {
        if (id in selected) {
            selected.remove(id)
        } else {
            if (selected.size >= maxSelect) {
                onLimitReached()
                return
            }
            selected.add(id)
        }
        notifyItemChanged(position)
        onSelectionChanged(selected.toSet())
    }

    fun selectedItems(): List<PrefOption> = items.filter { it.id in selected }

    fun selectedCount() = selected.size

    fun clear() {
        selected.clear()
        notifyDataSetChanged()
        onSelectionChanged(emptySet())
    }
}

// ---------------- Tâm trạng / Quy mô: chỉ chọn 1 ----------------
class CineMatchOptionAdapter(
    private val items: List<PrefOption>,
    initialSelectedId: String?,
    private val onSelected: (PrefOption?) -> Unit = {}
) : RecyclerView.Adapter<CineMatchOptionAdapter.ViewHolder>() {

    private var selectedId: String? = initialSelectedId

    class ViewHolder(val binding: ItemCmOptionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemCmOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val b = holder.binding
        val ctx = b.root.context
        val isOn = item.id == selectedId

        b.tvTitle.text = item.title
        b.tvSubtitle.text = item.subtitle.orEmpty()

        b.card.setCardBackgroundColor(
            ctx.color(if (isOn) R.color.cinenow_accent else R.color.cinenow_surface_container_lowest)
        )
        b.tvTitle.setTextColor(ctx.color(if (isOn) R.color.cinenow_white else R.color.cinenow_text_primary))
        b.tvSubtitle.setTextColor(
            ctx.color(if (isOn) R.color.cinenow_primary_container else R.color.cinenow_text_secondary)
        )

        // Ô vuông chứa icon
        b.ivTile.setBackgroundResource(R.drawable.bg_cm_tile)
        b.ivTile.backgroundTintList =
            ctx.tint(if (isOn) R.color.cinenow_secondary else R.color.cinenow_secondary_container)
        b.ivTile.setImageResource(item.iconRes)
        b.ivTile.imageTintList =
            ctx.tint(if (isOn) R.color.cinenow_white else R.color.cinenow_primary_variant)

        // Nút radio
        b.ivRadio.setImageResource(if (isOn) R.drawable.ic_cm_radio_on else R.drawable.ic_cm_radio_off)
        b.ivRadio.imageTintList =
            ctx.tint(if (isOn) R.color.cinenow_white else R.color.cinenow_text_tertiary)

        b.card.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            select(items[pos].id)
        }
    }

    private fun select(id: String) {
        if (id == selectedId) return
        val oldIndex = items.indexOfFirst { it.id == selectedId }
        val newIndex = items.indexOfFirst { it.id == id }
        selectedId = id
        if (oldIndex >= 0) notifyItemChanged(oldIndex)
        if (newIndex >= 0) notifyItemChanged(newIndex)
        onSelected(selectedItem())
    }

    fun selectedItem(): PrefOption? = items.firstOrNull { it.id == selectedId }

    fun clear() {
        selectedId = null
        notifyDataSetChanged()
        onSelected(null)
    }
}