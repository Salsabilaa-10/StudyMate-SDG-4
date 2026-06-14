package com.example.a207945_salsabilaa_izwan_lab3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.a207945_salsabilaa_izwan_lab3.ui.theme.AppTheme

sealed class Screen(val route: String, val label: String, val icon: String) {
    object Home : Screen("home", "Home", "🏠")
    object History : Screen("history", "History", "📚")
    object Profile : Screen("profile", "Profile", "👤")
    object AddTask : Screen("add_task", "Add Task", "➕")
    object AddExam : Screen("add_exam", "Add Exam", "📝")
    object AddClass : Screen("add_class", "Add Class", "🏫")
    object Flashcards : Screen("flashcards", "Flashcards", "📚")
    object AIChat : Screen("ai_chat", "AI Chat", "🤖")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: StudyMateViewModel = viewModel(factory = StudyMateViewModel.Factory)
            val isDarkModePref by viewModel.isDarkMode.collectAsState()
            val isDark = isDarkModePref ?: isSystemInDarkTheme()
            
            AppTheme(darkTheme = isDark) { 
                StudyMateApp(viewModel) 
            } 
        }
    }
}

@Composable
fun StudyMateApp(viewModel: StudyMateViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val isDarkModePref by viewModel.isDarkMode.collectAsState()
    val isDark = isDarkModePref ?: isSystemInDarkTheme()
    
    val mainBgGradient = if (isDark) {
        Brush.verticalGradient(
            listOf(
                MaterialTheme.colorScheme.surfaceContainerLowest, // Very deep dark
                MaterialTheme.colorScheme.surfaceContainerLow,    // Slightly lighter
                MaterialTheme.colorScheme.surfaceContainerLowest  // Back to deep dark
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                MaterialTheme.colorScheme.primaryContainer, 
                MaterialTheme.colorScheme.surface, 
                MaterialTheme.colorScheme.secondaryContainer, 
                MaterialTheme.colorScheme.surface, 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            )
        )
    }

    val glassBg = if (isDark) {
        Brush.verticalGradient(
            listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.05f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            )
        )
    }

    val glassBorder = if (isDark) {
        Brush.linearGradient(
            listOf(
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        )
    }

    Box(Modifier.fillMaxSize().background(mainBgGradient)) {
        NavHost(navController, Screen.Home.route, Modifier.fillMaxSize()) {
            composable(Screen.Home.route) {
                Column(Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
                    HomeScreen(
                        liquidGlassBg = glassBg, 
                        liquidGlassBorder = glassBorder, 
                        viewModel = viewModel,
                        onAddTaskClick = { navController.navigate(Screen.AddTask.route) },
                        onAddExamClick = { navController.navigate(Screen.AddExam.route) },
                        onAddClassClick = { navController.navigate(Screen.AddClass.route) },
                        onFlashcardClick = { navController.navigate(Screen.Flashcards.route) },
                        onSearch = { query ->
                            viewModel.sendMessage(query)
                            navController.navigate(Screen.AIChat.route)
                        }
                    )
                    Box(Modifier.height(160.dp))
                }
            }
            composable(Screen.History.route) {
                Column(Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
                    HistoryScreen(
                        liquidGlassBg = glassBg, 
                        liquidGlassBorder = glassBorder, 
                        viewModel = viewModel,
                        onEditTask = { navController.navigate(Screen.AddTask.route) },
                        onEditExam = { navController.navigate(Screen.AddExam.route) },
                        onEditClass = { navController.navigate(Screen.AddClass.route) },
                        onChatClick = { navController.navigate(Screen.AIChat.route) }
                    )
                    Box(Modifier.height(160.dp))
                }
            }
            composable(Screen.Profile.route) {
                Column(Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
                    ProfileScreen(glassBg, glassBorder, viewModel)
                    Box(Modifier.height(160.dp))
                }
            }
            composable(Screen.AddTask.route) {
                AddTaskScreen(viewModel) {
                    navController.popBackStack()
                }
            }
            composable(Screen.AddExam.route) {
                AddExamScreen(viewModel) {
                    navController.popBackStack()
                }
            }
            composable(Screen.AddClass.route) {
                AddClassScreen(viewModel) {
                    navController.popBackStack()
                }
            }
            composable(Screen.Flashcards.route) {
                FlashcardScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AIChat.route) {
                AIChatScreen(viewModel) {
                    navController.popBackStack()
                }
            }
        }

        val showNavBar = currentDestination in listOf(
            Screen.Home.route, 
            Screen.History.route, 
            Screen.Profile.route,
            Screen.Flashcards.route,
            Screen.AIChat.route
        )

        if (showNavBar) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 30.dp).width(330.dp).height(75.dp)) {
                Box(Modifier.fillMaxSize().clip(RoundedCornerShape(38.dp)).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)).border(1.2.dp, glassBorder, RoundedCornerShape(38.dp)))
                Row(Modifier.fillMaxSize(), Arrangement.SpaceAround, Alignment.CenterVertically) {
                    listOf(Screen.History, Screen.Home, Screen.Profile).forEach { screen ->
                        NavBarItem(screen.label, screen.icon, currentDestination == screen.route) {
                            if (currentDestination != screen.route) navController.navigate(screen.route) { popUpTo(navController.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true }
                        }
                    }
                }
            }
        }
    }
}
