/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.os.SystemClock
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author lsh
 * @date 2024/4/11 11:02
 * @description
 */
object ThreadPoolManager {
    /** 非核心线程空闲后等待新任务的最长时长 */
    private const val KEEP_ALIVE_TIME = 0L

    /** 固定队列最大保持长度 */
    private const val CAPACITY_SIZE = 10

    fun getFixedThreadPool(corePoolSize: Int, maximumPoolSize: Int): ThreadPoolExecutor {
        val workQueue: BlockingQueue<Runnable> = LinkedBlockingQueue(CAPACITY_SIZE)
        val time = SystemClock.elapsedRealtime()
        val factory = ThreadFactory { r: Runnable? -> Thread(r, "Thread_${time}") }
        return ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS,
            workQueue,
            factory
        )
    }

    fun getSingleThreadPool(): ThreadPoolExecutor {
        return getFixedThreadPool(1, 1)
    }
}