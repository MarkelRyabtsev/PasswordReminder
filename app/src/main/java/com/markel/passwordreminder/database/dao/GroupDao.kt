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

    @Query("SELECT MAX(position) FROM groups")
    fun getMaxPosition() : Int

    @Transaction
    fun addFolderWithNotes(folderName: String, notes: List<NoteEntity>) {
        val position = getMaxPosition() + 1
        val folderId = addFolder(GroupEntity(name = folderName, position = position)).toInt()
        for (note in notes) {
            addJunction(note.id, folderId)
        }
    }

    @Transaction
    fun updateFolderWithNotes(folderId: Int, newFolderName: String, notes: List<NoteEntity>) {
        val folder = getFolderById(folderId)
        folder?.let {
            if (folder.name != newFolderName){
                folder.name = newFolderName
                updateFolder(folder)
            }
        }

        deleteJunctionByFolderId(folderId)

        for (note in notes) {
            addJunction(note.id, folderId)
        }
    }

    @Transaction
    fun deleteFolderWithNotes(folder: GroupEntity) {
        deleteJunctionByFolderId(folder.id)
        deleteFolder(folder)
    }

    @Update
    fun updateFolder(folder: GroupEntity)

    @Query("DELETE FROM group_note_junction WHERE groupId = :folderId")
    fun deleteJunctionByFolderId(folderId: Int)

    @Query("INSERT INTO group_note_junction VALUES(:groupId, :noteId)")
    fun addJunction(noteId: Int, groupId: Int)
}