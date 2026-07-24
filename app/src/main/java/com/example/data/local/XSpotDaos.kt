package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
  @Query("SELECT * FROM cached_locations")
  fun getAllLocations(): Flow<List<LocationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLocations(locations: List<LocationEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLocation(location: LocationEntity)
}

@Dao
interface RouterDao {
  @Query("SELECT * FROM cached_routers")
  fun getAllRouters(): Flow<List<RouterEntity>>

  @Query("SELECT * FROM cached_routers WHERE locationId = :locationId")
  fun getRoutersForLocation(locationId: String): Flow<List<RouterEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRouters(routers: List<RouterEntity>)
}

@Dao
interface VoucherDao {
  @Query("SELECT * FROM cached_vouchers ORDER BY createdAt DESC")
  fun getAllVouchers(): Flow<List<VoucherEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertVouchers(vouchers: List<VoucherEntity>)
}

@Dao
interface PendingSyncDao {
  @Query("SELECT * FROM pending_sync_actions ORDER BY createdAt ASC")
  fun getAllPendingActions(): Flow<List<PendingSyncActionEntity>>

  @Insert
  suspend fun addPendingAction(action: PendingSyncActionEntity)

  @Query("DELETE FROM pending_sync_actions WHERE id = :id")
  suspend fun deletePendingAction(id: Int)

  @Query("DELETE FROM pending_sync_actions")
  suspend fun clearAll()
}
