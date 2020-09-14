package com.example.emotionrecognition.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.emotionrecognition.data.Constants
import com.example.emotionrecognition.data.EmotionEnum
import com.example.emotionrecognition.data.EmotionRectDto
import org.koin.core.KoinComponent

class EmotionOverlay(context: Context?, attrSet: AttributeSet?)
    : View(context, attrSet), KoinComponent {

    private var emotionRectangles: Array<EmotionRectDto> = emptyArray()
    private val paintMap: Map<EmotionEnum, Pair<Paint, Paint>> by lazy {
        EmotionEnum.values()
            .map { emotionEnum ->
                Pair(
                    emotionEnum,
                    Pair(
                        providePaintForBox(emotionEnum.colorResource),
                        providePaintForText(emotionEnum.colorResource)
                    )
                )
            }
            .toMap()
    }

    private fun providePaintForBox(paintColor: Int) = Paint().apply {
        color = context.getColor(paintColor)
        strokeWidth = Constants.OVERLAY_RECT_WIDTH
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    private fun providePaintForText(paintColor: Int) = Paint().apply {
        color = context.getColor(paintColor)
        textSize = Constants.OVERLAY_TEXT_SIZE
        strokeWidth = Constants.OVERLAY_TEXT_WIDTH
        strokeCap = Paint.Cap.SQUARE
        style = Paint.Style.FILL
    }

    private val defaultPaint = Pair(Paint(), Paint())

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        emotionRectangles.forEach { emotionRect ->
            val paints = paintMap.getOrElse(emotionRect.emotion) { defaultPaint }
            canvas.drawRect(emotionRect.rectangleBounds, paints.first)
            val text = context.getString(emotionRect.emotion.textResource)
            val textX = emotionRect.rectangleBounds.left + Constants.OVERLAY_TEXT_OFFSET
            val textY = emotionRect.rectangleBounds.bottom + Constants.OVERLAY_TEXT_SIZE
            canvas.drawText(text, textX, textY, paints.second)
        }
    }

    fun update(emotionRects: Array<EmotionRectDto>) {
        this.emotionRectangles = emotionRects
        invalidate()
    }
}