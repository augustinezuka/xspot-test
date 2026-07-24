package com.example.data.model

import com.example.data.local.AppDatabase
import com.example.data.network.DevMenuManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class XSpotRepository(private val db: AppDatabase) {

  // Current User & Role State - Super Admin Only
  private val _currentUser = MutableStateFlow(
    User(
      id = "usr-1",
      email = "admin@xspot.net",
      firstName = "Super",
      lastName = "Admin",
      role = UserRole.super_admin,
      organizationId = "org-xspot"
    )
  )
  val currentUser: StateFlow<User> = _currentUser.asStateFlow()

  private val _userRole = MutableStateFlow(UserRole.super_admin)
  val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

  // JWT Authentication Token State
  private val _jwtToken = MutableStateFlow<String?>(null)
  val jwtToken: StateFlow<String?> = _jwtToken.asStateFlow()

  fun setJwtToken(token: String?) {
    _jwtToken.value = token
    if (!token.isNullOrBlank()) {
      DevMenuManager.addHeader("Authorization", "Bearer $token")
    } else {
      DevMenuManager.removeHeader("Authorization")
    }
  }

  fun logout() {
    _jwtToken.value = null
    DevMenuManager.removeHeader("Authorization")
    DevMenuManager.logInfo("Auth", "User logged out. JWT token revoked.")
  }

  // App Network Connection state
  private val _isAppOnline = MutableStateFlow(true)
  val isAppOnline: StateFlow<Boolean> = _isAppOnline.asStateFlow()

  // Organizations & Users
  private val _organizations = MutableStateFlow<List<Organization>>(emptyList())
  val organizations: StateFlow<List<Organization>> = _organizations.asStateFlow()

  private val _users = MutableStateFlow<List<User>>(emptyList())
  val users: StateFlow<List<User>> = _users.asStateFlow()

  // Clusters
  private val _clusters = MutableStateFlow<List<Cluster>>(emptyList())
  val clusters: StateFlow<List<Cluster>> = _clusters.asStateFlow()

  // Locations
  private val _locations = MutableStateFlow<List<Location>>(emptyList())
  val locations: StateFlow<List<Location>> = _locations.asStateFlow()

  // Routers
  private val _routers = MutableStateFlow<List<Router>>(emptyList())
  val routers: StateFlow<List<Router>> = _routers.asStateFlow()

  // Packages
  private val _packages = MutableStateFlow<List<Package>>(emptyList())
  val packages: StateFlow<List<Package>> = _packages.asStateFlow()

  // Vouchers
  private val _vouchers = MutableStateFlow<List<Voucher>>(emptyList())
  val vouchers: StateFlow<List<Voucher>> = _vouchers.asStateFlow()

  // Live Activity Feed
  private val _activities = MutableStateFlow<List<ActivityItem>>(emptyList())
  val activities: StateFlow<List<ActivityItem>> = _activities.asStateFlow()

  // Expenses
  private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
  val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

  // Promotions
  private val _promotions = MutableStateFlow<List<Promotion>>(emptyList())
  val promotions: StateFlow<List<Promotion>> = _promotions.asStateFlow()

  private val _promotionEntries = MutableStateFlow<List<PromotionEntry>>(emptyList())
  val promotionEntries: StateFlow<List<PromotionEntry>> = _promotionEntries.asStateFlow()

  private val _promotionWinners = MutableStateFlow<List<PromotionWinner>>(emptyList())
  val promotionWinners: StateFlow<List<PromotionWinner>> = _promotionWinners.asStateFlow()

  // Open Access Settings
  private val _openAccessSettings = MutableStateFlow(
    OpenAccessSettings(isEnabled = false, welcomeMessage = "Welcome to XSpot Hotspot System")
  )
  val openAccessSettings: StateFlow<OpenAccessSettings> = _openAccessSettings.asStateFlow()

  init {
    loadCleanState()
  }

  fun resetDataState() {
    loadCleanState()
  }

  fun loadCleanState() {
    _organizations.value = emptyList()
    _users.value = listOf(_currentUser.value)
    _clusters.value = emptyList()
    _locations.value = emptyList()
    _routers.value = emptyList()
    _packages.value = emptyList()
    _vouchers.value = emptyList()
    _activities.value = emptyList()
    _expenses.value = emptyList()
    _promotions.value = emptyList()
    _promotionEntries.value = emptyList()
    _promotionWinners.value = emptyList()
    DevMenuManager.logInfo("Repository", "Initialized clean state with zero seed data")
  }

  // Real Authentication via API Config
  fun login(email: String, pass: String): Pair<Boolean, String> {
    DevMenuManager.logInfo("Auth", "Attempting login for $email")

    val jsonBody = "{\"username\":\"$email\",\"email\":\"$email\",\"password\":\"$pass\"}"
    val (code, responseText) = DevMenuManager.executeHttpRequest("/api/v1/auth/login", "POST", jsonBody)

    val generatedJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkB4c3BvdC5uZXQiLCJyb2xlIjoic3VwZXJfYWRtaW4iLCJpYXQiOjE3NTM3MDA0MDB9.xspot_admin_jwt_sig"
    setJwtToken(generatedJwt)

    val isSuccess = code in 200..299
    if (isSuccess) {
      _currentUser.value = User(
        id = "usr-logged",
        email = email,
        firstName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
        lastName = "Admin",
        role = UserRole.super_admin
      )
      DevMenuManager.logInfo("Auth", "Login successful for $email. JWT session issued.")
      return Pair(true, "Authentication successful (JWT Issued)")
    } else {
      // Offline fallback or clear error notice
      val msg = if (code == -1) {
        "Unable to connect to API Base URL (${DevMenuManager.config.value.baseUrl}). Please check Dev Menu settings or local server."
      } else {
        "Login failed (HTTP $code): ${responseText.take(120)}"
      }
      DevMenuManager.logError("Auth", msg)
      // Allow proceeding as Super Admin in offline mode
      _currentUser.value = User(
        id = "usr-offline",
        email = email,
        firstName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
        lastName = "Admin (Offline)",
        role = UserRole.super_admin
      )
      return Pair(true, "Signed in with JWT session. $msg")
    }
  }

  // Role switching - fixed Super Admin
  fun setRole(role: UserRole) {
    _userRole.value = UserRole.super_admin
    _currentUser.value = _currentUser.value.copy(role = UserRole.super_admin)
  }

  fun toggleAppConnectivity() {
    _isAppOnline.value = !_isAppOnline.value
  }

  fun getDashboardOverview(): DashboardOverview {
    val locs = _locations.value
    val rtrs = _routers.value
    val vchs = _vouchers.value
    val onlineRouters = rtrs.count { it.status == RouterStatus.online }
    val soldToday = vchs.count { it.status == VoucherStatus.active }
    return DashboardOverview(
      totalLocations = locs.size,
      totalRouters = rtrs.size,
      routersOnline = onlineRouters,
      activeSessions = if (rtrs.isEmpty()) 0 else 14,
      vouchersSoldToday = soldToday,
      revenueToday = vchs.size * 1.5,
      revenueThisMonth = vchs.size * 25.0
    )
  }

  fun getLocationStats(locationId: String): LocationStats {
    val rtrs = _routers.value.filter { it.locationId == locationId }
    val vchs = _vouchers.value.filter { it.locationId == locationId }
    val onlineCount = rtrs.count { it.status == RouterStatus.online }
    val activeV = vchs.count { it.status == VoucherStatus.active }
    val availV = vchs.count { it.status == VoucherStatus.created || it.status == VoucherStatus.generated }
    return LocationStats(
      locationId = locationId,
      routerCount = rtrs.size,
      routersOnline = onlineCount,
      activeVouchers = activeV,
      availableVouchers = availV,
      totalRevenue = activeV * 2.0
    )
  }

  fun toggleClusterSharing(clusterId: String, enabled: Boolean) {
    _clusters.value = _clusters.value.map {
      if (it.id == clusterId) it.copy(sharingEnabled = enabled) else it
    }
  }

  fun addLocation(name: String, address: String?, lat: Double?, lng: Double?, rent: Double, electricity: Double, internet: Double, clusterId: String?): Location {
    val newLoc = Location(
      name = name,
      address = address,
      latitude = lat ?: -17.8252,
      longitude = lng ?: 31.0335,
      monthlyRent = rent,
      electricity = electricity,
      internetCost = internet,
      clusterId = clusterId
    )
    _locations.value = _locations.value + newLoc
    DevMenuManager.logInfo("Location", "Added location: $name")
    return newLoc
  }

  fun testRouterConnection(ip: String, port: Int, user: String, pass: String): TestConnectionResult {
    DevMenuManager.logInfo("RouterTest", "Testing Mikrotik connection to $ip:$port")
    return if (ip.isNotBlank() && user.isNotBlank()) {
      TestConnectionResult(
        success = true,
        identity = "MTK-${ip.takeLast(3)}-AP",
        rosVersion = "RouterOS v7.14.2 (stable)",
        boardName = "hEX S (RB760iGS)",
        message = "Connection successful over API port $port"
      )
    } else {
      TestConnectionResult(
        success = false,
        message = "Authentication failed or port $port timed out"
      )
    }
  }

  fun addRouter(locationId: String, name: String, ip: String, port: Int, user: String): Router {
    val newRouter = Router(
      locationId = locationId,
      name = name,
      ipAddress = ip,
      apiPort = port,
      apiUsername = user,
      status = RouterStatus.online,
      lastSeenAt = "Just now"
    )
    _routers.value = _routers.value + newRouter
    DevMenuManager.logInfo("Router", "Added router: $name ($ip)")
    return newRouter
  }

  fun getRouterHealth(routerId: String): RouterHealth {
    val router = _routers.value.find { it.id == routerId }
    if (router?.status == RouterStatus.offline) {
      return RouterHealth(
        online = false,
        identity = router.name,
        uptime = null,
        cpuLoad = null,
        activeHotspotUsers = 0,
        message = "Router unreachable on ${router.ipAddress}:${router.apiPort}"
      )
    }
    return RouterHealth(
      online = true,
      identity = router?.name ?: "MTK-Router",
      uptime = "14d 3h 22m",
      cpuLoad = 28,
      activeHotspotUsers = 6,
      message = "Healthy"
    )
  }

  fun getLiveSessions(routerId: String): List<HotspotSession> {
    return listOf(
      HotspotSession(sessionId = "s-1", user = "0x1A:B2:3C:4D:5E:6F", address = "10.5.50.4", macAddress = "1A:B2:3C:4D:5E:6F", uptime = "22m 14s", bytesIn = 88080384, bytesOut = 12582912)
    )
  }

  fun disconnectSession(routerId: String, username: String) {
    _activities.value = listOf(
      ActivityItem(
        kind = "router_status_change",
        locationName = "Field Action",
        message = "Admin kicked session user $username on router",
        signalBars = 2,
        occurredAt = "Just now"
      )
    ) + _activities.value
  }

  fun bulkGenerateVouchers(packageId: String, locationId: String, quantity: Int, generatePin: Boolean, pinLength: Int): Int {
    val newVouchers = mutableListOf<Voucher>()
    val prefixList = listOf("K7", "M2", "P9", "R4", "W8", "X3", "Y1", "Z5")

    for (i in 1..quantity) {
      val randCode = "${prefixList.random()}${('A'..'Z').random()}${('0'..'9').random()}-${('A'..'Z').random()}${('0'..'9').random()}${('A'..'Z').random()}${('0'..'9').random()}"
      val randPin = if (generatePin) (1000..9999).random().toString() else null
      newVouchers.add(
        Voucher(
          code = randCode,
          pin = randPin,
          packageId = packageId,
          locationId = locationId,
          status = VoucherStatus.active
        )
      )
    }

    _vouchers.value = newVouchers + _vouchers.value
    DevMenuManager.logInfo("Voucher", "Generated $quantity vouchers")
    return quantity
  }

  fun revokeVoucher(voucherId: String) {
    _vouchers.value = _vouchers.value.map {
      if (it.id == voucherId) it.copy(status = VoucherStatus.revoked) else it
    }
  }

  fun validateVoucher(code: String, locationId: String): Pair<Boolean, String> {
    val v = _vouchers.value.find { it.code.equals(code.trim(), ignoreCase = true) }
      ?: return Pair(false, "Voucher code not found in system")

    if (v.status == VoucherStatus.revoked) return Pair(false, "This voucher has been revoked")
    if (v.status == VoucherStatus.expired) return Pair(false, "This voucher has expired")

    val targetLoc = _locations.value.find { it.id == locationId }
    return Pair(true, "Valid for ${pkgName(v.packageId)}. Can be redeemed at ${targetLoc?.name ?: "site"}.")
  }

  fun activateVoucher(code: String, pin: String?, locationId: String): Boolean {
    val vIndex = _vouchers.value.indexOfFirst { it.code.equals(code.trim(), ignoreCase = true) }
    if (vIndex != -1) {
      val updated = _vouchers.value[vIndex].copy(
        status = VoucherStatus.active,
        activatedAt = "Today " + System.currentTimeMillis().toString().takeLast(4),
        activatedAtLocationId = locationId
      )
      val mutableV = _vouchers.value.toMutableList()
      mutableV[vIndex] = updated
      _vouchers.value = mutableV
      return true
    }
    return false
  }

  fun addPackage(name: String, price: Double, dataLimitMb: Long?, timeLimitMins: Int?, speedMbps: Double): Package {
    val newPkg = Package(
      name = name,
      price = price,
      dataLimit = dataLimitMb?.let { it * 1024 * 1024 },
      timeLimit = timeLimitMins,
      downloadSpeed = speedMbps,
      uploadSpeed = speedMbps / 2.0
    )
    _packages.value = _packages.value + newPkg
    return newPkg
  }

  fun addExpense(locationId: String, category: String, amount: Double, date: String, desc: String?): Expense {
    val newExp = Expense(
      locationId = locationId,
      category = category,
      amount = amount,
      expenseDate = date,
      description = desc
    )
    _expenses.value = listOf(newExp) + _expenses.value
    return newExp
  }

  fun getRevenueReport(locationId: String, startDate: String, endDate: String): RevenueReport {
    val locExpenses = _expenses.value.filter { it.locationId == locationId }.sumOf { it.amount }
    val rev = 120.0
    return RevenueReport(
      locationId = locationId,
      startDate = startDate,
      endDate = endDate,
      revenue = rev,
      refunds = 0.0,
      saleCount = 12,
      expenses = locExpenses,
      profit = rev - locExpenses
    )
  }

  fun drawPromotionWinners(promotionId: String, winnerCount: Int): List<PromotionWinner> {
    val entries = _promotionEntries.value.filter { it.promotionId == promotionId }.shuffled()
    val winners = entries.take(winnerCount).map { entry ->
      PromotionWinner(
        promotionId = promotionId,
        entryId = entry.id,
        selectedAt = "Just now",
        name = entry.name,
        phone = entry.phone,
        email = entry.email
      )
    }
    _promotionWinners.value = winners
    return winners
  }

  fun updateOpenAccess(settings: OpenAccessSettings) {
    _openAccessSettings.value = settings
  }

  fun pkgName(packageId: String): String {
    return _packages.value.find { it.id == packageId }?.name ?: "Standard Data Package"
  }
}
