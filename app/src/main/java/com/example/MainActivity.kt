package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Ember100
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember500
import com.example.ui.theme.Ember600
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppDatabase
import com.example.data.model.Location
import com.example.data.model.Router
import com.example.data.model.UserRole
import com.example.data.model.XSpotRepository
import com.example.data.network.DevMenuManager
import com.example.ui.components.AddExpenseDialog
import com.example.ui.components.AddLocationDialog
import com.example.ui.components.AddRouterDialog
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.dashboard.HomeScreen
import com.example.ui.screens.dev.DevMenuScreen
import com.example.ui.screens.locations.LocationDetailScreen
import com.example.ui.screens.locations.LocationsScreen
import com.example.ui.screens.more.MoreScreen
import com.example.ui.screens.routers.RouterDetailScreen
import com.example.ui.screens.routers.RoutersScreen
import com.example.ui.screens.vouchers.VouchersScreen
import com.example.ui.theme.DarkGlassSurface
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.LightGlassSurface
import com.example.ui.theme.Umber400
import com.example.ui.theme.XSpotFieldTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  object Home : Screen("home", "Home", Icons.Default.Home)
  object Locations : Screen("locations", "Locations", Icons.Default.LocationOn)
  object Routers : Screen("routers", "Routers", Icons.Default.Router)
  object Vouchers : Screen("vouchers", "Vouchers", Icons.Default.ConfirmationNumber)
  object More : Screen("more", "More", Icons.Default.MoreHoriz)
}

class MainActivity : ComponentActivity() {

  private lateinit var repository: XSpotRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val db = AppDatabase.getDatabase(applicationContext)
    repository = XSpotRepository(db)

    setContent {
      val config by DevMenuManager.config.collectAsState()
      XSpotFieldTheme(themeMode = config.themeMode) {
        XSpotFieldApp(repository)
      }
    }
  }
}

