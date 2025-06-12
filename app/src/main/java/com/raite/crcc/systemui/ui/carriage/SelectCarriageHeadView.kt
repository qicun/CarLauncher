package com.raite.crcc.systemui.ui.carriage

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.raite.crcc.common.data.type.CarriageType
import com.raite.crcc.common.util.LogUtil
import com.raite.crcc.common.util.appContext
import com.raite.crcc.common.util.updateVisibilityIfChanged
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.databinding.DialogSelectCarriageHeadBinding
import com.raite.crcc.systemui.utils.getString
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SelectCarriageHeadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), LifecycleOwner {

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    private val mBinding by lazy {
        DialogSelectCarriageHeadBinding.inflate(
            LayoutInflater.from(context), this, true
        )
    }

    private lateinit var mRotateAnimation: Animation

    private val mLifecycleRegistry by lazy {
        LifecycleRegistry(this)
    }

    override val lifecycle: Lifecycle
        get() = mLifecycleRegistry

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        LogUtil.i(mObjectTag, "onAttachedToWindow")
        initView()
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        bindFeatureState()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        LogUtil.i(mObjectTag, "onDetachedFromWindow")
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        mBinding.ivLoad.clearAnimation()
    }


    private fun initView() {
        mRotateAnimation = AnimationUtils.loadAnimation(
            context, R.anim.cluster_load_rotate_animation
        )
        mBinding.ivCarriageMc1.setOnClickListener {
            LogUtil.i(mObjectTag, "ivCarriageMc1 onClick")
            SelectCarriageHeadManager.setPrepareSelectMainCab(CarriageType.Car1)
        }

        mBinding.ivCarriageMc2.setOnClickListener {
            LogUtil.i(mObjectTag, "ivCarriageMc2 onClick")
            SelectCarriageHeadManager.setPrepareSelectMainCab(CarriageType.Car3)
        }

        mBinding.tvSetButton.setOnClickListener {
            LogUtil.i(mObjectTag, "tvSetButton onClick")
            SelectCarriageHeadManager.setSelectMainCab()
        }

        mBinding.tvCancelButton.setOnClickListener {
            LogUtil.i(mObjectTag, "tvCancelButton onClick")
            SelectCarriageHeadManager.setPrepareSelectMainCab(CarriageType.Unknown)
        }
    }

    private fun updateConfirmViewVisible(type: CarriageType) {
        mBinding.ivCarriageMc1.background = null
        mBinding.ivCarriageMc2.background = null
        val carriage = when (type) {
            CarriageType.Car1 -> {
                mBinding.ivCarriageMc1.setBackgroundResource(R.drawable.select_carriage_mc1)
                getString(R.string.carriage_mc1_def)
            }
            CarriageType.Car3 -> {
                mBinding.ivCarriageMc2.setBackgroundResource(R.drawable.select_carriage_mc2)
                getString(R.string.carriage_mc2_def)
            }
            else -> ""
        }
        val visible = type == CarriageType.Car1 || type == CarriageType.Car3
        mBinding.tvConfirmMsg.text = appContext().getString(
            R.string.str_select_carriage_head_confirm_msg, carriage
        )
        mBinding.groupConfirm.updateVisibilityIfChanged(visible)
    }

    private fun updateLoadingViewVisible(visible: Boolean) {
        mBinding.groupLoad.updateVisibilityIfChanged(visible)
        if (visible) {
            mBinding.ivLoad.startAnimation(mRotateAnimation)
        } else {
            mBinding.ivLoad.clearAnimation()
        }
    }

    private fun bindFeatureState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    SelectCarriageHeadManager.prepareSelectMainCab.collectLatest {
                        LogUtil.i(mObjectTag, "prepareSelectMainCab:$it")
                        updateConfirmViewVisible(it)
                    }
                }
                launch {
                    SelectCarriageHeadManager.localSaveMainCab.collectLatest {
                        LogUtil.i(mObjectTag, "selectMainCab:$it")
                        val isValidCab = it == CarriageType.Car1 || it == CarriageType.Car3
                        updateLoadingViewVisible(isValidCab)
                    }
                }
            }
        }
    }
}