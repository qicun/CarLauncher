/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.common

import android.app.ITransientNotificationCallback
import android.app.StatusBarManager
import android.content.ComponentName
import android.hardware.biometrics.BiometricAuthenticator.Modality
import android.hardware.biometrics.IBiometricSysuiReceiver
import android.hardware.biometrics.PromptInfo
import android.hardware.fingerprint.IUdfpsHbmListener
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.view.InsetsState.InternalInsetsType
import android.view.InsetsVisibilities
import com.android.internal.statusbar.IStatusBar
import com.android.internal.statusbar.StatusBarIcon
import com.android.internal.view.AppearanceRegion

interface MessageCallback {

    fun setIcon(slot: String?, icon: StatusBarIcon?) {}
    fun removeIcon(slot: String?) {}

    /**
     * Called to notify that disable flags are updated.
     * @see IStatusBar.disable
     * @param displayId The id of the display to notify.
     * @param state1 The combination of following DISABLE_* flags: DisableFlags
     * @param state2 The combination of following DISABLE2_* flags: Disable2Flags
     * @param animate `true` to show animations.
     */
    fun disable(
        displayId: Int, state1: Int, state2: Int,
        animate: Boolean
    ) {
    }

    fun animateExpandNotificationsPanel() {}
    fun animateCollapsePanels(flags: Int, force: Boolean) {}
    fun togglePanel() {}
    fun animateExpandSettingsPanel(obj: String?) {}

    /**
     * Called to notify IME window status changes.
     *
     * @param displayId The id of the display to notify.
     * @param token IME token.
     * @param vis IME visibility.
     * @param backDisposition  BackDispositionMode Disposition mode of back button.
     *      It should be one of below flags:
     * @param showImeSwitcher `true` to show IME switch button.
     */
    fun setImeWindowStatus(
        displayId: Int, token: IBinder?, vis: Int,
        backDisposition: Int, showImeSwitcher: Boolean
    ) {
    }

    fun showRecentApps(triggeredFromAltTab: Boolean) {}
    fun hideRecentApps(triggeredFromAltTab: Boolean, triggeredFromHomeKey: Boolean) {}
    fun toggleRecentApps() {}
    fun toggleSplitScreen() {}
    fun preloadRecentApps() {}
    fun dismissKeyboardShortcutsMenu() {}
    fun toggleKeyboardShortcutsMenu(deviceId: Int) {}
    fun cancelPreloadRecentApps() {}

    /**
     * Called to notify window state changes.
     * @see IStatusBar.setWindowState
     * @param displayId The id of the display to notify.
     * @param window Window type. It should be one of [StatusBarManager.WINDOW_STATUS_BAR]
     * or [StatusBarManager.WINDOW_NAVIGATION_BAR]
     * @param state Window visible state. @WindowVisibleState
     */
    fun setWindowState(
        displayId: Int, windowType: Int,
        state: Int
    ) {
    }

    fun showScreenPinningRequest(taskId: Int) {}

    /**
     * Called to notify System UI that an application transition is pending.
     * @see IStatusBar.appTransitionPending
     * @param displayId The id of the display to notify.
     * @param forced `true` to force transition pending.
     */
    fun appTransitionPending(displayId: Int, forced: Boolean) {}

    /**
     * Called to notify System UI that an application transition is canceled.
     * @see IStatusBar.appTransitionCancelled
     * @param displayId The id of the display to notify.
     */
    fun appTransitionCancelled(displayId: Int) {}

    /**
     * Called to notify System UI that an application transition is starting.
     * @see IStatusBar.appTransitionStarting
     * @param displayId The id of the display to notify.
     * @param startTime Transition start time.
     * @param duration Transition duration.
     * @param forced `true` to force transition pending.
     */
    fun appTransitionStarting(
        displayId: Int, startTime: Long, duration: Long, forced: Boolean
    ) {
    }

    /**
     * Called to notify System UI that an application transition is finished.
     * @see IStatusBar.appTransitionFinished
     * @param displayId The id of the display to notify.
     */
    fun appTransitionFinished(displayId: Int) {}
    fun showAssistDisclosure() {}
    fun startAssist(args: Bundle?) {}
    fun onCameraLaunchGestureDetected(source: Int) {}

    /**
     * Notifies SysUI that the emergency action gesture was detected.
     */
    fun onEmergencyActionLaunchGestureDetected() {}
    fun showPictureInPictureMenu() {}
    fun setTopAppHidesStatusBar(topAppHidesStatusBar: Boolean) {}

    fun addQsTile(tile: ComponentName?) {}
    fun remQsTile(tile: ComponentName?) {}
    fun clickTile(tile: ComponentName?) {}

