package com.codecamp.tripcplaner.model.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInit {
    private val okHttpClient = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS).build()

    private val retrofit = Retrofit.Builder().baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()

    val openAIChatApi: OpenAIChatApi = retrofit.create(OpenAIChatApi::class.java)
}