@Composable
fun XSpotFieldApp(repository: XSpotRepository) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  val navController = rememberNavController()
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val userRole by repository.userRole.collectAsState()
  val jwtToken by repository.jwtToken.collectAsState()
  val isAuthenticated = !jwtToken.isNullOrBlank()
  val isAppOnline by repository.isAppOnline.collectAsState()

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  LaunchedEffect(currentRoute, jwtToken) {
    if (currentRoute != null && currentRoute != "login" && currentRoute != "dev_menu" && !isAuthenticated) {
      Toast.makeText(context, "Authentication required (JWT missing). Redirecting to login.", Toast.LENGTH_SHORT).show()
      navController.navigate("login") {
        popUpTo(0) { inclusive = true }
      }
    }
  }

  val overview = repository.getDashboardOverview()
  val activities by repository.activities.collectAsState()
  val locations by repository.locations.collectAsState()
  val routers by repository.routers.collectAsState()
  val clusters by repository.clusters.collectAsState()
  val vouchers by repository.vouchers.collectAsState()
  val packages by repository.packages.collectAsState()
  val expenses by repository.expenses.collectAsState()
  val promotions by repository.promotions.collectAsState()
  val promotionWinners by repository.promotionWinners.collectAsState()
  val openAccess by repository.openAccessSettings.collectAsState()
  val organizations by repository.organizations.collectAsState()
  val users by repository.users.collectAsState()

  var selectedLocationDetail by remember { mutableStateOf<Location?>(null) }
  var selectedRouterDetail by remember { mutableStateOf<Router?>(null) }

  // Modal dialog states
  var showAddLocationModal by remember { mutableStateOf(false) }
  var showAddRouterModal by remember { mutableStateOf(false) }
  var showAddExpenseModal by remember { mutableStateOf(false) }

  val bottomNavItems = listOf(
    Screen.Home,
    Screen.Locations,
    Screen.Routers,
    Screen.Vouchers,
    Screen.More
  )

  Scaffold(
    bottomBar = {
      if (currentRoute != "login" && currentRoute != "dev_menu" && isAuthenticated) {
        LiquidGlassNavBar(
          items = bottomNavItems,
          currentRoute = currentRoute,
          onNavigate = { screen ->
            navController.navigate(screen.route) {
              popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
              }
              launchSingleTop = true
              restoreState = true
            }
          }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      NavHost(
        navController = navController,
        startDestination = "login"
      ) {
        composable("login") {
          LoginScreen(
            onLogin = { email, pass ->
              scope.launch(Dispatchers.IO) {
                val (success, msg) = repository.login(email, pass)
                withContext(Dispatchers.Main) {
                  Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                  navController.navigate(Screen.Home.route) {
                    popUpTo("login") { inclusive = true }
                  }
                }
              }
            },
            onOpenDevMenu = { navController.navigate("dev_menu") }
          )
        }

        composable("dev_menu") {
          DevMenuScreen(
            repository = repository,
            onBackClick = { navController.popBackStack() },
            onNavigateHome = {
              navController.navigate(Screen.Home.route) {
                popUpTo("login") { inclusive = true }
              }
            }
          )
        }

        composable(Screen.Home.route) {
          HomeScreen(
            overview = overview,
            activities = activities,
            userRole = userRole,
            isAppOnline = isAppOnline,
            onNavigateToVoucherGenerate = { navController.navigate(Screen.Vouchers.route) },
            onNavigateToAddLocation = { showAddLocationModal = true },
            onNavigateToTestRouter = { showAddRouterModal = true },
            onNavigateToValidateVoucher = { navController.navigate(Screen.Vouchers.route) },
            onActivityClick = { item ->
              if (item.kind == "voucher_activated") {
                navController.navigate(Screen.Vouchers.route)
              } else {
                navController.navigate(Screen.Routers.route)
              }
            }
          )
        }

        composable(Screen.Locations.route) {
          LocationsScreen(
            locations = locations,
            clusters = clusters,
            onLocationSelected = { loc ->
              selectedLocationDetail = loc
              navController.navigate("location_detail")
            },
            onAddLocationClick = { showAddLocationModal = true }
          )
        }

        composable("location_detail") {
          selectedLocationDetail?.let { loc ->
            LocationDetailScreen(
              location = loc,
              stats = repository.getLocationStats(loc.id),
              clusters = clusters,
              routers = routers.filter { it.locationId == loc.id },
              vouchers = vouchers.filter { it.locationId == loc.id },
              expenses = expenses.filter { it.locationId == loc.id },
              onBackClick = { navController.popBackStack() },
              onAddRouterClick = { showAddRouterModal = true },
              onGenerateVouchersClick = { navController.navigate(Screen.Vouchers.route) },
              onAddExpenseClick = { showAddExpenseModal = true }
            )
          }
        }

        composable(Screen.Routers.route) {
          RoutersScreen(
            routers = routers,
            locations = locations,
            onRouterSelected = { router ->
              selectedRouterDetail = router
              navController.navigate("router_detail")
            },
            onAddRouterClick = { showAddRouterModal = true },
            onTestConnectionClick = { showAddRouterModal = true }
          )
        }

        composable("router_detail") {
          selectedRouterDetail?.let { router ->
            RouterDetailScreen(
              router = router,
              health = repository.getRouterHealth(router.id),
              sessions = repository.getLiveSessions(router.id),
              onBackClick = { navController.popBackStack() },
              onRefreshHealth = {
                selectedRouterDetail = router
              },
              onDisconnectSession = { username ->
                repository.disconnectSession(router.id, username)
              }
            )
          }
        }

        composable(Screen.Vouchers.route) {
          VouchersScreen(
            vouchers = vouchers,
            packages = packages,
            locations = locations,
            onBulkGenerate = { pkgId, locId, qty, pin, pinLen ->
              repository.bulkGenerateVouchers(pkgId, locId, qty, pin, pinLen)
            },
            onRevokeVoucher = { id -> repository.revokeVoucher(id) },
            onValidateVoucher = { code, locId -> repository.validateVoucher(code, locId) },
            onActivateVoucher = { code, pin, locId -> repository.activateVoucher(code, pin, locId) }
          )
        }

        composable(Screen.More.route) {
          MoreScreen(
            userRole = userRole,
            onRoleChanged = { role -> repository.setRole(role) },
            organizations = organizations,
            users = users,
            promotions = promotions,
            winners = promotionWinners,
            openAccess = openAccess,
            expenses = expenses,
            vouchers = vouchers,
            onDrawPromotion = { promId, count -> repository.drawPromotionWinners(promId, count) },
            onUpdateOpenAccess = { settings -> repository.updateOpenAccess(settings) },
            onAddExpenseClick = { showAddExpenseModal = true },
            onOpenDevMenu = { navController.navigate("dev_menu") },
            onLogout = {
              repository.logout()
              navController.navigate("login") {
                popUpTo(0) { inclusive = true }
              }
            }
          )
        }
      }

      // Modals
      if (showAddLocationModal) {
        AddLocationDialog(
          clusters = clusters,
          onDismiss = { showAddLocationModal = false },
          onSave = { name, address, rent, electricity, internet, clusterId ->
            repository.addLocation(name, address, null, null, rent, electricity, internet, clusterId)
          }
        )
      }

      if (showAddRouterModal) {
        AddRouterDialog(
          locations = locations,
          onDismiss = { showAddRouterModal = false },
          onTestConnection = { ip, port, user, pass ->
            repository.testRouterConnection(ip, port, user, pass)
          },
          onSave = { locationId, name, ip, port, user ->
            repository.addRouter(locationId, name, ip, port, user)
          }
        )
      }

      if (showAddExpenseModal) {
        AddExpenseDialog(
          locations = locations,
          onDismiss = { showAddExpenseModal = false },
          onSave = { locationId, category, amount, desc ->
            repository.addExpense(locationId, category, amount, "2026-07-24", desc)
          }
        )
      }
    }
  }
}

