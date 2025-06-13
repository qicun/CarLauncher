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
import com.raite.crcc.systemui.utils.Plog

class WallpaperAdapter(
    private val listener: WallpaperAdapterListener
) : ListAdapter<WallpaperItem, RecyclerView.ViewHolder>(WallpaperDiffCallback()) {

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    init {
        Plog.i(mObjectTag, "Adapter instance created.")
    }

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
        val type = when (getItem(position)) {
            is WallpaperItem.Header -> VIEW_TYPE_HEADER
            is WallpaperItem.Thumbnail -> VIEW_TYPE_THUMBNAIL
            is WallpaperItem.AddButton -> VIEW_TYPE_ADD_BUTTON
        }
        Plog.d(mObjectTag, "getItemViewType at position $position is ${typeToString(type)}")
        return type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Plog.d(mObjectTag, "onCreateViewHolder for viewType: ${typeToString(viewType)}")
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(inflater.inflate(R.layout.item_wallpaper_header, parent, false))
            VIEW_TYPE_THUMBNAIL -> ThumbnailViewHolder(inflater.inflate(R.layout.item_wallpaper_thumbnail, parent, false))
            VIEW_TYPE_ADD_BUTTON -> AddButtonViewHolder(inflater.inflate(R.layout.item_wallpaper_add, parent, false))
            else -> {
                Plog.e(mObjectTag, "Invalid view type: $viewType")
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        Plog.d(mObjectTag, "onBindViewHolder for position $position, item: ${item.javaClass.simpleName}")
        when (holder) {
            is HeaderViewHolder -> holder.bind(item as WallpaperItem.Header, listener)
            is ThumbnailViewHolder -> holder.bind(item as WallpaperItem.Thumbnail, listener)
            is AddButtonViewHolder -> holder.bind(listener)
        }
    }

    private fun typeToString(viewType: Int): String {
        return when (viewType) {
            VIEW_TYPE_HEADER -> "HEADER"
            VIEW_TYPE_THUMBNAIL -> "THUMBNAIL"
            VIEW_TYPE_ADD_BUTTON -> "ADD_BUTTON"
            else -> "UNKNOWN"
        }
    }

    // ViewHolder们
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.headerTitle)
        private val expandButton: TextView = view.findViewById(R.id.expandButton)
        private val mObjectTag by lazy {
            "${javaClass.simpleName}@${System.identityHashCode(this)}"
        }

        init {
            Plog.i(mObjectTag, "HeaderViewHolder instance created.")
        }

        fun bind(item: WallpaperItem.Header, listener: WallpaperAdapterListener) {
            Plog.d(mObjectTag, "Binding Header: '${item.title}', isExpanded: ${item.isExpanded}")
            title.text = item.title
            expandButton.text = if (item.isExpanded) "收起 ▴" else "展开更多 ▾"
            itemView.setOnClickListener {
                Plog.i(mObjectTag, "Header '${item.title}' clicked.")
                listener.onToggleSection(item.section)
            }
        }
    }

    class ThumbnailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val thumbnail: ImageView = view.findViewById(R.id.wallpaperThumbnail)
        private val selectionOverlay: View = view.findViewById(R.id.selectionOverlay)
        private val checkmark: ImageView = view.findViewById(R.id.selectionCheckmark)
        private val mObjectTag by lazy {
            "${javaClass.simpleName}@${System.identityHashCode(this)}"
        }

        init {
            Plog.i(mObjectTag, "ThumbnailViewHolder instance created.")
        }

        fun bind(item: WallpaperItem.Thumbnail, listener: WallpaperAdapterListener) {
            Plog.d(mObjectTag, "Binding Thumbnail: '${item.wallpaperUrl}', isSelected: ${item.isSelected}")
            thumbnail.load(item.wallpaperUrl) {
                crossfade(true)
                placeholder(R.drawable.dialog_wallpaper_background) // 使用一个简单的占位符
            }

            selectionOverlay.visibility = if (item.isSelected) View.VISIBLE else View.GONE
            checkmark.visibility = if (item.isSelected) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                Plog.i(mObjectTag, "Thumbnail '${item.wallpaperUrl}' clicked.")
                listener.onWallpaperClick(item.wallpaperUrl)
            }
        }
    }

    class AddButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mObjectTag by lazy {
            "${javaClass.simpleName}@${System.identityHashCode(this)}"
        }

        init {
            Plog.i(mObjectTag, "AddButtonViewHolder instance created.")
        }
        fun bind(listener: WallpaperAdapterListener) {
            Plog.d(mObjectTag, "Binding AddButton.")
            itemView.setOnClickListener {
                Plog.i(mObjectTag, "AddButton clicked.")
                listener.onAddWallpaperClick()
            }
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