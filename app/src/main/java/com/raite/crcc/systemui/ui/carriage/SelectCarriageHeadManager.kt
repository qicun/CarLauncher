package com.raite.crcc.systemui.ui.carriage

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import com.raite.crcc.common.data.network.CarriageNetworkConfigRepository
import com.raite.crcc.common.data.network.CarriageNetworkConfigService
import com.raite.crcc.common.data.network.CarriageNetworkConfigService.Companion.NETWORK_CONTROL_ID
import com.raite.crcc.common.data.network.NetworkManagerService
import com.raite.crcc.common.data.provider.CarriageHeadManager
import com.raite.crcc.common.data.type.CarriageType
import com.raite.crcc.common.data.type.SwitchState
import com.raite.crcc.common.data.type.TrdpCompleteType
import com.raite.crcc.common.util.LogUtil
import com.raite.crcc.common.util.appContext
import com.raite.crcc.common.util.customScope
import com.raite.crcc.common.util.defaultLog
import io.github.idonans.appcontext.AppContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

object SelectCarriageHeadManager {

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    private val mCustomScope = customScope(coroutineName = mObjectTag)
    private val mainHandler = Handler(Looper.getMainLooper())

    private val mContext = AppContext.getContext().applicationContext as Application
    private val mSelectCarriageHeadViewInner = SelectCarriageHeadView(mContext)
    private var mWindowManager: WindowManager? =null
    private var mLayoutParams: WindowManager.LayoutParams? = null

    private var mCarriageNetworkConfigService = CarriageNetworkConfigService(NETWORK_CONTROL_ID)
    private var mConfigCarNetwork = false

    // 准备选择的车厢
    private val mPrepareSelectMainCab = MutableStateFlow<CarriageType>(CarriageType.Unknown)
    val prepareSelectMainCab = mPrepareSelectMainCab.asStateFlow()

    // 本地保存的车厢
    private val mLocalSaveMainCab = MutableStateFlow<CarriageType>(CarriageType.Unknown)
    val localSaveMainCab = mLocalSaveMainCab.asStateFlow()

    private fun initWindowView() {
        mWindowManager = appContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager

        mLayoutParams = WindowManager.LayoutParams().apply {
            this.x = 0
            this.y = 0
            this.gravity = Gravity.START or Gravity.TOP
            this.width = WindowManager.LayoutParams.MATCH_PARENT
            this.height = WindowManager.LayoutParams.MATCH_PARENT
        }
    }

    private fun showDialog() {
        if (mSelectCarriageHeadViewInner.windowToken == null) {
            mLayoutParams?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            kotlin.runCatching {
                LogUtil.i(mObjectTag, "show")
                mWindowManager?.addView(mSelectCarriageHeadViewInner, mLayoutParams)
                hideSystemUI()
            }.onFailure { it.defaultLog(mObjectTag) }
        } else {
            LogUtil.i(mObjectTag, "SelectCarriageHeadWindow is already visible")
        }
    }

    fun hideDialog() {
        kotlin.runCatching {
            if (mSelectCarriageHeadViewInner.windowToken != null) {
                LogUtil.i(mObjectTag, "hideDialog")
                mWindowManager?.removeViewImmediate(mSelectCarriageHeadViewInner)
            }
        }.onFailure { it.defaultLog(mObjectTag) }
    }

    private fun hideSystemUI() {
        LogUtil.i(mObjectTag, "hideSystemUI")
        mSelectCarriageHeadViewInner.windowInsetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    //准备选择的车厢
    fun setPrepareSelectMainCab(type: CarriageType){
        LogUtil.d(mObjectTag, "setPrepareSelectMainCab type:$type")
        mPrepareSelectMainCab.value = type
    }

    //本地保存的车厢
    private fun setLocalSaveMainCab(type: CarriageType){
        LogUtil.d(mObjectTag, "setLocalSaveMainCab type:$type")
        mLocalSaveMainCab.value = type
        if (isValidCab(type)){
            configureMainCabAndNetwork(type)
        }
    }

    fun setSelectMainCab(){
        LogUtil.d(mObjectTag, "setSelectMainCab")
        mLocalSaveMainCab.value = mPrepareSelectMainCab.value
        configureMainCabAndNetwork(mLocalSaveMainCab.value)
    }

    private fun configureMainCabAndNetwork(type: CarriageType) {
        if (mConfigCarNetwork)
            return
        mConfigCarNetwork = true
        NetworkManagerService.setSelectMainCab(type)
        mCarriageNetworkConfigService.configureNetworkByCarriage(type)
    }

    private fun isValidCab(type: CarriageType): Boolean {
        return type == CarriageType.Car1 || type == CarriageType.Car3
    }

    init {
        LogUtil.d(mObjectTag, "init")
        initWindowView()
        mainHandler.post{
            showDialog()
        }
        mCustomScope.launch {
            launch {
                combine(
                    mCarriageNetworkConfigService.networkManager,
                    CarriageHeadManager.carriageHead,
                ) { manager, cab ->
                    Pair(manager, cab)
                }.collectLatest {
                    LogUtil.i(mObjectTag, "networkManager carriageHead:$it")
                    if (it.first != null){
                        setLocalSaveMainCab(it.second)
                    }
                }
            }
            launch {
                combine(
                    CarriageNetworkConfigRepository.trdpCompleteType,
                    CarriageNetworkConfigRepository.systemReady,
                ) { complete, ready ->
                    Pair(complete, ready)
                }.collectLatest {
                    LogUtil.d(mObjectTag, "complete:${it.first} ready:${it.second.toUByteArray()}")
                    if (it.second.isNotEmpty() && it.second.size >= 2) {
                        if (it.second[0].toInt() == 1 && it.second[1].toInt() == 1
                            && it.first == TrdpCompleteType.LoginSuccess) {
                            NetworkManagerService.setSystemReady(NETWORK_CONTROL_ID, SwitchState.On)
                            CarriageHeadManager.setCarriageHead(
                                localSaveMainCab.value
                            )
                            mainHandler.post{
                                hideDialog()
                            }
                        }
                    }
                }
            }
        }
    }
}