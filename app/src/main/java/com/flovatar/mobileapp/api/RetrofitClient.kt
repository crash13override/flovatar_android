package com.flovatar.mobileapp.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    const val BASE_URL = "https://flovatar.com/"
    const val STAGE_BASE_URL = "https://test.flovatar.com/"

    private val client = OkHttpClient
        .Builder()
        .addInterceptor(createLogInterceptor())
        .build()

    private fun createLogInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return interceptor
    }

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)

    private val retrofitNoConverter =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)

    private val stageRetrofit =
        Retrofit.Builder()
            .baseUrl(STAGE_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)

    fun buildService(): ApiService {
        return retrofit
    }
    fun buildServiceNoConverter(): ApiService {
        return retrofitNoConverter
    }

    fun buildStageService(): ApiService {
        return stageRetrofit
    }

}
