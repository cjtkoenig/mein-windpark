package app.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    var uiState: ProfileUiState by mutableStateOf(ProfileUiState())
        private set

    fun setNotificationsEnabled(enabled: Boolean) {
        uiState = uiState.copy(notificationsEnabled = enabled)
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        uiState = uiState.copy(darkModeEnabled = enabled)
    }

    fun onLanguageClick() = Unit

    fun onPrivacyClick() = Unit

    fun onLogoutClick() = Unit
}
