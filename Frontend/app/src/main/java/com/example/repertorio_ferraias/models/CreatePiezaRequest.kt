package com.example.repertorio_ferraias.models

data class CreatePiezaRequest(
    val id: Int,
    val titulo: String,
    val estilo: String,
    val puntuacion: Int
)