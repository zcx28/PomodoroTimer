package com.zzzcc.pomodorotimer.ui.navigation

import androidx.annotation.StringRes
import com.zzzcc.pomodorotimer.R

enum class AppDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val symbol: String
) {
    Focus(
        route = "focus",
        labelRes = R.string.nav_focus,
        symbol = "◉"
    ),
    Tasks(
        route = "tasks",
        labelRes = R.string.nav_tasks,
        symbol = "✓"
    ),
    Statistics(
        route = "statistics",
        labelRes = R.string.nav_statistics,
        symbol = "▥"
    ),
    Settings(
        route = "settings",
        labelRes = R.string.nav_settings,
        symbol = "⚙"
    )
}
