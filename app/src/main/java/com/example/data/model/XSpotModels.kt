package com.example.data.model

import java.util.UUID

enum class UserRole {
  super_admin,
  location_admin
}

data class User(
  val id: String = UUID.randomUUID().toString(),
  val organizationId: String? = null,
  val email: String,
  val phone: String? = null,
  val firstName: String,
  val lastName: String,
  val role: UserRole = UserRole.super_admin,
  val isActive: Boolean = true,
  val emailVerified: Boolean = true,
  val phoneVerified: Boolean = true,
  val createdAt: String = "2026-07-24T00:00:00Z",
  val updatedAt: String = "2026-07-24T00:00:00Z"
)

data class Organization(
  val id: String = UUID.randomUUID().toString(),
  val name: String,
  val createdAt: String = "2026-07-24T00:00:00Z",
  val updatedAt: String = "2026-07-24T00:00:00Z"
)

data class Location(
  val id: String = UUID.randomUUID().toString(),
  val organizationId: String = "org-1",
  val clusterId: String? = null,
  val name: String,
  val address: String? = null,
  val latitude: Double? = null,
  val longitude: Double? = null,
  val monthlyRent: Double? = 0.0,
  val electricity: Double? = 0.0,
  val internetCost: Double? = 0.0,
  val isActive: Boolean = true,
  val createdAt: String = "2026-07-24T00:00:00Z",
  val updatedAt: String = "2026-07-24T00:00:00Z"
)

data class LocationStats(
  val locationId: String,
  val routerCount: Int = 0,
  val routersOnline: Int = 0,
  val activeVouchers: Int = 0,
  val availableVouchers: Int = 0,
  val totalRevenue: Double = 0.0
)

data class Cluster(
  val id: String = UUID.randomUUID().toString(),
  val organizationId: String = "org-1",
  val name: String,
  val description: String? = null,
  val sharingEnabled: Boolean = true,
  val locationCount: Int = 0,
  val createdAt: String = "2026-07-24T00:00:00Z",
  val updatedAt: String = "2026-07-24T00:00:00Z"
)

enum class RouterStatus {
  online,
  offline,
  unknown
}

data class Router(
  val id: String = UUID.randomUUID().toString(),
  val locationId: String,
  val name: String,
  val ipAddress: String,
  val apiPort: Int = 8728,
  val apiUsername: String = "admin",
  val status: RouterStatus = RouterStatus.online,
  val lastSeenAt: String? = "Just now",
  val createdAt: String = "2026-07-24T00:00:00Z",
  val updatedAt: String = "2026-07-24T00:00:00Z"
)

data class RouterHealth(
  val online: Boolean = true,
  val identity: String? = "MikroTik Router",
  val uptime: String? = "14d 3h 22m",
  val cpuLoad: Int? = 32,
  val activeHotspotUsers: Int? = 6,
  val message: String? = null
)

data class TestConnectionResult(
  val success: Boolean,
  val identity: String? = null,
  val rosVersion: String? = null,
  val boardName: String? = null,
  val message: String? = null
)

enum class PackageStatus {
  active,
  inactive,
  archived
}

data class Package(
  val id: String = UUID.randomUUID().toString(),
  val organizationId: String = "org-1",
  val name: String,
  val description: String? = null,
  val timeLimit: Int? = 1440, // minutes
  val dataLimit: Long? = 1073741824, // bytes (e.g. 1GB)
  val downloadSpeed: Double? = 5.0, // Mbps
  val uploadSpeed: Double? = 2.0, // Mbps
  val price: Double = 1.0,
  val currency: String = "USD",
  val isUnlimited: Boolean = false,
  val status: PackageStatus = PackageStatus.active,
  val createdAt: String = "2026-07-24T00:00:00Z",
  val updatedAt: String = "2026-07-24T00:00:00Z"
)

enum class VoucherStatus {
  created,
  generated,
  activated,
  active,
  expired,
  revoked
}

