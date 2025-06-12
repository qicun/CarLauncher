/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui

import android.annotation.SuppressLint
import android.app.INotificationManager
import android.app.ITransientNotificationCallback
import android.content.Context
import android.os.IBinder
import android.os.ServiceManager
import android.os.UserHandle
import android.view.accessibility.IAccessibilityManager
import android.widget.ToastPresenter
import com.android.internal.R
import com.raite.crcc.systemui.CmdQueue
import com.raite.crcc.systemui.utils.Plog
import java.lang.reflect.Method

/**
 * @Author lsh
 * @Date 2023/8/11 16:04
 * @Description for show App Toast
 * flow:App<showToast>
 *     if TargetSdk>=29 NotificationManagerService will call SystemUI operate this Toast
 *     else use SystemUI Toast
 */
class ToastUI private constructor(var mContext: Context, mCmdQueue: CmdQueue) : CmdQueue.Callback {
    private var mNotificationManager: INotificationManager? = null
    private var mAccessibilityManager: IAccessibilityManager? = null
    private var mGravity = 0
    private var mY = 0
    private var mPresenter: ToastPresenter? = null
    private var mCallback: ITransientNotificationCallback? = null

    companion object {
        private const val TAG = "ToastUI"

        @SuppressLint("StaticFieldLeak")
        private var mInstance: ToastUI? = null

        @Synchronized
        fun getInstance(context: Context, mCmdQueue: CmdQueue): ToastUI {
            if (mInstance == null) {
                mInstance = ToastUI(context, mCmdQueue)
            }
            return mInstance!!
        }
    }

    init {
        mCmdQueue.registerCallback(this)
        mNotificationManager = INotificationManager.Stub.asInterface(
            ServiceManager.getService(Context.NOTIFICATION_SERVICE)
        )
        mAccessibilityManager = IAccessibilityManager.Stub.asInterface(
            ServiceManager.getService(Context.ACCESSIBILITY_SERVICE)
        )
        val resources = mContext.resources
        mGravity = resources.getInteger(R.integer.config_toastDefaultGravity)
        mY = resources.getDimensionPixelSize(R.dimen.toast_y_offset)
    }

    override fun showToast(
        uid: Int,
        packageName: String?,
        token: IBinder?,
        text: CharSequence?,
        windowToken: IBinder?,
        duration: Int,
        callback: ITransientNotificationCallback?
    ) {
        Plog.i(TAG, "showToast package=$packageName")
        if (mPresenter != null) {
            hideCurrentToast()
        }
        val createContextAsUser: Method = mContext.javaClass.getMethod(
            "createContextAsUser",
            UserHandle::class.java, Int::class.java
        )
        val context =
            createContextAsUser.invoke(mContext, UserHandle.getUserHandleForUid(uid), 0) as Context
        val view = ToastPresenter.getTextToastView(context, text)
        mCallback = callback
        mPresenter = ToastPresenter(
            context, mAccessibilityManager, mNotificationManager,
            packageName
        )
        mPresenter?.show(
            view, token, windowToken, duration, mGravity,
            0, mY, 0f, 0f, mCallback
        )
    }

    override fun hideToast(packageName: String?, token: IBinder?) {
        Plog.i(TAG, "hideToast package=$packageName")
        if (mPresenter == null || mPresenter!!.packageName != packageName
            || mPresenter!!.token != token
        ) {
            Plog.w(TAG, "Attempt to hide non-current toast from package $packageName")
            return
        }
        hideCurrentToast()
    }

    private fun hideCurrentToast() {
        mPresenter?.hide(mCallback)
        mPresenter = null
    }
}