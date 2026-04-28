package com.example.repertorio_ferraias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.repertorio_ferraias.screen.PiezasScreen
import com.example.repertorio_ferraias.ui.theme.Repertorio_FerraiasTheme
import com.example.repertorio_ferraias.viewmodel.PiezasViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Repertorio_FerraiasTheme {
                val piezasViewModel: PiezasViewModel = viewModel()
                PiezasScreen(viewModel = piezasViewModel)
            }
        }
    }
}
