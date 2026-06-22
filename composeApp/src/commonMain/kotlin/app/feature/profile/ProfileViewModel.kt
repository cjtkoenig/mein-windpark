package app.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.data.repository.WindParkRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: WindParkRepository) : ViewModel() {
    var uiState: ProfileUiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadProfileState()
    }

    private fun loadProfileState() {
        viewModelScope.launch {
            val attribution = repository.getSnapshotAttribution()
            val limitations = repository.getSnapshotLimitations()
            val isOffshoreEnabled = repository.isOffshoreEnabled()
            uiState = uiState.copy(
                attribution = attribution,
                limitations = limitations,
                isOffshoreEnabled = isOffshoreEnabled,
            )
        }
    }

    fun toggleOffshoreEnabled() {
        setOffshoreEnabled(!uiState.isOffshoreEnabled)
    }

    fun setOffshoreEnabled(enabled: Boolean) {
        if (uiState.isOffshoreEnabled == enabled) return
        viewModelScope.launch {
            repository.setOffshoreEnabled(enabled)
            uiState = uiState.copy(isOffshoreEnabled = enabled)
        }
    }
}
