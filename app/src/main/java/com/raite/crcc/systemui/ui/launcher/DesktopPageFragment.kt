package com.raite.crcc.systemui.ui.launcher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raite.crcc.systemui.R
import kotlinx.coroutines.launch

/**
 * 代表一个桌面页面/屏幕的 Fragment。
 * 它负责展示网格布局的应用列表。
 */
class DesktopPageFragment : Fragment() {

    // 通过 activityViewModels() 委托获取共享的 LauncherViewModel 实例。
    private val viewModel: LauncherViewModel by activityViewModels()
    private lateinit var appListAdapter: AppListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加载此 Fragment 的布局文件。
        return inflater.inflate(R.layout.fragment_desktop_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        observeViewModel()
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
        recyclerView.apply {
            // 使用网格布局，每行5个图标。
            layoutManager = GridLayoutManager(context, 5)
            adapter = appListAdapter
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

    companion object {
        /**
         * 创建一个新的 DesktopPageFragment 实例。
         * 使用此工厂方法可以方便地传递参数（如果未来需要的话）。
         */
        fun newInstance() = DesktopPageFragment()
    }
} 