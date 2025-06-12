package com.raite.crcc.systemui.ui.launcher

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import coil.load
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
        viewPager.adapter = object : FragmentStateAdapter(this) {
            /**
             * 返回桌面页面的总数。
             * 此处硬编码为2，代表有两个桌面屏幕。
             */
            override fun getItemCount(): Int = 2

            /**
             * 根据位置创建对应的桌面 Fragment。
             * @param position 页面位置索引。
             * @return 一个新的 DesktopPageFragment 实例。
             */
            override fun createFragment(position: Int): Fragment {
                return DesktopPageFragment.newInstance()
            }
        }

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
            repeatOnLifecycle(lifecycle.Lifecycle.State.STARTED) {
                while (true) {
                    val nextWallpaperUrl = WallpaperProvider.getNextWallpaper()

                    // 使用 Coil 加载图片。
                    // Coil 会在后台线程处理网络请求和图片解码，不会阻塞UI线程。
                    // 它还会自动缓存图片，并根据 ImageView 的大小优化内存使用。
                    wallpaperView.load(nextWallpaperUrl) {
                        // crossfade(true) 提供了一个平滑的淡入淡出过渡效果。
                        crossfade(true)
                        crossfade(500) // 动画时长500ms
                    }

                    // 延迟1分钟，实现定时切换。
                    delay(60_000L)
                }
            }
        }
    }
} 