package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ImovelDao
import com.example.data.local.dao.LocalidadeDao
import com.example.data.local.dao.QuarteiraoDao
import com.example.data.local.dao.RgDao
import com.example.data.local.entity.ImovelEntity
import com.example.data.local.entity.LocalidadeEntity
import com.example.data.local.entity.QuarteiraoEntity
import com.example.data.local.entity.RgEntity

@Database(
    entities = [
        RgEntity::class,
        LocalidadeEntity::class,
        QuarteiraoEntity::class,
        ImovelEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rgDao(): RgDao
    abstract fun localidadeDao(): LocalidadeDao
    abstract fun quarteiraoDao(): QuarteiraoDao
    abstract fun imovelDao(): ImovelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rg203_database.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
