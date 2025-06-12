/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.common

import android.content.res.Configuration

interface IConfigChangedListener {
    fun onConfigurationChanged(configuration: Configuration)
}