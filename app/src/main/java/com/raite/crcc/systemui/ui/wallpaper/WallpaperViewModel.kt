package com.raite.crcc.systemui.ui.wallpaper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raite.crcc.systemui.data.model.Section
import com.raite.crcc.systemui.data.model.WallpaperItem
import com.raite.crcc.systemui.data.repository.WallpaperRepository
import com.raite.crcc.systemui.utils.Plog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 壁纸选择对话框的 ViewModel
 * 管理UI状态和业务逻辑
 */
class WallpaperViewModel(private val repository: WallpaperRepository) : ViewModel() {

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    private val _uiState = MutableStateFlow<WallpaperUiState>(WallpaperUiState.Loading)
    val uiState: StateFlow<WallpaperUiState> = _uiState.asStateFlow()

    // 存储从仓库获取的原始列表
    private var originalList: List<WallpaperItem> = emptyList()
    // 当前选中的壁纸
    private var selectedWallpaperUrl: String? = null

    init {
        Plog.i(mObjectTag, "ViewModel instance created.")
        loadWallpapers()
    }

    /**
     * 加载壁纸列表
     * @param forceError 强制触发一个错误用于测试
     */
    fun loadWallpapers(forceError: Boolean = false) {
        Plog.i(mObjectTag, "loadWallpapers called with forceError: $forceError")
        _uiState.value = WallpaperUiState.Loading
        viewModelScope.launch {
            val result = repository.getWallpapers(forceError)
            result.onSuccess { items ->
                Plog.i(mObjectTag, "Successfully loaded ${items.size} items from repository.")
                originalList = items
                updateDisplayedList()
            }.onFailure {
                Plog.e(mObjectTag, "Failed to load wallpapers: ${it.message}")
                _uiState.value = WallpaperUiState.Error(it.message ?: "未知错误")
            }
        }
    }

    /**
     * 处理壁纸点击事件
     */
    fun onWallpaperSelected(wallpaperUrl: String) {
        Plog.i(mObjectTag, "Wallpaper selected: $wallpaperUrl")
        selectedWallpaperUrl = wallpaperUrl
        updateDisplayedList()
    }

    /**
     * 切换板块的展开/折叠状态
     */
    fun toggleSection(section: Section) {
        Plog.i(mObjectTag, "Toggling section: ${section.name}")
        val currentState = _uiState.value
        if (currentState is WallpaperUiState.Success) {
            val newList = currentState.wallpaperItems.map { item ->
                if (item is WallpaperItem.Header && item.section == section) {
                    item.copy(isExpanded = !item.isExpanded)
                } else {
                    item
                }
            }
            originalList = newList // 更新原始列表以保存折叠状态
            updateDisplayedList()
        }
    }

    /**
     * 根据当前状态（原始列表、选中项、折叠状态）更新UI状态
     */
    private fun updateDisplayedList() {
        Plog.d(mObjectTag, "Updating displayed list...")
        val displayedList = mutableListOf<WallpaperItem>()
        var currentSectionExpanded = true

        for (item in originalList) {
            when (item) {
                is WallpaperItem.Header -> {
                    currentSectionExpanded = item.isExpanded
                    displayedList.add(item)
                }
                is WallpaperItem.Thumbnail -> {
                    if (currentSectionExpanded) {
                        // 更新选中状态
                        displayedList.add(item.copy(isSelected = item.wallpaperUrl == selectedWallpaperUrl))
                    }
                }
                is WallpaperItem.AddButton -> {
                     if (currentSectionExpanded) {
                        displayedList.add(item)
                    }
                }
            }
        }
        _uiState.value = WallpaperUiState.Success(displayedList, selectedWallpaperUrl != null)
        Plog.d(mObjectTag, "Displayed list updated. Item count: ${displayedList.size}, Finish enabled: ${selectedWallpaperUrl != null}")
    }
}

/**
 * 定义壁纸选择UI的状态
 */
sealed interface WallpaperUiState {
    object Loading : WallpaperUiState
    data class Success(
        val wallpaperItems: List<WallpaperItem>,
        val isFinishButtonEnabled: Boolean
    ) : WallpaperUiState
    data class Error(val message: String) : WallpaperUiState
} 