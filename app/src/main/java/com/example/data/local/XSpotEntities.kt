package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_locations")
data class LocationEntity(
  @PrimaryKey val id: String,
  val organizationId: String,
  val clusterId: String?,
  val name: String,
  val address: String?,
  val latitude: Double?,
  val longitude: Double?,
  val monthlyRent: Double?,
  val electricity: Double?,
  val internetCost: Double?,
  val isActive: Boolean
)

@Entity(tableName = "cached_routers")
data class RouterEntity(
  @PrimaryKey val id: String,
  val locationId: String,
  val name: String,
  val ipAddress: String,
  val apiPort: Int,
  val apiUsername: String,
  val status: String,
  val lastSeenAt: String?
)

@Entity(tableName = "cached_vouchers")
data class VoucherEntity(
  @PrimaryKey val id: String,
  val code: String,
  val pin: String?,
  val packageId: String,
  val locationId: String,
  val status: String,
  val createdAt: String
)

@Entity(tableName = "cached_expenses")
data class ExpenseEntity(
  @PrimaryKey val id: String,
  val locationId: String,
  val category: String,
  val description: String?,
  val amount: Double,
  val expenseDate: String
)

@Entity(tableName = "pending_sync_actions")
data class PendingSyncActionEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val actionType: String, // CREATE_VOUCHER_BULK, ADD_EXPENSE, REVOKE_VOUCHER, DISCONNECT_SESSION
  val payloadJson: String,
  val createdAt: Long = System.currentTimeMillis()
)
