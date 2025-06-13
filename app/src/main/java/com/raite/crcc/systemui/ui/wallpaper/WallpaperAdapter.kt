package com.raite.crcc.systemui.ui.wallpaper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.data.model.Section
import com.raite.crcc.systemui.data.model.WallpaperItem

class WallpaperAdapter(
    private val listener: WallpaperAdapterListener
) : ListAdapter<WallpaperItem, RecyclerView.ViewHolder>(WallpaperDiffCallback()) {

    companion object {
        // 定义视图类型
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_THUMBNAIL = 1
        private const val VIEW_TYPE_ADD_BUTTON = 2
    }

    // 监听器接口
    interface WallpaperAdapterListener {
        fun onWallpaperClick(wallpaperUrl: String)
        fun onToggleSection(section: Section)
        fun onAddWallpaperClick()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is WallpaperItem.Header -> VIEW_TYPE_HEADER
            is WallpaperItem.Thumbnail -> VIEW_TYPE_THUMBNAIL
            is WallpaperItem.AddButton -> VIEW_TYPE_ADD_BUTTON
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(inflater.inflate(R.layout.item_wallpaper_header, parent, false))
            VIEW_TYPE_THUMBNAIL -> ThumbnailViewHolder(inflater.inflate(R.layout.item_wallpaper_thumbnail, parent, false))
            VIEW_TYPE_ADD_BUTTON -> AddButtonViewHolder(inflater.inflate(R.layout.item_wallpaper_add, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeaderViewHolder -> holder.bind(item as WallpaperItem.Header, listener)
            is ThumbnailViewHolder -> holder.bind(item as WallpaperItem.Thumbnail, listener)
            is AddButtonViewHolder -> holder.bind(listener)
        }
    }

    // ViewHolder们
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.headerTitle)
        private val expandButton: TextView = view.findViewById(R.id.expandButton)

        fun bind(item: WallpaperItem.Header, listener: WallpaperAdapterListener) {
            title.text = item.title
            expandButton.text = if (item.isExpanded) "收起 ▴" else "展开更多 ▾"
            itemView.setOnClickListener { listener.onToggleSection(item.section) }
        }
    }

    class ThumbnailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val thumbnail: ImageView = view.findViewById(R.id.wallpaperThumbnail)
        private val selectionOverlay: View = view.findViewById(R.id.selectionOverlay)
        private val checkmark: ImageView = view.findViewById(R.id.selectionCheckmark)

        fun bind(item: WallpaperItem.Thumbnail, listener: WallpaperAdapterListener) {
            thumbnail.load(item.wallpaperUrl) {
                crossfade(true)
                placeholder(R.drawable.dialog_wallpaper_background) // 使用一个简单的占位符
            }

            selectionOverlay.visibility = if (item.isSelected) View.VISIBLE else View.GONE
            checkmark.visibility = if (item.isSelected) View.VISIBLE else View.GONE

            itemView.setOnClickListener { listener.onWallpaperClick(item.wallpaperUrl) }
        }
    }

    class AddButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(listener: WallpaperAdapterListener) {
            itemView.setOnClickListener { listener.onAddWallpaperClick() }
        }
    }
}

// DiffUtil 回调
class WallpaperDiffCallback : DiffUtil.ItemCallback<WallpaperItem>() {
    override fun areItemsTheSame(oldItem: WallpaperItem, newItem: WallpaperItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WallpaperItem, newItem: WallpaperItem): Boolean {
        return oldItem == newItem
    }
} 