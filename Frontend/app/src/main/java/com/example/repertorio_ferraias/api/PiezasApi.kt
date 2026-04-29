package com.example.repertorio_ferraias.api

import com.example.repertorio_ferraias.models.CreatePiezaRequest
import com.example.repertorio_ferraias.models.PiezaDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.DELETE

interface PiezasApi {

    @GET("api/piezas")
    suspend fun getPiezas(): List<PiezaDto>

    @POST("api/piezas")
    suspend fun createPieza(
        @Body request: CreatePiezaRequest
    ): PiezaDto

    @PUT("api/piezas/{id}")
    suspend fun updatePieza(
        @Path("id") id: String,
        @Body request: CreatePiezaRequest
    )

    @DELETE("api/piezas/{id}")
    suspend fun deletePieza(
        @Path("id") id: String
    )
}