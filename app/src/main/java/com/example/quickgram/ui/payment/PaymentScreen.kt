package com.example.quickgram.ui.payment

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController
) {
    var isPaymentSuccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Demo") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isPaymentSuccess) {
                Text("✅ Payment Successful (Demo)", style = MaterialTheme.typography.headlineSmall)
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Processing payment...")
                    } else {
                        Button(
                            onClick = {
                                Log.d("PaymentScreen", "Pay button clicked")
                                isLoading = true

                                // Simulate delay (demo mode)
                                android.os.Handler().postDelayed({
                                    Log.d("PaymentScreen", "Payment simulated success")
                                    isLoading = false
                                    isPaymentSuccess = true
                                }, 2000)
                            }
                        ) {
                            Text("Pay with Google Pay (Demo)")
                        }
                    }
                }
            }
        }
    }
}
