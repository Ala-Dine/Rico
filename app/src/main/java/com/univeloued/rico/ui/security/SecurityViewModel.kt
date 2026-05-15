package com.univeloued.rico.ui.security

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

val LocalSecurityViewModel = staticCompositionLocalOf<SecurityViewModel> {
    error("No SecurityViewModel provided")
}

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val isAuthenticated: StateFlow<Boolean> = savedStateHandle.getStateFlow(AUTH_KEY, false)
    private var lastBackgroundTime: Long = 0

    fun setAuthenticated(authenticated: Boolean) {
        savedStateHandle[AUTH_KEY] = authenticated
    }

    fun onMoveToBackground() {
        lastBackgroundTime = System.currentTimeMillis()
    }

    fun onReturnToForeground(): Boolean {
        if (lastBackgroundTime == 0L) return false
        
        val backgroundDuration = System.currentTimeMillis() - lastBackgroundTime
        lastBackgroundTime = 0L // Reset
        
        // If backgrounded for more than 10 minutes, lock the app
        // 10 minutes is very generous for file pickers, cameras, or switching to a password manager
        return backgroundDuration > 600000
    }

    fun setIgnoreNextStop(ignore: Boolean) {
        savedStateHandle[IGNORE_KEY] = ignore
    }

    fun isIgnoringNextStop(): Boolean {
        return savedStateHandle.get<Boolean>(IGNORE_KEY) ?: false
    }

    fun clearIgnoreNextStop() {
        savedStateHandle[IGNORE_KEY] = false
    }

    companion object {
        private const val AUTH_KEY = "is_authenticated"
        private const val IGNORE_KEY = "ignore_next_stop"
    }
}
