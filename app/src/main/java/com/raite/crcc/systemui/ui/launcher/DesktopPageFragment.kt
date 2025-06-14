package com.raite.crcc.systemui.ui.launcher

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.data.model.AppInfo
import com.raite.crcc.systemui.ui.wallpaper.WallpaperSelectionDialogFragment
import com.raite.crcc.systemui.util.GridSpacingItemDecoration
import com.raite.crcc.systemui.utils.Plog
import kotlinx.coroutines.launch
import java.util.*

/**
 * 代表一个桌面页面/屏幕的 Fragment。
 * 它负责展示网格布局的应用列表。
 */
class DesktopPageFragment : Fragment() {

    // 通过 activityViewModels() 委托获取共享的 LauncherViewModel 实例。
    private val viewModel: LauncherViewModel by viewModels()
    private lateinit var appListAdapter: AppListAdapter
    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }
    private var pagePosition = 0
    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences("DesktopOrderPrefs", Context.MODE_PRIVATE)
    }
    private val prefsKey by lazy { "desktop_order_$pagePosition" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pagePosition = it.getInt(ARG_POSITION, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加载此 Fragment 的布局文件。
        return inflater.inflate(R.layout.fragment_desktop_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val adapter = AppListAdapter { appInfo ->
            // 应用点击事件
            when (appInfo.itemType) {
                AppInfo.TYPE_APP -> {
                    try {
                        val intent = requireContext().packageManager.getLaunchIntentForPackage(appInfo.packageName)
                        if (intent != null) {
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        Plog.e(mObjectTag, "Failed to launch app: ${appInfo.packageName}", e)
                    }
                }
                AppInfo.TYPE_WALLPAPER_ACTION -> {
                    WallpaperSelectionDialogFragment.newInstance()
                        .show(parentFragmentManager, WallpaperSelectionDialogFragment.TAG)
                }
            }
        }

        // 使用 GridLayoutManager 来创建网格布局
        recyclerView.layoutManager = GridLayoutManager(context, 5) // 假设每行5个图标
        recyclerView.adapter = adapter

        setupDragAndDrop(recyclerView, adapter)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.apps.collect { apps ->
                val finalList = apps.toMutableList()

                // 根据保存的顺序重新排序
                val savedOrder = sharedPreferences.getString(prefsKey, null)
                if (savedOrder != null) {
                    val savedPackageNames = savedOrder.split(',').filter { it.isNotBlank() }
                    if (savedPackageNames.isNotEmpty()) {
                        val appMap = finalList.associateBy { it.packageName }
                        finalList.clear()
                        // 首先按照保存的顺序添加应用
                        savedPackageNames.forEach { pkgName ->
                            appMap[pkgName]?.let { finalList.add(it) }
                        }
                        // 添加任何不在保存顺序中的新应用（例如新安装的）
                        val newApps = appMap.values.filterNot { savedPackageNames.contains(it.packageName) }
                        finalList.addAll(newApps)
                    }
                }

                if (pagePosition == 0) {
                    // 确保"更换壁纸"按钮始终在第一位，且不参与排序
                    finalList.removeAll { it.itemType == AppInfo.TYPE_WALLPAPER_ACTION }
                    val wallpaperAction = AppInfo(
                        label = "更换壁纸",
                        packageName = "action.wallpaper",
                        icon = requireContext().getDrawable(R.drawable.wallpaper)!!,
                        itemType = AppInfo.TYPE_WALLPAPER_ACTION
                    )
                    finalList.add(0, wallpaperAction)
                }
                adapter.submitList(finalList)
            }
        }

        // 从 Activity 获取底部栏应用并加载过滤后的桌面应用
        val launcherActivity = requireActivity() as? LauncherActivity
        if (launcherActivity != null) {
            val excludedPackages = launcherActivity.allBottomApps.map { it.packageName }.toSet()
            viewModel.loadApps(excludedPackages)
        } else {
            // 如果无法获取 Activity，则加载所有应用作为回退
            viewModel.loadApps()
        }
    }

    /**
     * 初始化 RecyclerView，设置其布局管理器和适配器。
     */
    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        // 创建适配器，并定义点击事件的回调。
        appListAdapter = AppListAdapter { appInfo ->
            try {
                val intent = requireContext().packageManager.getLaunchIntentForPackage(appInfo.packageName)
                if (intent != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "无法启动 ${appInfo.label}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "启动 ${appInfo.label} 出错", Toast.LENGTH_SHORT).show()
            }
        }
        
        val spacing = resources.getDimensionPixelSize(R.dimen.app_icon_spacing)
        val itemContainerWidth = resources.getDimensionPixelSize(R.dimen.app_icon_container_size)

        // 动态计算列数
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val spanCount = (screenWidth - spacing) / (itemContainerWidth + spacing)

        recyclerView.apply {
            // 使用网格布局
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = appListAdapter
            // 添加间距装饰器
            // 确保如果已存在，先移除旧的，防止重复添加
            if (itemDecorationCount > 0) {
                removeItemDecorationAt(0)
            }
            addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, true))
        }
    }

    /**
     * 观察 ViewModel 中的数据变化。
     * 当应用列表数据更新时，自动将新列表提交给适配器以刷新UI。
     */
    private fun observeViewModel() {
        // 使用 lifecycleScope 来确保观察的生命周期与 Fragment 的视图生命周期绑定。
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.apps.collect { apps ->
                appListAdapter.submitList(apps)
            }
        }
    }

    private fun setupDragAndDrop(recyclerView: RecyclerView, adapter: AppListAdapter) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0 // 我们不处理滑动删除
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                val list = adapter.currentList.toMutableList()

                // 不允许将项目拖动到"更换壁纸"按钮的位置（如果存在）
                if (pagePosition == 0 && (toPosition == 0 || fromPosition == 0)) {
                    return false
                }

                Collections.swap(list, fromPosition, toPosition)
                adapter.submitList(list)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 不处理
            }

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                // 不允许拖动"更换壁纸"按钮
                if (pagePosition == 0 && viewHolder.adapterPosition == 0) {
                    return makeMovementFlags(0, 0)
                }
                return super.getMovementFlags(recyclerView, viewHolder)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // 在拖动结束后保存顺序
                saveAppsOrder(adapter.currentList)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun saveAppsOrder(appList: List<AppInfo>) {
        // 过滤掉非应用类型的项目（如"更换壁纸"按钮），然后保存顺序
        val packageOrder = appList
            .filter { it.itemType == AppInfo.TYPE_APP }
            .joinToString(",") { it.packageName }

        sharedPreferences.edit().putString(prefsKey, packageOrder).apply()
        Plog.d(mObjectTag, "Saved order for page $pagePosition: $packageOrder")
    }

    companion object {
        private const val ARG_POSITION = "position"

        /**
         * 创建一个新的 DesktopPageFragment 实例。
         * 使用此工厂方法可以方便地传递参数（如果未来需要的话）。
         */
        fun newInstance(position: Int): DesktopPageFragment {
            val fragment = DesktopPageFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_POSITION, position)
            }
            return fragment
        }
    }
} 