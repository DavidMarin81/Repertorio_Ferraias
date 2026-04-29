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
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PiezasScreen(
    viewModel: PiezasViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var piezaEnEdicion by remember { mutableStateOf<PiezaDto?>(null) }
    var piezaAEliminar by remember { mutableStateOf<PiezaDto?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    piezaEnEdicion = null
                    showAddSheet = true
                }
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
                        .consumeWindowInsets(innerPadding),
                    onEdit = { pieza ->
                        piezaEnEdicion = pieza
                        showAddSheet = true
                    },
                    onDelete = { pieza ->
                        piezaAEliminar = pieza
                    }
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
                piezaAEditar = piezaEnEdicion,
                onDismiss = {
                    showAddSheet = false
                    piezaEnEdicion = null
                },
                onSave = { titulo, estilo, puntuacion ->
                    if (piezaEnEdicion == null) {
                        viewModel.crearPieza(
                            titulo = titulo,
                            estilo = estilo,
                            puntuacion = puntuacion
                        )
                    } else {
                        viewModel.actualizarPieza(
                            id = piezaEnEdicion!!.id,
                            titulo = titulo,
                            estilo = estilo,
                            puntuacion = puntuacion
                        )
                    }

                    showAddSheet = false
                    piezaEnEdicion = null
                }
            )

        }
    }

    piezaAEliminar?.let { pieza ->
        AlertDialog(
            onDismissRequest = { piezaAEliminar = null },
            title = {
                Text("Eliminar pieza")
            },
            text = {
                Text("¿Quieres eliminar \"${pieza.titulo}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.borrarPieza(pieza.id)
                        piezaAEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { piezaAEliminar = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
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
    modifier: Modifier = Modifier,
    onEdit: (PiezaDto) -> Unit,
    onDelete: (PiezaDto) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(piezas) { pieza ->
            PiezaItem(
                pieza = pieza,
                onEdit = { onEdit(pieza) },
                onDelete = { onDelete(pieza) }
            )
        }
    }
}

@Composable
fun PiezaItem(
    pieza: PiezaDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colorFondo = colorFondoPorEstilo(pieza.estilo)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorFondo
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = pieza.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "⭐".repeat(pieza.puntuacion),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (pieza.estilo == "Muineira") "Muiñeira" else pieza.estilo,
                    style = MaterialTheme.typography.bodyMedium
                )

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar pieza"
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar pieza"
                        )
                    }
                }
            }

        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPiezaSheet(
    piezaAEditar: PiezaDto?,
    onDismiss: () -> Unit,
    onSave: (String, String, Int) -> Unit
) {
    var titulo by remember { mutableStateOf(piezaAEditar?.titulo ?: "") }
    var estiloSeleccionado by remember {
        mutableStateOf(
            piezaAEditar?.estilo?.let { Estilo.valueOf(it) }
        )
    }
    var expanded by remember { mutableStateOf(false) }
    var puntuacionSeleccionada by remember {
        mutableStateOf(piezaAEditar?.puntuacion ?: 1)
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (piezaAEditar == null) "Nueva pieza" else "Editar pieza",
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
                Text(if (piezaAEditar == null) "Guardar" else "Actualizar")
            }

        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

fun colorFondoPorEstilo(estilo: String): Color {
    return when (estilo) {
        "Muineira" -> Color(0xFF2E7D32).copy(alpha = 0.18f)
        "Xota" -> Color(0xFFD84315).copy(alpha = 0.18f)
        "Pasodobre" -> Color(0xFF1565C0).copy(alpha = 0.18f)
        "Rumba" -> Color(0xFF8E24AA).copy(alpha = 0.18f)
        "Pasacorredoira" -> Color(0xFF00897B).copy(alpha = 0.18f)
        "Mazurca" -> Color(0xFFF9A825).copy(alpha = 0.18f)
        "Outros" -> Color(0xFF6D4C41).copy(alpha = 0.18f)
        else -> Color.LightGray
    }
}

