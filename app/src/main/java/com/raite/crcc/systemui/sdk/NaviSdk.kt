package com.raite.crcc.systemui.sdk

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Navi SDK存根
 */
object NaviSdk {
    fun getNavigationStatus(): Flow<String> = flowOf("No active route")
} 