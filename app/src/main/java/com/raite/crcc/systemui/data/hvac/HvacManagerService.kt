/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.data.hvac

import android.hardware.automotive.vehicle.V2_0.VehicleProperty
import com.raite.crcc.cluster.data.drive.DriveManagerService
import com.raite.crcc.common.data.carprop.CarProp
import com.raite.crcc.common.data.carprop.CarVehicleArea
import com.raite.crcc.common.data.carprop.CarVehiclePropertyType
import com.raite.crcc.common.data.carprop.PropFrozen
import com.raite.crcc.common.data.carprop.PropMode
import com.raite.crcc.common.data.carprop.PropertyValue
import com.raite.crcc.common.data.provider.CarriageHeadManager
import com.raite.crcc.common.data.type.CarriageType
import com.raite.crcc.common.util.LogUtil
import com.raite.crcc.common.util.customScope
import com.raite.crcc.systemui.data.hvac.HvacManagerService.carPropertyValue2HVAC1Temp10UW
import com.raite.crcc.systemui.data.hvac.HvacManagerService.carPropertyValue2HVAC3Temp10UW
import com.raite.crcc.systemui.data.hvac.HvacManagerService.carriage2carPropertyValue
import com.raite.crcc.systemui.data.hvac.HvacManagerService.zoneTemp2CarProperty
import com.raite.crcc.systemui.data.type.CarriageState
import com.raite.crcc.systemui.data.type.HvacCarriageMode


object HvacManagerService {

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    const val HVAC_DEFAULT_TEMP = 28.0F
    const val HVAC_DEFAULT_TEMP_MIN = 14.0F
    // 关联所有车厢的温度调节
    private val mTempFrozen = PropFrozen(
        mDebugName = "mTempFrozen",
        mScope = customScope(coroutineName = "mTempFrozen")
    )
    /////////////////////////////////////////////////////////////
    /**
     * 1车客室车内温度只读
     */
    val passengerZoneTempRow1CarProp by lazy {
        CarProp<Int, Float>(
            mPropertyName = "mPassengerZoneTempRow1CarProp",
            mInitValue = PropertyValue(value = HVAC_DEFAULT_TEMP),
            mFrozen = mTempFrozen,
            mPropertyId = VehicleProperty.VCU_HMI_Car1InnerTemp,
            mCarVehiclePropertyType = CarVehiclePropertyType.Int32,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mReadValueParser = { carPropertyValue2PassengerZoneTemp() },
            mPropMode = PropMode(PropMode.READ or PropMode.CHANGE),
        )
    }

    /**
     * 偏移量 80，即（数值-80）*0.5℃，Temp=(val-80)*0.5
     */
    private fun Int?.carPropertyValue2PassengerZoneTemp(): Float {
        LogUtil.i(mObjectTag, "carPropertyValue2PassengerZoneTemp: $this")
        if (this == null) {
            return 0F
        }
        return (this - 80) * 0.5F
    }

    /////////////////////////////////////////////////////////////
    /**
     * 2车客室车内温度只读
     */
    val passengerZoneTempRow2CarProp by lazy {
        CarProp<Int, Float>(
            mPropertyName = "mPassengerZoneTempRow2CarProp",
            mInitValue = PropertyValue(value = HVAC_DEFAULT_TEMP),
            mFrozen = mTempFrozen,
            mPropertyId = VehicleProperty.VCU_HMI_Car2InnerTemp,
            mCarVehiclePropertyType = CarVehiclePropertyType.Int32,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mReadValueParser = { carPropertyValue2PassengerZoneTemp2() },
            mPropMode = PropMode(PropMode.READ or PropMode.CHANGE),
        )
    }

    private fun Int?.carPropertyValue2PassengerZoneTemp2(): Float {
        LogUtil.i(mObjectTag, "carPropertyValue2PassengerZoneTemp2: $this")
        if (this == null) {
            return 0F
        }
        return (this - 80) * 0.5F
    }
    /////////////////////////////////////////////////////////////
    /**
     * 3车客室车内温度只读
     */
    val passengerZoneTempRow3CarProp by lazy {
        CarProp<Int, Float>(
            mPropertyName = "mPassengerZoneTempRow3CarProp",
            mInitValue = PropertyValue(value = HVAC_DEFAULT_TEMP),
            mFrozen = mTempFrozen,
            mPropertyId = VehicleProperty.VCU_HMI_Car3InnerTemp,
            mCarVehiclePropertyType = CarVehiclePropertyType.Int32,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mReadValueParser = { carPropertyValue2PassengerZoneTemp3() },
            mPropMode = PropMode(PropMode.READ or PropMode.CHANGE),
        )
    }

