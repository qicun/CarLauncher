/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.navi

import android.content.Intent
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.view.Display
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raite.crcc.cluster.data.drive.DriveManagerService
import com.raite.crcc.common.data.provider.CarriageHeadManager
import com.raite.crcc.common.util.LogUtil
import com.raite.crcc.systemui.App
import com.raite.crcc.systemui.config.CctvConfig
import com.raite.crcc.systemui.config.DisplayType
import com.raite.crcc.systemui.config.ScreenConfig
import com.raite.crcc.systemui.config.UpdateType
import com.raite.crcc.systemui.config.UserConfig
import com.raite.crcc.systemui.data.hvac.HvacManagerService
import com.raite.crcc.systemui.data.hvac.HvacManagerService.HVAC_DEFAULT_TEMP
import com.raite.crcc.systemui.data.hvac.HvacManagerService.HVAC_DEFAULT_TEMP_MIN
import com.raite.crcc.systemui.data.type.CarriageState
import com.raite.crcc.systemui.data.type.DisplayState
import com.raite.crcc.systemui.data.type.HvacCarriageMode
import com.raite.crcc.systemui.data.type.SubDisplayState
import com.raite.crcc.systemui.data.type.fixTemperature
import com.raite.crcc.systemui.provider.DefaultProviderContract.KEY_DISPLAY_TYPE
import com.raite.crcc.systemui.provider.DefaultProviderContract.KEY_SUB_DISPLAY_TYPE
import com.raite.crcc.systemui.provider.DefaultProviderContract.getProviderChangeUri
import com.raite.crcc.systemui.provider.DefaultProviderManager
import com.raite.crcc.systemui.ui.subscreen.SubNaviBarInfo
import com.raite.crcc.systemui.utils.KeyEventUtil
import com.raite.crcc.systemui.utils.Plog
import com.raite.crcc.systemui.utils.SingleLiveData
import com.raite.crcc.systemui.utils.Tools
import com.raite.crcc.systemui.utils.getGlobalInt
import com.raite.crcc.systemui.utils.getUserLevelUri
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @Author lsh
 * @Date 2023/8/14 15:40
 * @Description
 */
class NavigationBarVM : ViewModel() {

    companion object {
        private const val TAG = "NavigationBarVM"
    }
    private var mLiveData: SingleLiveData<NaviBarInfo> = SingleLiveData()
    private var mSubLiveData: SingleLiveData<SubNaviBarInfo> = SingleLiveData()
    private val mUserLevelObserver: UserLevelObserver = UserLevelObserver()
    private var mHvacWindowShow = false
    private var mHvacSubWindowShow = false
    private var mContext = App.mContext

    private var mSelectDisplayType = DisplayType.DISPLAY_TYPE_NONE
    private var mSelectSubDisplayType = DisplayType.DISPLAY_TYPE_NONE

    fun getLiveData(): SingleLiveData<NaviBarInfo> = mLiveData
    fun getSubLiveData(): SingleLiveData<SubNaviBarInfo> = mSubLiveData

