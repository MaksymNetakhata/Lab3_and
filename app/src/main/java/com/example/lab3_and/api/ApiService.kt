package com.example.lab3_and.api
import com.example.lab3_and.models.ApiItem
import retrofit2.http.GET;


interface ApiService {
    @GET("todos/2")
    suspend fun getItem(): ApiItem
}