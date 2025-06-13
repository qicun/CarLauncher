package com.raite.crcc.systemui.ui.wallpaper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raite.crcc.systemui.data.repository.WallpaperRepository

/**
 * 为 WallpaperViewModel 提供依赖项的工厂类
 */
class WallpaperViewModelFactory(private val repository: WallpaperRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WallpaperViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WallpaperViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 