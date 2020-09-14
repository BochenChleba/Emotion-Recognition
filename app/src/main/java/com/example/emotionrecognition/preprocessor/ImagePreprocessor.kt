package com.example.emotionrecognition.preprocessor

import android.graphics.Bitmap
import android.graphics.Rect

interface ImagePreprocessor {
    fun preprocess(bitmap: Bitmap, faceBounds: Rect): FloatArray
}