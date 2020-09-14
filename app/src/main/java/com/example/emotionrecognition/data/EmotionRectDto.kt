package com.example.emotionrecognition.data

import android.graphics.Rect

data class EmotionRectDto(
    val emotion: EmotionEnum,
    val rectangleBounds: Rect
)
