package com.raite.crcc.systemui.data.source

/**
 * 提供壁纸图片 URL 的单例对象。
 */
object WallpaperProvider {

    private val wallpapers = listOf(
        "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?q=80&w=2560&auto=format&fit=crop", // Yellow Sports Car
        "https://images.unsplash.com/photo-1597404294360-feeeda04612e?q=80&w=2560&auto=format&fit=crop", // Red Vintage Car
        "https://images.unsplash.com/photo-1525609004556-c46c7d6cf023?q=80&w=2560&auto=format&fit=crop", // Blue Lamborghini
        "https://images.unsplash.com/photo-1503376780353-7e6692767b70?q=80&w=2560&auto=format&fit=crop", // Black Porsche
        "https://images.unsplash.com/photo-1494976388531-d1058494cdd8?q=80&w=2560&auto=format&fit=crop", // Yellow Mustang
        "https://images.unsplash.com/photo-1583121274602-3e2820c69888?q=80&w=2560&auto=format&fit=crop", // Red Ferrari
        "https://images.unsplash.com/photo-1617083273183-57995964264a?q=80&w=2560&auto=format&fit=crop", // White Audi
        "https://images.unsplash.com/photo-1517524008621-57f8cc863f8c?q=80&w=2560&auto=format&fit=crop", // Offroad Jeep
        "https://images.unsplash.com/photo-1542281286-9e0e16bb7366?q=80&w=2560&auto=format&fit=crop", // Red Sports Car on Road
        "https://images.unsplash.com/photo-1553440569-bcc63803a83d?q=80&w=2560&auto=format&fit=crop"  // Silver Mercedes
    )

    private var currentIndex = 0

    /**
     * 获取下一个壁纸的 URL，并循环播放列表。
     */
    fun getNextWallpaper(): String {
        if (currentIndex >= wallpapers.size) {
            currentIndex = 0
        }
        return wallpapers[currentIndex++]
    }
} 