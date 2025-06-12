/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.focus.indicator

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.annotation.UiThread
import com.raite.crcc.common.util.LogUtil

@UiThread
class FocusIndicatorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val DEBUG_WIDGET = true
    }

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    // 动画开始位置
    private var mIndicatorLocationAnimateStart: IndicatorLocation? = null

    // 动画结束位置
    private var mIndicatorLocationAnimateEnd: IndicatorLocation? = null

    // indicator 正在显示的位置
    private var mIndicatorLocationCurrent: IndicatorLocation? = null

    // 动画运行的进度[0.0f - 1.0f]
    private val mProgressAnimator = ValueAnimator.ofFloat(0f, 1f)

    private val mFocusIndicatorView: FocusIndicatorView = FocusIndicatorView(context)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val indicatorLocationCurrent = mIndicatorLocationCurrent ?: return
        val width = indicatorLocationCurrent.width.toInt()
        val height = indicatorLocationCurrent.height.toInt()
        mFocusIndicatorView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val indicatorLocationCurrent = mIndicatorLocationCurrent ?: return
        mFocusIndicatorView.layout(
            indicatorLocationCurrent.x.toInt(),
            indicatorLocationCurrent.y.toInt(),
            (indicatorLocationCurrent.x + mFocusIndicatorView.measuredWidth).toInt(),
            (indicatorLocationCurrent.y + mFocusIndicatorView.measuredHeight).toInt(),
        )
    }

    fun showAtLocation(animate: Boolean, location: IndicatorLocation) {
        mProgressAnimator.cancel()

        mIndicatorLocationAnimateStart = mIndicatorLocationCurrent
        mIndicatorLocationAnimateEnd = location

        // 第一次显示 focus indicator 时不使用动画
        // if (width == 0 || height == 0) 时，
        // 从看得见的矩形框到消失，或者从消失到看得见的矩形框时，不执行动画
        val locationAnimate =  mIndicatorLocationAnimateStart?.width != 0f
                && mIndicatorLocationAnimateStart?.height != 0f
                && mIndicatorLocationAnimateEnd?.width != 0f
                && mIndicatorLocationAnimateEnd?.height != 0f
        if (animate && mIndicatorLocationAnimateStart != null
            && locationAnimate
        ) {
            mProgressAnimator.start()
        } else {
            mProgressAnimator.end()
        }
        populateIndicatorLocationCurrent()
    }

    private fun populateIndicatorLocationCurrent() {
        if (DEBUG_WIDGET) {
            LogUtil.i(mObjectTag, "populateIndicatorLocationCurrent")
        }

        val progress = mProgressAnimator.animatedValue as Float

        val startX = mIndicatorLocationAnimateStart?.x ?: 0f
        val endX = mIndicatorLocationAnimateEnd?.x ?: 0f
        val startY = mIndicatorLocationAnimateStart?.y ?: 0f
        val endY = mIndicatorLocationAnimateEnd?.y ?: 0f
        val startWidth = mIndicatorLocationAnimateStart?.width ?: 0f
        val endWidth = mIndicatorLocationAnimateEnd?.width ?: 0f
        val startHeight = mIndicatorLocationAnimateStart?.height ?: 0f
        val endHeight = mIndicatorLocationAnimateEnd?.height ?: 0f


        mIndicatorLocationCurrent = IndicatorLocation(
            x = startX + (endX - startX) * progress,
            y = startY + (endY - startY) * progress,
            width = startWidth + (endWidth - startWidth) * progress,
            height = startHeight + (endHeight - startHeight) * progress,
        )

        if (DEBUG_WIDGET) {
            LogUtil.i(mObjectTag, "mIndicatorLocationCurrent is $mIndicatorLocationCurrent")
        }

        requestLayout()
    }

    init {
        addView(mFocusIndicatorView)

        mProgressAnimator.interpolator = DecelerateInterpolator()
        mProgressAnimator.duration = 200L
        mProgressAnimator.addUpdateListener {
            populateIndicatorLocationCurrent()
        }
    }

}