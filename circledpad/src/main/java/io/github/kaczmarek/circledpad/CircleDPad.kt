package io.github.kaczmarek.circledpad

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class CircleDPad @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    var onClickCircleDPadListener: OnClickCircleDPadListener? = null
    private var dividersWidth = 2f
    private var dividersColor = ContextCompat.getColor(context, R.color.PrimaryRedEnabled)
    private var centerButtonPercentage = .3f
    private var centerButtonRadius = 0f
    private var centerButtonColor: ColorStateList? = ContextCompat.getColorStateList(context, R.color.center_button_color)
    private var centerButtonPaint: Paint
    private var dividersPaint: Paint
    private val drawableButtonResources = IntArray(4)
    private val crossButtonsColors = arrayOfNulls<ColorStateList>(4)
    private val crossPaint: Paint
    private val pressedState = BooleanArray(5)


    interface OnClickCircleDPadListener {
        fun onClickButton(section: Int)
    }


    init {

        context.withStyledAttributes(attrs, R.styleable.CircleDPad) {
            dividersWidth = getDimensionPixelSize(R.styleable.CircleDPad_dividersWidth, 2).toFloat()
            dividersColor = getColor(R.styleable.CircleDPad_dividersColor, ContextCompat.getColor(context, R.color.PrimaryRedEnabled))
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
                getColorStateList(R.styleable.CircleDPad_leftButtonColor) ?: ContextCompat.getColorStateList(context, R.color.cross_buttons_color)
            crossButtonsColors[TOP_BUTTON] =
                getColorStateList(R.styleable.CircleDPad_topButtonColor) ?: ContextCompat.getColorStateList(context, R.color.cross_buttons_color)
            crossButtonsColors[RIGHT_BUTTON] =
                getColorStateList(R.styleable.CircleDPad_rightButtonColor) ?: ContextCompat.getColorStateList(context, R.color.cross_buttons_color)
            crossButtonsColors[BOTTOM_BUTTON] =
                getColorStateList(R.styleable.CircleDPad_bottomButtonColor) ?: ContextCompat.getColorStateList(context, R.color.cross_buttons_color)
            centerButtonColor = getColorStateList(R.styleable.CircleDPad_centerButtonColor) ?: ContextCompat.getColorStateList(context, R.color.center_button_color)
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
        val halfInnerRadius = centerButtonRadius / 2
        val background = background
        background?.draw(canvas)
        val rect = Rect()
        getDrawingRect(rect)
        val rectF = RectF(rect)
        val angleOffset = 45
        for (i in 0 until pressedState.size - 1) {
            val startAngle = angleOffset + 90 * i
            drawCrossButton(
                canvas,
                rectF,
                crossButtonsColors[i]!!,
                startAngle,
                pressedState[i]
            )
        }

        for (i in 0 until pressedState.size - 1) {
            val startAngle = angleOffset + 90 * i
            drawDividers(canvas, radius, startAngle, halfWidth, halfHeight)
        }

        drawImage(canvas, LEFT_BUTTON, halfWidth, halfHeight, halfInnerRadius)
        drawImage(canvas, TOP_BUTTON, halfWidth, halfHeight, halfInnerRadius)
        drawImage(canvas, RIGHT_BUTTON, halfWidth, halfHeight, halfInnerRadius)
        drawImage(canvas, BOTTOM_BUTTON, halfWidth, halfHeight, halfInnerRadius)

        if (centerButtonRadius > 0) {
            centerButtonPaint.style = Paint.Style.FILL
            centerButtonColor?.defaultColor?.let {
                centerButtonPaint.color = it
            }
            canvas.drawCircle(halfWidth, halfHeight, centerButtonRadius, centerButtonPaint)
            centerButtonPaint.style = Paint.Style.STROKE
            centerButtonPaint.strokeWidth = dividersWidth
            centerButtonPaint.color = dividersColor
            canvas.drawCircle(halfWidth, halfHeight, centerButtonRadius, centerButtonPaint)
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

    companion object {
        @IntDef(LEFT_BUTTON, TOP_BUTTON, RIGHT_BUTTON, BOTTOM_BUTTON, CENTER_BUTTON)
        @Retention(AnnotationRetention.SOURCE)
        annotation class DPadButton

        const val LEFT_BUTTON = 0
        const val TOP_BUTTON = 1
        const val RIGHT_BUTTON = 2
        const val BOTTOM_BUTTON = 3
        const val CENTER_BUTTON = 4
    }
}