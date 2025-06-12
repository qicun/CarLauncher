package com.raite.crcc.systemui.sdk

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

/**
 * 统一的API SDK存根
 */
object ApiSdk {
    fun getCarSpeed(): Flow<Int> = flow {
        while (true) {
            emit(Random.nextInt(0, 120))
            delay(1000)
        }
    }
} 