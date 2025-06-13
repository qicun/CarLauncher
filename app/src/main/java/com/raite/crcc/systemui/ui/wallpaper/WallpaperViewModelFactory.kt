package com.raite.crcc.systemui.ui.wallpaper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raite.crcc.systemui.data.repository.WallpaperRepository

/**
 * 为 WallpaperViewModel 提供依赖项的工厂类
 */
class WallpaperViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WallpaperViewModel::class.java)) {
            // 在工厂内部创建 Repository，并将 context 传递进去
            val repository = WallpaperRepository(context)
            @Suppress("UNCHECKED_CAST")
            return WallpaperViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 