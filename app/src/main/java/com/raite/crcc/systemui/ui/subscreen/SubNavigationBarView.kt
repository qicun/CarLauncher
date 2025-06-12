/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.subscreen

import android.annotation.DisplayContext
import android.annotation.SuppressLint
import android.content.Context
import android.view.Display
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.raite.crcc.common.focus.FocusManager
import com.raite.crcc.common.util.updateVisibilityIfChanged
import com.raite.crcc.systemui.CmdQueue
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.common.BaseView
import com.raite.crcc.systemui.config.DisplayType.SUB_DISPLAY_TYPE_NAVIGATION_BAR
import com.raite.crcc.systemui.config.ScreenConfig
import com.raite.crcc.systemui.config.UpdateType
import com.raite.crcc.systemui.config.UserConfig
import com.raite.crcc.systemui.data.type.SubDisplayState
import com.raite.crcc.systemui.databinding.SubNavibarLayoutBinding
import com.raite.crcc.systemui.ui.focus.indicator.GlobalDisplayFocusIndicatorManager.cleanDisplayFocusView
import com.raite.crcc.systemui.ui.focus.indicator.GlobalDisplayFocusIndicatorManager.getLastDisplayType
import com.raite.crcc.systemui.ui.navi.NaviBarInfo
import com.raite.crcc.systemui.ui.navi.NavigationBarVM
import com.raite.crcc.systemui.utils.Plog

/**
 * @Author zl
 * @Date 2023/8/21
 * @Description 副屏导航栏
 */
class SubNavigationBarView(
    @DisplayContext val mDisplayContext: Context,
    commandQueue: CmdQueue, displayId: Int
) : BaseView(), View.OnClickListener, CmdQueue.Callback, View.OnKeyListener {

    private val TAG = "SubNavigationBarView"
    private val mBind by lazy {
        SubNavibarLayoutBinding.inflate(LayoutInflater.from(mDisplayContext))
    }
    private var mDisplayId = displayId

    private val mViewModel by lazy { ViewModelProvider(this)[NavigationBarVM::class.java] }

    private val mSubDisplays = arrayOf(
        SubDisplayState.Camera,
        SubDisplayState.A360,
        SubDisplayState.Video,
        SubDisplayState.Disk,
        SubDisplayState.Log,
    )

    fun getRootView(): View = mBind.root

    fun getHomeView(): View = mBind.root.findViewById(R.id.cameraBtn)

    private var observer = Observer { (type, resId, value, boolValue, value1): NaviBarInfo ->
        Plog.i(
            TAG,
            "type = $type,resId = $resId,value = $value；boolValue = $boolValue; value1 = $value1"
        )
        when (type) {
            UpdateType.UPDATE_TYPE_SUB_DISPLAY -> {
                resId?.let {
                    updateSelectItemState(SubDisplayState.fromTypeId(it))
                }
            }

            UpdateType.UPDATE_TYPE_USER_LEVEL -> {
                when (value1) {
                    UserConfig.USER_LEVEL_1 -> {
                        mBind.videoBtn.updateVisibilityIfChanged(false)
                        mBind.logBtn.updateVisibilityIfChanged(false)
                    }

                    UserConfig.USER_LEVEL_2 -> {
                        mBind.videoBtn.updateVisibilityIfChanged(true)
                        mBind.logBtn.updateVisibilityIfChanged(false)
                    }

                    UserConfig.USER_LEVEL_3 -> {
                        mBind.videoBtn.updateVisibilityIfChanged(true)
                        mBind.logBtn.updateVisibilityIfChanged(true)
                    }
                }
            }
        }
    }

    init {
        commandQueue.registerCallback(this)
        mViewModel.getLiveData().observeForever(observer)
        initView()
        FocusManager.bindGlobalFocusIndicator(
            getRootView(),
            getHomeView(),
            SUB_DISPLAY_TYPE_NAVIGATION_BAR
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        for (display in mSubDisplays) {
            getRootView().findViewById<View>(display.viewId).setOnClickListener(this)
            getRootView().findViewById<View>(display.viewId).setOnKeyListener(this)
        }
        getRootView().setOnTouchListener{_, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_OUTSIDE ->
                    ScreenController.updateSubDisplayNaviFocus(false)
            }
            Plog.i(TAG, "onclick setOnTouchListener motionEvent:$motionEvent")
            false
        }
        updateSelectItemState(SubDisplayState.fromTypeId(mViewModel.getSubDisplayTypeSelect()))
    }

    override fun onClick(v: View) {
        updateSelectItemState(SubDisplayState.fromViewId(v.id))
        ScreenController.updateSubDisplayStatusFocus(false)
        ScreenController.updateSubDisplayNaviFocus(true)
        when (v.id) {
            R.id.cameraBtn -> goCctvCamera()
            R.id.a360Btn -> goCctv360()
            R.id.videoBtn -> goCctvVideo()
            R.id.diskBtn -> goCctvDisk()
            R.id.logBtn -> goCctvLog()
        }
    }

    private fun goCctvCamera() {
        Plog.i(TAG, "onclick goCctvCamera")
        mViewModel.openCCTVDisplay(SubDisplayState.Camera)
    }

    private fun goCctv360() {
        Plog.i(TAG, "onclick goCctv360")
        mViewModel.openCCTVDisplay(SubDisplayState.A360)
    }

    private fun goCctvVideo() {
        Plog.i(TAG, "onclick goCctvVideo")
        mViewModel.openCCTVDisplay(SubDisplayState.Video)
    }

    private fun goCctvDisk() {
        Plog.i(TAG, "onclick goCctvDisk")
        mViewModel.openCCTVDisplay(SubDisplayState.Disk)
    }

    private fun goCctvLog() {
        Plog.i(TAG, "onclick goCctvLog")
        mViewModel.openCCTVDisplay(SubDisplayState.Log)
    }

    private fun updateSelectItemState(selectDisplay: SubDisplayState){
        Plog.i(TAG, "updateSelectItemState selectDisplay$selectDisplay")
        for (item in mSubDisplays) {
            getRootView().findViewById<View>(item.viewId).isSelected = selectDisplay == item
        }
    }

    override fun onKey(view: View?, keyCode: Int, event : KeyEvent?): Boolean {
        Plog.i(TAG, "onclick onKey keyCode:$keyCode event:$event")
        if (event?.action == KeyEvent.ACTION_DOWN
            && getLastDisplayType() == Display.DEFAULT_DISPLAY
        ) {
            cleanDisplayFocusView(ScreenConfig.SECOND_DISPLAY_ID)
            ScreenController.updateSubDisplayNaviFocus(focusable = true, requestFocus = true)
            return true
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event?.action == KeyEvent.ACTION_DOWN) {
            ScreenController.updateSubDisplayNaviFocus(focusable = false, requestFocus = true)
            return true
        }
        return false
    }
}