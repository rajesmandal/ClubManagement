package com.sohitechnology.clubmanagement.main

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sohitechnology.clubmanagement.R
import com.sohitechnology.clubmanagement.core.NavigationEvent
import com.sohitechnology.clubmanagement.core.NavigationManager
import com.sohitechnology.clubmanagement.navigation.BottomNavItem
import com.sohitechnology.clubmanagement.navigation.MainRoute
import com.sohitechnology.clubmanagement.navigation.mainNavGraph
import com.sohitechnology.clubmanagement.ui.UiMessage
import com.sohitechnology.clubmanagement.ui.UiMessageType
import com.sohitechnology.clubmanagement.ui.auth.BiometricAuthenticator
import com.sohitechnology.clubmanagement.ui.common.CenterPopup
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainContainerScreen(
    viewModel: MainViewModel = hiltViewModel(),
    biometricAuthenticator: BiometricAuthenticator
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val context = LocalContext.current

    val themeMode by viewModel.themeMode.collectAsState()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showLogoutPopup by remember { mutableStateOf(false) }

    // Handle Logout Event
    LaunchedEffect(Unit) {
        viewModel.logoutEvent.collect {
            NavigationManager.navigate(NavigationEvent.ToLogin)
        }
    }

    // Fetch Version Info
    val packageInfo = try {
        context.packageManager.getPackageInfo(context.packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
    val versionName = packageInfo?.versionName ?: "1.0"
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo?.longVersionCode ?: 1L
    } else {
        @Suppress("DEPRECATION")
        packageInfo?.versionCode?.toLong() ?: 1L
    }

    MainContainerContent(
        drawerState = drawerState,
        themeMode = themeMode,
        isLogoutLoading = viewModel.isLogoutLoading,
        currentRoute = currentRoute,
        showLogoutPopup = showLogoutPopup,
        versionName = versionName,
        versionCode = versionCode,
        onLogoutPopupToggle = { showLogoutPopup = it },
        onThemeToggle = { viewModel.setThemeMode(if (it) "dark" else "light") },
        onLogoutConfirm = { viewModel.logout() },
        onNavItemClick = { route ->
            scope.launch { drawerState.close() }
            // To allow back navigation, we just navigate without popping up to the start destination
            navController.navigate(route) {
                // If the user specifically wants back to work from drawer, 
                // we avoid launchSingleTop and popUpTo for destinations that shouldn't reset the stack.
                // However, for Notification, we definitely want standard back behavior.
                if (route == BottomNavItem.Home.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                    }
                }
                launchSingleTop = true
            }
        },
        navHost = {
            SharedTransitionLayout {
                androidx.navigation.compose.NavHost(
                    navController = navController,
                    startDestination = com.sohitechnology.clubmanagement.navigation.MainRoute.MemberGraph.route
                ) {
                    mainNavGraph(
                        navController = navController,
                        onMenuClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        biometricAuthenticator = biometricAuthenticator
                    )
                }
            }
        }
    )
}

@Composable
fun MainContainerContent(
    drawerState: DrawerState,
    themeMode: String,
    isLogoutLoading: Boolean,
    currentRoute: String?,
    showLogoutPopup: Boolean,
    versionName: String,
    versionCode: Long,
    onLogoutPopupToggle: (Boolean) -> Unit,
    onThemeToggle: (Boolean) -> Unit,
    onLogoutConfirm: () -> Unit,
    onNavItemClick: (String) -> Unit,
    navHost: @Composable () -> Unit
) {
    // Determine if we are currently in dark mode (considering system setting)
    val isSystemDark = isSystemInDarkTheme()
    val isCurrentDark = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemDark
    }

    val sideBarItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Members,
        BottomNavItem.Report,
        BottomNavItem.Notification,
        BottomNavItem.Profile
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header with Logo and Title
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Club",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                lineHeight = 24.sp
                            )
                            Text(
                                text = "Management",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }

                Column {
                    sideBarItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = { onNavItemClick(item.route) },
                            icon = {
                                Icon(
                                    if (currentRoute == item.route) item.selectedIcon else item.unSelectedIcon,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Footer Section: Theme Switcher
                Row(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dark Mode",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isCurrentDark,
                        onCheckedChange = onThemeToggle,
                        thumbContent = {
                            Icon(
                                if (isCurrentDark) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                                contentDescription = null,
                                modifier = Modifier.padding(2.dp).size(16.dp)
                            )
                        }
                    )
                }

                // Footer Section: Version Info
                Text(
                    text = "Version $versionName ($versionCode)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            navHost()

            if (isLogoutLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            if (showLogoutPopup) {
                CenterPopup(
                    uiMessage = UiMessage(
                        title = "Logout",
                        message = "Are you sure you want to logout?",
                        type = UiMessageType.INFO
                    ),
                    onDismiss = { onLogoutPopupToggle(false) },
                    actionText = "Logout",
                    onAction = {
                        onLogoutPopupToggle(false)
                        onLogoutConfirm()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContainerScreenPreview() {
    ClubManagementTheme {
        MainContainerContent(
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            themeMode = "system",
            isLogoutLoading = false,
            currentRoute = BottomNavItem.Home.route,
            showLogoutPopup = false,
            versionName = "1.0",
            versionCode = 1L,
            onLogoutPopupToggle = {},
            onThemeToggle = {},
            onLogoutConfirm = {},
            onNavItemClick = {},
            navHost = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Main Content Area")
                }
            }
        )
    }
}
