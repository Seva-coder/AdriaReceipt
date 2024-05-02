package mne.seva.mnereceipt.presentation.graphicActivity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import mne.seva.mnereceipt.R


/**
 * View to show costs in "bars" in scrollable space
 */
class BarViewDiagram @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RecyclerView(context, attrs, defStyleAttr) {

    private val paintLines = Paint()
    private val paintText = Paint()
    private val path = Path()

    init {
        paintLines.color = Color.GRAY
        paintLines.style = Paint.Style.STROKE
        paintLines.pathEffect = DashPathEffect(floatArrayOf(50f, 50f), 0f)
        paintLines.strokeWidth = 6f
        paintLines.alpha = 200

        paintText.isAntiAlias = true
        paintText.color = Color.GREEN
        paintText.textSize = 55f
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        path.moveTo(0f, 0.5f * height)
        path.lineTo(width.toFloat(), 0.5f * height)
    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)

        val maxCost = (adapter as BarAdapter).maxCost

        canvas.drawPath(path, paintLines)

        canvas.drawText(context.getString(R.string.cost_in_euro, maxCost), 0f, paintText.textSize, paintText)
        canvas.drawText(context.getString(R.string.cost_in_euro, maxCost / 2), 0f, 0.5f * height - 6f, paintText)
        canvas.drawText(context.getString(R.string.cost_in_euro, 0f), 0f, height - 4f, paintText)
    }

}