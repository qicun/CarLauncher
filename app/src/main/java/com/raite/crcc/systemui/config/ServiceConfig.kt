/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.config

import android.annotation.DisplayContext
import android.annotation.UiContext
import android.app.UiModeManager
import android.app.WallpaperManager
import android.bluetooth.BluetoothManager
import android.car.Car
import android.content.Context
import android.content.om.OverlayManager
import android.hardware.display.DisplayManager
import android.hardware.usb.UsbManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.TetheringManager
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.os.storage.StorageManager
import android.telephony.TelephonyManager
import android.view.WindowManager
import com.raite.crcc.systemui.App
import com.raite.crcc.systemui.utils.DisplayWindowManager
import com.raite.crcc.systemui.utils.UiWindowManager

/**
 * @Author zl
 * @Date 2023/8/22
 * @Description 所有Service注册表
 */
object ServiceConfig {

    @UiContext
    private val mUiContext = App.mContext

    fun getBluetoothManager(@UiContext context: Context = mUiContext): BluetoothManager {
        return context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    fun getPowerManager(@UiContext context: Context = mUiContext): PowerManager {
        return context.getSystemService(PowerManager::class.java)
    }

    fun getAudioManager(@UiContext context: Context = mUiContext): AudioManager {
        return context.getSystemService(AudioManager::class.java)
    }

    fun getStorageManager(@UiContext context: Context = mUiContext): StorageManager {
        return context.getSystemService(StorageManager::class.java)
    }

    @UiWindowManager
    fun getWindowManagerByUiContext(@UiContext context: Context = mUiContext): WindowManager {
        return context.getSystemService(WindowManager::class.java)
    }

    @DisplayWindowManager
    fun getWindowManagerByDisplayContext(@DisplayContext context: Context): WindowManager {
        return context.getSystemService(WindowManager::class.java)
    }

    fun getTelephonyManager(@UiContext context: Context = mUiContext): TelephonyManager {
        return context.getSystemService(TelephonyManager::class.java)
    }

    fun getConnectivityManager(@UiContext context: Context = mUiContext): ConnectivityManager {
        return context.getSystemService(ConnectivityManager::class.java)
    }

    fun getWifiManager(@UiContext context: Context = mUiContext): WifiManager {
        return context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    fun getDisplayManager(@UiContext context: Context = mUiContext): DisplayManager {
        return context.getSystemService(DisplayManager::class.java)
    }

    fun getUiModeManager(@UiContext context: Context = mUiContext): UiModeManager {
        return context.getSystemService(UiModeManager::class.java)
    }

    fun getWallpaperManager(@UiContext context: Context = mUiContext): WallpaperManager {
        return context.getSystemService(WallpaperManager::class.java)
    }

    fun getConnectivityManagerService(@UiContext context: Context = mUiContext): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun getTetherManager(@UiContext context: Context = mUiContext): TetheringManager {
        return context.getSystemService(TetheringManager::class.java)
    }

    fun getUsbManager(@UiContext context: Context = mUiContext): UsbManager {
        return context.getSystemService(Context.USB_SERVICE) as UsbManager
    }
}