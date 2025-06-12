/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.focus.indicator

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.UiThread
import com.raite.crcc.systemui.R

@UiThread
class FocusIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    init {
        setBackgroundResource(R.drawable.focus_indicator_default)
    }

}