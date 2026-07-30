package com.zzzcc.pomodorotimer.ui.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zzzcc.pomodorotimer.feature.focus.FocusRoute
import com.zzzcc.pomodorotimer.feature.settings.SettingsScreen
import com.zzzcc.pomodorotimer.feature.statistics.StatisticsScreen
import com.zzzcc.pomodorotimer.feature.tasks.TasksScreen

@Composable
fun PomodoroAppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val destinations = AppDestination.entries

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(AppDestination.Focus.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Text(
                                text = destination.symbol,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        label = {
                            Text(text = stringResource(destination.labelRes))
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.background,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Focus.route,
            modifier = Modifier
        ) {
            composable(AppDestination.Focus.route) {
                FocusRoute(contentPadding = innerPadding)
            }
            composable(AppDestination.Tasks.route) {
                TasksScreen(contentPadding = innerPadding)
            }
            composable(AppDestination.Statistics.route) {
                StatisticsScreen(contentPadding = innerPadding)
            }
            composable(AppDestination.Settings.route) {
                SettingsScreen(contentPadding = innerPadding)
            }
        }
    }
}
