package com.markel.passwordreminder.ui.folders.new_folder

import androidx.lifecycle.MutableLiveData
import com.markel.passwordreminder.base.ui.BaseViewModel
import com.markel.passwordreminder.base.vo.Resource
import com.markel.passwordreminder.data.RequestResult
import com.markel.passwordreminder.data.repository.GroupRepository
import com.markel.passwordreminder.data.repository.NoteRepository
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.setError
import com.markel.passwordreminder.ext.setLoading
import com.markel.passwordreminder.ext.setSuccess

class NewFolderViewModel(
    private val groupRepository: GroupRepository,
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    val noteListLiveData: MutableLiveData<Resource<List<NoteEntity>>> = MutableLiveData()
    val newFolderLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    private var noteList: List<NoteEntity> = listOf()

    init {
        getNotes()
    }

    fun saveFolder(folderName: String, includedNotes: List<NoteEntity>) {
        makeRequest({ groupRepository.addFolderWithNotes(folderName, includedNotes)}) {
            when (it) {
                is RequestResult.Success -> {
                    newFolderLiveData.setSuccess(true)
                }
                is RequestResult.Error -> newFolderLiveData.setError(it.error)
            }
        }
    }

    fun getNotesById(noteIds: List<Int>?) : List<NoteEntity>? {
        return noteIds?.let {
            noteList.filter { note -> note.id in noteIds }
        }
    }

    private fun getNotes() {
        noteListLiveData.setLoading()
        makeRequest({ noteRepository.getAllNotes() }) {
            when (it) {
                is RequestResult.Success -> {
                    noteListLiveData.setSuccess(it.result)
                    noteList = it.result
                }
                is RequestResult.Error -> {
                    noteListLiveData.setError(it.error)
                }
            }
        }
    }
}