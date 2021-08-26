package com.asasuccessacademy.teamremoteapp

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MyAPI {

    @Multipart
    @POST("upload")
    fun uploadImage(
        @Part file:MultipartBody.Part
    ): Call<UploadResponse>
    companion object{
        operator fun invoke():MyAPI{
            return Retrofit.Builder()
                .baseUrl("https://api.anonfiles.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyAPI::class.java)
        }
    }
}
