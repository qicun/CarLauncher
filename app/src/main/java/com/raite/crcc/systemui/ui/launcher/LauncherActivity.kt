package com.raite.crcc.systemui.ui.launcher

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import coil.ImageLoader
import coil.imageLoader
import coil.request.ImageRequest
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.data.source.WallpaperProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 应用的主 Activity，作为桌面的主要入口点。
 * 它承载一个 ViewPager2，用于实现多屏桌面的左右滑动效果。
 * 同时，它也负责管理背景壁纸的循环切换。
 */
class LauncherActivity : AppCompatActivity() {

    private lateinit var wallpaperView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        wallpaperView = findViewById(R.id.wallpaper)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        // 设置 ViewPager2 的适配器
        viewPager.adapter = DesktopPagerAdapter(this)

        startWallpaperCycle()
    }

    /**
     * 启动一个生命周期感知的协程来循环切换壁纸。
     */
    private fun startWallpaperCycle() {
        // 使用 lifecycleScope.launch 启动一个协程，该协程的生命周期与 Activity 绑定。
        // 当 Activity 销毁时，这个协程会自动取消，防止内存泄漏。
        lifecycleScope.launch {
            // repeatOnLifecycle 会在 Activity 进入 STARTED 状态时执行代码块，
            // 并在其进入 STOPPED 状态时挂起（取消）协程。
            // 这确保了只有当应用在前台可见时，壁纸才会切换，避免了后台不必要的资源消耗。
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    val nextWallpaperUrl = WallpaperProvider.getNextWallpaper()

                    // 显式创建 ImageRequest
                    val request = ImageRequest.Builder(this@LauncherActivity)
                        .data(nextWallpaperUrl) // 图片 URL
                        .target(wallpaperView) // 目标 ImageView
                        .crossfade(true) // 开启淡入淡出效果
                        .crossfade(500) // 动画时长 500ms
                        .build()

                    // 获取单例的 ImageLoader 实例并执行请求
                    this@LauncherActivity.imageLoader.enqueue(request)

                    // 延迟1分钟，实现定时切换。
                    delay(60_000L)
                }
            }
        }
    }
} 