package com.markel.passwordreminder.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.markel.passwordreminder.database.dao.*
import com.markel.passwordreminder.database.entity.*

@Database(
    entities = [
        NoteEntity::class,
        GroupEntity::class,
        GroupNoteEntity::class
    ], version = 2, exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    abstract fun groupDao(): GroupDao

    abstract fun junctionDao(): JunctionDao
}