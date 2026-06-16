package com.example.plantify.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private val sharedOkHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain: Interceptor.Chain ->
            val originalRequest = chain.request()
            //biar ga di anggap bot
            val requestWithUserAgent = originalRequest.newBuilder()
                .header("User-Agent", "PlantifyApp/1.0")
                .build()
            chain.proceed(requestWithUserAgent)
        }
        .build()
}

private fun createRetrofit(baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(sharedOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

object LocationApiClient {
    private const val BASE_URL = "https://wilayah.id/api/"

    val instance: LocationApiService by lazy {
        createRetrofit(BASE_URL).create(LocationApiService::class.java)
    }
}

object NominatimApiClient {
    private const val BASE_URL = "https://nominatim.openstreetmap.org/"

    val instance: NominatimApiService by lazy {
        createRetrofit(BASE_URL).create(NominatimApiService::class.java)
    }
}

object BmkgApiClient {
    private const val BASE_URL = "https://api.bmkg.go.id/publik/"

    val instance: BmkgApiService by lazy {
        createRetrofit(BASE_URL).create(BmkgApiService::class.java)
    }
}