data class Voucher(
  val id: String = UUID.randomUUID().toString(),
  val code: String,
  val pin: String? = null,
  val packageId: String,
  val locationId: String,
  val batchId: String? = null,
  val status: VoucherStatus = VoucherStatus.active,
  val activatedAt: String? = null,
  val expiresAt: String? = null,
  val activatedAtRouterId: String? = null,
  val activatedAtLocationId: String? = null,
  val customerMacAddress: String? = null,
  val createdAt: String = "2026-07-24T00:00:00Z",
  val updatedAt: String = "2026-07-24T00:00:00Z"
)

data class Transaction(
  val id: String = UUID.randomUUID().toString(),
  val voucherId: String? = null,
  val locationId: String,
  val type: String = "voucher_sale", // voucher_sale, refund, adjustment
  val status: String = "completed",
  val amount: Double,
  val currency: String = "USD",
  val createdAt: String = "2026-07-24T00:00:00Z"
)

data class RevenueReport(
  val locationId: String,
  val startDate: String,
  val endDate: String,
  val revenue: Double,
  val refunds: Double,
  val saleCount: Int,
  val expenses: Double,
  val profit: Double
)

data class Expense(
  val id: String = UUID.randomUUID().toString(),
  val locationId: String,
  val category: String, // rent, electricity, internet, other
  val description: String? = null,
  val amount: Double,
  val expenseDate: String,
  val createdAt: String = "2026-07-24T00:00:00Z",
  val updatedAt: String = "2026-07-24T00:00:00Z"
)

data class Promotion(
  val id: String = UUID.randomUUID().toString(),
  val organizationId: String = "org-1",
  val name: String,
  val prize: String,
  val description: String? = null,
  val startDate: String,
  val endDate: String,
  val status: String = "active", // draft, active, ended
  val createdAt: String = "2026-07-24T00:00:00Z",
  val updatedAt: String = "2026-07-24T00:00:00Z"
)

data class PromotionEntry(
  val id: String = UUID.randomUUID().toString(),
  val promotionId: String,
  val name: String,
  val phone: String? = null,
  val email: String? = null,
  val createdAt: String = "2026-07-24T00:00:00Z"
)

data class PromotionWinner(
  val id: String = UUID.randomUUID().toString(),
  val promotionId: String,
  val entryId: String,
  val selectedAt: String,
  val name: String,
  val phone: String? = null,
  val email: String? = null
)

data class DashboardOverview(
  val totalLocations: Int = 12,
  val totalRouters: Int = 11,
  val routersOnline: Int = 9,
  val activeSessions: Int = 42,
  val vouchersSoldToday: Int = 184,
  val revenueToday: Double = 842.0,
  val revenueThisMonth: Double = 14250.0
)

data class ActivityItem(
  val id: String = UUID.randomUUID().toString(),
  val kind: String, // voucher_activated, router_status_change
  val refId: String = UUID.randomUUID().toString(),
  val locationName: String,
  val message: String,
  val signalBars: Int = 4, // 0 to 4
  val occurredAt: String = "2m ago"
)

data class OpenAccessSettings(
  val organizationId: String = "org-1",
  val isEnabled: Boolean = false,
  val startTime: String? = "08:00",
  val endTime: String? = "18:00",
  val downloadSpeedMbps: Double? = 2.0,
  val uploadSpeedMbps: Double? = 1.0,
  val welcomeMessage: String? = "Welcome to XSpot Free Wi-Fi!",
  val updatedAt: String = "2026-07-24T00:00:00Z"
)

data class HotspotSession(
  val sessionId: String = UUID.randomUUID().toString(),
  val user: String,
  val address: String? = "10.5.50.4",
  val macAddress: String? = "0x1A:B2:3C:4D:5E:6F",
  val uptime: String? = "22m 14s",
  val bytesIn: Long? = 88080384, // 84MB
  val bytesOut: Long? = 12582912, // 12MB
  val startTime: String? = "14:10",
  val endTime: String? = null
)
