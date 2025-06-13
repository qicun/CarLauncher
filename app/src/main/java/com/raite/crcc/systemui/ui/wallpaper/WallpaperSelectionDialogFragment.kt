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
import com.raite.crcc.systemui.data.model.Section
import com.raite.crcc.systemui.data.model.WallpaperItem
import com.raite.crcc.systemui.data.repository.WallpaperRepository
import com.raite.crcc.systemui.databinding.DialogWallpaperSelectionBinding
import kotlinx.coroutines.flow.collectLatest

class WallpaperSelectionDialogFragment : DialogFragment(), WallpaperAdapter.WallpaperAdapterListener {

    // View Binding
    private var _binding: DialogWallpaperSelectionBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: WallpaperViewModel by viewModels {
        WallpaperViewModelFactory(WallpaperRepository()) // 在实际应用中，这里应该通过依赖注入提供Repository
    }

    private lateinit var wallpaperAdapter: WallpaperAdapter
    private var selectionListener: OnWallpaperSelectedListener? = null
    private var selectedWallpaperUrl: String? = null

    interface OnWallpaperSelectedListener {
        fun onWallpaperSelected(wallpaperUrl: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 将父 Fragment 或 Activity 设置为监听器
        selectionListener = parentFragment as? OnWallpaperSelectedListener ?: context as? OnWallpaperSelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWallpaperSelectionBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent) // 使用透明背景以显示圆角
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        wallpaperAdapter = WallpaperAdapter(this)
        val gridLayoutManager = GridLayoutManager(context, 4) // 初始设置为4列

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // 让 Header 和 AddButton 占据整行
                return when (wallpaperAdapter.getItemViewType(position)) {
                    0, 2 -> gridLayoutManager.spanCount // VIEW_TYPE_HEADER, VIEW_TYPE_ADD_BUTTON
                    else -> 1 // VIEW_TYPE_THUMBNAIL
                }
            }
        }

        binding.wallpaperRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = wallpaperAdapter
        }
    }

    private fun setupClickListeners() {
        binding.closeButton.setOnClickListener { dismiss() }
        binding.finishButton.setOnClickListener {
            selectedWallpaperUrl?.let { url ->
                selectionListener?.onWallpaperSelected(url)
            }
            dismiss()
        }
        binding.retryButton.setOnClickListener { viewModel.loadWallpapers() }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                // 控制整体 Loading 视图
                binding.loadingProgressBar.isVisible = state is WallpaperUiState.Loading
                binding.errorLayout.isVisible = state is WallpaperUiState.Error
                binding.wallpaperRecyclerView.isVisible = state is WallpaperUiState.Success

                when (state) {
                    is WallpaperUiState.Success -> {
                        wallpaperAdapter.submitList(state.wallpaperItems)
                        binding.finishButton.isEnabled = state.isFinishButtonEnabled
                        // 保存当前选中的URL，以便完成按钮使用
                        state.wallpaperItems.filterIsInstance<WallpaperItem.Thumbnail>()
                            .find { it.isSelected }?.let { selectedWallpaperUrl = it.wallpaperUrl }
                    }
                    is WallpaperUiState.Error -> {
                        // 可以选择性地显示更详细的错误信息
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    WallpaperUiState.Loading -> {
                        // 加载时禁用完成按钮
                        binding.finishButton.isEnabled = false
                    }
                }
            }
        }
    }

    // 实现 Adapter 的监听器接口
    override fun onWallpaperClick(wallpaperUrl: String) {
        viewModel.onWallpaperSelected(wallpaperUrl)
    }

    override fun onToggleSection(section: Section) {
        viewModel.toggleSection(section)
    }

    override fun onAddWallpaperClick() {
        Toast.makeText(context, "添加新作品功能待实现", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 防止内存泄漏
    }

    companion object {
        const val TAG = "WallpaperSelectionDialog"

        fun newInstance(): WallpaperSelectionDialogFragment {
            return WallpaperSelectionDialogFragment()
        }
    }
} 