package com.payoff.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payoff.R
import com.payoff.data.preferences.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val useBiometric: StateFlow<Boolean> = settingsRepository.useBiometric
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setUseBiometric(enabled)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val biometricEnabled by viewModel.useBiometric.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SettingsCategory("Core Settings")
            SettingsRow(
                icon = Icons.Default.Language,
                title = stringResource(R.string.settings_language),
                subtitle = "English"
            )
            SettingsRow(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.settings_applock_change_pin),
                subtitle = "Update your 4-digit PIN"
            )
            SettingsSwitchRow(
                icon = Icons.Default.Lock,
                title = "Use Biometric App Lock",
                checked = biometricEnabled,
                onCheckedChange = { viewModel.toggleBiometric(it) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            SettingsCategory("Limits")
            SettingsRow(
                icon = Icons.Default.MoneyOff,
                title = stringResource(R.string.settings_charge_budget),
                subtitle = "₹25.00 limit before warning"
            )

            Spacer(modifier = Modifier.height(16.dp))
            SettingsCategory("Info")
            SettingsRow(
                icon = Icons.Default.Info,
                title = stringResource(R.string.settings_about),
                subtitle = "Version 1.0.0"
            )
        }
    }
}

@Composable
private fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, subtitle: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(icon: ImageVector, title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
