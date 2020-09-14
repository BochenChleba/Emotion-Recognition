package com.example.emotionrecognition.data

import android.Manifest

object Constants {
    const val TAG = "EmotionRecognition"
    const val REQUEST_CODE_PERMISSIONS = 1
    const val TF_MODEL_FILE_NAME = "model_facial_expression_quant.tflite"

    const val OVERLAY_TEXT_SIZE = 64f
    const val OVERLAY_TEXT_WIDTH = 1f
    const val OVERLAY_TEXT_OFFSET = 32f
    const val OVERLAY_RECT_WIDTH = 5f

    const val TF_INPUT_IMAGE_WIDTH = 64
    const val TF_INPUT_IMAGE_HEIGHT = 64
    const val TF_INPUT_IMAGE_WIDTH_DOUBLE = TF_INPUT_IMAGE_WIDTH.toDouble()
    const val TF_INPUT_IMAGE_HEIGHT_DOUBLE = TF_INPUT_IMAGE_HEIGHT.toDouble()

    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
}
