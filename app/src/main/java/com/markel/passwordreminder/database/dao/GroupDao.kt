package com.markel.passwordreminder.database.dao

import androidx.room.*
import com.markel.passwordreminder.database.entity.GroupEntity

@Dao
interface GroupDao {
    @Insert
    fun addGroup(group: GroupEntity)

    @Query("SELECT * FROM groups")
    fun getGroups(): List<GroupEntity>
}