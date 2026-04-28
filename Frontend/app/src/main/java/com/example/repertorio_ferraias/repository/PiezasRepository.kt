package com.example.repertorio_ferraias.repository

import com.example.repertorio_ferraias.models.CreatePiezaRequest
import com.example.repertorio_ferraias.models.PiezaDto
import com.example.repertorio_ferraias.network.RetrofitClient

class PiezasRepository {
    suspend fun getPiezas(): List<PiezaDto> {
        return RetrofitClient.api.getPiezas()
    }

    suspend fun createPieza(request: CreatePiezaRequest): PiezaDto {
        return RetrofitClient.api.createPieza(request)
    }
}