    private fun Int?.carPropertyValue2PassengerZoneTemp3(): Float {
        LogUtil.i(mObjectTag, "carPropertyValue2PassengerZoneTemp3: $this")
        if (this == null) {
            return 0F
        }
        return (this - 80) * 0.5F
    }
    /////////////////////////////////////////////////////////////
    /**
     * HMI1车内温度只写
     */
    private val mZoneTempCar1Prop by lazy {
        CarProp<Int, Float>(
            mPropertyName = "mZoneTempCar1Prop",
            mInitValue = PropertyValue<Float>(value = 0F),
            mPropertyId = VehicleProperty.HMI1_HAVCTemp10UW,
            mCarVehiclePropertyType = CarVehiclePropertyType.Int32,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mFrozen = mTempFrozen,
            mWriteValueParser = { zoneTemp2CarProperty() },
            mPropMode = PropMode(PropMode.Companion.WRITE),
        )
    }

    /**
     * HMI2车内温度只写
     */
    private val mZoneTempCar2Prop by lazy {
        CarProp<Int, Float>(
            mPropertyName = "mZoneTempCar2Prop",
            mInitValue = PropertyValue<Float>(value = 0F),
            mPropertyId = VehicleProperty.HMI2_HAVCTemp10UW,
            mCarVehiclePropertyType = CarVehiclePropertyType.Int32,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mFrozen = mTempFrozen,
            mWriteValueParser = { zoneTemp2CarProperty() },
            mPropMode = PropMode(PropMode.Companion.WRITE),
        )
    }

    /**
     * 温度=数值*0.5℃
     */
    private fun Float.zoneTemp2CarProperty(): Int {
        LogUtil.i(mObjectTag, "zoneTemp2CarProperty: $this")
        return (this * 2).toInt()
    }

    /**
     * 设置车厢温度
     */
    fun setCurrentZoneTemperature(carriage: CarriageState, temp: Float) {
        val carriageHead: CarriageType = CarriageHeadManager.carriageHead.value
        LogUtil.i(mObjectTag, "setCurrentZoneTemperature, carriage: $carriage, temp: $temp"
                + ", carriageHead: $carriageHead")
        mTempFrozen.setFrozen()
        if (carriageHead is CarriageType.Car1) {
            mZoneTempCar1Prop.propOps.writeToRemote(temp)
            hmi1HvacControlModeCarProp.propOps.writeToRemote(carriage)
        } else if (carriageHead is CarriageType.Car3) {
            mZoneTempCar2Prop.propOps.writeToRemote(temp)
            hmi2HvacControlModeCarProp.propOps.writeToRemote(carriage)
        }
    }

    /////////////////////////////////////////////////////////////
    /**
     * 设置车厢
     */
    val hvacCarriageCarProp by lazy {
        CarProp<Int, CarriageState>(
            mPropertyName = "mHvacCarriageCarProp",
            mInitValue = PropertyValue(value = CarriageState.Unknown),
            mFrozen = mTempFrozen,
            mPropertyId = VehicleProperty.HMI_VCU_HAVC_Carriage,
            mCarVehiclePropertyType = CarVehiclePropertyType.Int32,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mWriteValueParser = { carriage2carPropertyValue() },
            mPropMode = PropMode(PropMode.WRITE),
        )
    }

    private fun CarriageState?.carriage2carPropertyValue(): Int {
        LogUtil.i(mObjectTag, "carriage2carPropertyValue: $this")
        return this?.typeId ?: 0
    }

    /////////////////////////////////////////////////////////////
    /**
     * 空调控制模式 读取
     */
    val hvacControlModeCarProp by lazy {
        CarProp<Int, Int>(
            mPropertyName = "mHvacCarriageCarProp",
            mInitValue = PropertyValue(value = 0),
            mFrozen = mTempFrozen,
            mPropertyId = VehicleProperty.VCU_HMI_HAVCControlMode,
            mCarVehiclePropertyType = CarVehiclePropertyType.Int32,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mReadValueParser = { carPropertyValue2HvacControlMode() },
            mPropMode = PropMode(PropMode.READ or PropMode.CHANGE),
        )
    }

