package ru.netology.statsview.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
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
    private var colors = emptyList<Int>()
    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null
    private var animMode = 0
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
            animMode = getInteger(R.styleable.StatsView_animMode, 0) // Решение HW Attributes*
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            update()
        }

    private var startAngle = 0F

    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F
        valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 2000
            interpolator = LinearInterpolator()
        }.also { it.start() }
    }

    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()
    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    private val textPaint = Paint(
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
        startAngle = -90F
        data.forEachIndexed { index, datum ->
            when (animMode) { // Решение HW Attributes*
                0 -> {
                    val angle = datum * 360F
                    paint.color =
                        colors.getOrElse(index) { generateRandomColor() } // FIXME: Random дает мерцание при data.size > colors.size
                    canvas.drawArc(
                        oval,
                        startAngle,
                        angle * progress,
                        false,
                        paint
                    )
                    startAngle += angle
                }
                1 -> {
                    if (progress < 0) return@forEachIndexed // Решение HW Sequential*
                    else {
                        val angle = datum * 360F
                        paint.color = colors.getOrElse(index) { generateRandomColor() } // FIXME: Random дает мерцание при data.size > colors.size
                        canvas.drawArc(
                            oval,
                            startAngle,
                            angle * if (progress <= datum) progress / datum else 1F, // if (progress <= datum) progress / datum else 1F Решение HW Sequential*
                            false,
                            paint
                        )
                        progress -= datum // Решение HW Sequential*
                        startAngle += angle
                    }
                }
            }
        }

        paint.color = colors[0] // Взятие исходного цвета
        canvas.drawPoint(center.x,center.y - radius, paint) // Добавление замыкающей точки HV Dot

        canvas.drawText(
            "%.2f%%".format(data.sum() * 100F),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
    }

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}
