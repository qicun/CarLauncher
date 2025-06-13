package com.raite.crcc.systemui.data.repository

import android.content.Context
import com.raite.crcc.systemui.data.model.Section
import com.raite.crcc.systemui.data.model.WallpaperItem
import com.raite.crcc.systemui.utils.Plog
import com.raite.crcc.systemui.R
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * 壁纸数据仓库
 * 负责提供壁纸数据，目前为模拟数据。
 */
class WallpaperRepository(private val context: Context) {

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    private val recommendedWallpapers = listOf(
        "https://images.unsplash.com/photo-1511447333015-45b65e60f6d5?w=500",
        "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=500",
        "https://images.unsplash.com/photo-1487088678257-3a541e6e3922?w=500",
        "https://images.unsplash.com/photo-1558591710-4b4a1ae0f04d?w=500",
        "https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=500",
        "https://images.unsplash.com/photo-1542401886-65d6c61db217?w=500",
        "https://images.unsplash.com/photo-1552083375-1447ce886485?w=500",
        "https://images.unsplash.com/photo-1444703686981-a3abbc4d4fe3?w=500",
        "https://images.unsplash.com/photo-1490730141103-6cac27aaab94?w=500"
    )

    private val myWallpapers = mutableListOf(
        "https://images.unsplash.com/photo-1444703686981-a3abbc4d4fe3?w=500",
        "https://images.unsplash.com/photo-1490730141103-6cac27aaab94?w=500"
    )
    // 定义多个本地壁纸资源的列表
    private val localWallpapers = listOf(
        R.drawable.wallpaper,
        R.drawable.pexels5,
        R.drawable.pexels_kaan,
        R.drawable.pexels_pripicart,
    )


    /**
     * 获取完整的壁纸列表，并组装成UI模型。
     * 这个方法现在更加健壮，总是会返回本地壁纸，即使网络获取失败。
     * @param forceError 是否强制模拟一个错误
     * @return Result 包装的壁纸列表
     */
    suspend fun getWallpapers(forceError: Boolean = false): Result<List<WallpaperItem>> {
        Plog.i(mObjectTag, "getWallpapers called with forceError: $forceError")
        val items = mutableListOf<WallpaperItem>()

        // 1. 添加本地壁纸部分（总是成功）
        try {
            items.add(WallpaperItem.Header("本地壁纸", section = Section.LOCAL))

            // 遍历每个本地壁纸资源并添加到 items
            localWallpapers.forEach { resourceId ->
                val uri = "android.resource://${context.packageName}/$resourceId"
                items.add(WallpaperItem.Thumbnail(uri, Section.LOCAL))
            }

            Plog.i(mObjectTag, "Added local wallpaper section.")
        } catch (e: Exception) {
            Plog.e(mObjectTag, "Failed to add local wallpaper section", e)
        }


        // 2. 尝试添加网络壁纸部分（可能会失败）
        try {
            // 模拟网络延迟
            delay(1500)

            if (forceError || Random.nextFloat() < 0.2f) { // 20%的概率失败
                throw Exception("Failed to load wallpapers from network.")
            }

            // 添加推荐壁纸部分
            items.add(WallpaperItem.Header("壁纸推荐", section = Section.RECOMMENDED))
            items.addAll(recommendedWallpapers.map { WallpaperItem.Thumbnail(it, Section.RECOMMENDED) })

            Plog.i(mObjectTag, "Successfully added network wallpapers.")
        } catch (e: Exception) {
            // 网络部分失败，只记录错误，不影响整体结果
            Plog.e(mObjectTag, "Failed to get network wallpapers, but proceeding with local items.", e)
        }
        try {
            // 添加我的壁纸部分
            items.add(WallpaperItem.Header("我的作品", section = Section.MY_WALLPAPERS))
            items.add(WallpaperItem.AddButton(Section.MY_WALLPAPERS))
//            items.addAll(myWallpapers.map { WallpaperItem.Thumbnail(it, Section.MY_WALLPAPERS) })
            // 遍历每个本地壁纸资源并添加到 items
            localWallpapers.forEach { resourceId ->
                val uri = "android.resource://${context.packageName}/$resourceId"
                items.add(WallpaperItem.Thumbnail(uri, Section.MY_WALLPAPERS))
            }
            items.addAll(myWallpapers.map { WallpaperItem.Thumbnail(it.toString(), Section.MY_WALLPAPERS) })


            Plog.i(mObjectTag, "Added local wallpaper section.")
        } catch (e: Exception) {
            Plog.e(mObjectTag, "Failed to add local wallpaper section", e)
        }
        Plog.i(mObjectTag, "getWallpapers finished, returning ${items.size} items in total.")
        return Result.success(items)
    }
} 