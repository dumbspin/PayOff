package com.payoff.ui.screens.send

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.payoff.R
import com.payoff.data.db.dao.TransactionDao
import com.payoff.data.model.Transaction
import com.payoff.data.model.TransactionStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ResultScreen(
    transactionId: String,
    transactionDao: TransactionDao,
    onDone: () -> Unit,
    onRetry: () -> Unit
) {
    var tx by remember { mutableStateOf<Transaction?>(null) }
    
    LaunchedEffect(transactionId) {
        tx = transactionDao.getById(transactionId)
    }

    if (tx == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val t = tx!!
    val isSuccess = t.status == TransactionStatus.SUCCESS
    val isPending = t.status == TransactionStatus.PENDING || t.status == TransactionStatus.UNKNOWN
    
    val color = when {
        isSuccess -> MaterialTheme.colorScheme.primary
        isPending -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
    val icon = when {
        isSuccess -> Icons.Default.Check
        isPending -> Icons.Default.Warning
        else -> Icons.Default.Close
    }
    val title = when {
        isSuccess -> stringResource(R.string.result_success_title)
        isPending -> stringResource(R.string.result_pending_title)
        else -> stringResource(R.string.result_failure_title)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "₹${String.format("%.2f", t.amountRupees)} to ${t.recipientLabel}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isPending) {
            Text(
                text = stringResource(R.string.result_pending_msg),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Details Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                if (t.ussdRef != null) {
                    DetailRow(stringResource(R.string.result_ref_id), t.ussdRef!!)
                    Spacer(Modifier.height(16.dp))
                }
                
                val fmt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                DetailRow(stringResource(R.string.result_timestamp), fmt.format(Date(t.timestamp)))
                
                if (!isSuccess && t.note != null) {
                    Spacer(Modifier.height(16.dp))
                    DetailRow("Reason", t.note!!)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isSuccess || isPending) {
            OutlinedButton(
                onClick = { /* Share intent */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.result_btn_share))
            }
            Spacer(Modifier.height(16.dp))
        }

        Button(
            onClick = if (!isSuccess && !isPending) onRetry else onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(if (!isSuccess && !isPending) stringResource(R.string.result_btn_retry) else stringResource(R.string.result_btn_done), fontSize = 18.sp)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}
