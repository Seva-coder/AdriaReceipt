package mne.seva.mnereceipt.presentation.graphicActivity


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import mne.seva.mnereceipt.R


/**
 * View to show costs in segments of a circle
 */
class CircleDiagramView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val colors = mutableListOf(Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.DKGRAY)
    private val circlePosition = 0.17f

    private var parts = emptyList<Double>()

    private var groupsExist = false

    private var progress: Float = 0.0f

    private val paintCircle: Paint = Paint()
    private val paintText: Paint = Paint()
    private val paintMarker = Paint()
    private val paintNoGroupsText = Paint()
    private var maxPadding: Int = 0

    private var radius = 0f
    private var circleX = 0f
    private var circleY = 0f

    private val markerRadius = 20f
    private var markersX = 0f
    private var markersYstart = 0f

    private val beforeTextSpace = 15f
    private val underMarkerSpace = 40f
    private var textX = markersX + markerRadius + beforeTextSpace
    private var textYstart = markersYstart + 0.5f * markerRadius

    private var availableTextWidth = 0f
    private var availableWidthTextNoGroups = 0f

    private val listGroups: ArrayList<String> = ArrayList()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleDiagramView)
        val textColor = typedArray.getColor(R.styleable.CircleDiagramView_android_textColor, Color.GREEN)
        typedArray.recycle()

        paintCircle.color = Color.DKGRAY
        paintCircle.style = Paint.Style.FILL
        paintCircle.isAntiAlias = true

        paintText.color = textColor
        paintText.style = Paint.Style.FILL  // textSize will be setted in calcTextWidth
        paintText.textAlign = Paint.Align.LEFT
        paintText.isAntiAlias = true

        paintNoGroupsText.color = textColor
        paintNoGroupsText.style = Paint.Style.FILL
        paintNoGroupsText.textSize = markerRadius * 3
        paintNoGroupsText.textAlign = Paint.Align.CENTER
        paintNoGroupsText.isAntiAlias = true

        paintMarker.color = Color.RED
        paintMarker.style = Paint.Style.FILL
        paintMarker.isAntiAlias = true

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        radius = circlePosition * measuredWidth - maxPadding
        val desiredHeight = maxOf(2 * radius + paddingTop + paddingBottom,
            parts.size * (underMarkerSpace + 2 * markerRadius) + paddingTop + paddingBottom)
        val width = measureDimension(desiredWidth, widthMeasureSpec)
        val height = measureDimension(desiredHeight.toInt(), heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = result.coerceAtMost(specSize)
            }
        }
        return result
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        maxPadding = maxOf(paddingTop, paddingLeft, paddingBottom)

        circleX = circlePosition * measuredWidth
        circleY = circlePosition * measuredWidth

        markersX = 2 * circlePosition * measuredWidth + maxPadding + markerRadius
        markersYstart = maxPadding + markerRadius

        textX = markersX + markerRadius + beforeTextSpace
        textYstart = markersYstart + 0.5f * markerRadius

        availableTextWidth = measuredWidth - textX - paddingRight

        availableWidthTextNoGroups = (measuredWidth - paddingLeft - paddingRight).toFloat()

        if (groupsExist) {  // case when setGroups was called first, before onLayout
            calcTextWidth()  // it happens when tab switches
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (groupsExist) {
            drawCircleDiagram(canvas)
            drawGroupsNames(canvas)
        } else {
            drawNoGroups(canvas)
        }
    }

    fun setGroups(groupNames: List<String>, groupValues: List<Double>) {
        if (groupNames.isNotEmpty()) {
            groupsExist = true
            val total = groupValues.sum()
            listGroups.clear()
            progress = 0.0f
            groupNames.forEachIndexed { index, s ->
                val groupText = context.getString(R.string.diagram_view_group_cost, s, groupValues[index], 100 * groupValues[index] / total)
                listGroups.add(groupText)
            }
            calcTextWidth()
            parts = groupValues
            if (parts.size % colors.size == 1) {
                colors.removeLast()  // for different colors in top part of the circle
            }
        } else {
            groupsExist = false
        }
        requestLayout()
    }

    private fun calcTextWidth() {
        val bounds = Rect()
        paintText.textSize = markerRadius * 3
        val maxTextWidth = listGroups.maxOfOrNull { name ->
            paintText.getTextBounds(name, 0, name.length, bounds)
            bounds.width()
        } ?: 0
        val calculatedTextSize = paintText.textSize * availableTextWidth / maxTextWidth
        if (calculatedTextSize < paintText.textSize) {
            paintText.textSize = calculatedTextSize  // case when some string longer then available space to right from diagram
        }
    }

    fun setAnimParam(progress: Float) {
        if (groupsExist) {
            this.progress = progress
            invalidate()
        }
    }

    private fun drawCircleDiagram(canvas: Canvas) {
        var prevAngle = -90f
        val totalSum = parts.sum().toFloat()
        parts.forEachIndexed { index: Int, value: Double ->
            val color = colors[index % colors.size]
            paintCircle.color = color
            val startAngle = prevAngle
            var sweepAngle = (value.toFloat() / totalSum) * 360f * progress
            if (sweepAngle < 0.001f) sweepAngle = 0.1f  // because of the android bug with arc with small angles
            canvas.drawArc(maxPadding.toFloat(), maxPadding.toFloat(), circleX + radius,
                circleY + radius, startAngle, sweepAngle, true, paintCircle)
            prevAngle += sweepAngle
        }
    }

    private fun drawGroupsNames(canvas: Canvas) {
        var y = textYstart
        listGroups.forEachIndexed { index, text ->
            val color = colors[index % colors.size]
            paintMarker.color = color
            canvas.drawCircle(markersX, y, markerRadius, paintMarker)
            canvas.drawText(text, textX, y + 0.8f * markerRadius, paintText)

            y += underMarkerSpace + 2 * markerRadius
        }

    }

    private fun drawNoGroups(canvas: Canvas) {
        // measuring real size of text before draw
        val bounds = Rect()
        val noGroupsText = context.getString(R.string.diagram_view_no_buys)
        paintNoGroupsText.getTextBounds(noGroupsText, 0, noGroupsText.length, bounds)
        val textWidth = bounds.width()
        val calculatedTextSize = paintNoGroupsText.textSize * availableWidthTextNoGroups / textWidth
        // using smallest text size
        if (calculatedTextSize < paintNoGroupsText.textSize) {
            paintNoGroupsText.textSize = calculatedTextSize
        }

        canvas.drawColor(Color.LTGRAY)
        canvas.drawText(noGroupsText, width / 2f,height / 2f, paintNoGroupsText)
        requestLayout()
    }

}