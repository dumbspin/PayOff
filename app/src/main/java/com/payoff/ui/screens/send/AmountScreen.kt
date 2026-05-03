package com.payoff.ui.screens.send

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.payoff.R
import com.payoff.ui.components.NumPad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountScreen(
    recipientName: String,
    onBack: () -> Unit,
    onAmountConfirmed: (Long) -> Unit // paise
) {
    var amountStr by remember { mutableStateOf("0") }
    
    val amountRupees = amountStr.toDoubleOrNull() ?: 0.0
    val amountPaise = (amountRupees * 100).toLong()
    
    val isZero = amountRupees == 0.0
    val isOverLimit = amountRupees > 5000.0
    val isNearLimit = amountRupees >= 4500.0 && !isOverLimit

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.amount_title)) },
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Paying $recipientName",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "₹$amountStr",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // Warnings (FR-04)
            AnimatedVisibility(visible = isOverLimit) {
                Text(
                    text = stringResource(R.string.amount_error_exceeded),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            AnimatedVisibility(visible = isNearLimit) {
                Text(
                    text = stringResource(R.string.amount_warning_4500),
                    color = MaterialTheme.colorScheme.tertiary, // Amber
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            AnimatedVisibility(visible = !isZero && !isOverLimit) {
                Text(
                    text = stringResource(R.string.amount_ussd_charge),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onAmountConfirmed(amountPaise) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isZero && !isOverLimit
            ) {
                Text(stringResource(R.string.amount_btn_proceed), fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            NumPad(
                onNumberClick = { num ->
                    if (amountStr == "0") amountStr = num
                    else if (amountStr.length < 5) amountStr += num
                },
                onDeleteClick = {
                    if (amountStr.length > 1) amountStr = amountStr.dropLast(1)
                    else amountStr = "0"
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
