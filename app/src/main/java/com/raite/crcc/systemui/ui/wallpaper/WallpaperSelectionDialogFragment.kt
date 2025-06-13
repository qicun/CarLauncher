package com.raite.crcc.systemui.ui.wallpaper

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.data.model.Section
import com.raite.crcc.systemui.data.model.WallpaperItem
import com.raite.crcc.systemui.data.repository.WallpaperRepository
import com.raite.crcc.systemui.databinding.DialogWallpaperSelectionBinding
import com.raite.crcc.systemui.util.GridSpacingItemDecoration
import com.raite.crcc.systemui.utils.Plog
import kotlinx.coroutines.flow.collectLatest

class WallpaperSelectionDialogFragment : DialogFragment(), WallpaperAdapter.WallpaperAdapterListener {

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    // View Binding
    private var _binding: DialogWallpaperSelectionBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: WallpaperViewModel by viewModels {
        // 使用 applicationContext 来避免内存泄漏
        WallpaperViewModelFactory(requireContext().applicationContext)
    }

    private lateinit var wallpaperAdapter: WallpaperAdapter
    private var selectionListener: OnWallpaperSelectedListener? = null
    private var selectedWallpaperUrl: String? = null

    interface OnWallpaperSelectedListener {
        fun onWallpaperSelected(wallpaperUrl: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Plog.i(mObjectTag, "onAttach called. Context: ${context.javaClass.simpleName}")
        // 将父 Fragment 或 Activity 设置为监听器
        selectionListener = parentFragment as? OnWallpaperSelectedListener ?: context as? OnWallpaperSelectedListener
        if (selectionListener == null) {
            Plog.w(mObjectTag, "OnWallpaperSelectedListener not implemented by parent Fragment or host Activity.")
        }
    }

    override fun onStart() {
        super.onStart()
        // 设置弹窗尺寸
        dialog?.window?.let {
            Plog.i(mObjectTag, "Setting dialog window size to 1440x810")
            it.setLayout(1440, 810)
            it.setBackgroundDrawableResource(R.color.dialog_background_grey)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Plog.i(mObjectTag, "onCreateView called.")
        _binding = DialogWallpaperSelectionBinding.inflate(inflater, container, false)
        // 移动到 onStart 以确保 dialog 非空
        // dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent) 
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Plog.i(mObjectTag, "onViewCreated called.")
        setupRecyclerView()
        setupClickListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        Plog.d(mObjectTag, "Setting up RecyclerView.")
        wallpaperAdapter = WallpaperAdapter(this)
        
        // 基于项目和弹窗尺寸动态计算列数
        val spacing = resources.getDimensionPixelSize(R.dimen.wallpaper_item_spacing)
        val itemWidth = resources.getDimensionPixelSize(R.dimen.wallpaper_item_width)
        // 总宽度 - 两侧的 padding
        val availableWidth = 1440 - (spacing * 2) 
        val spanCount = availableWidth / (itemWidth + spacing)
        Plog.i(mObjectTag, "Calculated span count: $spanCount")

        val gridLayoutManager = GridLayoutManager(context, spanCount)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // 让 Header 和 AddButton 占据整行
                return when (wallpaperAdapter.getItemViewType(position)) {
                    0, 2 -> spanCount // VIEW_TYPE_HEADER, VIEW_TYPE_ADD_BUTTON
                    else -> 1 // VIEW_TYPE_THUMBNAIL
                }
            }
        }

        binding.wallpaperRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = wallpaperAdapter
            // 添加自定义的间距装饰器
            addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, false))
        }
    }

    private fun setupClickListeners() {
        Plog.d(mObjectTag, "Setting up click listeners.")
        binding.closeButton.setOnClickListener {
            Plog.i(mObjectTag, "Close button clicked.")
            dismiss()
        }
        binding.finishButton.setOnClickListener {
            selectedWallpaperUrl?.let { url ->
                Plog.i(mObjectTag, "Finish button clicked. Selected URL: $url")
                selectionListener?.onWallpaperSelected(url)
            }
            dismiss()
        }
        binding.retryButton.setOnClickListener {
            Plog.i(mObjectTag, "Retry button clicked.")
            viewModel.loadWallpapers()
        }
    }

    private fun observeUiState() {
        Plog.d(mObjectTag, "Observing UI state.")
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                Plog.i(mObjectTag, "UI State changed to: ${state.javaClass.simpleName}")
                // 控制整体 Loading 视图
                binding.loadingProgressBar.isVisible = state is WallpaperUiState.Loading
                binding.errorLayout.isVisible = state is WallpaperUiState.Error
                binding.wallpaperRecyclerView.isVisible = state is WallpaperUiState.Success

                when (state) {
                    is WallpaperUiState.Success -> {
                        Plog.d(mObjectTag, "Updating adapter with ${state.wallpaperItems.size} items. Finish button enabled: ${state.isFinishButtonEnabled}")
                        wallpaperAdapter.submitList(state.wallpaperItems)
                        binding.finishButton.isEnabled = state.isFinishButtonEnabled
                        // 保存当前选中的URL，以便完成按钮使用
                        state.wallpaperItems.filterIsInstance<WallpaperItem.Thumbnail>()
                            .find { it.isSelected }?.let { selectedWallpaperUrl = it.wallpaperUrl }
                    }
                    is WallpaperUiState.Error -> {
                        Plog.e(mObjectTag, "Displaying error state: ${state.message}")
                        // 可以选择性地显示更详细的错误信息
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    WallpaperUiState.Loading -> {
                        Plog.d(mObjectTag, "Displaying loading state.")
                        // 加载时禁用完成按钮
                        binding.finishButton.isEnabled = false
                    }
                }
            }
        }
    }

    // 实现 Adapter 的监听器接口
    override fun onWallpaperClick(wallpaperUrl: String) {
        Plog.i(mObjectTag, "onWallpaperClick received from adapter: $wallpaperUrl")
        viewModel.onWallpaperSelected(wallpaperUrl)
    }

    override fun onToggleSection(section: Section) {
        Plog.i(mObjectTag, "onToggleSection received from adapter: ${section.name}")
        viewModel.toggleSection(section)
    }

    override fun onAddWallpaperClick() {
        Plog.i(mObjectTag, "onAddWallpaperClick received from adapter.")
        Toast.makeText(context, "添加新作品功能待实现", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Plog.i(mObjectTag, "onDestroyView called.")
        _binding = null // 防止内存泄漏
    }

    companion object {
        const val TAG = "WallpaperSelectionDialog"

        fun newInstance(): WallpaperSelectionDialogFragment {
            return WallpaperSelectionDialogFragment()
        }
    }
} 