    private val mObserverMaps = mapOf(
        KEY_DISPLAY_TYPE to object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) = checkDisplayProvider()
        },
        KEY_SUB_DISPLAY_TYPE to object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) = checkSubDisplayProvider()
        }
    )

    fun isHvacWindowShow(): Boolean = mHvacWindowShow

    fun isHvacSubWindowShow(): Boolean = mHvacSubWindowShow

    //温度
    private var mTemperatureMC1 = HVAC_DEFAULT_TEMP
    private var mTemperatureT = HVAC_DEFAULT_TEMP
    private var mTemperatureMC2 = HVAC_DEFAULT_TEMP
    private var mTemperature = HVAC_DEFAULT_TEMP
    private val mTemperatureOffset = 0.5f

    //选中的车厢
    private var mCarriageSelect : CarriageState = CarriageState.MC1
    private var mCarriageSelIndex = mCarriageSelect.typeId - 1

    private val mCarriageList = arrayListOf(
        CarriageState.MC1,
        CarriageState.T,
        CarriageState.MC2,
        CarriageState.ALL,
    )

    /**
     * 1车空调模式
     */
    private var mHvacModeCar1State: HvacCarriageMode? =
        HvacManagerService.hvacModeCar1Prop.propOps.read.value.value?.value

    /**
     * 2车空调模式
     */
    private var mHvacModeCar2State: HvacCarriageMode? =
        HvacManagerService.hvacModeCar2Prop.propOps.read.value.value?.value

    /**
     * 2车空调模式
     */
    private var mHvacModeCar3State: HvacCarriageMode? =
        HvacManagerService.hvacModeCar3Prop.propOps.read.value.value?.value

    /**
     * 调节车厢 -
     */
    fun turnDownMC() {
        if (mCarriageSelIndex <= 0) {
            mCarriageSelIndex = mCarriageList.size - 1
        } else {
            mCarriageSelIndex  -= 1
        }
        setCarriage()
    }

    /**
     * 调节车厢 +
     */
    fun turnUpMC() {
        if (mCarriageSelIndex >= mCarriageList.size - 1) {
            mCarriageSelIndex = 0
        } else {
            mCarriageSelIndex  += 1
        }
        setCarriage()
    }

    /**
     * 设置车厢
     */
    private fun setCarriage(){
        updateCarriage()
//        DefaultProviderManager.setCarriageType(mContext, mCarriageSelect.typeId)
        HvacManagerService.setHvacControlMode(mCarriageSelect)
    }

    /**
     * 设置选中的车厢和对应温度
     */
    private fun updateCarriage(){
        LogUtil.d(TAG, "updateCarriage, mCarriageSelIndex: $mCarriageSelIndex")
        if (mCarriageSelIndex >= 0 && mCarriageSelIndex<= mCarriageList.size - 1) {
            mCarriageSelect = mCarriageList[mCarriageSelIndex]
        } else {
            mCarriageSelect = CarriageState.MC1
            mCarriageSelIndex = mCarriageSelect.typeId -1
        }

        mLiveData.setValue(
            NaviBarInfo(
                UpdateType.UPDATE_TYPE_CARRIAGE,
                resId = mCarriageSelect.nameRes
            )
        )
        mTemperature = when (mCarriageSelect) {
            CarriageState.MC1 -> mTemperatureMC1
            CarriageState.T -> mTemperatureT
            CarriageState.MC2 -> mTemperatureMC2
            else -> HVAC_DEFAULT_TEMP
        }
        mLiveData.setValue(NaviBarInfo(UpdateType.UPDATE_TYPE_TEMPERATURE))
    }

    /**
     * 调节温度 -
     */
    fun turnDownTemp() {
        setTemp(mTemperature - mTemperatureOffset)
    }

    /**
     * 调节温度 +
     */
    fun turnUpTemp() {
        setTemp(mTemperature + mTemperatureOffset)
    }

    /**
     * 长按自动调节温度-
     */
    fun autoTurnDownTemp() {
        autoTurnTemp(-mTemperatureOffset)
    }

    /**
     * 长按自动调节温度+
     */
    fun autoTurnUpTemp() {
        autoTurnTemp(mTemperatureOffset)
    }

    private val AUTO_INTERVAL = 200L //自旋间隔，毫秒
    private var mInterval = AUTO_INTERVAL
    private var mAutoTurnTempJob: Job? = null
    private fun autoTurnTemp(temperatureOffset: Float) {
        LogUtil.d(TAG, "autoTurnTemp: $temperatureOffset")
        mAutoTurnTempJob = viewModelScope.launch {
            while (true) {
                setTemp(mTemperature + temperatureOffset)
                //每次自旋间隔递减(加速效果)，最小间隔50毫秒
                mInterval = (mInterval * 0.9).toLong().coerceAtLeast(50L)
                delay(mInterval)
            }
        }
    }

    /**
     * 取消自动调节
     */
    fun cancelAutoTurnTemp() {
        LogUtil.d(TAG, "cancelAutoTurnTemp")
        mAutoTurnTempJob?.cancel()
        mInterval = AUTO_INTERVAL
    }

    /**
     * 设置温度
     */
    private fun setTemp(temp: Float){
        val tempCheck = temp.coerceAtLeast(14F).coerceAtMost(28F)
        Plog.i(TAG,  "setTemp, mTemperature:$mTemperature"
                + ", mCarriageSelect: $mCarriageSelect"
                + ", tempCheck: $tempCheck")
        if (mTemperature != tempCheck) {
            mTemperature = tempCheck
            mLiveData.setValue(NaviBarInfo(UpdateType.UPDATE_TYPE_TEMPERATURE))

            var tempFix = 0F
            when (mCarriageSelect) {
                is CarriageState.MC1 -> {
                    tempFix = mHvacModeCar1State.fixTemperature(tempCheck)
                    mTemperatureMC1 = tempFix
                }
                is CarriageState.T -> {
                    tempFix = mHvacModeCar2State.fixTemperature(temp)
                    mTemperatureT = tempFix
                }
                is CarriageState.MC2 -> {
                    tempFix = mHvacModeCar3State.fixTemperature(temp)
                    mTemperatureMC2 = tempFix
                }
                else -> {
                    // not update
                }
            }
            HvacManagerService.setCurrentZoneTemperature(mCarriageSelect, tempFix)
        }
    }

    fun getDisplayTypeSelect() = mSelectDisplayType

    fun getSubDisplayTypeSelect() = mSelectSubDisplayType

    fun getCarriageSelect() = mCarriageSelect

    fun getTemperature() = mTemperature

    fun getTemperatureHi() = when (mCarriageSelect) {
        is CarriageState.MC1 -> mHvacModeCar1State?.tempMax?: HVAC_DEFAULT_TEMP
        is CarriageState.T -> mHvacModeCar2State?.tempMax?: HVAC_DEFAULT_TEMP
        is CarriageState.MC2 -> mHvacModeCar3State?.tempMax?: HVAC_DEFAULT_TEMP
        else -> HVAC_DEFAULT_TEMP
    }

    fun getTemperatureLow() = when (mCarriageSelect) {
        is CarriageState.MC1 -> mHvacModeCar1State?.tempMin?: HVAC_DEFAULT_TEMP_MIN
        is CarriageState.T -> mHvacModeCar2State?.tempMin?: HVAC_DEFAULT_TEMP_MIN
        is CarriageState.MC2 -> mHvacModeCar3State?.tempMin?: HVAC_DEFAULT_TEMP_MIN
        else -> HVAC_DEFAULT_TEMP_MIN
    }

    fun goHome(displayId: Int) {
        KeyEventUtil.backHome(displayId)
        DefaultProviderManager.setDisplayType(mContext, DisplayType.DISPLAY_TYPE_HOME)
    }

    /**
     * 打开主屏页面
     */
    fun openControllerDisplay(state: DisplayState) {
        val intent = Intent(state.classAction)
        Tools.openApp(context = mContext, intent, Display.DEFAULT_DISPLAY)
        DefaultProviderManager.setDisplayType(mContext, state.typeId)
    }

    /**
     * 打开副屏页面
     */
    fun openCCTVDisplay(state: SubDisplayState) {
        mSelectSubDisplayType = state.typeId
        DefaultProviderManager.setSubDisplayType(mContext, state.typeId)
        Tools.openApp(
            context = mContext,
            packageName = CctvConfig.CCTV_PACKAGE_NAME,
            className = state.className,
            displayId = ScreenConfig.SECOND_DISPLAY_ID
        )
    }

    private fun checkDisplayProvider() {
        val newDisplayType = DefaultProviderManager.getDisplayType(mContext)
        Plog.i(TAG, "checkDisplayProvider mSelectDisplayType:$mSelectDisplayType "
                + "newDisplayType:$newDisplayType")
        if (mSelectDisplayType != newDisplayType) {
            mSelectDisplayType = newDisplayType
            mLiveData.setValue(
                NaviBarInfo(
                    UpdateType.UPDATE_TYPE_DISPLAY,
                    resId = mSelectDisplayType
                )
            )
        }
    }

    private fun checkSubDisplayProvider() {
        val newSubDisplayType = DefaultProviderManager.getSubDisplayType(mContext)
        Plog.i(TAG, "checkSubDisplayProvider mSelectSubDisplayType:$mSelectSubDisplayType "
                + "newSubDisplayType:$newSubDisplayType")
        if (mSelectSubDisplayType != newSubDisplayType) {
            mSelectSubDisplayType = newSubDisplayType
            mLiveData.setValue(
                NaviBarInfo(
                    UpdateType.UPDATE_TYPE_SUB_DISPLAY,
                    resId = mSelectSubDisplayType
                )
            )
        }
    }

    init{
        mObserverMaps.forEach { (key, observer) ->
            mContext.contentResolver.registerContentObserver(
                getProviderChangeUri(key), false, observer
            )
        }
        mContext.contentResolver.registerContentObserver(
            getUserLevelUri(),
            false,
            mUserLevelObserver
        )
        checkDisplayProvider()
        checkSubDisplayProvider()
        viewModelScope.launch {
            DriveManagerService.car1AsMainCarriageCarProp

            launch {
                HvacManagerService.hvacControlModeCarProp.propOps.read.collectLatest {
                    val newCarriageTypeIndex = (it.value?.value?: 0) - 1
                    Plog.i(TAG, "hvacControlModeCarProp collectLatest:$it"
                            + ", mCarriageSelIndex:$mCarriageSelIndex"
                            + ", newCarriageTypeIndex:$newCarriageTypeIndex")
                    if (mCarriageSelIndex != newCarriageTypeIndex) {
                        mCarriageSelIndex = newCarriageTypeIndex
                        updateCarriage()
                    }
                }
            }

            launch {
                HvacManagerService.hvacTemp10UWCar1Prop.propOps.read.collectLatest {
                    Plog.i(TAG,  "hvacTemp10UWCar1Prop collectLatest:$it")
                    mTemperatureMC1 = it.value?.value ?: HVAC_DEFAULT_TEMP
                    if (mCarriageSelect == CarriageState.MC1 && mTemperature != mTemperatureMC1) {
                        mTemperature = mTemperatureMC1
                        mLiveData.setValue(NaviBarInfo(UpdateType.UPDATE_TYPE_TEMPERATURE))
                    }
                }
            }

            launch {
                HvacManagerService.hvacTemp10UWCar2Prop.propOps.read.collectLatest {
                    Plog.i(TAG,  "hvacTemp10UWCar2Prop collectLatest:$it")
                    mTemperatureT = it.value?.value ?: HVAC_DEFAULT_TEMP
                    if (mCarriageSelect == CarriageState.T && mTemperature != mTemperatureT) {
                        mTemperature = mTemperatureT
                        mLiveData.setValue(NaviBarInfo(UpdateType.UPDATE_TYPE_TEMPERATURE))
                    }
                }
            }

            launch {
                HvacManagerService.hvacTemp10UWCar3Prop.propOps.read.collectLatest {
                    Plog.i(TAG,  "hvacTemp10UWCar3Prop collectLatest:$it")
                    mTemperatureMC2 = it.value?.value ?: HVAC_DEFAULT_TEMP
                    if (mCarriageSelect == CarriageState.MC2 && mTemperature != mTemperatureMC2) {
                        mTemperature = mTemperatureMC2
                        mLiveData.setValue(NaviBarInfo(UpdateType.UPDATE_TYPE_TEMPERATURE))
                    }
                }
            }

            launch {
                HvacManagerService.hvacModeCar1Prop.propOps.read.collectLatest {
                    LogUtil.d(TAG, "hvacModeCar1Prop collectLatest:$it")
                    mHvacModeCar1State = it.value?.value
                }
            }

            launch {
                HvacManagerService.hvacModeCar2Prop.propOps.read.collectLatest {
                    LogUtil.d(TAG, "hvacModeCar2Prop collectLatest:$it")
                    mHvacModeCar2State = it.value?.value
                }
            }

            launch {
                HvacManagerService.hvacModeCar3Prop.propOps.read.collectLatest {
                    LogUtil.d(TAG, "hvacModeCar3Prop collectLatest:$it")
                    mHvacModeCar3State = it.value?.value
                }
            }

            launch {
                CarriageHeadManager.carriageHead.collectLatest {
                    LogUtil.d(TAG, "carriageHead collectLatest:$it")
                }
            }
        }
    }

    inner class UserLevelObserver : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            val level =
                mContext.getGlobalInt(UserConfig.SETTINGS_USER_LEVEL, UserConfig.USER_LEVEL_3)
            LogUtil.d(TAG, "User level change level=$level")
            mLiveData.setValue(
                NaviBarInfo(
                    UpdateType.UPDATE_TYPE_USER_LEVEL,
                    value1 = level
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        mObserverMaps.forEach { _, observer ->
            mContext.contentResolver.unregisterContentObserver(observer)
        }
        mContext.contentResolver.unregisterContentObserver(mUserLevelObserver)
    }
}