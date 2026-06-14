package app.feature.start

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class StartViewModel : ViewModel() {
    var uiState: StartUiState by mutableStateOf(StartUiState())
        private set
}
