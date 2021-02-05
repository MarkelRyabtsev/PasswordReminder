package com.markel.passwordreminder.data.repository

import com.markel.passwordreminder.data.ApiCaller
import com.markel.passwordreminder.data.CoroutineCaller
import com.markel.passwordreminder.database.dao.GroupDao
import com.markel.passwordreminder.database.dao.JunctionDao
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.database.entity.GroupNoteEntity
import com.markel.passwordreminder.database.entity.NoteEntity

class GroupRepository(
    private val groupDao: GroupDao,
    private val junctionDao: JunctionDao
) : CoroutineCaller by ApiCaller {

    suspend fun addGroup(group: GroupEntity) = apiCall {
        groupDao.addGroup(group)
    }

    suspend fun getGroups() = apiCall {
        groupDao.getGroups()
    }

    suspend fun saveFolder(folderName: String, includedNotes: List<NoteEntity>) = apiCall {
        val folderId = groupDao.addFolder(folderName).toInt()
        for(note in includedNotes) {
            junctionDao.insertJunction(GroupNoteEntity(folderId, note.id))
        }
    }
}