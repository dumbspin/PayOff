package com.payoff.ui.screens.send

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payoff.R
import com.payoff.data.db.dao.ContactDao
import com.payoff.data.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ContactPickerViewModel @Inject constructor(
    private val contactDao: ContactDao
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val contacts = _searchQuery
        .debounce(200)
        .flatMapLatest { query ->
            if (query.isBlank()) contactDao.observeAll()
            else contactDao.search(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactPickerScreen(
    onBack: () -> Unit,
    onContactSelected: (Contact) -> Unit,
    viewModel: ContactPickerViewModel = hiltViewModel()
) {
    val query by viewModel.searchQuery.collectAsState()
    val contacts by viewModel.contacts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.contact_picker_title)) },
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
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.contact_search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (query.isNotBlank() && query.contains("@")) {
                    // Quick-add manually typed UPI ID
                    item {
                        ManualUpiRow(upiId = query, onClick = {
                            val tempContact = Contact(name = query, upiId = query)
                            onContactSelected(tempContact)
                        })
                    }
                }

                items(contacts, key = { it.id }) { contact ->
                    ContactRow(contact = contact, onClick = { onContactSelected(contact) })
                }
            }
        }
    }
}

@Composable
private fun ContactRow(contact: Contact, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                contact.name.take(1).uppercase(),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(contact.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(
                contact.displayId, 
                style = MaterialTheme.typography.bodySmall, 
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ManualUpiRow(upiId: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text("Pay to UPI ID", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(
                upiId, 
                style = MaterialTheme.typography.bodySmall, 
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
