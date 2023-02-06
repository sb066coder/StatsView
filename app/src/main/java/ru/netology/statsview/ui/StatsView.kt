package ru.netology.statsview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

open class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes,
) {

    private var textSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 5)
    var colors = emptyList<Int>()
    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth.toFloat()).toInt()
            colors = listOf(
                getColor(R.styleable.StatsView_color1, generateRandomColor()),
                getColor(R.styleable.StatsView_color2, generateRandomColor()),
                getColor(R.styleable.StatsView_color3, generateRandomColor()),
                getColor(R.styleable.StatsView_color4, generateRandomColor()),
            )
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }
    var radius = 0F
    var center = PointF()
    var oval = RectF()
    val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@StatsView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }
        var startAngle = -90F
        data.forEachIndexed { index, datum ->
            val angle = datum / data.sum() * 360F // Взятие долей от полного круга HV Smart StatsView
            paint.color = colors.getOrElse(index) { generateRandomColor() }
            canvas.drawArc(oval, startAngle, angle, false, paint)
            startAngle += angle
        }
        paint.color = colors[0] // Взятие исходного цвета
        canvas.drawPoint(center.x,center.y - radius, paint) // Добавление замыкающей точки HV Dot

        canvas.drawText(
            "%.2f%%".format(100F), // Распределение значений всегда даст 100% HV Smart StatsView
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
    }

    fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}

class StatsViewNotFilled @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : StatsView(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes,
) {
    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }
        var startAngle = -90F
        paint.color =  0xFFDDDDDD.toInt()
        canvas.drawCircle(center.x, center.y, radius, paint)
        data.forEachIndexed { index, datum ->
            val angle = datum * 360F // Исходный вариант и HV Not Filled*
            paint.color = colors.getOrElse(index) { generateRandomColor() }
            canvas.drawArc(oval, startAngle, angle, false, paint)
            startAngle += angle
        }
        paint.color = colors[0] // Взятие исходного цвета
        canvas.drawPoint(center.x,center.y - radius, paint) // Добавление замыкающей точки HV Dot

        canvas.drawText(
            "%.2f%%".format(data.sum() * 100), // Исходный вариант и HV Not Filled*
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
    }
}
