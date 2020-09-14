package com.example.emotionrecognition.activity

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.emotionrecognition.R
import com.example.emotionrecognition.analyzer.ImageAnalyzer
import com.example.emotionrecognition.classifier.EmotionClassifier
import com.example.emotionrecognition.classifier.EmotionClassifierImpl
import com.example.emotionrecognition.data.Constants
import com.example.emotionrecognition.data.Constants.REQUEST_CODE_PERMISSIONS
import com.example.emotionrecognition.data.Constants.REQUIRED_PERMISSIONS
import com.example.emotionrecognition.data.EmotionRectDto
import com.example.emotionrecognition.preprocessor.ImagePreprocessor
import com.example.emotionrecognition.preprocessor.OpenCVPreprocessorImpl
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import splitties.toast.toast
import java.util.concurrent.Executors
import android.util.Size as JSize

class MainActivity : OpenCVActivity() {
    private val processingExecutor = Executors.newCachedThreadPool()
    private val compositeDisposable = CompositeDisposable()
    private var imagePreprocessor: ImagePreprocessor = OpenCVPreprocessorImpl()
    private val emotionClassifier: EmotionClassifier by lazy {
        val fileDescriptor = assets.openFd(Constants.TF_MODEL_FILE_NAME)
        EmotionClassifierImpl(fileDescriptor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onOpenCVInitialized() {
        startCameraOrRequestPermissions()
    }

    private fun startCameraOrRequestPermissions() {
        if (permissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun permissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun startCamera() {
        val cameraFuture = ProcessCameraProvider.getInstance(this)
        cameraFuture.addListener(Runnable {
            val cameraProvider = cameraFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val useCases = arrayOf(getPreviewUseCase(), getImageAnalysisUseCase())
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, *useCases)
            } catch (ex: Throwable) {
                Log.e(Constants.TAG, "Binding failed", ex)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun getPreviewUseCase() = Preview.Builder()
        .build()
        .also { preview ->
            preview.setSurfaceProvider(cameraView.createSurfaceProvider())
        }

    private fun getImageAnalysisUseCase() = ImageAnalysis.Builder()
        .build()
        .also { analysis ->
            val targetSize = JSize(cameraView.width, cameraView.height)
            val imageAnalyzer =
                ImageAnalyzer(targetSize) { faceRectList ->
                    recognizeEmotions(faceRectList)
                }
            analysis.setAnalyzer(processingExecutor, imageAnalyzer)
        }

    private fun recognizeEmotions(faceRectList: List<Rect>) {
        Single.just(faceRectList)
            .map { faceRect ->
                val bitmap: Bitmap = cameraView.bitmap!!
                faceRect.mapNotNull { rect ->
                    val preprocessedImage = imagePreprocessor.preprocess(bitmap, rect)
                    val recognizedEmotion = emotionClassifier.classify(preprocessedImage)
                        ?: return@mapNotNull null
                    EmotionRectDto(
                        recognizedEmotion,
                        rect
                    )
                }
            }
            .subscribeOn(Schedulers.from(processingExecutor))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ emotions ->
                emotionOverlay.update(emotions.toTypedArray())
            }, { exception ->
                emotionOverlay.update(emptyArray())
                exception.printStackTrace()
            })
            .let { compositeDisposable.add(it) }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (permissionsGranted()) {
                startCamera()
            } else {
                toast(R.string.permissions_not_granted_toast)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        processingExecutor.shutdown()
    }

}
