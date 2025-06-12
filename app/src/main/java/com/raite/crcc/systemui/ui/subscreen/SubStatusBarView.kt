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
import android.content.res.Configuration
import android.view.Display
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.raite.crcc.common.focus.FocusManager
import com.raite.crcc.common.util.LogUtil
import com.raite.crcc.systemui.CmdQueue
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.common.BaseView
import com.raite.crcc.systemui.common.IConfigChangedListener
import com.raite.crcc.systemui.config.DisplayType.SUB_DISPLAY_TYPE_STATUS_BAR
import com.raite.crcc.systemui.config.ScreenConfig
import com.raite.crcc.systemui.config.UpdateType
import com.raite.crcc.systemui.config.UserConfig
import com.raite.crcc.systemui.databinding.SubStatusbarLayoutBinding
import com.raite.crcc.systemui.ui.focus.indicator.GlobalDisplayFocusIndicatorManager.cleanDisplayFocusView
import com.raite.crcc.systemui.ui.focus.indicator.GlobalDisplayFocusIndicatorManager.getLastDisplayType
import com.raite.crcc.systemui.ui.status.StatusBarInfo
import com.raite.crcc.systemui.ui.status.StatusBarVM
import com.raite.crcc.systemui.utils.Dates
import com.raite.crcc.systemui.utils.Plog
import com.raite.crcc.systemui.utils.UiUtil.setRaiteBackgroundColor
import com.raite.crcc.systemui.utils.UiUtil.setRaiteTextColor
import com.raite.crcc.systemui.utils.getGlobalInt
import com.raite.crcc.systemui.utils.getGlobalString
import com.raite.crcc.systemui.utils.getString

/**
 * @Author zl
 * @Date 2023/8/21
 * @Description 副屏状态栏
 */
class SubStatusBarView(
    @DisplayContext val mDisplayContext: Context,
    commandQueue: CmdQueue?, displayId: Int,
) : BaseView(), View.OnClickListener, CmdQueue.Callback, IConfigChangedListener,
    View.OnKeyListener {

    private var mDisplayId = displayId
    private val mViewModel by lazy { ViewModelProvider(this)[StatusBarVM::class.java] }
    private var mRootView: View
    private lateinit var mBind: SubStatusbarLayoutBinding
    private val TAG = "SubStatusBarView"

    private val clickIds = intArrayOf(
        R.id.ivAvatar,
        R.id.tvUserName,
    )

    /**
     * 回调状态
     */
    private val observer =
        Observer { (updateType, resId, time, v1, v2, v3, v4, v5): StatusBarInfo ->
            Plog.i(TAG, "updateType = $updateType,resid:$resId v1:$v1 v2:$v2 v3:$v3 v4:$v4 v5:$v5")
            when (updateType) {
                UpdateType.UPDATE_TYPE_USER_LOGIN,
                UpdateType.UPDATE_TYPE_USER_NAME,
                UpdateType.UPDATE_TYPE_USER_LEVEL -> updateUserInfo()
                UpdateType.UPDATE_TYPE_TIME -> updateTimeUi(time)
                UpdateType.UPDATE_TYPE_EXTERNAL_TEMP -> updateTempUi(v5)
            }
        }


    fun getRootView(): View = mRootView

    fun getAvatarView(): View = mRootView.findViewById(R.id.ivAvatar)

    init {
        commandQueue?.registerCallback(this)
        mBind = SubStatusbarLayoutBinding.inflate(LayoutInflater.from(mDisplayContext))
        mRootView = mBind.root
        mViewModel.getLiveData().observeForever(observer)
        mViewModel.getTempLiveData().observeForever(observer)
        mViewModel.getSingleLiveData().observes(observer)
        mBind.tvTime.text = Dates().getTimeFormat()
        changeStatusBarSkin()
        initView()
        FocusManager.bindGlobalFocusIndicator(
            mRootView,
            getAvatarView(),
            SUB_DISPLAY_TYPE_STATUS_BAR
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        for (id in clickIds) {
            mRootView.findViewById<View>(id).setOnClickListener(this)
            mRootView.findViewById<View>(id).setOnKeyListener(this)
        }
        mRootView.setOnTouchListener{_, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_OUTSIDE ->
                    ScreenController.updateSubDisplayStatusFocus(false)
            }
            Plog.i(TAG, "onclick setOnTouchListener motionEvent:$motionEvent")
            false
        }
    }

    private fun changeStatusBarSkin() {
        Plog.i(TAG, "副屏=====修改状态栏皮肤=====生效=====")
        // change status bar background
        mBind.statusView.setRaiteBackgroundColor(mDisplayContext, R.color.sub_status_bar_bg)
        // change status bar icon
        mBind.tvTime.setRaiteTextColor(mDisplayContext, R.color.white)

        mViewModel.changeTime()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivAvatar, R.id.tvUserName -> goUseApp()
        }
    }

    private fun goUseApp(){
        Plog.i(TAG, "onclick goUseApp")
        ScreenController.updateSubDisplayStatusFocus(false)
        mViewModel.openCCTVUse()
    }

    private fun updateUserInfo() {
        val userLogin = mDisplayContext.getGlobalInt(
            UserConfig.SETTINGS_USER_LOGIN,
            UserConfig.USER_LOGIN_NOT_ID
        )
        val userName = mDisplayContext.getGlobalString(UserConfig.SETTINGS_USER_NAME)
        LogUtil.d(TAG, "updateUserInfo userLogin:$userLogin userName:$userName")
        if (userLogin == UserConfig.USER_LOGIN_ALREADY_ID) {
            mBind.tvUserName.text = userName
        } else {
            mBind.tvUserName.text = getString(R.string.str_status_bar_use_no_login)
        }
    }

    private fun updateTimeUi(time: String) {
        mBind.tvTime.text = time
    }

    private fun updateTempUi(temp: String) {
        mBind.tvTemp.text = getString(R.string.str_status_bar_temp_des, temp)
    }

    override fun onConfigurationChanged(configuration: Configuration) = changeStatusBarSkin()

    override fun onKey(view: View?, keyCode: Int, event : KeyEvent?): Boolean {
        Plog.i(TAG, "onclick onKey keyCode:$keyCode event:$event")

        if (event?.action == KeyEvent.ACTION_DOWN
            && getLastDisplayType() == Display.DEFAULT_DISPLAY
        ) {
            cleanDisplayFocusView(ScreenConfig.SECOND_DISPLAY_ID)
            ScreenController.updateSubDisplayStatusFocus(focusable = true, requestFocus = true)
            return true
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event?.action == KeyEvent.ACTION_DOWN) {
            ScreenController.updateSubDisplayStatusFocus(focusable = false, requestFocus = true)
            return true
        }
        return false
    }
}