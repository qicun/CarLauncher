/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui

import android.app.ITransientNotificationCallback
import android.content.ComponentName
import android.content.Context
import android.hardware.biometrics.IBiometricSysuiReceiver
import android.hardware.biometrics.PromptInfo
import android.hardware.display.DisplayManager
import android.hardware.fingerprint.IUdfpsHbmListener
import android.os.*
import android.view.InsetsVisibilities
import com.android.internal.os.SomeArgs
import com.android.internal.statusbar.IStatusBar
import com.android.internal.statusbar.StatusBarIcon
import com.android.internal.view.AppearanceRegion
import com.raite.crcc.systemui.common.MessageCallback
import com.raite.crcc.systemui.config.ServiceConfig
import com.raite.crcc.systemui.utils.Plog

/**
 * @Author lsh
 * @Date 2023/7/11 15:44
 * @Description
 */
class CmdQueue(mContext: Context) : IStatusBar.Stub(), DisplayManager.DisplayListener {
    private val mLock = Any()

    companion object {
        private const val TAG = "CmdQueue"

        private const val OP_SET_ICON = 1
        private const val OP_REMOVE_ICON = 2

        private const val MSG_ICON = 1
        private const val MSG_DISABLE = 2
        private const val MSG_EXPAND_NOTIFICATIONS = 3
        private const val MSG_COLLAPSE_PANELS = 4
        private const val MSG_EXPAND_SETTINGS = 5
        private const val MSG_SYSTEM_BAR_CHANGED = 6
        private const val MSG_DISPLAY_READY = 7
        private const val MSG_SHOW_IME_BUTTON = 8
        private const val MSG_TOGGLE_RECENT_APPS = 9
        private const val MSG_PRELOAD_RECENT_APPS = 10
        private const val MSG_CANCEL_PRELOAD_RECENT_APPS = 11
        private const val MSG_SET_WINDOW_STATE = 12
        private const val MSG_SHOW_RECENT_APPS = 13
        private const val MSG_HIDE_RECENT_APPS = 14
        private const val MSG_SHOW_SCREEN_PIN_REQUEST = 18
        private const val MSG_APP_TRANSITION_PENDING = 19
        private const val MSG_APP_TRANSITION_CANCELLED = 20
        private const val MSG_APP_TRANSITION_STARTING = 21
        private const val MSG_ASSIST_DISCLOSURE = 22
        private const val MSG_START_ASSIST = 23
        private const val MSG_CAMERA_LAUNCH_GESTURE = 24
        private const val MSG_TOGGLE_KEYBOARD_SHORTCUTS = 25
        private const val MSG_SHOW_PICTURE_IN_PICTURE_MENU = 26
        private const val MSG_ADD_QS_TILE = 27
        private const val MSG_REMOVE_QS_TILE = 28
        private const val MSG_CLICK_QS_TILE = 29
        private const val MSG_TOGGLE_APP_SPLIT_SCREEN = 30
        private const val MSG_APP_TRANSITION_FINISHED = 31
        private const val MSG_DISMISS_KEYBOARD_SHORTCUTS = 32
        private const val MSG_HANDLE_SYSTEM_KEY = 33
        private const val MSG_SHOW_GLOBAL_ACTIONS = 34
        private const val MSG_TOGGLE_PANEL = 35
        private const val MSG_SHOW_SHUTDOWN_UI = 36
        private const val MSG_SET_TOP_APP_HIDES_STATUS_BAR = 37
        private const val MSG_ROTATION_PROPOSAL = 38
        private const val MSG_BIOMETRIC_SHOW = 39
        private const val MSG_BIOMETRIC_AUTHENTICATED = 40
        private const val MSG_BIOMETRIC_HELP = 41
        private const val MSG_BIOMETRIC_ERROR = 42
        private const val MSG_BIOMETRIC_HIDE = 43
        private const val MSG_SHOW_CHARGING_ANIMATION = 44
        private const val MSG_SHOW_PINNING_TOAST_ENTER_EXIT = 45
        private const val MSG_SHOW_PINNING_TOAST_ESCAPE = 46
        private const val MSG_RECENTS_ANIMATION_STATE_CHANGED = 47
        private const val MSG_SHOW_TRANSIENT = 48
        private const val MSG_ABORT_TRANSIENT = 49
        private const val MSG_SHOW_INATTENTIVE_SLEEP_WARNING = 50
        private const val MSG_DISMISS_INATTENTIVE_SLEEP_WARNING = 51
        private const val MSG_SHOW_TOAST = 52
        private const val MSG_HIDE_TOAST = 53
        private const val MSG_TRACING_STATE_CHANGED = 54
        private const val MSG_SUPPRESS_AMBIENT_DISPLAY = 55
        private const val MSG_REQUEST_WINDOW_MAGNIFICATION_CONNECTION = 56
        private const val MSG_HANDLE_WINDOW_MANAGER_LOGGING_COMMAND = 57
        private const val MSG_EMERGENCY_ACTION_LAUNCH_GESTURE = 58
        private const val MSG_SET_NAVIGATION_BAR_LUMA_SAMPLING_ENABLED = 59
        private const val MSG_SET_UDFPS_HBM_LISTENER = 60
        private const val MSG_ADD_BAR = 61

        private const val FLAG_EXCLUDE_NONE = 0
        private const val FLAG_EXCLUDE_SEARCH_PANEL = 1
        private const val FLAG_EXCLUDE_RECENTS_PANEL = 2
        private const val FLAG_EXCLUDE_NOTIFICATION_PANEL = 4
        private const val FLAG_EXCLUDE_INPUT_METHODS_PANEL = 8
        private const val FLAG_EXCLUDE_COMPAT_MODE_PANEL = 16
    }

