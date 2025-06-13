/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.status

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raite.crcc.cluster.data.drive.DriveManagerService
import com.raite.crcc.cluster.data.line.LineManagerService
import com.raite.crcc.systemui.bean.NetWorkBean
import com.raite.crcc.systemui.bean.StatusBean
import com.raite.crcc.systemui.config.CctvConfig
import com.raite.crcc.systemui.config.UpdateType
import com.raite.crcc.systemui.config.UserConfig
import com.raite.crcc.systemui.util.ContextUtil
import com.raite.crcc.systemui.utils.Block
import com.raite.crcc.systemui.utils.Dates
import com.raite.crcc.systemui.utils.Plog
import com.raite.crcc.systemui.utils.SingleLiveData
import com.raite.crcc.systemui.utils.Tools
import com.raite.crcc.systemui.utils.getGlobalInt
import com.raite.crcc.systemui.utils.getTimeFormatUri
import com.raite.crcc.systemui.utils.getUserLevelUri
import com.raite.crcc.systemui.utils.getUserLoginUri
import com.raite.crcc.systemui.utils.getUserNameUri
import com.raite.crcc.systemui.utils.logi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import org.greenrobot.eventbus.EventBus

/**
 * @Author lsh
 * @Date 2023/6/30 15:20
 * @Description
 */
open class StatusBarVM : ViewModel() {
    private var TAG = StatusBarVM::class.java.simpleName
    private var mContext = ContextUtil.context
    private var mLastTime: String = Dates().getTimeFormat()
    protected var mLiveData: SingleLiveData<StatusBarInfo> = SingleLiveData()
    protected var mTempLiveData: SingleLiveData<StatusBarInfo> = SingleLiveData()
    protected var mTrainNumLiveData: SingleLiveData<StatusBarInfo> = SingleLiveData()
    protected val sLiveData by lazy { SingleLiveData<StatusBarInfo>().apply {
        postValue(StatusBarInfo(UpdateType.UPDATE_TYPE_TIME, time = mLastTime))
    } }
    private var mTimeReceiver: TimeReceiver = TimeReceiver()
    private val mMainHandler: Handler = Handler(Looper.getMainLooper())
    private val mUserLoginObserver: UserLoginObserver = UserLoginObserver()
    private val mUserNameObserver: UserNameObserver = UserNameObserver()
    private val mUserLevelObserver: UserLevelObserver = UserLevelObserver()
    private val mDateFormatObserver: DateFormatObserver = DateFormatObserver()
    private var isCarLinked = false
    private var mIsLogin = false
    var statusBean: MutableLiveData<StatusBean> = MutableLiveData()
    var netWorkBean: MutableLiveData<NetWorkBean> = MutableLiveData()
    var time: MutableLiveData<String> = MutableLiveData()

    companion object {
        private const val TAG = "StatusBarVM"
    }

    fun getLiveData(): SingleLiveData<StatusBarInfo> = mLiveData
    fun getTempLiveData(): SingleLiveData<StatusBarInfo> = mTempLiveData
    fun getTrainNumLiveData(): SingleLiveData<StatusBarInfo> = mTrainNumLiveData
    fun getSingleLiveData(): SingleLiveData<StatusBarInfo> = sLiveData

