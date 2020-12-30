package com.markel.passwordreminder.data.repository

import com.markel.passwordreminder.data.ApiCaller
import com.markel.passwordreminder.data.CoroutineCaller
import com.markel.passwordreminder.database.dao.GroupDao
import com.markel.passwordreminder.database.entity.GroupEntity

class GroupRepository(
    private val groupDao: GroupDao
) : CoroutineCaller by ApiCaller {

    suspend fun addGroup(group: GroupEntity) = apiCall {
        groupDao.addGroup(group)
    }

    suspend fun getGroups() = apiCall {
        groupDao.getGroups()
    }
}