    private val mCallbacks = ArrayList<Callback>()

    private val mHandler: Handler = MHandler(Looper.getMainLooper())

    init {
        ServiceConfig.getDisplayManager(mContext).registerDisplayListener(this, mHandler)
    }

    fun registerCallback(callback: Callback) {
        mCallbacks.add(callback)
    }

    fun unRegisterCallback(callback: Callback) {
        mCallbacks.remove(callback)
    }

    interface Callback : MessageCallback

    @Throws(RemoteException::class)
    override fun setIcon(s: String?, statusBarIcon: StatusBarIcon?) {
        Plog.i(TAG, "setIcon s:$s  statusBarIcon:$statusBarIcon")
    }

    @Throws(RemoteException::class)
    override fun removeIcon(s: String?) {
        Plog.i(TAG, "removeIcon s:$s")
    }

    @Throws(RemoteException::class)
    override fun disable(i: Int, i1: Int, i2: Int) {
        Plog.i(TAG, "disable i:$i  i1:$i1  i2:$i2")
    }

    @Throws(RemoteException::class)
    override fun animateExpandNotificationsPanel() {
        Plog.i(TAG, "animateExpandNotificationsPanel")
    }

    @Throws(RemoteException::class)
    override fun animateExpandSettingsPanel(s: String?) {
        Plog.i(TAG, "animateExpandSettingsPanel s:$s")
    }

    @Throws(RemoteException::class)
    override fun animateCollapsePanels() {
        Plog.i(TAG, "animateCollapsePanels")
    }

    @Throws(RemoteException::class)
    override fun togglePanel() {
        Plog.i(TAG, "togglePanel")
    }

    @Throws(RemoteException::class)
    override fun showWirelessChargingAnimation(i: Int) {
        Plog.i(TAG, "showWirelessChargingAnimation  i:$i")
    }

    @Throws(RemoteException::class)
    override fun setImeWindowStatus(
        displayId: Int, token: IBinder?, vis: Int, backDisposition: Int,
        showImeSwitcher: Boolean, isMultiClientImeEnabled: Boolean
    ) {
        Plog.i(
            TAG,
            "setImeWindowStatus displayId:$displayId token:$token backDisposition:$backDisposition showImeSwitcher:$showImeSwitcher isMultiClientImeEnabled:$isMultiClientImeEnabled"
        )
    }

    @Throws(RemoteException::class)
    override fun setWindowState(displayId: Int, window: Int, state: Int) {
        Plog.i(TAG, "setWindowState displayId:$displayId window:$window state:$state")
    }

    @Throws(RemoteException::class)
    override fun showRecentApps(b: Boolean) {
        Plog.i(TAG, "showRecentApps b:$b")
    }

    @Throws(RemoteException::class)
    override fun hideRecentApps(b: Boolean, b1: Boolean) {
        Plog.i(TAG, "hideRecentApps b:$b b1:$b1")
    }

    @Throws(RemoteException::class)
    override fun toggleRecentApps() {
        Plog.i(TAG, "toggleRecentApps")
    }

    @Throws(RemoteException::class)
    override fun toggleSplitScreen() {
        Plog.i(TAG, "toggleSplitScreen")
    }

    @Throws(RemoteException::class)
    override fun preloadRecentApps() {
        Plog.i(TAG, "preloadRecentApps")
    }

