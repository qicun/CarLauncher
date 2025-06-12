/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.navi

import android.annotation.DisplayContext
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.view.Display
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.raite.crcc.common.focus.FocusManager.bindGlobalFocusIndicator
import com.raite.crcc.common.util.LogUtil
import com.raite.crcc.systemui.CmdQueue
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.common.BaseView
import com.raite.crcc.systemui.common.IConfigChangedListener
import com.raite.crcc.systemui.config.DisplayType.DISPLAY_TYPE_NAVIGATION_BAR
import com.raite.crcc.systemui.config.ScreenConfig
import com.raite.crcc.systemui.config.UpdateType
import com.raite.crcc.systemui.data.type.DisplayState
import com.raite.crcc.systemui.databinding.NavigationbarLayoutBinding
import com.raite.crcc.systemui.ui.focus.indicator.GlobalDisplayFocusIndicatorManager.cleanDisplayFocusView
import com.raite.crcc.systemui.ui.focus.indicator.GlobalDisplayFocusIndicatorManager.getLastDisplayType
import com.raite.crcc.systemui.ui.subscreen.ScreenController
import com.raite.crcc.systemui.utils.Plog
import com.raite.crcc.systemui.utils.getString
import com.raite.crcc.systemui.utils.setGreyView
import com.raite.crcc.systemui.utils.setRaiteOnLongClickListener

/**
 * @Author lsh
 * @Date 2023/7/11 15:13
 * @Description
 */
