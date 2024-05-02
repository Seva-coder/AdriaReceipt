package mne.seva.mnereceipt.presentation.graphicActivity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View


class BarView@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val rectPaint = Paint()
    private val textPaint = Paint()
    private val rectPaintNew = Paint()
    private val bounds = Rect()

    private var cost = 0.0
    private var text = ""

    private var h = 0f

    init {
        textPaint.color = Color.GRAY
        textPaint.isAntiAlias = true
        textPaint.textSize = 55f
        textPaint.textAlign = Paint.Align.LEFT

        rectPaint.color = Color.BLUE
        rectPaint.isAntiAlias = true

        rectPaintNew.color = Color.DKGRAY
        rectPaintNew.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 60
        val desiredHeight = 150

        val specModWidth = MeasureSpec.getMode(widthMeasureSpec)
        val specSizeWidth = MeasureSpec.getSize(widthMeasureSpec)

        val width = when(specModWidth) {
            MeasureSpec.EXACTLY -> specSizeWidth
            MeasureSpec.AT_MOST -> minOf(desiredWidth, specSizeWidth)
            else -> desiredWidth
        }

        val specModHeight = MeasureSpec.getMode(heightMeasureSpec)
        val specSizeHeight = MeasureSpec.getSize(heightMeasureSpec)

        val height = when(specModHeight) {
            MeasureSpec.EXACTLY -> specSizeHeight
            MeasureSpec.AT_MOST -> minOf(desiredHeight, specSizeHeight)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        h = (measuredHeight - paddingBottom - paddingTop) * cost.toFloat()

        // canvas rotated -> width and height are changed
        val availableTextHeight = 0.6f * (measuredWidth - paddingLeft - paddingRight)
        val availableTextWidth  = (measuredHeight - paddingTop - paddingBottom).toFloat()

        textPaint.getTextBounds(text, 0, text.length, bounds)
        val currentTextHeight = bounds.height()
        val currentTextWith = bounds.width()
        val calcByHeightTextSize = textPaint.textSize * availableTextHeight / currentTextHeight
        val calcByWidthTextSize = textPaint.textSize * availableTextWidth / currentTextWith

        textPaint.textSize = minOf(calcByHeightTextSize, calcByWidthTextSize)
    }


    fun setParams(cost: Double, text: String) {
        this.cost = cost
        this.text = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(0f + paddingLeft, measuredHeight - (paddingBottom + h), (measuredWidth - paddingRight).toFloat(), (measuredHeight - paddingBottom).toFloat(), 15f, 15f, rectPaint)
        canvas.save()
        canvas.translate(measuredWidth - paddingRight.toFloat() - 0.3f * textPaint.textSize,
            measuredHeight - 2 * paddingBottom.toFloat())
        canvas.rotate(-90f)
        canvas.drawText(text, 0f, 0f, textPaint)
        canvas.restore()
    }


}