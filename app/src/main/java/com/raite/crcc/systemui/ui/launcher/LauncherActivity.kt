package com.raite.crcc.systemui.ui.launcher

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.imageLoader
import coil.request.ImageRequest
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.data.model.AppInfo
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
    private lateinit var viewPager: ViewPager2
    private lateinit var pageIndicatorContainer: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var fixedAppsAdapter: AppListAdapter
    private lateinit var editableAppsAdapter: AppListAdapter
    val allBottomApps = mutableListOf<AppInfo>()

    companion object {
        private const val PREFS_NAME = "CarLauncherPrefs"
        private const val KEY_WALLPAPER_URL = "wallpaper_url"
        private const val KEY_EDITABLE_APPS = "editable_apps"
        private const val NUM_PAGES = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        Plog.i(mObjectTag, "onCreate called.")

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        wallpaperView = findViewById(R.id.wallpaper)
        viewPager = findViewById(R.id.viewPager)
        pageIndicatorContainer = findViewById(R.id.page_indicator_container)
        val desktopSwitcherIcon: View = findViewById(R.id.desktop_switcher_icon)

        // 设置 ViewPager2 的适配器
        viewPager.adapter = DesktopPagerAdapter(this)

        setupPageIndicator()
        setupPageChangeListener()

        // 桌面切换图标点击事件
        desktopSwitcherIcon.setOnClickListener {
            val nextItem = (viewPager.currentItem + 1) % NUM_PAGES
            viewPager.setCurrentItem(nextItem, true)
        }

        // 加载当前壁纸
        loadWallpaper()

        // 初始化底部栏的应用列表
        setupBottomBarApps()
    }

    private fun setupBottomBarApps() {
        val fixedAppsList = findViewById<RecyclerView>(R.id.fixed_apps_list)
        val editableAppsList = findViewById<RecyclerView>(R.id.editable_apps_list)

        fixedAppsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        editableAppsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 初始化适配器
        fixedAppsAdapter = AppListAdapter { appInfo ->
            try {
                val intent = packageManager.getLaunchIntentForPackage(appInfo.packageName)
                startActivity(intent)
            } catch (e: Exception) {
                Plog.e(mObjectTag, "Failed to launch app: ${appInfo.packageName}", e)
            }
        }
        editableAppsAdapter = AppListAdapter { appInfo ->
            try {
                val intent = packageManager.getLaunchIntentForPackage(appInfo.packageName)
                startActivity(intent)
            } catch (e: Exception) {
                Plog.e(mObjectTag, "Failed to launch app: ${appInfo.packageName}", e)
            }
        }

        fixedAppsList.adapter = fixedAppsAdapter
        editableAppsList.adapter = editableAppsAdapter

        loadFixedApps()
        loadEditableApps()
    }

    private fun loadFixedApps() {
        val fixedAppPackages = resources.getStringArray(R.array.fixed_apps_whitelist)
        val fixedApps = getAppsInfo(fixedAppPackages.toSet())
        fixedAppsAdapter.submitList(fixedApps)
        allBottomApps.addAll(fixedApps)
    }

    private fun loadEditableApps() {
        // TODO: 从 SharedPreferences 加载可编辑应用列表
        // 这是一个占位符，未来可以实现一个让用户选择应用的界面
        val editableAppPackages = sharedPreferences.getStringSet(KEY_EDITABLE_APPS, emptySet()) ?: emptySet()
        val editableApps = getAppsInfo(editableAppPackages)
        editableAppsAdapter.submitList(editableApps)
        allBottomApps.addAll(editableApps)
    }

    private fun getAppsInfo(packageNames: Set<String>): List<AppInfo> {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        val allApps: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)
        return allApps
            .filter { packageNames.contains(it.activityInfo.packageName) }
            .map {
                AppInfo(
                    label = it.loadLabel(pm).toString(),
                    packageName = it.activityInfo.packageName,
                    icon = it.loadIcon(pm)
                )
            }
    }

    private fun setupPageIndicator() {
        for (i in 0 until NUM_PAGES) {
            val dot = ImageView(this)
            dot.setImageResource(R.drawable.page_indicator_dot_unselected)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dot.layoutParams = params
            pageIndicatorContainer.addView(dot)
        }
        // 默认选中第一个
        updatePageIndicator(0)
    }

    private fun setupPageChangeListener() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Plog.i(mObjectTag, "Page selected: $position")
                updatePageIndicator(position)
            }
        })
    }

    private fun updatePageIndicator(position: Int) {
        for (i in 0 until pageIndicatorContainer.childCount) {
            val dot = pageIndicatorContainer.getChildAt(i) as ImageView
            if (i == position) {
                dot.setImageResource(R.drawable.page_indicator_dot_selected)
            } else {
                dot.setImageResource(R.drawable.page_indicator_dot_unselected)
            }
        }
    }

    /**
     * 从 SharedPreferences 加载保存的壁纸URL，
     * 如果没有保存的URL或加载失败，则加载本地的默认/回退壁纸。
     */
    private fun loadWallpaper() {
        val wallpaperUrl = sharedPreferences.getString(KEY_WALLPAPER_URL, null)
        Plog.i(mObjectTag, "Loading wallpaper. Saved URL: $wallpaperUrl")

        val data: Any = wallpaperUrl ?: R.drawable.home_wallpaper

        val request = ImageRequest.Builder(this)
            .data(data)
            .target(wallpaperView)
            .crossfade(true)
            .error(R.drawable.home_wallpaper) // 如果从URL加载失败，则显示此drawable
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