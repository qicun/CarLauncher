package com.raite.crcc.systemui.data.repository

import com.raite.crcc.systemui.data.model.Section
import com.raite.crcc.systemui.data.model.WallpaperItem
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.raite.crcc.systemui.utils.Plog

/**
 * 壁纸数据仓库
 * 负责提供壁纸数据，目前为模拟数据。
 */
class WallpaperRepository {

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    private val recommendedWallpapers = listOf(
        "https://images.unsplash.com/photo-1511447333015-45b65e60f6d5?w=500",
        "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=500",
        "https://images.unsplash.com/photo-1487088678257-3a541e6e3922?w=500",
        "https://images.unsplash.com/photo-1558591710-4b4a1ae0f04d?w=500",
        "https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=500",
        "https://images.unsplash.com/photo-1507525428034-b723a9ce6890?w=500",
        "https://images.unsplash.com/photo-1542401886-65d6c61db217?w=500",
        "https://images.unsplash.com/photo-1552083375-1447ce886485?w=500",
        "https://images.unsplash.com/photo-1562043236-65ab60354359?w=500",
        "https://images.unsplash.com/photo-1528184039930-bd0395222146?w=500"
    )

    private val myWallpapers = mutableListOf(
        "https://images.unsplash.com/photo-1444703686981-a3abbc4d4fe3?w=500",
        "https://images.unsplash.com/photo-1490730141103-6cac27aaab94?w=500"
    )

    /**
     * 获取完整的壁纸列表，并组装成UI模型
     * @param forceError 是否强制模拟一个错误
     * @return Result 包装的壁纸列表
     */
    suspend fun getWallpapers(forceError: Boolean = false): Result<List<WallpaperItem>> {
        Plog.i(mObjectTag, "getWallpapers called with forceError: $forceError")
        // 模拟网络延迟
        delay(1500)

        if (forceError || Random.nextFloat() < 0.2f) { // 20%的概率失败
            val error = Exception("Failed to load wallpapers from network.")
            Plog.e(mObjectTag, "getWallpapers failed: ${error.message}")
            return Result.failure(error)
        }

        val items = mutableListOf<WallpaperItem>()

        // 添加推荐壁纸部分
        items.add(WallpaperItem.Header("壁纸推荐", section = Section.RECOMMENDED))
        items.addAll(recommendedWallpapers.map { WallpaperItem.Thumbnail(it, Section.RECOMMENDED) })


        // 添加我的壁纸部分
        items.add(WallpaperItem.Header("我的作品", section = Section.MY_WALLPAPERS))
        items.add(WallpaperItem.AddButton(Section.MY_WALLPAPERS))
        items.addAll(myWallpapers.map { WallpaperItem.Thumbnail(it, Section.MY_WALLPAPERS) })

        Plog.i(mObjectTag, "getWallpapers succeeded with ${items.size} items.")
        return Result.success(items)
    }
} 