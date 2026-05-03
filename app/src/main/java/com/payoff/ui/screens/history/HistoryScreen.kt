package com.payoff.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payoff.R
import com.payoff.data.db.dao.MonthlySummary
import com.payoff.data.db.dao.TransactionDao
import com.payoff.data.model.Transaction
import com.payoff.ui.screens.home.TransactionRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val transactionDao: TransactionDao
) : ViewModel() {

    // Simple fixed date range for the MVP (current month)
    private val cal = Calendar.getInstance()
    init {
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
    }
    private val startOfMonth = cal.timeInMillis
    
    init {
        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.MILLISECOND, -1)
    }
    private val endOfMonth = cal.timeInMillis

    val transactions: StateFlow<List<Transaction>> = transactionDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthlySummary = mutableStateOf(MonthlySummary(0, 0, 0))

    init {
        viewModelScope.launch {
            monthlySummary.value = transactionDao.getMonthlySummary(startOfMonth, endOfMonth)
        }
    }

    // Workaround coroutine launch without explicit block in snippet
    private fun kotlinx.coroutines.CoroutineScope.launch(block: suspend kotlinx.coroutines.CoroutineScope.() -> Unit) {
        kotlinx.coroutines.launch(context = coroutineContext, block = block)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val txs by viewModel.transactions.collectAsState()
    val summary by viewModel.monthlySummary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                MonthlySummaryCard(summary)
            }

            if (txs.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.history_empty), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    }
                }
            } else {
                items(txs, key = { it.id }) { tx ->
                    Box(Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                        TransactionRow(tx)
                    }
                    Divider(Modifier.padding(horizontal = 24.dp))
                }
            }
        }
    }
}

@Composable
private fun MonthlySummaryCard(summary: MonthlySummary) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(stringResource(R.string.history_monthly_summary), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.history_total_sent))
                Text("₹${String.format("%.2f", summary.totalSentPaise / 100.0)}", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.history_total_received))
                Text("₹${String.format("%.2f", summary.totalReceivedPaise / 100.0)}", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.history_total_charges), color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text("₹${String.format("%.2f", summary.totalChargesPaise / 100.0)}", color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}
