package com.asasuccessacademy.teamremoteapp

import android.os.Handler
import android.os.Looper
import android.webkit.ValueCallback
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import retrofit2.Response
import java.io.File
import java.io.FileInputStream

class UploadRequestBody (
    val file:File,
    val contentType:String,
    val callback: UploadCallback
    ):RequestBody() {

    interface UploadCallback{
        fun onProgressUpdate(percentage:Int)
    }
    inner class ProgressUpdate(private val uploaded:Long,private val total:Long):Runnable{
        override fun run() {
            callback.onProgressUpdate((100*uploaded/total).toInt())

        }
    }
    override fun contentType() = MediaType.parse("$contentType/*")
    override fun contentLength() = file.length()

    override fun writeTo(sink: BufferedSink) {
        val length = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        val uploaded = 0L
        fileInputStream.use {fileInput->
            var read:Int
            val handler = Handler(Looper.getMainLooper())
            while(fileInput.read(buffer).also { read = it } != -1){
                handler.post(ProgressUpdate(uploaded,length))
                sink.write(buffer,0,read)
            }
        }
    }
    companion object{
        private val DEFAULT_BUFFER_SIZE = 1048
    }
}


