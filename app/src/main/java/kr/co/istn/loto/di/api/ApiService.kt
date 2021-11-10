package kr.co.istn.loto.di.api

import kr.co.istn.loto.di.model.TEST
import retrofit2.http.*
import javax.inject.Singleton

@Singleton
interface ApiService {

    @POST("/test")
    @Headers("Content-Type: application/json")
    suspend fun getTest(@Query("test") test: String): String

}