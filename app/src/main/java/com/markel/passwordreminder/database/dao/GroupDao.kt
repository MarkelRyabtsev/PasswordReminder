package com.markel.passwordreminder.database.dao

import androidx.room.*
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.database.entity.NoteEntity

@Dao
interface GroupDao {
    @Insert
    fun addFolder(folder: GroupEntity) : Long

    @Delete
    fun deleteFolder(folder: GroupEntity)

    @Query("SELECT * FROM groups")
    fun getGroups(): List<GroupEntity>

    @Query("SELECT * FROM groups WHERE id = :folderId")
    fun getFolderById(folderId: Int): GroupEntity?

    @Query("INSERT INTO groups VALUES(77, :folderName, (SELECT MAX(position) FROM groups) + 1)")
    fun addFolder(folderName: String) : Long

    @Query("SELECT MAX(position) FROM groups")
    fun getMaxPosition() : Int

    @Transaction
    fun addFolderWithNotes(folderName: String, notes: List<NoteEntity>) {
        val position = getMaxPosition() + 1
        val folderId = addFolder(GroupEntity(name = folderName, position = position))
        for (note in notes) {
            addJunction(note.id, folderId)
        }
    }

    @Transaction
    fun deleteFolderWithNotes(folder: GroupEntity) {
        deleteJunctionByFolderId(folder.id)
        deleteFolder(folder)
    }

    @Query("DELETE FROM group_note_junction WHERE groupId = :folderId")
    fun deleteJunctionByFolderId(folderId: Int)

    @Query("INSERT INTO group_note_junction VALUES(:groupId, :noteId)")
    fun addJunction(noteId: Int, groupId: Long)
}