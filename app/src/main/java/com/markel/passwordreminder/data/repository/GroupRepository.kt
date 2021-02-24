package com.markel.passwordreminder.data.repository

import com.markel.passwordreminder.data.ApiCaller
import com.markel.passwordreminder.data.CoroutineCaller
import com.markel.passwordreminder.database.dao.GroupDao
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.database.entity.NoteEntity

class GroupRepository(
    private val groupDao: GroupDao
) : CoroutineCaller by ApiCaller {

    suspend fun addGroup(group: GroupEntity) = apiCall {
        groupDao.addFolder(group)
    }

    suspend fun getGroups() = apiCall {
        groupDao.getGroups()
    }

    suspend fun getFolderById(folderId: Int) = apiCall {
        groupDao.getFolderById(folderId)
    }

    suspend fun addFolderWithNotes(folderName: String, includedNotes: List<NoteEntity>) = apiCall {
        groupDao.addFolderWithNotes(folderName, includedNotes)
    }

    suspend fun saveFolderChanges(folderId: Int, folderName: String, includedNotes: List<NoteEntity>) = apiCall {
        groupDao.updateFolderWithNotes(folderId, folderName, includedNotes)
    }

    suspend fun deleteFolderWithNotes(folder: GroupEntity) = apiCall {
        groupDao.deleteFolderWithNotes(folder)
    }
}