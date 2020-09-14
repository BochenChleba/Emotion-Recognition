package com.example.emotionrecognition.activity

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionrecognition.data.Constants
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

abstract class OpenCVActivity : AppCompatActivity() {

    abstract fun onOpenCVInitialized()

    override fun onResume() {
        super.onResume()
        initOpenCV()
    }

    private fun initOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            Log.d(Constants.TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, openCvLoaderCallback)
        } else {
            Log.d(Constants.TAG, "OpenCV library found inside package. Using it!")
            openCvLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    private val openCvLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i(Constants.TAG, "OpenCV loaded successfully")
                    onOpenCVInitialized()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }
}
