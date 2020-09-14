package com.example.emotionrecognition.classifier

import android.content.res.AssetFileDescriptor
import com.example.emotionrecognition.data.EmotionEnum
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class EmotionClassifierImpl(private val fileDescriptor: AssetFileDescriptor)
    : EmotionClassifier {
    private val modelBuffer: MappedByteBuffer by lazy { loadModelFile() }

    private fun loadModelFile(): MappedByteBuffer {
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long = fileDescriptor.startOffset
        val declaredLength: Long = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    override fun classify(input: FloatArray): EmotionEnum? {
        val interpreter = Interpreter(modelBuffer)
        val inputShape = interpreter.getInputTensor(0).shape()
        val outputShape = interpreter.getOutputTensor(0).shape()
        val tensorInputBuffer = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)
        val tensorOutputBuffer = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)

        tensorInputBuffer.loadArray(input)
        interpreter.run(tensorInputBuffer.buffer, tensorOutputBuffer.buffer)

        val output = tensorOutputBuffer.floatArray
        val max = output.max() ?: return null
        val index = output.indexOf(max)
        if (index == -1) {
            return null
        }
        interpreter.close()
        return EmotionEnum.values()[index]
    }
}
