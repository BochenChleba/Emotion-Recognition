package com.example.emotionrecognition.preprocessor

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import com.example.emotionrecognition.data.Constants

class CustomPreprocessorImpl :
    ImagePreprocessor {

    override fun preprocess(bitmap: Bitmap, faceBounds: Rect): FloatArray {
        val croppedBitmap = Bitmap.createBitmap(
            bitmap,
            faceBounds.left,
            faceBounds.top,
            faceBounds.width(),
            faceBounds.height()
        )
        val scaledBitmap = Bitmap.createScaledBitmap(
            croppedBitmap,
            Constants.TF_INPUT_IMAGE_WIDTH,
            Constants.TF_INPUT_IMAGE_HEIGHT,
            true
        )
        return IntArray(Constants.TF_INPUT_IMAGE_WIDTH * Constants.TF_INPUT_IMAGE_HEIGHT)
            .also { pixelArray ->
                scaledBitmap.getPixels(
                    pixelArray,
                    0,
                    scaledBitmap.width,
                    0,
                    0,
                    scaledBitmap.width,
                    scaledBitmap.height
                )
            }
            .map { pixel ->
                pixel.toGrey() / 8290687.5f + 1
            }
            .toFloatArray()
    }

    private fun Int.toGrey(): Int {
        val alpha = Color.alpha(this)
        val red = Color.red(this)
        val green = Color.green(this)
        val blue = Color.blue(this)
        val averageColor = (red + green + blue)/3
        return (alpha shl 24) or
                (averageColor shl 16) or
                (averageColor shl 8) or
                (averageColor)
    }
}
