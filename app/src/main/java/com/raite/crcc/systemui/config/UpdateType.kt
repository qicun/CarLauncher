/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.config

/**
 * @Author lsh
 * @Date 2023/6/30 14:11
 * @Description
 */
object UpdateType {
    const val UPDATE_TYPE_NONE = 0

    /** 主屏页面显示 **/
    const val UPDATE_TYPE_DISPLAY = 1

    /** 副屏页面显示 **/
    const val UPDATE_TYPE_SUB_DISPLAY = 2

    /** 用户登录 */
    const val UPDATE_TYPE_USER_LOGIN = 3

    /** 时间 */
    const val UPDATE_TYPE_TIME = 4

    /** 车厢 **/
    const val UPDATE_TYPE_CARRIAGE = 5

    /** 温度 **/
    const val UPDATE_TYPE_TEMPERATURE = 6

    /** 用户等级 */
    const val UPDATE_TYPE_USER_LEVEL = 7

    /** 用户名称 */
    const val UPDATE_TYPE_USER_NAME = 8

    /** 室外温度 */
    const val UPDATE_TYPE_EXTERNAL_TEMP = 9

    /** 列车号 */
    const val UPDATE_TYPE_TRAIN_NUM = 10

    /** 终点站名称 */
    const val UPDATE_TYPE_TERMINAL_STATION_NAME = 11

    /** 当前站名称 */
    const val UPDATE_TYPE_CURRENT_STATION_NAME = 12

    /** 下一站名称 */
    const val UPDATE_TYPE_NEXT_STATION_NAME = 13
}