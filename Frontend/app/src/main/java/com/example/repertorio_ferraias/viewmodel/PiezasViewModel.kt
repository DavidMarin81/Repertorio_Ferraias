package com.example.repertorio_ferraias.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repertorio_ferraias.models.CreatePiezaRequest
import com.example.repertorio_ferraias.repository.PiezasRepository
import com.example.repertorio_ferraias.ui.theme.piezas.PiezasUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PiezasViewModel : ViewModel() {

    private val repository = PiezasRepository()

    private val _uiState = MutableStateFlow<PiezasUiState>(PiezasUiState.Loading)
    val uiState : StateFlow<PiezasUiState> = _uiState.asStateFlow()

    init {
        cargarPiezas()
    }

    fun cargarPiezas() {
        viewModelScope.launch {
            _uiState.value = PiezasUiState.Loading

            try {
                val piezas = repository.getPiezas()
                _uiState.value = PiezasUiState.Success(piezas)
            } catch (e: Exception) {
                _uiState.value = PiezasUiState.Error(
                    e.message ?: "Ha ocurrido un error al cargar las piezas"
                )
            }
        }
    }

    fun crearPieza(
        titulo: String,
        estilo: String,
        puntuacion: Int
    ) {
        viewModelScope.launch {
            try {
                val piezasActuales = repository.getPiezas()
                val nuevoId = (piezasActuales.maxOfOrNull { it.id } ?: 0) + 1

                val request = CreatePiezaRequest(
                    id = nuevoId,
                    titulo = titulo,
                    estilo = estilo,
                    puntuacion = puntuacion
                )

                repository.createPieza(request)
                cargarPiezas()
            } catch (e: Exception) {
                _uiState.value = PiezasUiState.Error(
                    e.message ?: "Ha ocurrido un error al crear la pieza"
                )
            }
        }
    }

}