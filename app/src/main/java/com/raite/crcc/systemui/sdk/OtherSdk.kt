package com.raite.crcc.systemui.sdk

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * 其他SDK存根
 */
object OtherSdk {
    fun getSomeOtherData(): Flow<String> = flowOf("Some data")
} 