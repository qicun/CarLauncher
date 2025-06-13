package com.raite.crcc.systemui.data.model

import com.raite.crcc.systemui.R
import java.util.UUID

/**
 * 定义壁纸选择列表中不同项目类型的密封接口。
 * 使用密封接口可以方便地在 Adapter 中处理不同的视图类型。
 */
sealed interface WallpaperItem {
    val id: String

    data class Header(
        val title: String,
        val isExpanded: Boolean = true,
        val section: Section
    ) : WallpaperItem {
        override val id: String get() = title
    }

    data class Thumbnail(
        val wallpaperUrl: String,
        val section: Section,
        var isSelected: Boolean = false
    ) : WallpaperItem {
        override val id: String get() = wallpaperUrl
    }

    data class AddButton(val section: Section) : WallpaperItem {
        override val id: String = "add_button"
    }
}

/**
 * 壁纸板块的定义
 */
enum class Section {
    LOCAL, // 本地壁纸
    RECOMMENDED,
    MY_WALLPAPERS
} 