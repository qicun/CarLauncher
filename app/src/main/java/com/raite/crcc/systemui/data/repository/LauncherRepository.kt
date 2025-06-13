package com.raite.crcc.systemui.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.raite.crcc.systemui.data.model.AppInfo
import com.raite.crcc.systemui.util.ContextUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 应用启动器的数据仓库 (Repository)。
 * 作为所有启动器相关数据的唯一真实来源 (Single Source of Truth)。
 * 负责获取、缓存和管理应用列表及其他状态。
 * 使用单例模式 (object) 以确保在应用中有且仅有一个实例。
 */
object LauncherRepository {

    // 使用 MutableStateFlow 来持有和管理应用列表，以便UI层可以观察其变化。
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps = _apps.asStateFlow()

    /**
     * 从系统中加载所有可启动的应用程序。
     * 查询 PackageManager，获取包含 CATEGORY_LAUNCHER 的应用，
     * 并将它们映射为 AppInfo 对象列表。
     * @param excludedPackages 一个包含不应加载的应用包名的Set。
     */
    fun loadLaunchableApps(excludedPackages: Set<String> = emptySet()) {
        val pm = ContextUtil.context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList = pm.queryIntentActivities(intent, 0)
        val appList = resolveInfoList
            .filterNot { excludedPackages.contains(it.activityInfo.packageName) }
            .map {
                AppInfo(
                    label = it.loadLabel(pm).toString(),
                    packageName = it.activityInfo.packageName,
                    icon = it.loadIcon(pm)
                )
            }.sortedBy { it.label } // 按应用名称排序
        _apps.value = appList
    }

    /**
     * 更新指定应用的通知角标状态。
     * @param packageName 需要更新角标的应用包名。
     * @param show true 显示角标, false 隐藏角标。
     */
    fun updateBadge(packageName: String, show: Boolean) {
        val currentApps = _apps.value.toMutableList()
        val appIndex = currentApps.indexOfFirst { it.packageName == packageName }
        if (appIndex != -1) {
            currentApps[appIndex].showBadge = show
            _apps.value = currentApps.toList() // 触发 StateFlow 更新
        }
    }
} 