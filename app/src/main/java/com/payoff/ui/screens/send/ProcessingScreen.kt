package com.payoff.ui.screens.send

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.payoff.R
import com.payoff.ussd.UssdState

@Composable
fun ProcessingScreen(
    currentUssdState: UssdState,
    onFinished: () -> Unit
) {
    // Navigate out automatically when reached a terminal state
    LaunchedEffect(currentUssdState) {
        if (currentUssdState == UssdState.SUCCESS || 
            currentUssdState == UssdState.FAILURE || 
            currentUssdState == UssdState.TIMEOUT || 
            currentUssdState == UssdState.INTERRUPTED) {
            onFinished()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Pulsing Rings Animation
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500),
                repeatMode = RepeatMode.Restart
            ),
            label = "scale"
        )
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500),
                repeatMode = RepeatMode.Restart
            ),
            label = "alpha"
        )

        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Text("USSD", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.processing_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        val stepMessage = when(currentUssdState) {
            UssdState.IDLE, UssdState.LANG_SELECT, UssdState.BANK_SELECT -> stringResource(R.string.processing_step_dialing)
            UssdState.MAIN_MENU, UssdState.RECIPIENT_INPUT -> stringResource(R.string.processing_step_connected)
            UssdState.AMOUNT_INPUT, UssdState.PIN_INPUT -> stringResource(R.string.processing_step_sending)
            UssdState.AWAITING_RESULT -> stringResource(R.string.processing_step_confirming)
            else -> "..."
        }

        Text(
            text = stepMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.processing_do_not_close),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}
