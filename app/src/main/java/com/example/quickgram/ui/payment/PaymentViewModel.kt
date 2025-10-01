package com.example.quickgram.ui.payment

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet

class PaymentViewModel : ViewModel() {

    fun startPayment(activity: Activity, paymentSheet: PaymentSheet?) {
        // Initialize Stripe with your publishable key
        PaymentConfiguration.init(
            activity,
            "pk_test_YourPublicKeyFromStripe"
        )

        // Normally you’d fetch this from your backend
        val customerId = "cus_test123"
        val ephemeralKeySecret = "ek_test_abc"
        val paymentIntentClientSecret = "pi_test_secret"

        paymentSheet?.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "QuickGram",
                googlePay = PaymentSheet.GooglePayConfiguration(
                    environment = PaymentSheet.GooglePayConfiguration.Environment.Test,
                    countryCode = "IN",
                    currencyCode = "INR"
                )
            )
        )
    }
}
