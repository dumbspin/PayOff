package com.payoff.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payoff.R
import com.payoff.data.db.dao.TransactionDao
import com.payoff.data.model.Transaction
import com.payoff.data.model.TransactionDirection
import com.payoff.data.model.TransactionStatus
import com.payoff.ui.components.OfflineBadge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionDao: TransactionDao
) : ViewModel() {
    val recentTransactions: StateFlow<List<Transaction>> = transactionDao.observeRecent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@Composable
fun HomeScreen(
    onSendMoneyClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.recentTransactions.collectAsState()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onSendMoneyClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.home_send_money), fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            HomeTopBar(onSettingsClick)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            AmountCard()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_recent_transactions),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onHistoryClick) {
                    Text(stringResource(R.string.home_view_all))
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (transactions.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.home_no_transactions),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                } else {
                    items(transactions) { tx ->
                        TransactionRow(tx)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OfflineBadge()
        IconButton(onClick = onSettingsClick) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
    }
}

@Composable
private fun AmountCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.secondary, // Deep Indigo
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "PayOff",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Secure offline payments via GSM network. No internet required.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun TransactionRow(tx: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar shape
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = tx.recipientLabel.take(1).uppercase(),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tx.recipientLabel,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            val format = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            Text(
                text = format.format(Date(tx.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            val amountStr = String.format("₹%.2f", tx.amountRupees)
            val prefix = if (tx.direction == TransactionDirection.SENT) "-" else "+"
            val color = if (tx.direction == TransactionDirection.RECEIVED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            
            Text(
                text = "$prefix $amountStr",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            if (tx.status != TransactionStatus.SUCCESS) {
                Text(
                    text = tx.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (tx.status == TransactionStatus.FAILED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}
