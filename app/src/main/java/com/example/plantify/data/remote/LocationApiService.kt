package com.example.plantify.data.remote

import com.example.plantify.data.remote.model.BmkgWeatherResponse
import com.example.plantify.data.remote.model.NominatimResponse
import com.example.plantify.data.remote.model.NominatimSearchResponse
import com.example.plantify.data.remote.model.WilayahResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface LocationApiService {
    @GET("provinces.json")
    suspend fun getProvinces(): WilayahResponse

    @GET("regencies/{provinceId}.json")
    suspend fun getRegencies(@Path("provinceId") provinceId: String): WilayahResponse

    @GET("districts/{regencyId}.json")
    suspend fun getDistricts(@Path("regencyId") regencyId: String): WilayahResponse

    @GET("villages/{districtId}.json")
    suspend fun getVillages(@Path("districtId") districtId: String): WilayahResponse
}

interface NominatimApiService {
    @Headers("User-Agent: PlantifyApp/1.0")
    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("format") format: String = "json",
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): NominatimResponse

    @Headers("User-Agent: PlantifyApp/1.0")
    @GET("search")
    suspend fun searchGeocode(
        @Query("q") address: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 1
    ): List<NominatimSearchResponse>
}

interface BmkgApiService {
    @GET("prakiraan-cuaca")
    suspend fun getBmkgWeather(
        @Query("adm4") adm4: String
    ): BmkgWeatherResponse
}