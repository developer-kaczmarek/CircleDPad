package io.github.kaczmarek.circledpad

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.math.*


class CircleDPad @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    private val centerButtonPaint: Paint
    private val dividersPaint: Paint
    private val crossPaint: Paint
    private val pressedState = BooleanArray(5)
    private var centerButtonRadius = 0f

    private val drawableButtonResources = IntArray(4)
    private val crossButtonsColors = arrayOfNulls<ColorStateList>(4)

    var listener: OnClickCircleDPadListener? = null

    private var dividersWidth = 2f
    private var centerButtonPercentage = .3f

    private var centerButtonColor: ColorStateList? =
        ContextCompat.getColorStateList(context, R.color.center_button_color)

    private var dividersColor = ContextCompat.getColor(context, R.color.PrimaryRedEnabled)


    interface OnClickCircleDPadListener {
        fun onClickButton(@DPadButton button: Int)
    }

    init {

        context.withStyledAttributes(attrs, R.styleable.CircleDPad) {
            dividersWidth = getDimensionPixelSize(R.styleable.CircleDPad_dividersWidth, 2).toFloat()
            dividersColor = getColor(
                R.styleable.CircleDPad_dividersColor,
                ContextCompat.getColor(context, R.color.PrimaryRedEnabled)
            )
            centerButtonPercentage = getFloat(R.styleable.CircleDPad_centerButtonPercentage, .3f)
            drawableButtonResources[LEFT_BUTTON] =
                getResourceId(
                    R.styleable.CircleDPad_leftButtonDrawable,
                    R.drawable.ic_arrow_left_24
                )
            drawableButtonResources[TOP_BUTTON] =
                getResourceId(R.styleable.CircleDPad_topButtonDrawable, R.drawable.ic_arrow_top_24)
            drawableButtonResources[RIGHT_BUTTON] =
                getResourceId(
                    R.styleable.CircleDPad_rightButtonDrawable,
                    R.drawable.ic_arrow_right_24
                )
            drawableButtonResources[BOTTOM_BUTTON] =
                getResourceId(
                    R.styleable.CircleDPad_bottomButtonDrawable,
                    R.drawable.ic_arrow_bottom_24
                )

            crossButtonsColors[LEFT_BUTTON] =
                getColorStateList(R.styleable.CircleDPad_leftButtonColor)
                    ?: ContextCompat.getColorStateList(context, R.color.cross_buttons_color)
            crossButtonsColors[TOP_BUTTON] =
                getColorStateList(R.styleable.CircleDPad_topButtonColor)
                    ?: ContextCompat.getColorStateList(context, R.color.cross_buttons_color)
            crossButtonsColors[RIGHT_BUTTON] =
                getColorStateList(R.styleable.CircleDPad_rightButtonColor)
                    ?: ContextCompat.getColorStateList(context, R.color.cross_buttons_color)
            crossButtonsColors[BOTTOM_BUTTON] =
                getColorStateList(R.styleable.CircleDPad_bottomButtonColor)
                    ?: ContextCompat.getColorStateList(context, R.color.cross_buttons_color)
            centerButtonColor = getColorStateList(R.styleable.CircleDPad_centerButtonColor)
                ?: ContextCompat.getColorStateList(context, R.color.center_button_color)
        }
        crossPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        centerButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        dividersPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        dividersPaint.strokeWidth = dividersWidth
        dividersPaint.color = dividersColor
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val halfWidth = width / 2f
        val halfHeight = height / 2f
        val radius = min(halfHeight, halfWidth)
        centerButtonRadius = radius * min(abs(centerButtonPercentage), 1.0f)
        val background = background
        background?.draw(canvas)
        val rect = Rect()
        getDrawingRect(rect)
        for (i in 0 until pressedState.size - 1) {
            val startAngle = 45 + 90 * i
            drawCrossButton(
                canvas,
                RectF(rect),
                crossButtonsColors[i]!!,
                startAngle,
                pressedState[i]
            )
        }

        for (i in 0 until pressedState.size - 1) {
            val startAngle = 45 + 90 * i
            drawDividers(canvas, radius, startAngle, halfWidth, halfHeight)
            drawImage(canvas, i, halfWidth, halfHeight, centerButtonRadius / 2)
        }

        centerButtonColor?.let {
            if (centerButtonRadius > 0) {
                drawCenterButton(canvas, halfWidth, halfHeight, it, pressedState[CENTER_BUTTON])
            }
        }
    }

    private fun drawCrossButton(
        canvas: Canvas,
        rectF: RectF,
        color: ColorStateList,
        startAngle: Int,
        isPressed: Boolean
    ) {
        crossPaint.color = if (isPressed) color.getColorForState(
            intArrayOf(android.R.attr.state_pressed),
            color.defaultColor
        ) else color.defaultColor
        canvas.drawArc(rectF, startAngle.toFloat(), 90f, true, crossPaint)
    }

    private fun drawCenterButton(
        canvas: Canvas,
        halfWidth: Float,
        halfHeight: Float,
        color: ColorStateList,
        isPressed: Boolean
    ) {
        centerButtonPaint.style = Paint.Style.FILL
        centerButtonPaint.color = if (isPressed) color.getColorForState(
            intArrayOf(android.R.attr.state_pressed),
            color.defaultColor
        ) else color.defaultColor
        canvas.drawCircle(halfWidth, halfHeight, centerButtonRadius, centerButtonPaint)
    }

    private fun drawDividers(
        canvas: Canvas,
        radius: Float,
        startAngle: Int,
        halfWidth: Float,
        halfHeight: Float
    ) {
        canvas.drawLine(
            halfWidth, halfHeight,
            radius * cos(Math.toRadians(startAngle.toDouble())).toFloat() + halfWidth,
            radius * sin(Math.toRadians(startAngle.toDouble())).toFloat() + halfHeight,
            dividersPaint
        )
    }

    private fun drawImage(
        canvas: Canvas,
        @DPadButton button: Int,
        halfWidth: Float,
        halfHeight: Float,
        halfInnerRadius: Float
    ) {
        val drawableToDraw = ContextCompat.getDrawable(context, drawableButtonResources[button])
        drawableToDraw?.let {
            val halfDrawableWidth = drawableToDraw.intrinsicWidth / 2
            val halfDrawableHeight = drawableToDraw.intrinsicHeight / 2
            when (button) {
                LEFT_BUTTON -> drawableToDraw.setBounds(
                    (halfWidth / 2 - halfInnerRadius - halfDrawableWidth).toInt(),
                    halfHeight.toInt() - halfDrawableHeight,
                    (halfWidth / 2 - halfInnerRadius + halfDrawableWidth).toInt(),
                    halfHeight.toInt() + halfDrawableHeight
                )
                RIGHT_BUTTON -> drawableToDraw.setBounds(
                    (halfWidth + halfInnerRadius + halfWidth / 2).toInt() - halfDrawableWidth,
                    halfHeight.toInt() - halfDrawableHeight,
                    (halfWidth + halfInnerRadius + halfWidth / 2).toInt() + halfDrawableWidth,
                    halfHeight.toInt() + halfDrawableHeight
                )
                TOP_BUTTON -> drawableToDraw.setBounds(
                    halfWidth.toInt() - halfDrawableWidth,
                    (halfHeight / 2 - halfInnerRadius - halfDrawableHeight).toInt(),
                    halfWidth.toInt() + halfDrawableWidth,
                    (halfHeight / 2 - halfInnerRadius + halfDrawableHeight).toInt()
                )
                BOTTOM_BUTTON -> drawableToDraw.setBounds(
                    halfWidth.toInt() - halfDrawableWidth,
                    (halfHeight + halfInnerRadius + halfHeight / 2).toInt() - halfDrawableHeight,
                    halfWidth.toInt() + halfDrawableWidth,
                    (halfHeight + halfInnerRadius + halfHeight / 2).toInt() + halfDrawableHeight
                )
                else -> return
            }
            drawableToDraw.draw(canvas)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val x = event.x - width / 2f
        val y = event.y - height / 2f
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            val radius = sqrt(x.pow(2) + y.pow(2))
            if (radius > width / 2f || radius > height / 2f) {
                // click outside the drawn view
                val cancelEvent = MotionEvent.obtain(event)
                cancelEvent.action = MotionEvent.ACTION_CANCEL
                dispatchTouchEvent(cancelEvent)
                cancelEvent.recycle()
                updatePressedState()
                return false
            }
            if (radius < centerButtonRadius) { // CenterButton click
                updatePressedState()
                pressedState[CENTER_BUTTON] = true
            } else { // CrossButtons click
                var touchAngle = Math.toDegrees(
                    atan2(y.toDouble(), x.toDouble())
                ).toFloat()
                if (touchAngle < 0) {
                    touchAngle += 360f
                }
                for (i in 0 until pressedState.size - 1) {
                    val startAngle = 45 + 90 * i % 360
                    var endAngle = (startAngle + 90) % 360
                    if (startAngle > endAngle) {
                        if (touchAngle < startAngle && touchAngle < endAngle) {
                            touchAngle += 360f
                        }
                        endAngle += 360
                    }
                    if (startAngle <= touchAngle && endAngle >= touchAngle) {
                        updatePressedState()
                        pressedState[i] = true
                    }
                }
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            pressedState.forEachIndexed { index, b ->
                if (b) {
                    listener?.onClickButton(index)
                }
            }
            updatePressedState()
        }
        this.invalidate()
        return true
    }

    private fun updatePressedState() {
        for (i in pressedState.indices) {
            pressedState[i] = false
        }
    }

    companion object {
        @IntDef(BOTTOM_BUTTON, LEFT_BUTTON, TOP_BUTTON, RIGHT_BUTTON, CENTER_BUTTON)
        @Retention(AnnotationRetention.SOURCE)
        annotation class DPadButton

        const val BOTTOM_BUTTON = 0
        const val LEFT_BUTTON = 1
        const val TOP_BUTTON = 2
        const val RIGHT_BUTTON = 3
        const val CENTER_BUTTON = 4
    }
}