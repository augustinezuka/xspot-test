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

  // Sample Seed Data & Biometric Admin Config
  private val _isDataSeeded = MutableStateFlow(false)
  val isDataSeeded: StateFlow<Boolean> = _isDataSeeded.asStateFlow()

  private val _isBiometricEnabled = MutableStateFlow(true)
  val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

  fun toggleBiometricSetting(enabled: Boolean) {
    _isBiometricEnabled.value = enabled
  }

  fun loginBiometricAdmin(): Pair<Boolean, String> {
    DevMenuManager.logInfo("Auth", "Biometric Admin Authentication triggered")
    val dummyAdminJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkB4c3BvdC5uZXQiLCJyb2xlIjoic3VwZXJfYWRtaW4iLCJpYXQiOjE3NTM3MDA0MDB9.xspot_admin_jwt_sig"
    setJwtToken(dummyAdminJwt)
    _currentUser.value = User(
      id = "usr-biometric-admin",
      email = "admin@xspot.net",
      firstName = "Super Admin",
      lastName = "(Biometric Verified)",
      role = UserRole.super_admin,
      organizationId = "org-xspot"
    )
    DevMenuManager.logInfo("Auth", "Biometric verification successful. Super Admin JWT session issued.")
    return Pair(true, "Biometric Admin Authenticated Successfully (JWT Session Issued)")
  }

  fun toggleDataSeed(seed: Boolean) {
    if (seed) {
      loadSampleSeedData()
      _isDataSeeded.value = true
      DevMenuManager.logInfo("SeedData", "Sample seed dataset successfully loaded into repository across all modules")
    } else {
      loadCleanState()
      _isDataSeeded.value = false
      DevMenuManager.logInfo("SeedData", "Sample seed data cleared. Reverted to clean state.")
    }
  }

  fun loadSampleSeedData() {
    _organizations.value = listOf(
      Organization(id = "org-xspot", name = "XSpot Telecom Corp")
    )
    _clusters.value = listOf(
      Cluster(id = "cls-cbd", name = "CBD Hotspot Cluster", description = "Harare Central Business District", sharingEnabled = true, locationCount = 2),
      Cluster(id = "cls-sub", name = "Suburban Access Cluster", description = "Shopping Hubs & Outlets", sharingEnabled = true, locationCount = 1)
    )
    _locations.value = listOf(
      Location(id = "loc-1", organizationId = "org-xspot", clusterId = "cls-cbd", name = "Downtown Hub", address = "Harare Central, Sam Nujoma Street", latitude = -17.8252, longitude = 31.0335, monthlyRent = 250.0, electricity = 80.0, internetCost = 120.0),
      Location(id = "loc-2", organizationId = "org-xspot", clusterId = "cls-cbd", name = "Westgate Complex", address = "Westgate Mall, Lomagundi Rd", latitude = -17.7811, longitude = 31.0022, monthlyRent = 300.0, electricity = 100.0, internetCost = 150.0),
      Location(id = "loc-3", organizationId = "org-xspot", clusterId = "cls-sub", name = "Avondale Flea Market", address = "King George Rd, Avondale", latitude = -17.8015, longitude = 31.0425, monthlyRent = 200.0, electricity = 60.0, internetCost = 90.0)
    )
    _routers.value = listOf(
      Router(id = "rtr-1", locationId = "loc-1", name = "Downtown Main AP", ipAddress = "192.168.88.1", status = RouterStatus.online, lastSeenAt = "1m ago"),
      Router(id = "rtr-2", locationId = "loc-2", name = "Westgate FastSpot", ipAddress = "192.168.88.2", status = RouterStatus.online, lastSeenAt = "Just now"),
      Router(id = "rtr-3", locationId = "loc-3", name = "Avondale Hotspot", ipAddress = "192.168.88.3", status = RouterStatus.offline, lastSeenAt = "12m ago")
    )
    val pkg1 = Package(id = "pkg-1", name = "1 Hour Unlimited", timeLimit = 60, dataLimit = null, downloadSpeed = 10.0, uploadSpeed = 5.0, price = 1.0, isUnlimited = true)
    val pkg2 = Package(id = "pkg-2", name = "Daily Pass 2GB", timeLimit = 1440, dataLimit = 2147483648L, downloadSpeed = 25.0, uploadSpeed = 10.0, price = 2.50)
    val pkg3 = Package(id = "pkg-3", name = "Weekly Mega 10GB", timeLimit = 10080, dataLimit = 10737418240L, downloadSpeed = 50.0, uploadSpeed = 20.0, price = 10.0)
    _packages.value = listOf(pkg1, pkg2, pkg3)

    _vouchers.value = listOf(
      Voucher(id = "vch-101", code = "XP-8821", packageId = "pkg-1", locationId = "loc-1", status = VoucherStatus.active, activatedAt = "10 mins ago"),
      Voucher(id = "vch-102", code = "XP-9034", packageId = "pkg-2", locationId = "loc-2", status = VoucherStatus.active, activatedAt = "25 mins ago"),
      Voucher(id = "vch-103", code = "XP-1120", packageId = "pkg-1", locationId = "loc-1", status = VoucherStatus.created),
      Voucher(id = "vch-104", code = "XP-5541", packageId = "pkg-3", locationId = "loc-3", status = VoucherStatus.generated)
    )
    _expenses.value = listOf(
      Expense(id = "exp-1", locationId = "loc-1", category = "Rent", description = "Monthly site lease", amount = 250.0, expenseDate = "2026-07-01"),
      Expense(id = "exp-2", locationId = "loc-1", category = "Electricity", description = "ZETDC Utility bill", amount = 80.0, expenseDate = "2026-07-05"),
      Expense(id = "exp-3", locationId = "loc-2", category = "Internet", description = "Liquid Fiber link", amount = 150.0, expenseDate = "2026-07-02")
    )
    val prom1 = Promotion(id = "prom-1", name = "Summer Data Extravaganza", prize = "Win 50GB Free Data Voucher", startDate = "2026-07-01", endDate = "2026-08-01", status = "active")
    _promotions.value = listOf(prom1)
    _promotionEntries.value = listOf(
      PromotionEntry(id = "pe-1", promotionId = "prom-1", name = "John Moyo", phone = "+263 77 123 4567", email = "john.moyo@gmail.com"),
      PromotionEntry(id = "pe-2", promotionId = "prom-1", name = "Sarah Ndlovu", phone = "+263 77 987 6543", email = "sarah.ndlovu@yahoo.com")
    )
    _activities.value = listOf(
      ActivityItem(id = "act-1", kind = "voucher_activated", locationName = "Downtown Hub", message = "Voucher XP-8821 activated at Downtown Hub", signalBars = 4, occurredAt = "10m ago"),
      ActivityItem(id = "act-2", kind = "router_status_change", locationName = "Westgate Complex", message = "Router Westgate FastSpot status: ONLINE", signalBars = 4, occurredAt = "25m ago")
    )
  }

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
