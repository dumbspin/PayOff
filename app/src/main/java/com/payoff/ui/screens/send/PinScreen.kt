package com.payoff.ui.screens.send

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.payoff.R
import com.payoff.ui.components.NumPad
import com.payoff.ui.components.PinDots

@Composable
fun PinScreen(
    onPinComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // SEC-05: Block screen capture for PIN screen
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? ComponentActivity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    var pin by remember { mutableStateOf("") }
    val pinLength = 6 // Most banks use 6, some use 4. Hardcoded to 6 for demo

    LaunchedEffect(pin) {
        if (pin.length == pinLength) {
            onPinComplete(pin)
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
            text = stringResource(R.string.pin_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.pin_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        PinDots(
            pinLength = pinLength,
            currentLength = pin.length
        )
        
        Spacer(modifier = Modifier.weight(1f))

        NumPad(
            onNumberClick = { if (pin.length < pinLength) pin += it },
            onDeleteClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
