package com.doni.dots

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

abstract class Dots @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

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

    protected fun startDotPosition(otherValue: Float = 0F): Float {
        val dotsLength = (dotsCount - 1) * dotsRadius * 2
        val dotsSpaceLength = (dotsCount - 1) * dotsSpace

        return (layoutWidth.toFloat() - dotsSpaceLength - dotsLength - otherValue) / 2
    }

    protected fun startRectPosition(otherValue: Float = 0F): Float =
        startDotPosition(otherValue) - dotsRadius

    protected fun rectPosition(roundOffset: Int): Float =
        startRectPosition() + (roundOffset * 2 * dotsRadius) + (roundOffset * dotsSpace)
}

class WormDots @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Dots(context, attrs, defStyleAttr) {

    init {
        dotsCount = 5
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

class ExpandingDots @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Dots(context, attrs, defStyleAttr) {

    init {
        dotsCount = 5
        dotsSpace = 10F
    }

    private val expandingValue: Float = 190F

    private var rRect: RectF = RectF().apply {
        top = 0F
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            var position = startDotPosition(expandingValue)
            val roundOffset: Int = currentOffset.toInt()

            val firstDifference: Float = 1 - (currentOffset - roundOffset)
            val secondDifference: Float = 1 - firstDifference

            for (dot in 0 until dotsCount) {
                if (roundOffset == dot) {
                    val factor = expandingValue * firstDifference

                    position -= dotsRadius

                    rRect.let {
                        it.left = position
                        it.bottom = dotsRadius * 2
                        it.right = position + factor + (dotsRadius * 2)
                    }

                    position += factor + (dotsRadius * 2) + dotsSpace

                    drawRoundRect(rRect, dotsRadius, dotsRadius, activeColor)
                } else if (dot == roundOffset + (firstDifference + secondDifference).toInt()) {
                    val factor = expandingValue * secondDifference

                    rRect.let {
                        it.left = position
                        it.bottom = dotsRadius * 2
                        it.right = position + factor + (dotsRadius * 2)
                    }

                    position += factor + (dotsRadius * 2) + dotsSpace

                    drawRoundRect(rRect, dotsRadius, dotsRadius, activeColor)

                    position += dotsRadius
                } else {
                    drawCircle(position, layoutHeight.toFloat() / 2, dotsRadius, activeColor)
                    position += (dotsRadius * 2) + dotsSpace
                }
            }
        }
    }
}