    private fun Int?.carPropertyValue2HvacControlMode(): Int {
        LogUtil.i(mObjectTag, "carPropertyValue2HvacControlMode: $this")
        return this?: 0
    }

    /////////////////////////////////////////////////////////////
    /**
     * 空调控制模式 写入
     */
    val hmi1HvacControlModeCarProp by lazy {
        CarProp<Int, CarriageState>(
            mPropertyName = "mHvacCarriageCarProp",
            mInitValue = PropertyValue(value = CarriageState.Unknown),
            mFrozen = mTempFrozen,
            mPropertyId = VehicleProperty.HMI1_HAVC_CONTROL_MODE,
            mCarVehiclePropertyType = CarVehiclePropertyType.Int32,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mWriteValueParser = { hmi1HvacControlModeToCarProperty() },
            mPropMode = PropMode(PropMode.WRITE),
        )
    }

    private fun CarriageState?.hmi1HvacControlModeToCarProperty(): Int {
        LogUtil.i(mObjectTag, "hmi1HvacControlModeToCarProperty: $this")
        return return this?.typeId?: 0
    }

    val hmi2HvacControlModeCarProp by lazy {
        CarProp<Int, CarriageState>(
            mPropertyName = "mHvacCarriageCarProp",
            mInitValue = PropertyValue(value = CarriageState.Unknown),
            mFrozen = mTempFrozen,
            mPropertyId = VehicleProperty.HMI2_HAVC_CONTROL_MODE,
            mCarVehiclePropertyType = CarVehiclePropertyType.Int32,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mWriteValueParser = { hmi2HvacControlModeToCarProperty() },
            mPropMode = PropMode(PropMode.WRITE),
        )
    }

    private fun CarriageState?.hmi2HvacControlModeToCarProperty(): Int {
        LogUtil.i(mObjectTag, "hmi2HvacControlModeToCarProperty: $this")
        return return this?.typeId?: 0
    }

    /**
     * HMI空调车厢选择:
     *无效=0
     *空调车厢选择Mc01=1
     *空调车厢选择T02=2
     *空调车厢选择Mc03=3
     *空调全列控制模式=4
     */
    fun setHvacControlMode(carriage: CarriageState) {
        val carriageHead: CarriageType = CarriageHeadManager.carriageHead.value
        LogUtil.i(mObjectTag, "setHvacControlMode, carriage: $carriage, carriageHead: $carriageHead")
        mTempFrozen.setFrozen()
        if (carriageHead is CarriageType.Car1) {
            hmi1HvacControlModeCarProp.propOps.writeToRemote(carriage)
        } else if (carriageHead is CarriageType.Car3) {
            hmi2HvacControlModeCarProp.propOps.writeToRemote(carriage)
        }
    }

    /////////////////////////////////////////////////////////////
    /**
     * MC01客室目标温度
     */
    val hvacTemp10UWCar1Prop by lazy {
        CarProp<Int, Float>(
            mPropertyName = "hvacTemp10UWCar1Prop",
            mInitValue = PropertyValue<Float>(value = 0F),
            mPropertyId = VehicleProperty.VCU_HMI_HAVC1Temp10UW,
            mCarVehiclePropertyType = CarVehiclePropertyType.Float,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mFrozen = mTempFrozen,
            mReadValueParser = { carPropertyValue2HVAC1Temp10UW() },
            mPropMode = PropMode(PropMode.READ or PropMode.CHANGE),
        )
    }

    /**
     * 制热模式14~20℃；制冷模式22~28℃
     *0.5℃/bit，即温度=数值*0.5℃
     */
    private fun Int?.carPropertyValue2HVAC1Temp10UW(): Float {
        LogUtil.i(mObjectTag, "carPropertyValue2HVAC1Temp10UW:$this")
        return this?.toFloat()?.times(0.5F) ?: 0F
    }

    /////////////////////////////////////////////////////////////
    /**
     * T客室目标温度
     */
    val hvacTemp10UWCar2Prop by lazy {
        CarProp<Int, Float>(
            mPropertyName = "hvacTemp10UWCar2Prop",
            mInitValue = PropertyValue<Float>(value = 0F),
            mPropertyId = VehicleProperty.VCU_HMI_HAVC2Temp10UW,
            mCarVehiclePropertyType = CarVehiclePropertyType.Float,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mFrozen = mTempFrozen,
            mReadValueParser = { carPropertyValue2HVAC2Temp10UW() },
            mPropMode = PropMode(PropMode.READ or PropMode.CHANGE),
        )
    }

