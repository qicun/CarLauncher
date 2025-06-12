/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.data.type

import androidx.annotation.StringRes
import com.raite.crcc.systemui.R

/**
 * 车厢
 */
sealed class CarriageState(val typeId: Int = 0, @StringRes val nameRes: Int) {
    data object Unknown : CarriageState(nameRes = R.string.str_systemui_type_unknown)
    data object MC1 : CarriageState(1, R.string.carriage_mc1_def)
    data object T : CarriageState(2, R.string.carriage_t_def)
    data object MC2 : CarriageState(3, R.string.carriage_mc2_def)
    data object ALL : CarriageState(4 , R.string.carriage_control_def)

    /**
     * 是否是Unknown或ALL
     */
    fun isUnknownOrAll(): Boolean {
        return this == Unknown || this == ALL
    }

    /**
     * 是单独车厢
     */
    fun isSingleZone(): Boolean {
        return this == MC1 || this == T || this == MC2
    }
}
