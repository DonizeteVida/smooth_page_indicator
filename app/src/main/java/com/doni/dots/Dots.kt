package com.doni.dots

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

abstract class Dots : View {
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

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    protected var layoutWidth: Int = 0
    protected var layoutHeight: Int = 0

    protected var dotsCount: Int = 0
    protected var dotsRadius: Float = 0F
    protected var dotsSpace: Float = 0F

    val inactiveColor = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
    }

    val activeColor = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
    }

    var currentOffset: Float = 0F
        set(value) {
            field = value
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        layoutWidth = MeasureSpec.getSize(widthMeasureSpec)
        layoutHeight = MeasureSpec.getSize(heightMeasureSpec)

        dotsRadius = layoutHeight.toFloat() / 2

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    protected fun startDotPosition(): Float {
        val dotsLength = (dotsCount - 1) * dotsRadius * 2
        val dotsSpaceLength = (dotsCount - 1) * dotsSpace

        return (layoutWidth.toFloat() - dotsSpaceLength - dotsLength) / 2
    }

    protected fun startRectPosition(): Float =
        startDotPosition() - dotsRadius

    protected fun rectPosition(roundOffset: Int): Float =
        startRectPosition() + (roundOffset * 2 * dotsRadius) + (roundOffset * dotsSpace)
}

class WormDots : Dots {
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

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        dotsCount = 10
        dotsSpace = 10F
    }

    private var rRect: RectF = RectF().apply {
        top = 0F
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            var position = startDotPosition()

            for (dot in 0 until dotsCount) {
                drawCircle(position, layoutHeight.toFloat() / 2, dotsRadius, inactiveColor)
                position += (dotsRadius * 2) + dotsSpace
            }

            val roundOffset = currentOffset.toInt()

            val diff = currentOffset - roundOffset.toFloat()

            val (from, to) = if (diff > 0.5F) {

                val rDiff = ((diff / 0.5) - 1).toFloat()

                val from =
                    rectPosition(roundOffset) + (rDiff * dotsSpace) + (rDiff * dotsRadius * 2)

                val to = rectPosition(roundOffset + 2) - dotsSpace

                from to to
            } else {
                val rDiff = diff * 2
                val from = rectPosition(roundOffset)
                val to = from + (dotsRadius * 2) + (rDiff * dotsRadius * 2) + (rDiff * dotsSpace)

                from to to
            }

            rRect.let {
                it.left = from
                it.bottom = dotsRadius * 2
                it.right = to
            }

            drawRoundRect(rRect, dotsRadius, dotsRadius, activeColor)
        }
    }
}