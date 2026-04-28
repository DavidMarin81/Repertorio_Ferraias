package com.example.repertorio_ferraias.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.repertorio_ferraias.models.PiezaDto
import com.example.repertorio_ferraias.ui.theme.piezas.PiezasUiState
import com.example.repertorio_ferraias.viewmodel.PiezasViewModel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.repertorio_ferraias.models.Estilo
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.consumeWindowInsets


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PiezasScreen(
    viewModel: PiezasViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir pieza"
                )
            }
        }
    ) { innerPadding ->
        when (uiState) {
            is PiezasUiState.Loading -> {
                LoadingScreen(
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                )
            }

            is PiezasUiState.Error -> {
                val errorState = uiState as PiezasUiState.Error
                ErrorScreen(
                    message = errorState.message,
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                )
            }

            is PiezasUiState.Success -> {
                val successState = uiState as PiezasUiState.Success
                PiezasList(
                    piezas = successState.piezas,
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                )
            }
        }
    }

    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState
        ) {
            AddPiezaSheet(
                onDismiss = { showAddSheet = false },
                onSave = { titulo, estilo, puntuacion ->
                    viewModel.crearPieza(
                        titulo = titulo,
                        estilo = estilo,
                        puntuacion = puntuacion
                    )
                    showAddSheet = false
                }
            )
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun PiezasList(
    piezas: List<PiezaDto>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(piezas) { pieza ->
            PiezaItem(pieza)
        }
    }
}

@Composable
fun PiezaItem(pieza: PiezaDto) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row {
                Text(
                    text = pieza.titulo + " ⭐".repeat(pieza.puntuacion),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = "${pieza.estilo}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPiezaSheet(
    onDismiss: () -> Unit,
    onSave: (String, String, Int) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var estiloSeleccionado by remember { mutableStateOf<Estilo?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var puntuacionSeleccionada by remember { mutableStateOf(1) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Nueva pieza",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Titulo") },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = estiloSeleccionado?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Estilo") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Estilo.entries.forEach { estilo ->
                    DropdownMenuItem(
                        text = { Text(estilo.name) },
                        onClick = {
                            estiloSeleccionado = estilo
                            expanded = false
                        }
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Puntuacion",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (estrella in 1..5) {
                    Icon(
                        imageVector = if (estrella <= puntuacionSeleccionada) {
                            Icons.Filled.Star
                        } else {
                            Icons.Outlined.Star
                        },
                        contentDescription = "Seleccionar $estrella estrellas",
                        tint = if (estrella <= puntuacionSeleccionada) {
                            Color(0xFFFFC107)
                        } else {
                            Color.Gray
                        },
                        modifier = Modifier.clickable {
                            puntuacionSeleccionada = estrella
                        }
                    )
                }
            }
            Text(
                text = "Seleccion: $puntuacionSeleccionada/5",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(
                onClick = {
                    when {
                        titulo.isBlank() -> {
                            errorMessage = "Debes escribir un titulo"
                        }
                        estiloSeleccionado == null -> {
                            errorMessage = "Debes seleccionar un estilo"
                        }
                        else -> {
                            errorMessage = null
                            onSave(
                                titulo,
                                estiloSeleccionado!!.name,
                                puntuacionSeleccionada
                            )
                        }
                    }
                }
            ) {
                Text("Guardar")
            }

        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

