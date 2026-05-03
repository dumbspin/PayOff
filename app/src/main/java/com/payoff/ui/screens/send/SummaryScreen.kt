package com.payoff.ui.screens.send

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.payoff.R
import com.payoff.data.model.BankAccount
import com.payoff.data.model.Contact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    contact: Contact,
    amountPaise: Long, // e.g. 50000 for ₹500
    account: BankAccount,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    val amountRupees = amountPaise / 100.0
    val ussdCharge = 0.50
    val total = amountRupees + ussdCharge

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.summary_title)) },
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
                .padding(24.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // To
                    Text(stringResource(R.string.summary_to), style = MaterialTheme.typography.labelMedium)
                    Text(contact.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(contact.displayId, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Breakdown
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.summary_amount))
                        Text(String.format("₹%.2f", amountRupees), fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.summary_ussd_charge), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(String.format("₹%.2f", ussdCharge), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Total
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.summary_total), fontWeight = FontWeight.Bold)
                        Text(String.format("₹%.2f", total), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // From Account
            Text(stringResource(R.string.summary_from_account), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏦", fontSize = 16.sp) // Mock bank icon
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(account.bankName, fontWeight = FontWeight.SemiBold)
                        Text("Account ending in ${account.maskedAccount}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = stringResource(R.string.summary_warning),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.summary_btn_confirm), fontSize = 18.sp)
            }
        }
    }
}