@Composable
fun LiquidGlassNavBar(
  items: List<Screen>,
  currentRoute: String?,
  onNavigate: (Screen) -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  val surfaceBrush = if (isDark) {
    Brush.verticalGradient(
      colors = listOf(
        Color(0xFF1E293B).copy(alpha = 0.88f),
        Color(0xFF0F172A).copy(alpha = 0.95f)
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        Color.White.copy(alpha = 0.95f),
        Color(0xFFF1F5F9).copy(alpha = 0.90f)
      )
    )
  }

  val borderBrush = if (isDark) {
    Brush.verticalGradient(
      colors = listOf(
        Color.White.copy(alpha = 0.40f),
        Color.White.copy(alpha = 0.10f)
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        Color.White,
        Color(0xFFCBD5E1).copy(alpha = 0.75f)
      )
    )
  }

  val shape = RoundedCornerShape(24.dp)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp)
      .navigationBarsPadding()
      .shadow(
        elevation = if (isDark) 12.dp else 10.dp,
        shape = shape,
        spotColor = if (isDark) Color(0x60000000) else Color(0x250F172A),
        ambientColor = if (isDark) Color(0x40000000) else Color(0x180F172A)
      )
      .clip(shape)
      .background(surfaceBrush)
      .border(1.dp, borderBrush, shape)
      .padding(vertical = 6.dp, horizontal = 4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      items.forEach { screen ->
        val isSelected = currentRoute == screen.route
        val activePillBg = if (isDark) {
          Ember500.copy(alpha = 0.22f)
        } else {
          Ember100.copy(alpha = 0.90f)
        }
        val activePillBorder = if (isDark) Ember300.copy(alpha = 0.35f) else Ember500.copy(alpha = 0.30f)

        val iconTint = if (isSelected) {
          if (isDark) Ember300 else Ember600
        } else {
          if (isDark) Slate400 else Slate500
        }

        val textTint = if (isSelected) {
          if (isDark) Ember300 else Ember600
        } else {
          if (isDark) Slate400 else Slate500
        }

        val pillShape = RoundedCornerShape(16.dp)

        Box(
          modifier = Modifier
            .clip(pillShape)
            .then(
              if (isSelected) {
                Modifier
                  .background(activePillBg, pillShape)
                  .border(1.dp, activePillBorder, pillShape)
              } else Modifier
            )
            .clickable { onNavigate(screen) }
            .padding(horizontal = 12.dp, vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = screen.icon,
              contentDescription = screen.title,
              tint = iconTint,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = screen.title,
              fontSize = 10.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = textTint,
              maxLines = 1
            )
          }
        }
      }
    }
  }
}