    /**
     * 制热模式14~20℃；制冷模式22~28℃
     *0.5℃/bit，即温度=数值*0.5℃
     */
    private fun Int?.carPropertyValue2HVAC2Temp10UW(): Float {
        LogUtil.i(mObjectTag, "carPropertyValue2HVAC2Temp10UW:$this")
        return this?.toFloat()?.times(0.5F) ?: 0F
    }

    /////////////////////////////////////////////////////////////
    /**
     * MC02客室目标温度
     */
    val hvacTemp10UWCar3Prop by lazy {
        CarProp<Int, Float>(
            mPropertyName = "hvacTemp10UWCar3Prop",
            mInitValue = PropertyValue<Float>(value = 0F),
            mPropertyId = VehicleProperty.VCU_HMI_HAVC3Temp10UW,
            mCarVehiclePropertyType = CarVehiclePropertyType.Float,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mFrozen = mTempFrozen,
            mReadValueParser = { carPropertyValue2HVAC3Temp10UW() },
            mPropMode = PropMode(PropMode.READ or PropMode.CHANGE),
        )
    }

    /**
     * 制热模式14~20℃；制冷模式22~28℃
     *0.5℃/bit，即温度=数值*0.5℃
     */
    private fun Int?.carPropertyValue2HVAC3Temp10UW(): Float {
        LogUtil.i(mObjectTag, "carPropertyValue2HVAC3Temp10UW:$this")
        return this?.toFloat()?.times(0.5F) ?: 0F
    }

    /////////////////////////////////////////////////////////////
    /**
     * 1车空调模式-只读
     */
    val hvacModeCar1Prop by lazy {
        CarProp<Int, HvacCarriageMode>(
            mPropertyName = "hvacModeCar1Prop",
            mInitValue = PropertyValue<HvacCarriageMode>(value = HvacCarriageMode.Unknown),
            mPropertyId = VehicleProperty.VCU_HMI_Car1HVACModeUB,
            mCarVehiclePropertyType = CarVehiclePropertyType.Int32,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mFrozen = mTempFrozen,
            mReadValueParser = { carPropertyValue2HvacMode() },
            mPropMode = PropMode(PropMode.READ or PropMode.CHANGE),
        )
    }

    /**
     * 2车空调模式-只读
     */
    val hvacModeCar2Prop by lazy {
        CarProp<Int, HvacCarriageMode>(
            mPropertyName = "hvacModeCar2Prop",
            mInitValue = PropertyValue<HvacCarriageMode>(value = HvacCarriageMode.Unknown),
            mPropertyId = VehicleProperty.VCU_HMI_Car2HVACModeUB,
            mCarVehiclePropertyType = CarVehiclePropertyType.Int32,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mFrozen = mTempFrozen,
            mReadValueParser = { carPropertyValue2HvacMode() },
            mPropMode = PropMode(PropMode.READ or PropMode.CHANGE),
        )
    }

    /**
     * 3车空调模式-只读
     */
    val hvacModeCar3Prop by lazy {
        CarProp<Int, HvacCarriageMode>(
            mPropertyName = "hvacModeCar3Prop",
            mInitValue = PropertyValue<HvacCarriageMode>(value = HvacCarriageMode.Unknown),
            mPropertyId = VehicleProperty.VCU_HMI_Car3HVACModeUB,
            mCarVehiclePropertyType = CarVehiclePropertyType.Int32,
            mCarVehicleArea = CarVehicleArea.Global,
            mPropValueTypeClazz = Int::class.javaObjectType,
            mFrozen = mTempFrozen,
            mReadValueParser = { carPropertyValue2HvacMode() },
            mPropMode = PropMode(PropMode.READ or PropMode.CHANGE),
        )
    }

    private fun Int?.carPropertyValue2HvacMode(): HvacCarriageMode {
        LogUtil.i(mObjectTag, "carPropertyValue2HvacMode: $this")
        return when (this) {
            1 -> HvacCarriageMode.Wind
            2 -> HvacCarriageMode.Cold
            3 -> HvacCarriageMode.Warm
            4 -> HvacCarriageMode.Stop
            5 -> HvacCarriageMode.ForceCold
            6 -> HvacCarriageMode.ForceWarm
            7 -> HvacCarriageMode.Auto
            else -> HvacCarriageMode.Invalid
        }
    }
}