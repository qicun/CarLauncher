/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.annotation.UiContext
import android.annotation.UiThread
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.raite.crcc.systemui.App
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.databinding.LayoutToastBinding
import com.raite.crcc.systemui.utils.delayExecute

/**
 * @Author lsh
 * @Date 2023/8/4 15:17
 * @Description
 */

@UiThread
@UiContext
fun showToastShort(text: String) {
    Toast().show(text)
}

class Toast {

    private val mMillis = 1500L
    private val mContext = App.mContext
    private var mDialog: Dialog = Dialog(mContext, R.style.ToastStyle)
    private val mBinding = LayoutToastBinding.inflate(LayoutInflater.from(mContext))

    init {
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.setContentView(mBinding.root)
        mDialog.setCanceledOnTouchOutside(true)
        mDialog.setCancelable(true)

        val params = mDialog.window?.attributes
        params?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params?.height = getScreenHeight()
        params?.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mDialog.window?.attributes = params

        mDialog.window?.setDimAmount(0f)
        mDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        mDialog.window?.setType(2018)
        mDialog.window?.setGravity(Gravity.BOTTOM)
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.window?.setWindowAnimations(R.style.volume_bar_anim)
        mDialog.setOnDismissListener { mDialog.dismiss() }
    }

    private fun getScreenHeight(): Int {
        val height = Resources.getSystem().displayMetrics.heightPixels
        val px2dp = px2dp(height.toFloat()) / 2 - 55 // 185
        loge("get height : ${px2dp}--${height}")
        return px2dp
    }

    private fun px2dp(pxValue: Float): Int {
        val scale = mContext.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun show(text: String) {
        if (!mDialog.isShowing) {
            mBinding.tvContent.text = text
            mDialog.show()
        }
        delayExecute({
            if (mDialog.isShowing) {
                mDialog.dismiss()
            }
        }, mMillis)
    }

}