    fun handleSystemKey(arg1: Int) {}
    fun showPinningEnterExitToast(entering: Boolean) {}
    fun showPinningEscapeToast() {}
    fun handleShowGlobalActionsMenu() {}
    fun handleShowShutdownUi(isReboot: Boolean, reason: String?) {}

    fun showWirelessChargingAnimation(batteryLevel: Int) {}

    fun onRotationProposal(rotation: Int, isValid: Boolean) {}

    fun showAuthenticationDialog(
        promptInfo: PromptInfo?,
        receiver: IBiometricSysuiReceiver?,
        sensorIds: IntArray?, credentialAllowed: Boolean,
        requireConfirmation: Boolean, userId: Int, operationId: Long, opPackageName: String?,
        requestId: Long, multiSensorConfig: Int
    ) {
    }

    /** @see IStatusBar.onBiometricAuthenticated
     */
    fun onBiometricAuthenticated() {}

    /** @see IStatusBar.onBiometricHelp
     */
    fun onBiometricHelp(@Modality modality: Int, message: String?) {}

    /** @see IStatusBar.onBiometricError
     */
    fun onBiometricError(@Modality modality: Int, error: Int, vendorCode: Int) {}

    fun hideAuthenticationDialog() {}

    /**
     * @see IStatusBar.setUdfpsHbmListener
     */
    fun setUdfpsHbmListener(listener: IUdfpsHbmListener?) {}

    /**
     * @see IStatusBar.onDisplayReady
     */
    fun onDisplayReady(displayId: Int) {}

    /**
     * @see DisplayManager.DisplayListener.onDisplayRemoved
     */
    fun onDisplayRemoved(displayId: Int) {}

    /**
     * @see IStatusBar.onRecentsAnimationStateChanged
     */
    fun onRecentsAnimationStateChanged(running: Boolean) {}

    /**
     * @see IStatusBar.onSystemBarAttributesChanged
     */
    fun onSystemBarAttributesChanged(
        displayId: Int, appearance: Int,
        appearanceRegions: Array<AppearanceRegion>?, navbarColorManagedByIme: Boolean,
        behavior: Int, requestedVisibilities: InsetsVisibilities?,
        packageName: String?
    ) {
    }

    /**
     * @see IStatusBar.showTransient
     */
    fun showTransient(displayId: Int, @InternalInsetsType types: IntArray?) {}

    /**
     * @see IStatusBar.showTransient
     */
    fun showTransient(
        displayId: Int, @InternalInsetsType types: IntArray?,
        isGestureOnSystemBar: Boolean
    ) {
        showTransient(displayId, types)
    }

    /**
     * @see IStatusBar.abortTransient
     */
    fun abortTransient(displayId: Int, @InternalInsetsType types: IntArray?) {}

    /**
     * Called to notify System UI that a warning about the device going to sleep
     * due to prolonged user inactivity should be shown.
     */
    fun showInattentiveSleepWarning() {}

    /**
     * Called to notify System UI that the warning about the device going to sleep
     * due to prolonged user inactivity should be dismissed.
     */
    fun dismissInattentiveSleepWarning(animated: Boolean) {}

    /** Called to suppress ambient display.  */
    fun suppressAmbientDisplay(suppress: Boolean) {}

    /**
     * @see IStatusBar.showToast
     */
    fun showToast(
        uid: Int, packageName: String?, token: IBinder?, text: CharSequence?,
        windowToken: IBinder?, duration: Int,
        callback: ITransientNotificationCallback?
    ) {
    }

    /**
     * @see IStatusBar.hideToast
     */
    fun hideToast(packageName: String?, token: IBinder?) {}

    /**
     * @param enabled
     */
    fun onTracingStateChanged(enabled: Boolean) {}

    /**
     * Requests [com.android.systemui.accessibility.WindowMagnification] to invoke
     * `android.view.accessibility.AccessibilityManager#
     * setWindowMagnificationConnection(IWindowMagnificationConnection)`
     *
     * @param connect `true` if needs connection, otherwise set the connection to null.
     */
    fun requestWindowMagnificationConnection(connect: Boolean) {}

    /**
     * Handles a window manager shell logging command.
     */
    fun handleWindowManagerLoggingCommand(args: Array<String?>?, outFd: ParcelFileDescriptor?) {}

    /**
     * @see IStatusBar.setNavigationBarLumaSamplingEnabled
     */
    fun setNavigationBarLumaSamplingEnabled(displayId: Int, enable: Boolean) {}

    fun addBar() {}

}