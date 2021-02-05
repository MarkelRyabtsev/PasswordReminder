package com.markel.passwordreminder.database.dao

import androidx.room.*
import com.markel.passwordreminder.database.entity.GroupEntity

@Dao
interface GroupDao {
    @Insert
    fun addGroup(group: GroupEntity)

    @Query("SELECT * FROM groups")
    fun getGroups(): List<GroupEntity>

    @Query("INSERT INTO groups VALUES(77, :folderName, (SELECT MAX(position) FROM groups) + 1)")
    fun addFolder(folderName: String) : Long
}