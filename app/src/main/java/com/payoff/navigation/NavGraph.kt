package com.payoff.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
// (Need real Screen components imported here. Using placeholders if components missing)
import com.payoff.ui.screens.AppLockScreen
import com.payoff.ui.screens.home.HomeScreen
import com.payoff.ui.screens.send.ContactPickerScreen
import com.payoff.ui.screens.send.AmountScreen
import com.payoff.ui.screens.send.PinScreen
import com.payoff.ui.screens.send.ProcessingScreen
import com.payoff.ui.screens.send.ResultScreen
import com.payoff.ui.screens.history.HistoryScreen

sealed class Screen(val route: String) {
    object AppLock : Screen("applock")
    object Home : Screen("home")
    object ContactPicker : Screen("send/contact")
    object Amount : Screen("send/amount")
    object Pin : Screen("send/pin")
    object History : Screen("history")
    // Note: To keep routing simple, we rely on the ViewModel for passing complex items.
}

@Composable
fun PayOffNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AppLock.route,
        modifier = modifier
    ) {
        composable(Screen.AppLock.route) { 
            AppLockScreen(onUnlockSuccess = { 
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.AppLock.route) { inclusive = true }
                } 
            })
        }
        
        composable(Screen.Home.route) { 
            HomeScreen(
                onSendMoneyClick = { navController.navigate(Screen.ContactPicker.route) },
                onSettingsClick = { /* TODO */ },
                onHistoryClick = { navController.navigate(Screen.History.route) }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.ContactPicker.route) {
            // Simplified for proto: navigate direct to Amount 
            ContactPickerScreen(
                onBack = { navController.popBackStack() },
                onContactSelected = { _ -> 
                    navController.navigate(Screen.Amount.route)
                }
            )
        }

        composable(Screen.Amount.route) {
            AmountScreen(
                recipientName = "Selected Contact", // Ideally pulled from ViewModel Scope
                onBack = { navController.popBackStack() },
                onAmountConfirmed = { amountPaise ->
                    navController.navigate(Screen.Pin.route)
                }
            )
        }

        composable(Screen.Pin.route) {
            PinScreen(
                onPinComplete = { pin ->
                    // In a full implementation, the SharedViewModel starts processing here 
                    // and we navigate to ProcessingScreen. For prototype, we mock the jump:
                    navController.navigate("send/result")
                }
            )
        }
        
        composable("send/result") {
            // Placeholder Result — depends on ID usually
            ResultScreen(
                transactionId = "", 
                transactionDao = TODO("Dao usually comes from viewmodel"),
                onDone = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                onRetry = { navController.popBackStack() }
            )
        }
    }
}
