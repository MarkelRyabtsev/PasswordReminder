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
                                INSTANCE!!.groupDao().addFolder(GroupEntity(1, "TEST1", 1))
                                INSTANCE!!.groupDao().addFolder(GroupEntity(2, "TEST2", 2))
                                INSTANCE!!.groupDao().addFolder(GroupEntity(3, "TEST3", 3))
                                INSTANCE!!.groupDao().addFolder(GroupEntity(4, "TEST4", 4))

                                INSTANCE!!.noteDao().addNote(NoteEntity(1, "Test1", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(2, "Test2", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(3, "Test3", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(4, "Test4", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(5, "Test5", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(6, "Test6", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(7, "Test7", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(8, "Test8", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(9, "Test9", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(10, "Test10", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(11, "Test11", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(12, "Test12", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(13, "Test13", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(14, "Test14", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(15, "Test15", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(16, "Test16", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(17, "Test17", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(18, "Test18", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(19, "Test19", "qwerty"))
                                INSTANCE!!.noteDao().addNote(NoteEntity(20, "Test20", "qwerty"))

                                INSTANCE!!.junctionDao().addJunction(1, 1)
                                INSTANCE!!.junctionDao().addJunction(2, 1)
                                INSTANCE!!.junctionDao().addJunction(3, 1)
                                INSTANCE!!.junctionDao().addJunction(1, 2)
                                INSTANCE!!.junctionDao().addJunction(3, 2)
                                INSTANCE!!.junctionDao().addJunction(2, 3)
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
private val junctionDao = fun(db: AppDatabase) = db.junctionDao()

internal val roomModule = module {
    single { database(get()) }
    single { noteDao(get()) }
    single { groupDao(get()) }
    single { junctionDao(get()) }
}