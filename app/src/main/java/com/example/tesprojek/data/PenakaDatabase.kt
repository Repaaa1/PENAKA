package com.example.tesprojek.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Kategori::class, Materi::class, Kuis::class, PilihanKuis::class, HasilKuis::class],
    version = 1,
    exportSchema = false
)
abstract class PenakaDatabase : RoomDatabase() {

    abstract fun dao(): PenakaDao

    companion object {
        @Volatile
        private var instance: PenakaDatabase? = null

        fun getInstance(context: Context): PenakaDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PenakaDatabase::class.java,
                    "penaka.db"
                ).build().also { instance = it }
            }
    }
}