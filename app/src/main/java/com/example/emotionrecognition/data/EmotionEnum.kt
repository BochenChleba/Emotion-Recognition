package com.example.emotionrecognition.data

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.example.emotionrecognition.R

enum class EmotionEnum(
    @StringRes val textResource: Int,
    @ColorRes val colorResource: Int
) {
    ANGRY(R.string.angry, R.color.colorAngry),
    DISGUST(R.string.disgust, R.color.colorDisgust),
    FEAR(R.string.fear, R.color.colorFear),
    HAPPY(R.string.happy, R.color.colorHappy),
    SAD(R.string.sad, R.color.colorSad),
    SURPRISE(R.string.surprise, R.color.colorSurprise),
    NEUTRAL(R.string.neutral, R.color.colorNeutral)
}
