package com.markel.passwordreminder.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.markel.passwordreminder.database.AppDatabase
import com.markel.passwordreminder.database.entity.GroupEntity
import kotlinx.coroutines.*
import org.koin.dsl.module

private const val DATABASE_NAME = "PasswordReminder.db"

@Volatile
private var INSTANCE: AppDatabase? = null

private val database = fun(app: Application): AppDatabase {
    if (INSTANCE == null) {
        synchronized(AppDatabase::class.java) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(object: RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
                            applicationScope.launch {
                                INSTANCE!!.groupDao().addGroup(GroupEntity(1, "TEST1", 1))
                                INSTANCE!!.groupDao().addGroup(GroupEntity(2, "TEST2", 2))
                                INSTANCE!!.groupDao().addGroup(GroupEntity(3, "TEST3", 3))
                                INSTANCE!!.groupDao().addGroup(GroupEntity(4, "TEST4", 4))
                            }
                        }
                    })
                    .build()
            }
        }
    }
    return INSTANCE!!
}

private val noteDao = fun(db: AppDatabase) = db.noteDao()
private val groupDao = fun(db: AppDatabase) = db.groupDao()

internal val roomModule = module {
    single { database(get()) }
    single { noteDao(get()) }
    single { groupDao(get()) }
}