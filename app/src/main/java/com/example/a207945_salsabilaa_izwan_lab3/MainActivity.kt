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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.a207945_salsabilaa_izwan_lab3.ui.theme.AppTheme

sealed class Screen(val route: String, val label: String, val icon: String) {
    object Home : Screen("home", "Home", "🏠")
    object History : Screen("history", "History", "📚")
    object Profile : Screen("profile", "Profile", "👤")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppTheme { StudyMateApp() } }
    }
}

@Composable
fun StudyMateApp(viewModel: StudyMateViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val mainBgGradient = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)))
    val liquidGlassBg = Brush.linearGradient(listOf(MaterialTheme.colorScheme.surface.copy(alpha = 0.80f), MaterialTheme.colorScheme.surface.copy(alpha = 0.50f)))
    val liquidGlassBorder = Brush.linearGradient(listOf(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f), MaterialTheme.colorScheme.surface.copy(alpha = 0.4f), MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f)))

    Box(Modifier.fillMaxSize().background(mainBgGradient)) {
        NavHost(navController, Screen.Home.route, Modifier.fillMaxSize()) {
            composable(Screen.Home.route) { ScreenWrapper { HomeScreen(liquidGlassBg, liquidGlassBorder, viewModel) } }
            composable(Screen.History.route) { ScreenWrapper { HistoryScreen(liquidGlassBg, liquidGlassBorder) } }
            composable(Screen.Profile.route) { ScreenWrapper { ProfileScreen(liquidGlassBg, liquidGlassBorder, viewModel) } }
        }

        Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 30.dp).width(330.dp).height(75.dp)) {
            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(38.dp)).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)).border(1.2.dp, liquidGlassBorder, RoundedCornerShape(38.dp)))
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

@Composable
fun ScreenWrapper(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
        content()
        Box(Modifier.height(160.dp))
    }
}
