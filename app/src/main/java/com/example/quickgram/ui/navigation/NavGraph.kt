package com.example.quickgram.ui.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quickgram.ui.feed.FeedScreen
import com.example.quickgram.ui.auth.login.LoginScreen
import com.example.quickgram.ui.auth.signup.SignupScreen
import com.example.quickgram.ui.profile.ProfileScreen
import com.example.quickgram.ui.upload.UploadScreen
import com.example.quickgram.ui.payment.PaymentScreen


@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("feed") { FeedScreen(navController) }
        composable("profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            ProfileScreen(navController, userId)
        }
        composable("upload") { UploadScreen(navController) }
        composable("payment") { PaymentScreen(navController) }
    }
}
