package com.markel.passwordreminder.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.markel.passwordreminder.database.AppDatabase
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.database.entity.NoteEntity
import kotlinx.coroutines.*
import org.koin.dsl.module
import kotlin.random.Random

private const val DATABASE_NAME = "PasswordReminder.db"
private val PREPOPULATE_DATA_GROUP = GroupEntity(1, "All", 1)
private val PREPOPULATE_DATA_NOTE = NoteEntity(1, "test1", "qwerty")

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
                                INSTANCE!!.groupDao().addGroup(PREPOPULATE_DATA_GROUP)
                                INSTANCE!!.groupDao().addGroup(GroupEntity(2, "TEST1", 2))
                                INSTANCE!!.groupDao().addGroup(GroupEntity(3, "TEST2", 3))
                                INSTANCE!!.groupDao().addGroup(GroupEntity(4, "TEST3", 4))
                                INSTANCE!!.groupDao().addGroup(GroupEntity(5, "TEST4", 5))
                                for (i in 1..20) {
                                    when (i) {
                                        in 1..5 -> INSTANCE!!.noteDao().addNote(NoteEntity(i, "test $i", "qwerty", Random.nextBoolean(), 2))
                                        in 6..10 -> INSTANCE!!.noteDao().addNote(NoteEntity(i, "test $i", "qwerty", Random.nextBoolean(), 3))
                                        in 11..15 -> INSTANCE!!.noteDao().addNote(NoteEntity(i, "test $i", "qwerty", Random.nextBoolean(), 4))
                                        in 16..20 -> INSTANCE!!.noteDao().addNote(NoteEntity(i, "test $i", "qwerty", Random.nextBoolean(), 5))
                                    }
                                }
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