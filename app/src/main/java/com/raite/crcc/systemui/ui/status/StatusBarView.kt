/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.status

import android.annotation.DisplayContext
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.raite.crcc.common.util.LogUtil
import com.raite.crcc.systemui.App
import com.raite.crcc.systemui.CmdQueue
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.common.BaseView
import com.raite.crcc.systemui.common.IConfigChangedListener
import com.raite.crcc.systemui.config.SettingsConfig
import com.raite.crcc.systemui.config.UpdateType
import com.raite.crcc.systemui.config.UserConfig
import com.raite.crcc.systemui.databinding.StatusbarLayoutBinding
import com.raite.crcc.systemui.utils.Dates
import com.raite.crcc.systemui.utils.DisplayWindowManager
import com.raite.crcc.systemui.utils.Plog
import com.raite.crcc.systemui.utils.Tools
import com.raite.crcc.systemui.utils.UiUtil.setRaiteBackgroundColor
import com.raite.crcc.systemui.utils.UiUtil.setRaiteTextColor
import com.raite.crcc.systemui.utils.getGlobalInt
import com.raite.crcc.systemui.utils.getGlobalString
import com.raite.crcc.systemui.utils.getString
import com.raite.crcc.systemui.utils.loge
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Author lsh
 * @Date 2023/7/11 15:32
 * @Description
 */
class StatusBarView(
    @DisplayContext val mDisplayContext: Context,
    commandQueue: CmdQueue, displayId: Int,
    @DisplayWindowManager private val mDisplayWindowManager: WindowManager?
) : BaseView(), View.OnClickListener, CmdQueue.Callback,
    IConfigChangedListener {

    private var mDisplayId = displayId
    private val mViewModel by lazy { ViewModelProvider(this)[StatusBarVM::class.java] }
    private var mRootView: View
    private lateinit var mBind: StatusbarLayoutBinding
    private val TAG = "StatusBarView"

    // 连续点击10次后进入工程模式
    private var mJump2EngineModeJob: Job? = null
    private var mClick2EngineModeCount = 0

    /**
     * 回调状态
     */
    private val observer =
        Observer { (updateType, resId, time, v1, v2, v3, v4, v5, v6): StatusBarInfo ->
            Plog.i(TAG, "updateType = $updateType,resid:$resId v1:$v1 v2:$v2 v3:$v3 v4:$v4 "
                    + "v5:$v5 v6:$v6")
            when (updateType) {
                UpdateType.UPDATE_TYPE_USER_LOGIN,
                UpdateType.UPDATE_TYPE_USER_NAME,
                UpdateType.UPDATE_TYPE_USER_LEVEL -> updateUserInfo()
                UpdateType.UPDATE_TYPE_TIME -> updateTimeUi(time)
                UpdateType.UPDATE_TYPE_EXTERNAL_TEMP -> updateTempUi(v5)
                UpdateType.UPDATE_TYPE_TRAIN_NUM -> updateTrainNumUi(v5)
                UpdateType.UPDATE_TYPE_TERMINAL_STATION_NAME,
                UpdateType.UPDATE_TYPE_CURRENT_STATION_NAME,
                UpdateType.UPDATE_TYPE_NEXT_STATION_NAME -> updateStationName(updateType, v6)
            }
        }

    fun getRootView(): View = mRootView

    init {
        commandQueue.registerCallback(this)
        mBind = StatusbarLayoutBinding.inflate(LayoutInflater.from(mDisplayContext))
        mRootView = mBind.root
        initView()
        EventBus.getDefault().register(this);
        mViewModel.getLiveData().observeForever(observer)
        mViewModel.getTempLiveData().observeForever(observer)
        mViewModel.getTrainNumLiveData().observeForever(observer)
        mViewModel.getSingleLiveData().observes(observer)
        mBind.tvTime.text = Dates().getTimeFormat()
        changeStatusBarSkin()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: StatusBarInfo?) {

    }


    private fun changeStatusBarSkin() {
        Plog.i(TAG, "=====修改状态栏皮肤=====生效=====")
        // change status bar background
        mBind.statusView.setRaiteBackgroundColor(mDisplayContext, R.color.status_bar_bg)
        // change status bar icon
        mBind.tvTime.setRaiteTextColor(mDisplayContext, R.color.white)
        loge("carLinkView: gone")

        mViewModel.changeTime()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        mBind.statusView.setOnTouchListener { _, event ->
            // todo 控制下拉面板是否显示隐藏
            return@setOnTouchListener false
        }
        mBind.tvTime.setOnClickListener {
            // 点击10次进入工程模式界面
            jump2EngineMode()
        }
    }

    private fun jump2EngineMode() {
        mClick2EngineModeCount++
        if (mClick2EngineModeCount >= 10) {
            val intent = Intent(SettingsConfig.ACTION_ENGINE_MODE_ACTIVITY_OPEN)
            Tools.openApp(context = App.mContext, intent, Display.DEFAULT_DISPLAY)
            return
        }
        mJump2EngineModeJob?.cancel()
        mJump2EngineModeJob = mViewModel.viewModelScope.launch {
            delay(1000L)
            mClick2EngineModeCount = 0
        }
    }

    override fun onClick(v: View) {
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

    private fun updateTrainNumUi(trainNum: String) {
        mBind.ivTrainNum.text = getString(R.string.str_status_bar_train_num_des, trainNum)
    }

    private fun updateStationName(type: Int, name: String) {
        when (type) {
            UpdateType.UPDATE_TYPE_TERMINAL_STATION_NAME -> mBind.tvTerminalName.text = name
            UpdateType.UPDATE_TYPE_CURRENT_STATION_NAME -> mBind.tvCurrentStationName.text = name
            UpdateType.UPDATE_TYPE_NEXT_STATION_NAME -> mBind.tvNextStationName.text = name
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) = changeStatusBarSkin()
}
