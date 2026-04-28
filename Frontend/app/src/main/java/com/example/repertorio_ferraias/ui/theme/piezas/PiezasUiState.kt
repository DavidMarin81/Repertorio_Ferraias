package com.example.repertorio_ferraias.ui.theme.piezas

import com.example.repertorio_ferraias.models.PiezaDto

sealed interface PiezasUiState {
    data object  Loading: PiezasUiState
    data class Success(val piezas: List<PiezaDto>) : PiezasUiState
    data class Error(val message: String) : PiezasUiState
}