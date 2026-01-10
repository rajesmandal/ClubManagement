package com.sohitechnology.clubmanagement.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomBar(navController: NavHostController) {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Members,
        BottomNavItem.Report,
        BottomNavItem.Profile
    )

    val currentRoute =
        navController.currentBackStackEntryAsState()
            .value?.destination?.route

    NavigationBar {

        items.forEach { item ->

            val selected = currentRoute == item.route // selected check
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.1f else 1f,
                label = "icon-scale"
            )


            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(MainRoute.Home.route)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        modifier = Modifier.scale(scale),
                        imageVector =
                            if (selected) item.selectedIcon
                            else item.unSelectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor =
                        MaterialTheme.colorScheme.primary,
                    selectedTextColor =
                        MaterialTheme.colorScheme.primary,
                    unselectedIconColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor =
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}
