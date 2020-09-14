package com.example.emotionrecognition.analyzer

import android.graphics.Rect
import android.util.Log
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.emotionrecognition.data.Constants.TAG
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import kotlin.math.ceil
import kotlin.math.max

class ImageAnalyzer(
    private val targetSize: Size,
    private val onFaceRecognized: (List<Rect>)->Unit
) : ImageAnalysis.Analyzer {

    private val detector = FaceDetection.getClient()

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    val faceRectangles = faces
                        .mapNotNull { face ->
                            val sourceSize = Size(image.height, image.width)
                            face.boundingBox.resize(sourceSize, targetSize)
                        }
                    onFaceRecognized(faceRectangles)
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "Face detection failed", ex)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun Rect.resize(sourceSize: Size, targetSize: Size): Rect? {
        val scaleX = targetSize.width / sourceSize.width.toFloat()
        val scaleY = targetSize.height / sourceSize.height.toFloat()
        val scale = max(scaleX, scaleY)
        val scaledSize = Size(
            ceil(sourceSize.width * scale).toInt(),
            ceil(sourceSize.height * scale).toInt()
        )
        val offsetX = (targetSize.width - scaledSize.width) / 2
        val offsetY = (targetSize.height - scaledSize.height) / 2

        val resizedTop = (top * scale + offsetY).toInt()
        val resizedBottom = (bottom * scale + offsetY).toInt()
        val resizedLeft = (left * scale + offsetX).toInt()
        val resizedRight = (right * scale + offsetX).toInt()

        if (resizedTop >= targetSize.height ||
            resizedBottom <= 0 ||
            resizedLeft >= targetSize.width ||
            resizedRight <= 0) {
            return null
        }

        val targetTop =
            if (resizedTop < 0) { 0 } else { resizedTop }
        val targetBottom =
            if (resizedBottom > targetSize.height) { targetSize.height } else { resizedBottom }
        val targetLeft =
            if (resizedLeft < 0) { 0 } else { resizedLeft }
        val targetRight =
            if (resizedRight > targetSize.width) { targetSize.width } else { resizedRight }

        top = targetTop
        bottom = targetBottom
        left = targetLeft
        right = targetRight

        return this
    }
}
