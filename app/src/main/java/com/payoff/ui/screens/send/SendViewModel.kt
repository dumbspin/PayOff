package com.payoff.ui.screens.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payoff.data.db.dao.BankAccountDao
import com.payoff.data.db.dao.TransactionDao
import com.payoff.data.model.BankAccount
import com.payoff.data.model.Contact
import com.payoff.data.model.Transaction
import com.payoff.data.model.TransactionDirection
import com.payoff.data.model.TransactionStatus
import com.payoff.ussd.UssdState
import com.payoff.ussd.UssdStateMachine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendViewModel @Inject constructor(
    private val ussdStateMachine: UssdStateMachine,
    private val transactionDao: TransactionDao,
    private val bankAccountDao: BankAccountDao
) : ViewModel() {

    var selectedContact: Contact? = null
    var amountPaise: Long = 0L

    val ussdState = ussdStateMachine.currentState
    
    private val _activeAccount = MutableStateFlow<BankAccount?>(null)
    val activeAccount = _activeAccount.asStateFlow()

    private val _transactionResultId = MutableStateFlow<String?>(null)
    val transactionResultId = _transactionResultId.asStateFlow()

    init {
        viewModelScope.launch {
            _activeAccount.value = bankAccountDao.getDefault()
        }
    }

    fun startPayment(pin: String) {
        val contact = selectedContact ?: return
        val account = _activeAccount.value ?: return

        viewModelScope.launch {
            // Send request to *99# USSD FSM
            val reqUpiOrMobile = contact.upiId ?: contact.mobile ?: return@launch
            val amountRupees = (amountPaise / 100.0).toString()

            // The PIN is converted to a char array which is safely zeroed out in the state machine (SEC-01)
            val result = ussdStateMachine.startFlow(
                bankAccount = account,
                recipientUpi = reqUpiOrMobile,
                amount = amountRupees,
                pinChars = pin.toCharArray()
            )

            // Log it in local DB
            val txStatus = when(result.status) {
                UssdState.SUCCESS -> TransactionStatus.SUCCESS
                UssdState.FAILURE, UssdState.TIMEOUT -> TransactionStatus.FAILED
                UssdState.INTERRUPTED -> TransactionStatus.PENDING
                else -> TransactionStatus.UNKNOWN
            }

            val tx = Transaction(
                contactId = contact.id,
                recipientLabel = contact.name,
                recipientUpi = reqUpiOrMobile,
                amountPaise = amountPaise,
                direction = TransactionDirection.SENT,
                status = txStatus,
                ussdRef = result.referenceId,
                accountId = account.id,
                note = result.errorMessage
            )
            
            transactionDao.insert(tx)
            _transactionResultId.value = tx.id
        }
    }
}
