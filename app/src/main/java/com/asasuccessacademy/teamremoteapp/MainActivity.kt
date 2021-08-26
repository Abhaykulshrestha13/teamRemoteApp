package com.asasuccessacademy.teamremoteapp

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContentResolverCompat.query
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(),UploadRequestBody.UploadCallback {
    lateinit var imageView:ImageView
    lateinit var selectImage: CardView
    lateinit var uploadImage:CardView
    lateinit var progressBar:ProgressBar
    lateinit var textPercentage:TextView
    val REQUEST_CODE_IMAGE_PICKER = 100
    var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.view_image)
        selectImage = findViewById(R.id.select_file)
        uploadImage = findViewById(R.id.upload_file)
        progressBar = findViewById(R.id.progress_bar)
        textPercentage = findViewById(R.id.percentage_text)
        selectImage.setOnClickListener {
            openImageChooser()
        }
        uploadImage.setOnClickListener{
            uploadAImage()
        }
    }

    private fun uploadAImage() {
        if(imageUri == null){
            Toast.makeText(this,"Select an Image First",Toast.LENGTH_SHORT).show()
              return
        }
        val parcelFileDescriptor = contentResolver.openFileDescriptor(imageUri!!,"r",null)?:return
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir,contentResolver.getFileName(imageUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        progressBar.progress = 0
        textPercentage.text = "0"
        val body = UploadRequestBody(file,"image",this)
        MyAPI().uploadImage(
            MultipartBody.Part.createFormData("file",file.name,body)
        ).enqueue(object : retrofit2.Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                progressBar.progress = 100
                textPercentage.setText("100")
                Toast.makeText(this@MainActivity,"Upload Successfull",Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity,"Upload failed",Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onProgressUpdate(percentage: Int) {
        progressBar.progress = percentage
        textPercentage.setText(percentage.toString())
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            startActivityForResult(it,REQUEST_CODE_IMAGE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_CODE_IMAGE_PICKER -> {
                    imageUri = data?.data
                    imageView.setImageURI(imageUri)
                }
            }
        }
    }
}