package com.example.emotionrecognition.classifier

import com.example.emotionrecognition.data.EmotionEnum

interface EmotionClassifier {
    fun classify(input: FloatArray): EmotionEnum?
}
