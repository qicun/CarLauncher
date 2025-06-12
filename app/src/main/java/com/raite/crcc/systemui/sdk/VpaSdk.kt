package com.raite.crcc.systemui.sdk

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * VPA SDK存根
 */
object VpaSdk {
    fun getVpaStatus(): Flow<String> = flowOf("VPA Ready")
} 