    @Throws(RemoteException::class)
    override fun cancelPreloadRecentApps() {
        Plog.i(TAG, "cancelPreloadRecentApps")
    }

    @Throws(RemoteException::class)
    override fun showScreenPinningRequest(i: Int) {
        Plog.i(TAG, "showScreenPinningRequest i:$i")
    }

    @Throws(RemoteException::class)
    override fun dismissKeyboardShortcutsMenu() {
        Plog.i(TAG, "dismissKeyboardShortcutsMenu")
    }

    @Throws(RemoteException::class)
    override fun toggleKeyboardShortcutsMenu(i: Int) {
        Plog.i(TAG, "toggleKeyboardShortcutsMenu i:$i")
    }

    @Throws(RemoteException::class)
    override fun appTransitionPending(i: Int) {
        Plog.i(TAG, "appTransitionPending i:$i")
    }

    @Throws(RemoteException::class)
    override fun appTransitionCancelled(i: Int) {
        Plog.i(TAG, "appTransitionCancelled i:$i")
    }

    @Throws(RemoteException::class)
    override fun appTransitionStarting(displayId: Int, l: Long, l1: Long) {
        Plog.i(TAG, "appTransitionStarting displayId:$displayId l:$l l1:$l1")
    }

    @Throws(RemoteException::class)
    override fun appTransitionFinished(i: Int) {
        Plog.i(TAG, "appTransitionFinished i:$i")
    }

    @Throws(RemoteException::class)
    override fun showAssistDisclosure() {
        Plog.i(TAG, "showAssistDisclosure")
    }

    @Throws(RemoteException::class)
    override fun startAssist(bundle: Bundle?) {
        Plog.i(TAG, "startAssist bundle:$bundle")
    }

    @Throws(RemoteException::class)
    override fun onCameraLaunchGestureDetected(i: Int) {
        Plog.i(TAG, "onCameraLaunchGestureDetected i:$i")
    }

    override fun onEmergencyActionLaunchGestureDetected() {
        //检测到紧急动作启动手势
        Plog.i(TAG, "onEmergencyActionLaunchGestureDetected")
    }

    @Throws(RemoteException::class)
    override fun showPictureInPictureMenu() {
        Plog.i(TAG, "showPictureInPictureMenu")
    }

    @Throws(RemoteException::class)
    override fun showGlobalActionsMenu() {
        Plog.i(TAG, "showGlobalActionsMenu")
    }

    @Throws(RemoteException::class)
    override fun onProposedRotationChanged(i: Int, b: Boolean) {
        Plog.i(TAG, "onProposedRotationChanged i:$i b:$b")
    }

    @Throws(RemoteException::class)
    override fun setTopAppHidesStatusBar(b: Boolean) {
//        Plog.i(TAG,"setTopAppHidesStatusBar")
    }

    @Throws(RemoteException::class)
    override fun addQsTile(componentName: ComponentName?) {
        Plog.i(TAG, "addQsTile componentName:$componentName")
    }

    @Throws(RemoteException::class)
    override fun remQsTile(componentName: ComponentName?) {
        Plog.i(TAG, "remQsTile componentName:$componentName")
    }

    @Throws(RemoteException::class)
    override fun clickQsTile(componentName: ComponentName?) {
        Plog.i(TAG, "clickQsTile componentName:$componentName")
    }

    @Throws(RemoteException::class)
    override fun handleSystemKey(i: Int) {
        Plog.i(TAG, "handleSystemKey i:$i")
    }

    @Throws(RemoteException::class)
    override fun showPinningEnterExitToast(b: Boolean) {
        Plog.i(TAG, "showPinningEnterExitToast b:$b")
    }

    @Throws(RemoteException::class)
    override fun showPinningEscapeToast() {
        Plog.i(TAG, "showPinningEscapeToast")
    }

    @Throws(RemoteException::class)
    override fun showShutdownUi(b: Boolean, s: String?) {
        Plog.i(TAG, "showShutdownUi b:$b s:$s")
    }

    override fun showAuthenticationDialog(
        p0: PromptInfo?,
        p1: IBiometricSysuiReceiver?,
        p2: IntArray?,
        p3: Boolean,
        p4: Boolean,
        p5: Int,
        p6: Long,
        p7: String?,
        p8: Long,
        p9: Int
    ) {
        //显示身份验证对话框
        Plog.i(
            TAG,
            "showAuthenticationDialog p0:$p0 p1:$p1 p2:$p2 p3:$p3 p4:$p4 p5:$p5 p6:$p6 p7:$p7 p8:$p8 p9:$p9"
        )
    }