class NavigationBarView(
    @DisplayContext val mDisplayContext: Context, commandQueue: CmdQueue, displayId: Int,
) : BaseView(), View.OnClickListener, CmdQueue.Callback, IConfigChangedListener,
    View.OnKeyListener{

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    private val TAG = "NavigationBarView"
    private var mRootView: View
    private var mDisplayId = displayId
    private var mBind: NavigationbarLayoutBinding
    private val mViewModel by lazy { ViewModelProvider(this)[NavigationBarVM::class.java] }
    private val mHandler = Handler(Looper.getMainLooper())

    private val ids = intArrayOf(
        R.id.homeBtn,
        R.id.setBtn,
        R.id.mcDownBtn,
        R.id.mcUpBtn,
        R.id.tempDownBtn,
        R.id.tempUpBtn,
        R.id.airBtn,
        R.id.warnBtn,
        R.id.quesBtn,
    )

    private val mDisplays = arrayOf(
        DisplayState.Home,
        DisplayState.Setting,
        DisplayState.Hvac,
        DisplayState.Fault,
        DisplayState.Info,
    )

    private val tabIds = mDisplays.map { it.viewId }.toIntArray()

    fun getRootView(): View = mRootView

    fun getHomeView(): View = mRootView.findViewById(R.id.homeBtn)

    private var observer = Observer { (type, resId, value, boolValue): NaviBarInfo ->
        Plog.i(TAG, "type = $type,resId = $resId,value = $value；boolValue = $boolValue")
        when (type) {
            UpdateType.UPDATE_TYPE_TEMPERATURE -> updateTemp()
            UpdateType.UPDATE_TYPE_CARRIAGE -> resId?.let { updateCarriage(it) }
            UpdateType.UPDATE_TYPE_DISPLAY -> {
                resId?.let {
                    updateSelectItemByState(DisplayState.fromTypeId(it))
                }
            }
        }
    }

    init {
        commandQueue.registerCallback(this)
        mBind = NavigationbarLayoutBinding.inflate(LayoutInflater.from(mDisplayContext))
        mRootView = mBind.root
        mViewModel.getLiveData().observeForever(observer)
        initView()

        bindGlobalFocusIndicator(mRootView, getHomeView(), DISPLAY_TYPE_NAVIGATION_BAR)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        for (id in ids) {
            mRootView.findViewById<View>(id).setOnClickListener(this)
            mRootView.findViewById<View>(id).setOnKeyListener(this)
        }
        initAcTempClick()
        updateCarriage(mViewModel.getCarriageSelect().nameRes)
        updateTemp()
        updateSelectItemByState(DisplayState.fromTypeId(mViewModel.getDisplayTypeSelect()))
        mRootView.setOnTouchListener{_, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_OUTSIDE ->
                    ScreenController.updateDisplayNaviFocus(false)
            }
            Plog.i(TAG, "onclick setOnTouchListener motionEvent:$motionEvent")
            false
        }
    }

    private fun updateCarriage(resId: Int) {
        mBind.mcTv.text = getString(resId)
    }

    private fun updateTemp() {
        if (mViewModel.getCarriageSelect().isUnknownOrAll()) {
            mBind.tempUpBtn.setGreyView(true)
            mBind.tempDownBtn.setGreyView(true)
            mBind.tempTv.text = getString(R.string.default_temp)
        } else {
            val temperature = mViewModel.getTemperature()
            if (temperature <= mViewModel.getTemperatureLow()) {
                mBind.tempDownBtn.setGreyView(true)
                mBind.tempUpBtn.setGreyView(false)
            } else if (temperature >= mViewModel.getTemperatureHi()) {
                mBind.tempUpBtn.setGreyView(true)
                mBind.tempDownBtn.setGreyView(false)
            } else {
                mBind.tempUpBtn.setGreyView(false)
                mBind.tempDownBtn.setGreyView(false)

            }
            mBind.tempTv.text = String.format(
                getString(R.string.ac_temperature), mViewModel.getTemperature()
            )
        }
    }

    private fun updateWindSpeedState() {

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initAcTempClick() {
        mBind.tempUpBtn.setRaiteOnLongClickListener {
            LogUtil.d(TAG, "tempUpBtn longClick")
            mViewModel.autoTurnUpTemp()
        }
        mBind.tempDownBtn.setRaiteOnLongClickListener {
            LogUtil.d(TAG, "tempDownBtn longClick")
            mViewModel.autoTurnDownTemp()
        }
        val tl = View.OnTouchListener { p0, p1 ->
            if (p1.action == MotionEvent.ACTION_UP ||
                p1.action == MotionEvent.ACTION_CANCEL) {
                mViewModel.cancelAutoTurnTemp()
            }
            false
        }
        mBind.tempUpBtn.setOnTouchListener(tl)
        mBind.tempDownBtn.setOnTouchListener(tl)
    }

    override fun onClick(v: View) {
        updateSelectItemByID(v.id)
        ScreenController.updateDisplayNaviFocus(true)
        when (v.id) {
            R.id.homeBtn -> goHome()
            R.id.setBtn -> goSetting()
            R.id.mcDownBtn -> turnDownMc()
            R.id.mcUpBtn -> turnUpMc()
            R.id.tempDownBtn -> turnDownTemp()
            R.id.tempUpBtn -> turnUpTemp()
            R.id.airBtn -> goHVAC()
            R.id.warnBtn -> goFault()
            R.id.quesBtn -> goSettingHelp()
        }
    }

    private fun updateSelectItemByID(selId: Int) {
        Plog.d(TAG, "updateSelectItemByID:$selId")
        if (tabIds.contains(selId)) {
            for (id in tabIds) {
                mRootView.findViewById<View>(id).isSelected = selId == id
            }
        }
    }

    private fun updateSelectItemByState(selDisplay: DisplayState) {
        Plog.i(TAG, "updateSelectItemByState selDisplay:$selDisplay")
        for (item in mDisplays) {
            mRootView.findViewById<View>(item.viewId).isSelected = selDisplay == item
        }
    }

    private fun turnUpMc() {
        Plog.i(TAG, "onclick turnUpMc")
        mViewModel.turnUpMC()
    }

    private fun turnDownMc() {
        Plog.i(TAG, "onclick turnDownMc")
        mViewModel.turnDownMC()
    }

    private fun turnUpTemp() {
        Plog.i(TAG, "onclick turnUpTemp")
        if (mViewModel.getCarriageSelect().isSingleZone()) {
            mViewModel.turnUpTemp()
        }
    }

    private fun turnDownTemp() {
        Plog.i(TAG, "onclick turnDownTemp")
        if (mViewModel.getCarriageSelect().isSingleZone()) {
            mViewModel.turnDownTemp()
        }
    }

    private fun goHome() {
        Plog.i(TAG, "onclick goHome")
        mViewModel.goHome(mDisplayId)
    }

    private fun goSetting() {
        Plog.i(TAG, "onclick goSetting")
        mViewModel.openControllerDisplay(DisplayState.Setting)
    }

    private fun goSettingHelp() {
        Plog.i(TAG, "onclick goSettingHelp")
        mViewModel.openControllerDisplay(DisplayState.Info)
    }

    private fun goHVAC() {
        Plog.i(TAG, "onclick goHVAC")
        mViewModel.openControllerDisplay(DisplayState.Hvac)
    }

    private fun goFault() {
        Plog.i(TAG, "onclick goFault")
        mViewModel.openControllerDisplay(DisplayState.Fault)
    }

    override fun onConfigurationChanged(configuration: Configuration) {
    }

    override fun onKey(view: View?, keyCode: Int, event : KeyEvent?): Boolean {
        Plog.i(TAG, "onclick onKey keyCode:$keyCode event:$event")
        if (event?.action == KeyEvent.ACTION_DOWN
            && getLastDisplayType() == ScreenConfig.SECOND_DISPLAY_ID
        ) {
            cleanDisplayFocusView(Display.DEFAULT_DISPLAY)
            ScreenController.updateDisplayNaviFocus(focusable = true, requestFocus = true)
            return true
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event?.action == KeyEvent.ACTION_DOWN) {
            ScreenController.updateDisplayNaviFocus(focusable = false, requestFocus = true)
            return true
        }
        return false
    }

}