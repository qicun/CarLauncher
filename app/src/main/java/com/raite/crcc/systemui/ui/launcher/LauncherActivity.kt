package com.raite.crcc.systemui.ui.launcher

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import coil.imageLoader
import coil.request.ImageRequest
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.ui.wallpaper.WallpaperSelectionDialogFragment
import com.raite.crcc.systemui.utils.Plog

/**
 * 应用的主 Activity，作为桌面的主要入口点。
 * 它承载一个 ViewPager2，用于实现多屏桌面的左右滑动效果。
 * 同时，它也负责管理和显示用户选择的或默认的背景壁纸。
 */
class LauncherActivity : AppCompatActivity(), WallpaperSelectionDialogFragment.OnWallpaperSelectedListener {

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    private lateinit var wallpaperView: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PREFS_NAME = "CarLauncherPrefs"
        private const val KEY_WALLPAPER_URL = "wallpaper_url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        Plog.i(mObjectTag, "onCreate called.")

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        wallpaperView = findViewById(R.id.wallpaper)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val changeWallpaperButton: View = findViewById(R.id.changeWallpaperButton)

        // 设置 ViewPager2 的适配器
        viewPager.adapter = DesktopPagerAdapter(this)

        // 为更换壁纸按钮设置点击监听器以打开选择器
        changeWallpaperButton.setOnClickListener {
            Plog.i(mObjectTag, "Change wallpaper button clicked, showing selection dialog.")
            WallpaperSelectionDialogFragment.newInstance()
                .show(supportFragmentManager, WallpaperSelectionDialogFragment.TAG)
        }

        // 加载当前壁纸
        loadWallpaper()
    }

    /**
     * 从 SharedPreferences 加载保存的壁纸URL，
     * 如果没有保存的URL或加载失败，则加载本地的默认/回退壁纸。
     */
    private fun loadWallpaper() {
        val wallpaperUrl = sharedPreferences.getString(KEY_WALLPAPER_URL, null)
        Plog.i(mObjectTag, "Loading wallpaper. Saved URL: $wallpaperUrl")

        val data: Any = wallpaperUrl ?: R.drawable.wallpaper

        val request = ImageRequest.Builder(this)
            .data(data)
            .target(wallpaperView)
            .crossfade(true)
            .error(R.drawable.wallpaper) // 如果从URL加载失败，则显示此drawable
            .build()

        imageLoader.enqueue(request)
    }

    /**
     * 当从壁纸选择器中选择了一个新壁纸时被调用。
     */
    override fun onWallpaperSelected(wallpaperUrl: String) {
        Plog.i(mObjectTag, "New wallpaper selected: $wallpaperUrl")
        // 保存新选择的壁纸 URL
        sharedPreferences.edit().putString(KEY_WALLPAPER_URL, wallpaperUrl).apply()
        // 立即加载新壁纸
        loadWallpaper()
    }
} 