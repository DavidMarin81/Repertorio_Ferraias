package com.example.repertorio_ferraias.api

import com.example.repertorio_ferraias.models.CreatePiezaRequest
import com.example.repertorio_ferraias.models.PiezaDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PiezasApi {

    @GET("api/piezas")
    suspend fun getPiezas(): List<PiezaDto>

    @POST("api/piezas")
    suspend fun createPieza(
        @Body request: CreatePiezaRequest
    ): PiezaDto
}