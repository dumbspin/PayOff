package com.payoff.ussd

enum class UssdState {
    IDLE,
    LANG_SELECT,
    BANK_SELECT,
    MAIN_MENU,
    RECIPIENT_INPUT,
    AMOUNT_INPUT,
    PIN_INPUT,
    AWAITING_RESULT,
    SUCCESS,
    FAILURE,
    TIMEOUT,
    INTERRUPTED
}

data class UssdSessionResult(
    val status: UssdState,
    val referenceId: String? = null,
    val errorMessage: String? = null,
    val rawResponse: String? = null
)
