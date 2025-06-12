package com.raite.crcc.systemui.ui.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raite.crcc.systemui.data.model.AppInfo
import com.raite.crcc.systemui.data.repository.LauncherRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Launcher UI 的 ViewModel。
 * 遵循 MVVM 架构，作为 UI 控制器 (Activity/Fragment) 和数据层 (Repository) 之间的桥梁。
 * 它负责处理业务逻辑，并将数据以可观察的形式暴露给 UI。
 */
class LauncherViewModel : ViewModel() {

    /**
     * 应用列表的 StateFlow。
     * UI 层通过观察这个 Flow 来获取最新的应用列表并刷新界面。
     */
    val apps: StateFlow<List<AppInfo>> = LauncherRepository.apps

    init {
        // ViewModel 初始化时，立即触发加载应用列表的操作。
        loadApps()
    }

    /**
     * 调用 Repository 来加载可启动的应用列表。
     * 使用 viewModelScope 来确保协程的生命周期与 ViewModel 绑定，避免内存泄漏。
     */
    private fun loadApps() {
        viewModelScope.launch {
            LauncherRepository.loadLaunchableApps()
        }
    }
} 