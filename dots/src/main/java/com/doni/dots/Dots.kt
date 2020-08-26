package com.doni.dots

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

typealias OnDotClicked = ((Int) -> Unit)?

abstract class Dots @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var _onDotClicked: OnDotClicked = null

    fun setOnDotClicked(onDotClicked: OnDotClicked) {
        _onDotClicked = onDotClicked
    }

    private var layoutWidth: Int = 0
    private var layoutHeight: Int = 0

    protected var dotsCount: Int = 0
    protected var dotsSpace: Float = 0F
    protected var dotsRadius: Float = 0F

    protected var itemSize: Float = 0F

    ///Dot index, pair of start point and end point of index
    private val touchCordinate = HashMap<Int, Pair<Float, Float>>()

    protected fun updateTouchCordinate(dot: Int, from: Float, to: Float) {
        touchCordinate[dot] = from to to
    }

    private fun recoverTouchCordinate(dot: Int) = touchCordinate[dot] ?: Pair(0f, 0f)

    val inactiveColor = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
    }

    val activeColor = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
    }

    protected val rRect: RectF = RectF()

    protected fun updateRRect(from: Float, to: Float) {
        rRect.apply {
            top = 0F
            bottom = itemSize
            left = from
            right = to
        }
    }

    var currentOffset: Float = 0F
        set(value) {
            field = value
            invalidate()
        }

    init {
        context?.run {
            val attributes = theme.obtainStyledAttributes(attrs, R.styleable.Dots, defStyleAttr, 0)

            dotsCount = attributes.getInteger(R.styleable.Dots_dotsCount, 0)
            dotsSpace = attributes.getDimensionPixelSize(R.styleable.Dots_dotsSpace, 0).toFloat()
            dotsRadius = attributes.getFloat(R.styleable.Dots_dotsRadius, -99F)

            activeColor.color = attributes.getColor(R.styleable.Dots_activeColor, Color.BLUE)
            inactiveColor.color = attributes.getColor(R.styleable.Dots_inactiveColor, Color.GRAY)

            attributes.recycle()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        layoutWidth = MeasureSpec.getSize(widthMeasureSpec)
        layoutHeight = MeasureSpec.getSize(heightMeasureSpec)

        itemSize = layoutHeight.toFloat()
        if (dotsRadius == -99F) {
            dotsRadius = itemSize / 2
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun startPosition(otherValue: Float = 0F): Float {
        val dotsLength = dotsCount * itemSize
        val dotsSpaceLength = (dotsCount - 1) * dotsSpace

        return (layoutWidth.toFloat() - dotsSpaceLength - dotsLength - otherValue) / 2
    }

    protected fun startRectPosition(otherValue: Float = 0F): Float =
        startPosition(otherValue)

    protected fun rectPosition(roundOffset: Int): Float =
        startRectPosition() + (roundOffset * itemSize) + (roundOffset * dotsSpace)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.action
        if (action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            notifyTappedDot(x)
        }
        return super.onTouchEvent(event)
    }

    private fun notifyTappedDot(xCordinate: Float) {
        for (dot in 0 until dotsCount) {
            val (startPosition, endPosition) = recoverTouchCordinate(dot)
            if (xCordinate in startPosition..endPosition) {
                _onDotClicked?.invoke(dot)
                return
            }
        }
    }
}

class WormDots @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Dots(context, attrs, defStyleAttr) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {

            for (dot in 0 until dotsCount) {
                val rectPosition = rectPosition(dot)
                val (from, to) = rectPosition to rectPosition + itemSize
                updateRRect(from = from, to = to)
                updateTouchCordinate(dot, from = from, to = to)
                drawRoundRect(rRect, dotsRadius, dotsRadius, inactiveColor)
            }

            val roundOffset = currentOffset.toInt()

            val diff = currentOffset - roundOffset.toFloat()
            val move = dotsSpace + itemSize

            val fraction = diff * 2

            val (from, to) = if (diff > 0.5F) {
                val from = rectPosition(roundOffset) + ((fraction - 1) * move)
                val to = rectPosition(roundOffset + 2) - dotsSpace

                from to to
            } else {
                val from = rectPosition(roundOffset)
                val to = from + itemSize + fraction * move

                from to to
            }

            updateRRect(from = from, to = to)
            drawRoundRect(rRect, dotsRadius, dotsRadius, activeColor)
        }
    }
}

class ExpandingDots @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Dots(context, attrs, defStyleAttr) {

    private var expandingSpace: Float = 0F

    init {
        context?.run {
            val attributes =
                theme.obtainStyledAttributes(attrs, R.styleable.ExpandingDots, defStyleAttr, 0)

            expandingSpace =
                attributes.getDimensionPixelSize(R.styleable.ExpandingDots_expandingSpace, 0)
                    .toFloat()

            attributes.recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            var position = startRectPosition(otherValue = expandingSpace)
            val roundOffset: Int = currentOffset.toInt()

            val secondDifference: Float = currentOffset - roundOffset
            val firstDifference: Float = 1 - secondDifference

            for (dot in 0 until dotsCount) {
                val result = when (dot) {
                    roundOffset -> expandingSpace * firstDifference
                    (roundOffset + firstDifference + secondDifference).toInt() -> expandingSpace * secondDifference
                    else -> 0f
                }

                val to = position + result + itemSize

                updateRRect(from = position, to = to)
                updateTouchCordinate(dot, from = position, to = to)
                drawRoundRect(rRect, dotsRadius, dotsRadius, activeColor)

                position += result + itemSize + dotsSpace
            }
        }
    }
}

class SwapDots @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Dots(context, attrs, defStyleAttr) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {
            val roundOffset = currentOffset.toInt()

            val diff: Float = currentOffset - roundOffset

            for (dot in dotsCount - 1 downTo 0) {

                val rectPosition = rectPosition(dot)
                val move = (itemSize + dotsSpace) * diff

                val (color, quantity) = when (dot) {
                    roundOffset -> activeColor to move
                    roundOffset + 1 -> inactiveColor to -move
                    else -> inactiveColor to 0f
                }

                val (from, to) = (rectPosition + quantity) to (rectPosition + quantity + itemSize)

                updateRRect(from = from, to = to)
                updateTouchCordinate(dot, from = from, to = to)
                drawRoundRect(rRect, dotsRadius, dotsRadius, color)
            }
        }
    }
}

class SlideDots @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Dots(context, attrs, defStyleAttr) {

}