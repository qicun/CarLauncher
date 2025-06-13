/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import android.os.ServiceManager
import android.view.IWindowManager
import android.view.WindowManagerGlobal
import com.android.internal.statusbar.IStatusBarService
import com.raite.crcc.systemui.common.IConfigChangedListener
import com.raite.crcc.systemui.ui.ToastUI
import com.raite.crcc.systemui.ui.subscreen.ScreenController
import com.raite.crcc.systemui.utils.Plog
import com.raite.crcc.systemui.util.ContextUtil

/**
 * @Author lsh
 * @Date 2023/7/11 15:54
 * @Description
 */
class SystemUiMain private constructor(context: Context) :
    CmdQueue.Callback, IConfigChangedListener {

    private var mContext: Context = context
    private var mWindowManagerService: IWindowManager? = null
    private var mBarService: IStatusBarService? = null
    private var mCommandQueue: CmdQueue? = null
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private val TAG = "SystemUiMain"

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mInstance: SystemUiMain? = null

        @JvmStatic
        fun getInstance(context: Context = ContextUtil.context): SystemUiMain {
            synchronized(SystemUiMain::class.java) {
                if (mInstance == null) {
                    mInstance = SystemUiMain(context)
                }
            }
            return mInstance!!
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        // 获取当前语言，与之前记录的语言对比，可用于响应语言改变
        // Locale locale = mContext.getResources().getConfiguration().getLocales().get(0);
        ScreenController.initConfigurationChanged(configuration)
        Plog.e(TAG, "${TAG}---换肤改变回调====${configuration}")
    }

    fun start() {
        mWindowManagerService = WindowManagerGlobal.getWindowManagerService()
        registerMessageQueue()
        ScreenController.initScreen(mContext, mCommandQueue)
        ToastUI.getInstance(mContext, mCommandQueue!!)
    }

    fun isSystemUiVisible(): Boolean {
        return ScreenController.isVisible()
    }

    private fun registerMessageQueue() {
        mCommandQueue = CmdQueue(mContext)
        mCommandQueue!!.registerCallback(this)
        // Context.STATUS_BAR_SERVICE = statusbar
        mBarService = IStatusBarService.Stub.asInterface(
            ServiceManager.getService("statusbar")
        )
        /**
         * 1.注册回调
         * 2.result可用于设置SystemUi的ime-mode
         */
        try {
            mBarService?.registerStatusBar(mCommandQueue)
            Plog.i(TAG, "register mCommandQueue success")
        } catch (ex: RemoteException) {
            ex.printStackTrace()
            Plog.e(TAG, "registerStatusBar err${ex}")
        }
    }

    /**
     * CmdQueue 回调-打断瞬态
     */
    override fun abortTransient(displayId: Int, types: IntArray?) {
        // 移除——延时隐藏SystemUI瞬态
        mHandler.removeCallbacksAndMessages(null)
    }

    /**
     * MCommandQueue 回调-显示瞬态
     */
    override fun showTransient(displayId: Int, types: IntArray?) {
        // 延时隐藏瞬态systemui
        mHandler.postDelayed({ hideTransient(displayId) }, 3500)
    }

    /**
     * 隐藏瞬态systemui
     */
    private fun hideTransient(displayId: Int) {
        try {
            mWindowManagerService!!.hideTransientBars(displayId)
        } catch (ex: RemoteException) {
            Plog.w(TAG, "Cannot get WindowManager", ex)
        }
    }

    override fun onDisplayReady(displayId: Int) {
        ScreenController.onDisplayReady(mContext, mCommandQueue, displayId)
    }
}