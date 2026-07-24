package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    LocationEntity::class,
    RouterEntity::class,
    VoucherEntity::class,
    ExpenseEntity::class,
    PendingSyncActionEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun locationDao(): LocationDao
  abstract fun routerDao(): RouterDao
  abstract fun voucherDao(): VoucherDao
  abstract fun pendingSyncDao(): PendingSyncDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "xspot_field_db"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
