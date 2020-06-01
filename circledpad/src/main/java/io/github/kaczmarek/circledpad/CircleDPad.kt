package io.github.kaczmarek.circledpad

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View

import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


/*
class DpadView @JvmOverloads constructor(
    context: Context,
    @Nullable attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private val mXferPaint: Paint
    private val mInnerCirclePaint: Paint
    private val mDividerPaint: Paint
    private var mDividerWidth = 0
    private var mDividerColor = 0

    // 0 - bottom, 1 - left, 2 - top, 3 - right, 4 - center
    private val mSectionColors = arrayOfNulls<ColorStateList>(4)
    private val mIsPressed = BooleanArray(5)
    private val mDrawableResources = IntArray(4)
    private var mInnerCirclePercentage = 0f
    private var mInnerCircleRadius = 0f
    private var mDrawablesColor: ColorStateList? = null
    private var mInnerCircleColor: ColorStateList? = null
    private var mSectionClickedListener: OnSectionClickedListener? = null


    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val halfWidth = width / 2f
        val halfHeight = height / 2f
        val radius = if (halfWidth > halfHeight) halfHeight else halfWidth
        mInnerCircleRadius =
            radius * Math.min(Math.abs(mInnerCirclePercentage), 1.0f)
        val halfInnerRadius = mInnerCircleRadius / 2
        val background = background
        background?.draw(canvas)
        val rect = Rect()
        getDrawingRect(rect)
        val rectF = RectF(rect)

        // draw arcs
        val angleOffset = 45
        for (i in 0 until mIsPressed.size - 1) {
            val startAngle = angleOffset + 90 * i
            drawArc(
                canvas,
                rectF,
                if (mSectionColors[i] != null) mSectionColors[i] else ColorStateList.valueOf(
                    DEFAULT_SECTION_COLOR
                ),
                startAngle,
                mIsPressed[i]
            )
        }

        // draw dividers (this need to be after arcs in order to be drawn above them)
        for (i in 0 until mIsPressed.size - 1) {
            val startAngle = angleOffset + 90 * i
            drawDivider(canvas, radius, startAngle, halfWidth, halfHeight)
        }

        // draw icons
        drawIcon(canvas, SECTION_TOP, halfWidth, halfHeight, halfInnerRadius)
        drawIcon(canvas, SECTION_BOTTOM, halfWidth, halfHeight, halfInnerRadius)
        drawIcon(canvas, SECTION_LEFT, halfWidth, halfHeight, halfInnerRadius)
        drawIcon(canvas, SECTION_RIGHT, halfWidth, halfHeight, halfInnerRadius)


        // draw innerCircle
        if (mInnerCircleRadius > 0) {
            // draw fill
            mInnerCirclePaint.style = Paint.Style.FILL
            mInnerCirclePaint.color =
                if (mIsPressed[SECTION_CENTER]) mInnerCircleColor!!.getColorForState(
                    intArrayOf(android.R.attr.state_pressed),
                    mInnerCircleColor!!.defaultColor
                ) else mInnerCircleColor!!.defaultColor
            canvas.drawCircle(halfWidth, halfHeight, mInnerCircleRadius, mInnerCirclePaint)

            // draw stroke
            mInnerCirclePaint.style = Paint.Style.STROKE
            mInnerCirclePaint.strokeWidth = mDividerWidth.toFloat()
            mInnerCirclePaint.color = mDividerColor
            canvas.drawCircle(halfWidth, halfHeight, mInnerCircleRadius, mInnerCirclePaint)
        }
    }

    private fun drawArc(
        canvas: Canvas,
        rectF: RectF,
        color: ColorStateList?,
        startAngle: Int,
        isPressed: Boolean
    ) {
        mXferPaint.color = if (isPressed) color!!.getColorForState(
            intArrayOf(android.R.attr.state_pressed),
            color.defaultColor
        ) else color!!.defaultColor
        canvas.drawArc(rectF, startAngle.toFloat(), 90f, true, mXferPaint)
    }

    private fun drawDivider(
        canvas: Canvas,
        radius: Float,
        startAngle: Int,
        halfWidth: Float,
        halfHeight: Float
    ) {
        canvas.drawLine(
            halfWidth, halfHeight,
            radius * Math.cos(Math.toRadians(startAngle.toDouble())).toFloat() + halfWidth,
            radius * Math.sin(Math.toRadians(startAngle.toDouble())).toFloat() + halfHeight,
            mDividerPaint
        )
    }

    private fun drawIcon(
        canvas: Canvas,
        section: Int,
        halfWidth: Float,
        halfHeight: Float,
        halfInnerRadius: Float
    ) {
        val drawableToDraw: Drawable =
            ContextCompat.getDrawable(context, mDrawableResources[section])
        drawableToDraw.setColorFilter(
            if (mIsPressed[section]) mDrawablesColor!!.getColorForState(
                intArrayOf(android.R.attr.state_pressed),
                mDrawablesColor!!.defaultColor
            ) else mDrawablesColor!!.defaultColor, PorterDuff.Mode.SRC_IN
        )
        val halfDrawableWidth = drawableToDraw.intrinsicWidth / 2
        val halfDrawableHeight = drawableToDraw.intrinsicHeight / 2
        when (section) {
            SECTION_TOP -> drawableToDraw.setBounds(
                halfWidth.toInt() - halfDrawableWidth,  //left
                (halfHeight / 2 - halfInnerRadius - halfDrawableHeight).toInt(),  //top
                halfWidth.toInt() + halfDrawableWidth,  //right
                (halfHeight / 2 - halfInnerRadius + halfDrawableHeight).toInt()
            ) //bottom
            SECTION_BOTTOM -> drawableToDraw.setBounds(
                halfWidth.toInt() - halfDrawableWidth,  //left
                (halfHeight + halfInnerRadius + halfHeight / 2).toInt() - halfDrawableHeight,  //top
                halfWidth.toInt() + halfDrawableWidth,  //right
                (halfHeight + halfInnerRadius + halfHeight / 2).toInt() + halfDrawableHeight
            ) //bottom
            SECTION_LEFT -> drawableToDraw.setBounds(
                (halfWidth / 2 - halfInnerRadius - halfDrawableWidth).toInt(),  //left
                halfHeight.toInt() - halfDrawableHeight,  //top
                (halfWidth / 2 - halfInnerRadius + halfDrawableWidth).toInt(),  //bottom
                halfHeight.toInt() + halfDrawableHeight
            ) //right
            SECTION_RIGHT -> drawableToDraw.setBounds(
                (halfWidth + halfInnerRadius + halfWidth / 2).toInt() - halfDrawableWidth,  //left
                halfHeight.toInt() - halfDrawableHeight,  //top
                (halfWidth + halfInnerRadius + halfWidth / 2).toInt() + halfDrawableWidth,  //right
                halfHeight.toInt() + halfDrawableHeight
            ) //bottom
            else -> return
        }
        drawableToDraw.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val x = event.x - width / 2f
        val y = event.y - height / 2f
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            val radius = Math.sqrt(x * x + y * y.toDouble()).toFloat()
            if (radius > width / 2f || radius > height / 2f) {
                // click outside the drawn view
                val cancelEvent = MotionEvent.obtain(event)
                cancelEvent.action = MotionEvent.ACTION_CANCEL
                dispatchTouchEvent(cancelEvent)
                cancelEvent.recycle()
                resetIsPressed()
                return false
            }
            if (radius < mInnerCircleRadius) {
                // click inside of inner circle
                resetIsPressed()
                mIsPressed[SECTION_CENTER] = true
            } else {
                // click on drawn view but outside inner circle
                var touchAngle = Math.toDegrees(
                    Math.atan2(
                        y.toDouble(),
                        x.toDouble()
                    )
                ).toFloat()
                if (touchAngle < 0) {
                    touchAngle += 360f
                }
                for (i in 0 until mIsPressed.size - 1) {
                    val startAngle = 45 + 90 * i % 360
                    var endAngle = (startAngle + 90) % 360
                    if (startAngle > endAngle) {
                        if (touchAngle < startAngle && touchAngle < endAngle) {
                            touchAngle += 360f
                        }
                        endAngle += 360
                    }
                    if (startAngle <= touchAngle && endAngle >= touchAngle) {
                        resetIsPressed()
                        mIsPressed[i] = true
                    }
                }
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            for (i in mIsPressed.indices) {
                if (mIsPressed[i]) {
                    if (mSectionClickedListener != null) {
                        mSectionClickedListener!!.onSectionClicked(i)
                    }
                    break
                }
            }
            resetIsPressed()
        }
        this.invalidate()
        return true
    }

    private fun resetIsPressed() {
        for (i in mIsPressed.indices) {
            mIsPressed[i] = false
        }
    }

    companion object {
        const val SECTION_BOTTOM = 0
        const val SECTION_LEFT = 1
        const val SECTION_TOP = 2
        const val SECTION_RIGHT = 3
        const val SECTION_CENTER = 4
        private const val DEFAULT_SECTION_COLOR = Color.BLUE
        private const val DEFAULT_ICON_COLOR = Color.WHITE
    }

    init {
        val a =
            context.theme.obtainStyledAttributes(attrs, R.styleable.DpadView, 0, 0)
        try {
            mDividerWidth = a.getDimensionPixelSize(R.styleable.DpadView_dividerWidth, 0)
            mDividerColor =
                a.getColor(R.styleable.DpadView_dividerColor, Color.TRANSPARENT)
            mInnerCirclePercentage = a.getFloat(R.styleable.DpadView_innerCirclePercentage, 0.0f)
            mInnerCircleColor = a.getColorStateList(R.styleable.DpadView_innerCircleColor)
            if (mInnerCircleColor == null) {
                mInnerCircleColor = ColorStateList.valueOf(DEFAULT_ICON_COLOR)
            }
            if (a.hasValue(R.styleable.DpadView_sectionColor)) {
                for (i in mSectionColors.indices) {
                    mSectionColors[i] = a.getColorStateList(R.styleable.DpadView_sectionColor)
                }
            } else {
                mSectionColors[SECTION_BOTTOM] =
                    a.getColorStateList(R.styleable.DpadView_bottomSectionColor)
                mSectionColors[SECTION_LEFT] =
                    a.getColorStateList(R.styleable.DpadView_leftSectionColor)
                mSectionColors[SECTION_TOP] =
                    a.getColorStateList(R.styleable.DpadView_topSectionColor)
                mSectionColors[SECTION_RIGHT] =
                    a.getColorStateList(R.styleable.DpadView_rightSectionColor)
            }
            mDrawablesColor = a.getColorStateList(R.styleable.DpadView_drawablesColor)
            if (mDrawablesColor == null) {
                mDrawablesColor = ColorStateList.valueOf(DEFAULT_ICON_COLOR)
            }
            mDrawableResources[SECTION_BOTTOM] =
                a.getResourceId(R.styleable.DpadView_bottomDrawable, R.drawable.ic_down)
            mDrawableResources[SECTION_LEFT] =
                a.getResourceId(R.styleable.DpadView_leftDrawable, R.drawable.ic_left)
            mDrawableResources[SECTION_TOP] =
                a.getResourceId(R.styleable.DpadView_topDrawable, R.drawable.ic_up)
            mDrawableResources[SECTION_RIGHT] =
                a.getResourceId(R.styleable.DpadView_rightDrawable, R.drawable.ic_right)
        } finally {
            a.recycle()
        }
        mXferPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mInnerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mDividerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mDividerPaint.strokeWidth = mDividerWidth.toFloat()
        mDividerPaint.color = mDividerColor
        resetIsPressed()
    }
}
*/
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