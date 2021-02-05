package com.markel.passwordreminder.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.markel.passwordreminder.database.entity.GroupNoteEntity
import com.markel.passwordreminder.database.entity.NoteEntity

@Dao
interface JunctionDao {
    @Query("""
        INSERT INTO group_note_junction
        VALUES(:groupId, :noteId)
    """)
    fun addJunction(noteId: Int, groupId: Int)

    @Insert
    fun insertJunction(junction: GroupNoteEntity)

    @Query("DELETE FROM group_note_junction WHERE noteId = :noteId")
    fun deleteJunctionByNoteId(noteId: Int)

    @Query("DELETE FROM group_note_junction WHERE groupId = :groupId")
    fun deleteJunctionByGroupId(groupId: Int)

    @Query("""
        SELECT * FROM notes AS n
        LEFT JOIN group_note_junction AS gnj ON gnj.noteId = n.id
        WHERE gnj.groupId = :groupId
        """)
    fun getNotesByGroup(groupId: Int): List<NoteEntity>
}