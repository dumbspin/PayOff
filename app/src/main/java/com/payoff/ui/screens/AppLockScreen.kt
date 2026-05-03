package com.payoff.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.payoff.R
import com.payoff.ui.components.NumPad
import com.payoff.ui.components.PinDots
import kotlinx.coroutines.delay

@Composable
fun AppLockScreen(
    onUnlockSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // Mock verification for the prototype. In real app, check against DataStore hash.
    LaunchedEffect(pin) {
        if (pin.length == 4) {
            delay(200) // fake verification delay
            if (pin == "1234") { // Mock PIN for demo
                onUnlockSuccess()
            } else {
                isError = true
                delay(400)
                pin = ""
                isError = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.applock_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        PinDots(
            pinLength = 4,
            currentLength = pin.length,
            isError = isError
        )
        
        if (isError) {
            Text(
                text = stringResource(R.string.applock_mismatch),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = { /* Biometric prompt integration */ }) {
            Text(stringResource(R.string.applock_biometric_prompt))
        }

        Spacer(modifier = Modifier.height(24.dp))

        NumPad(
            onNumberClick = { if (pin.length < 4) pin += it },
            onDeleteClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