    private fun registerTimeReceiver() {
        Plog.i(TAG, "registerReceiver $mLastTime")
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_TIME_TICK)
        filter.addAction(Intent.ACTION_TIME_CHANGED)
        mContext.registerReceiver(mTimeReceiver, filter)
    }

    private inner class TimeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action?.let {
                when (it) {
                    Intent.ACTION_TIME_TICK -> checkTime()
                    Intent.ACTION_TIME_CHANGED -> checkTime()
                }
            }
        }
    }

    private fun checkUserLogin() {
        mIsLogin = mContext.getGlobalInt(
            UserConfig.SETTINGS_USER_LOGIN,
            UserConfig.USER_LOGIN_NOT_ID
        ) == UserConfig.USER_LOGIN_ALREADY_ID
        Plog.i(TAG, "checkUserLogin $mIsLogin")
        mLiveData.setValue(
            StatusBarInfo(
                UpdateType.UPDATE_TYPE_USER_LOGIN,
                value1 = mIsLogin
            )
        )
    }

    private fun checkTime() {
        val timeNow = Dates().getTimeFormat()
        if (mLastTime != timeNow) {
            mLastTime = timeNow
            sLiveData.postValue(
                StatusBarInfo(UpdateType.UPDATE_TYPE_TIME, time = timeNow)
            )
        }
    }

    fun changeTime() {
        val timeNow = Dates().getTimeFormat()
        logi("更新时间---${timeNow}")
        sLiveData.postValue(
            StatusBarInfo(UpdateType.UPDATE_TYPE_TIME, time = timeNow)
        )
    }

    inner class UserLoginObserver : ContentObserver(mMainHandler) {
        override fun onChange(selfChange: Boolean) = checkUserLogin()
    }

    inner class UserNameObserver : ContentObserver(mMainHandler) {
        override fun onChange(selfChange: Boolean) {
            mLiveData.setValue(StatusBarInfo(UpdateType.UPDATE_TYPE_USER_NAME))
        }
    }

    inner class UserLevelObserver : ContentObserver(mMainHandler) {
        override fun onChange(selfChange: Boolean) {
            mLiveData.setValue(StatusBarInfo(UpdateType.UPDATE_TYPE_USER_LEVEL))
        }
    }

    inner class DateFormatObserver : ContentObserver(mMainHandler) {
        override fun onChange(selfChange: Boolean) = checkTime()
    }

    private var callback: Block? = null

    fun showVolumeBar(callback: Block) {
        this.callback = callback
    }

    private fun updateData(data: StatusBarInfo) {
        EventBus.getDefault().post(data)
    }

    override fun onCleared() {
        super.onCleared()
        mContext.unregisterReceiver(mTimeReceiver)
        mContext.contentResolver.unregisterContentObserver(mUserLoginObserver)
        mContext.contentResolver.unregisterContentObserver(mUserNameObserver)
        mContext.contentResolver.unregisterContentObserver(mUserLevelObserver)
        mContext.contentResolver.unregisterContentObserver(mDateFormatObserver)
    }

    fun openCCTVUse() {
        Plog.i(TAG, "openCCTVUse $mIsLogin")
        if (mIsLogin) {
            Tools.openService(
                context = mContext,
                packageName = CctvConfig.CCTV_PACKAGE_NAME,
                className = CctvConfig.CCTV_SERVICE_CLASS_NAME,
                key = CctvConfig.CCTV_KEY_USE_LOGIN_TYPE,
                value = CctvConfig.CCTV_VALUE_USE_LOGIN_TYPE
            )
        }
    }

    private fun updateStationName(type: Int, name: String) {
        sLiveData.postValue(StatusBarInfo(
            updateType = type,
            value6 = name
        ))
    }

    init {
        registerTimeReceiver()
        checkUserLogin()
        mContext.contentResolver.registerContentObserver(
            getUserLoginUri(),
            false,
            mUserLoginObserver
        )
        mContext.contentResolver.registerContentObserver(getUserNameUri(), false, mUserNameObserver)
        mContext.contentResolver.registerContentObserver(
            getUserLevelUri(),
            false,
            mUserLevelObserver
        )
        mContext.contentResolver.registerContentObserver(
            getTimeFormatUri(),
            false,
            mDateFormatObserver
        )

        viewModelScope.launch {
            launch {
                DriveManagerService.car2ExternalTempCarProp.propOps.read.collectLatest {
                    val temp = it.value?.value ?: 0f
                    Plog.i(TAG, "car2ExternalTempCarProp temp:$temp")
                    mTempLiveData.setValue(
                        StatusBarInfo(
                            UpdateType.UPDATE_TYPE_EXTERNAL_TEMP,
                            value5 = temp.toString()
                        )
                    )
                }
            }

            launch {
                DriveManagerService.trainNumCarProp.propOps.read.collectLatest {
                    val trainNum = it.value?.value ?: 0
                    Plog.i(TAG, "car2ExternalTempCarProp trainNum:$trainNum")
                    mTrainNumLiveData.setValue(
                        StatusBarInfo(
                            UpdateType.UPDATE_TYPE_TRAIN_NUM,
                            value5 = trainNum.toString()
                        )
                    )
                }
            }

            launch {
                LineManagerService.terminalStationName.collectLatest {
                    updateStationName(UpdateType.UPDATE_TYPE_TERMINAL_STATION_NAME, it)
                }
            }

            launch {
                LineManagerService.currentStationName.collectLatest {
                    updateStationName(UpdateType.UPDATE_TYPE_CURRENT_STATION_NAME, it)
                }
            }

            launch {
                LineManagerService.nextStationName.collectLatest {
                    updateStationName(UpdateType.UPDATE_TYPE_NEXT_STATION_NAME, it)
                }
            }
            LineManagerService.pis1TerminalStationCarProp
            LineManagerService.pis2TerminalStationCarProp
            LineManagerService.pis1CurrentStationCarProp
            LineManagerService.pis2CurrentStationCarProp
            LineManagerService.pis1NextStationCarProp
            LineManagerService.pis2NextStationCarProp
        }
    }
}