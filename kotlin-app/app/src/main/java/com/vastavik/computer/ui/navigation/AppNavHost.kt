package com.vastavik.computer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vastavik.computer.ui.screens.auth.ForgotPasswordScreen
import com.vastavik.computer.ui.screens.auth.LoginScreen
import com.vastavik.computer.ui.screens.auth.SignupScreen
import com.vastavik.computer.ui.screens.auth.SplashScreen
import com.vastavik.computer.ui.screens.chat.ChatScreen
import com.vastavik.computer.ui.screens.home.HomeScreen
import com.vastavik.computer.ui.screens.learning.LearningPathScreen
import com.vastavik.computer.ui.screens.onboarding.AccountDeletedScreen
import com.vastavik.computer.ui.screens.onboarding.AdminDashboardScreen
import com.vastavik.computer.ui.screens.onboarding.EditProfileScreen
import com.vastavik.computer.ui.screens.onboarding.MyNotesScreen
import com.vastavik.computer.ui.screens.onboarding.PaymentHistoryScreen
import com.vastavik.computer.ui.screens.onboarding.PaymentScreen
import com.vastavik.computer.ui.screens.onboarding.PYQScreen
import com.vastavik.computer.ui.screens.onboarding.SearchResultsScreen
import com.vastavik.computer.ui.screens.onboarding.SettingsScreen
import com.vastavik.computer.ui.screens.onboarding.UserSetupScreen
import com.vastavik.computer.ui.screens.onboarding.WelcomeScreen
import com.vastavik.computer.ui.screens.editor.CodeEditorScreen
import com.vastavik.computer.ui.screens.editor.OcrExerciseScreen
import com.vastavik.computer.ui.screens.notifications.NotificationsScreen
import com.vastavik.computer.ui.screens.notifications.AppUpdateScreen
import com.vastavik.computer.ui.screens.practice.PracticeScreen
import com.vastavik.computer.ui.screens.profile.ProfileScreen
import com.vastavik.computer.ui.screens.quiz.QuizSetupScreen
import com.vastavik.computer.ui.screens.quiz.QuizTakingScreen
import com.vastavik.computer.ui.screens.video.VideoLessonScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startRoute: String = "splash"
) {
    NavHost(navController = navController, startDestination = startRoute) {
        composable("splash") {
            SplashScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("signup") {
            SignupScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo("signup") { inclusive = true }
                }
            })
        }
        composable("forgot_password") {
            ForgotPasswordScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo("forgot_password") { inclusive = true }
                }
            })
        }
        composable("welcome") {
            WelcomeScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo("welcome") { inclusive = true }
                }
            })
        }
        composable("user_setup") {
            UserSetupScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo("user_setup") { inclusive = true }
                }
            })
        }
        composable("home") {
            HomeScreen(onNavigate = { route ->
                navController.navigate(route)
            })
        }
        composable("learning_path") {
            LearningPathScreen(onNavigate = { route ->
                navController.navigate(route)
            })
        }
        composable("practice") {
            PracticeScreen(onNavigate = { route ->
                navController.navigate(route)
            })
        }
        composable("chat") {
            ChatScreen(onNavigate = { route ->
                navController.navigate(route)
            })
        }
        composable("profile") {
            ProfileScreen(onNavigate = { route ->
                navController.navigate(route)
            })
        }
        composable(
            route = "video_lesson/{lessonId}/{courseId}/{partId}/{subpartId}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("courseId") { type = NavType.StringType },
                navArgument("partId") { type = NavType.StringType },
                navArgument("subpartId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            VideoLessonScreen(
                lessonId = backStackEntry.arguments?.getString("lessonId") ?: "",
                courseId = backStackEntry.arguments?.getString("courseId") ?: "",
                partId = backStackEntry.arguments?.getString("partId") ?: "",
                subpartId = backStackEntry.arguments?.getString("subpartId") ?: "",
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(
            route = "quiz_setup/{topic}",
            arguments = listOf(navArgument("topic") { type = NavType.StringType })
        ) { backStackEntry ->
            QuizSetupScreen(
                topic = backStackEntry.arguments?.getString("topic") ?: "",
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(
            route = "quiz_taking/{quizId}",
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            QuizTakingScreen(
                quizId = backStackEntry.arguments?.getString("quizId") ?: "",
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable("payment") {
            PaymentScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("payment_history") {
            PaymentHistoryScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("edit_profile") {
            EditProfileScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("my_notes") {
            MyNotesScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("settings") {
            SettingsScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("admin") {
            AdminDashboardScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("account_deleted") {
            AccountDeletedScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("pyq") {
            PYQScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("search") {
            SearchResultsScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("code_editor") {
            CodeEditorScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("ocr_exercise") {
            OcrExerciseScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("notifications") {
            NotificationsScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("app_update") {
            AppUpdateScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("course") {
            LearningPathScreen(onNavigate = { route -> navController.navigate(route) })
        }
    }
}