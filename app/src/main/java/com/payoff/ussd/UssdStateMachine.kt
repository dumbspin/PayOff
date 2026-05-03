package com.payoff.ussd

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.payoff.data.model.BankAccount
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class UssdStateMachine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    
    private val _currentState = MutableStateFlow(UssdState.IDLE)
    val currentState: StateFlow<UssdState> = _currentState.asStateFlow()

    private val _lastResponse = MutableStateFlow<String?>(null)
    val lastResponse: StateFlow<String?> = _lastResponse.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())

    @Suppress("PrivateApi")
    fun isUssdSupported(): Boolean {
        return try {
            val hasPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
            hasPermission && telephonyManager.phoneType != TelephonyManager.PHONE_TYPE_NONE
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Start the *99# flow.
     */
    suspend fun startFlow(
        bankAccount: BankAccount,
        recipientUpi: String,
        amount: String,
        pinChars: CharArray
    ): UssdSessionResult {
        if (!isUssdSupported()) {
            return UssdSessionResult(UssdState.FAILURE, errorMessage = "USSD not supported on this device/carrier.")
        }

        return try {
            _currentState.value = UssdState.LANG_SELECT
            // 1. Dial *99# -> Wait for menu (usually lang select or bank select depending on registration state)
            val initialResponse = sendUssdRequest("*99#")
            
            // NOTE: In a real implementation, we must parse initialResponse to see what menu we are on.
            // *99# can land on:
            // 1) Welcome to *99#... (Registration)
            // 2) Select your bank (First run)
            // 3) Main menu (Already registered)
            
            // Assuming registered and landed on Main Menu for simplicity in this MVP flow
            _currentState.value = UssdState.MAIN_MENU
            
            // Main menu -> option '1' to Send Money
            val sendMenuResponse = sendUssdRequest("1")
            
            _currentState.value = UssdState.RECIPIENT_INPUT
            // Option '3' usually for UPI ID
            val upiMenuResponse = sendUssdRequest("3") 
            
            val amountMenuResponse = sendUssdRequest(recipientUpi)
            
            _currentState.value = UssdState.AMOUNT_INPUT
            val pinMenuResponse = sendUssdRequest(amount)
            
            _currentState.value = UssdState.PIN_INPUT
            
            // Critical SEC-01: PIN is sent, then char array is zeroed out immediately 
            val pinString = String(pinChars)
            pinChars.fill('0') 
            
            _currentState.value = UssdState.AWAITING_RESULT
            val finalResponse = sendUssdRequest(pinString)

            parseFinalResponse(finalResponse)
        } catch (e: Exception) {
            _currentState.value = UssdState.FAILURE
            UssdSessionResult(UssdState.FAILURE, errorMessage = e.message ?: "Unknown USSD Error")
        } finally {
            _currentState.value = UssdState.IDLE
        }
    }

    private suspend fun sendUssdRequest(code: String): String = suspendCancellableCoroutine { continuation ->
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            continuation.resumeWith(Result.failure(SecurityException("CALL_PHONE permission missing")))
            return@suspendCancellableCoroutine
        }

        val callback = object : TelephonyManager.UssdResponseCallback() {
            override fun onReceiveUssdResponse(
                telephonyManager: TelephonyManager?,
                request: String?,
                response: CharSequence?
            ) {
                val respStr = response?.toString() ?: ""
                _lastResponse.value = respStr
                continuation.resume(respStr)
            }

            override fun onReceiveUssdResponseFailed(
                telephonyManager: TelephonyManager?,
                request: String?,
                failureCode: Int
            ) {
                val errorMsg = when(failureCode) {
                    USSD_RETURN_FAILURE -> "Network error"
                    USSD_ERROR_SERVICE_UNAVAIL -> "Service unavailable"
                    else -> "USSD Error $failureCode"
                }
                continuation.resumeWith(Result.failure(Exception(errorMsg)))
            }
        }

        telephonyManager.sendUssdRequest(code, callback, handler)
    }

    private fun parseFinalResponse(rawText: String): UssdSessionResult {
        // Simple heuristic parsing. Real *99# responses vary by bank and language.
        val lowerText = rawText.lowercase()
        return if (lowerText.contains("success") || lowerText.contains("sent") || lowerText.contains("txn id")) {
            // Extract a rudimentary ref ID 
            val refPattern = Regex("\\d{12}") // 12-digit UPI ref
            val match = refPattern.find(rawText)
            UssdSessionResult(UssdState.SUCCESS, referenceId = match?.value ?: "UNKNOWN_REF", rawResponse = rawText)
        } else {
            UssdSessionResult(UssdState.FAILURE, errorMessage = rawText, rawResponse = rawText)
        }
    }
}