    @Throws(RemoteException::class)
    override fun onBiometricAuthenticated() {
        Plog.i(TAG, "onBiometricAuthenticated")
    }

    override fun onBiometricHelp(p0: Int, p1: String?) {
        //关于生物识别帮助
        Plog.i(TAG, "onBiometricHelp p0$p0 p1:$p1")
    }

    @Throws(RemoteException::class)
    override fun onBiometricError(i: Int, i1: Int, i2: Int) {
        Plog.i(TAG, "onBiometricError i:$i i1:$i1 i2:$i2")
    }

    @Throws(RemoteException::class)
    override fun hideAuthenticationDialog() {
        Plog.i(TAG, "hideAuthenticationDialog")
    }

    override fun setUdfpsHbmListener(p0: IUdfpsHbmListener?) {
        Plog.i(TAG, "setUdfpsHbmListener p0:$p0")
    }

    @Throws(RemoteException::class)
    override fun onDisplayReady(displayId: Int) {
        Plog.i(TAG, "onDisplayReady $displayId")
        synchronized(mLock) {
            mHandler.obtainMessage(MSG_DISPLAY_READY, displayId, 0).sendToTarget()
        }
    }

    override fun onDisplayAdded(displayId: Int) {
        Plog.i(TAG, "onDisplayAdded: $displayId")
    }

    override fun onDisplayRemoved(displayId: Int) {
        Plog.i(TAG, "onDisplayRemoved: $displayId")
    }

    override fun onDisplayChanged(displayId: Int) {
        Plog.i(TAG, "onDisplayChanged: $displayId")
    }

    @Throws(RemoteException::class)
    override fun onRecentsAnimationStateChanged(b: Boolean) {
        Plog.i(TAG, "onRecentsAnimationStateChanged b:$b")
    }

    override fun onSystemBarAttributesChanged(
        displayId: Int,
        appearance: Int,
        appearanceRegions: Array<AppearanceRegion>?,
        navbarColorManagedByIme: Boolean,
        behavior: Int,
        requestedVisibilities: InsetsVisibilities?,
        packageName: String?
    ) {
        Plog.i(TAG, "onSystemBarAttributesChanged displayId$displayId")
        SomeArgs.obtain().apply {
            argi1 = displayId
            argi2 = appearance
            argi3 = if (navbarColorManagedByIme) 1 else 0
            arg1 = appearanceRegions
            argi4 = behavior
            arg2 = requestedVisibilities
            arg3 = packageName
            mHandler.obtainMessage(MSG_SYSTEM_BAR_CHANGED, this).sendToTarget()
        }
    }

    /**
     * 显示瞬态--临时显示systemui
     */
    @Throws(RemoteException::class)
    override fun showTransient(displayId: Int, types: IntArray?, isGestureOnSystemBar: Boolean) {
        Plog.i(
            TAG,
            "showTransient displayId:$displayId types:$types isGestureOnSystemBar:$isGestureOnSystemBar"
        )
        synchronized(mLock) {
            mHandler.obtainMessage(MSG_SHOW_TRANSIENT, displayId, 0, types).sendToTarget()
        }
    }

    /**
     * 瞬态被打断
     */
    @Throws(RemoteException::class)
    override fun abortTransient(displayId: Int, types: IntArray?) { //终止瞬态
        Plog.i(TAG, "abortTransient displayId:$displayId types:$types")
        synchronized(mLock) {
            mHandler.obtainMessage(MSG_ABORT_TRANSIENT, displayId, 0, types).sendToTarget()
        }
    }

    @Throws(RemoteException::class)
    override fun showInattentiveSleepWarning() {
        Plog.i(TAG, "showInattentiveSleepWarning")
    }

    @Throws(RemoteException::class)
    override fun dismissInattentiveSleepWarning(b: Boolean) {
        Plog.i(TAG, "dismissInattentiveSleepWarning b:$b")
    }

    @Throws(RemoteException::class)
    override fun showToast(
        uid: Int,
        packageName: String?,
        token: IBinder?,
        text: CharSequence?,
        windowToken: IBinder?,
        duration: Int,
        callback: ITransientNotificationCallback?
    ) {
        Plog.i(
            TAG,
            "showToast uid:$uid packageName:$packageName token:$token text:$text windowToken:$windowToken duration:$duration"
        )
        synchronized(mLock) {
            SomeArgs.obtain().apply {
                arg1 = packageName
                arg2 = token
                arg3 = text
                arg4 = windowToken
                arg5 = callback
                argi1 = uid
                argi2 = duration
                mHandler.obtainMessage(MSG_SHOW_TOAST, this).sendToTarget()
            }
        }
    }

