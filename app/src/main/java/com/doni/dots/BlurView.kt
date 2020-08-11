package com.doni.dots

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class BlurView : ConstraintLayout {
    constructor(context: Context?) : super(context)

    constructor(
        context: Context?,
        attrs: AttributeSet?
    ) : super(context, attrs)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)


    private val blurPaint = Paint().apply {
        maskFilter = BlurMaskFilter(2000F, BlurMaskFilter.Blur.NORMAL)
        color = Color.BLACK
        alpha = 100
    }


}