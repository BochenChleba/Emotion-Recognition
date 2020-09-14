package com.example.emotionrecognition.preprocessor

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import android.graphics.Rect
import com.example.emotionrecognition.data.Constants

class OpenCVPreprocessorImpl : ImagePreprocessor {

    override fun preprocess(bitmap: Bitmap, faceBounds: Rect): FloatArray {
        val croppedBitmap = Bitmap.createBitmap(
            bitmap,
            faceBounds.left,
            faceBounds.top,
            faceBounds.width(),
            faceBounds.height()
        )
        val mat = Mat()
        Utils.bitmapToMat(croppedBitmap, mat)
        val size = Size(
            Constants.TF_INPUT_IMAGE_WIDTH_DOUBLE,
            Constants.TF_INPUT_IMAGE_HEIGHT_DOUBLE
        )

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2GRAY)
        Imgproc.resize(mat, mat, size, -1.0, -1.0, Imgproc.INTER_AREA)
        mat.convertTo(mat, CvType.CV_32F)
        Core.divide(mat, Scalar(127.5, 127.5, 127.5), mat)
        Core.subtract(mat, Scalar(1.0, 1.0, 1.0), mat)

        val floatArray = FloatArray(Constants.TF_INPUT_IMAGE_WIDTH * Constants.TF_INPUT_IMAGE_HEIGHT)
        mat.get(0, 0, floatArray)

        return floatArray
    }
}