    @Throws(RemoteException::class)
    override fun hideToast(packageName: String?, token: IBinder?) {
        Plog.i(TAG, "hideToast packageName:$packageName token:$token")
        synchronized(mLock) {
            SomeArgs.obtain().apply {
                arg1 = packageName
                arg2 = token
                mHandler.obtainMessage(MSG_HIDE_TOAST, this).sendToTarget()
            }
        }
    }

    @Throws(RemoteException::class)
    override fun startTracing() {
        Plog.i(TAG, "startTracing")
    }

    @Throws(RemoteException::class)
    override fun stopTracing() {
        Plog.i(TAG, "stopTracing")
    }

    override fun handleWindowManagerLoggingCommand(
        p0: Array<out String>?,
        p1: ParcelFileDescriptor?
    ) {
        Plog.i(TAG, "handleWindowManagerLoggingCommand p0:$p0 p1:$p1")
    }

    @Throws(RemoteException::class)
    override fun suppressAmbientDisplay(b: Boolean) {
        Plog.i(TAG, "suppressAmbientDisplay b:$b")
    }

    /**
     * Requests {@link com.android.systemui.accessibility.WindowMagnification} to invoke
     * {@code android.view.accessibility.AccessibilityManager#
     * setWindowMagnificationConnection(IWindowMagnificationConnection)}
     *
     * @param connect {@code true} if needs connection, otherwise set the connection to null.
     */
    override fun requestWindowMagnificationConnection(p0: Boolean) {
        Plog.i(TAG, "requestWindowMagnificationConnection p0:$p0")
    }

    override fun passThroughShellCommand(p0: Array<out String>?, p1: ParcelFileDescriptor?) {
        Plog.i(TAG, "passThroughShellCommand")
    }

    override fun setNavigationBarLumaSamplingEnabled(p0: Int, p1: Boolean) {
        Plog.i(TAG, "setNavigationBarLumaSamplingEnabled p0$p0 p1:$p1")
    }

    override fun runGcForTest() {
        Plog.i(TAG, "runGcForTest")
    }

    override fun addBar() {
        Plog.i(TAG, "addBar")
        synchronized(mLock) {
            mHandler.removeMessages(MSG_ADD_BAR)
            mHandler.obtainMessage(MSG_ADD_BAR, 0, 0, null).sendToTarget()
        }
    }

    inner class MHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val args: SomeArgs
            when (msg.what) {
                MSG_SHOW_TRANSIENT -> {
                    for (callbacks in mCallbacks) {
                        callbacks.showTransient(msg.arg1, msg.obj as IntArray)
                    }
                }

                MSG_ABORT_TRANSIENT -> {
                    for (callbacks in mCallbacks) {
                        callbacks.abortTransient(msg.arg1, msg.obj as IntArray)
                    }
                }

                MSG_SYSTEM_BAR_CHANGED -> {
                    args = msg.obj as SomeArgs
                    for (callbacks in mCallbacks) {
                        callbacks.onSystemBarAttributesChanged(
                            args.argi1,
                            args.argi2,
                            args.arg1 as Array<AppearanceRegion>?,
                            args.argi3 == 1,
                            args.argi4,
                            args.arg2 as InsetsVisibilities,
                            args.arg3 as String
                        )
                    }
                    args.recycle()
                }

                MSG_SHOW_TOAST -> {
                    args = msg.obj as SomeArgs
                    val packageName = args.arg1 as String
                    val token = args.arg2 as IBinder
                    val text = args.arg3 as CharSequence
                    val windowToken = args.arg4 as IBinder
                    val callback = args.arg5 as ITransientNotificationCallback
                    val uid = args.argi1
                    val duration = args.argi2
                    for (callbacks in mCallbacks) {
                        callbacks.showToast(
                            uid, packageName, token, text, windowToken, duration,
                            callback
                        )
                    }
                }

                MSG_HIDE_TOAST -> {
                    args = msg.obj as SomeArgs
                    val packageName = args.arg1 as String
                    val token = args.arg2 as IBinder
                    for (callbacks in mCallbacks) {
                        callbacks.hideToast(packageName, token)
                    }
                }

                MSG_ADD_BAR -> {
                    for (i in mCallbacks.indices) {
                        mCallbacks[i].addBar()
                    }
                }

                MSG_DISPLAY_READY -> {
                    for (i in mCallbacks.indices) {
                        mCallbacks[i].onDisplayReady(msg.arg1)
                    }
                }
            }
        }
    }
}