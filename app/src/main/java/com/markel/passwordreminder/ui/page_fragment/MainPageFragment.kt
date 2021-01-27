package com.markel.passwordreminder.ui.page_fragment

import android.os.Bundle
import android.view.View
import com.markel.passwordreminder.base.fragment.BasePageFragment
import com.markel.passwordreminder.base.vo.Status
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.observe
import com.markel.passwordreminder.ui.page_fragment.view_model.MainPageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainPageFragment : BasePageFragment() {

    private val mainViewModel: MainPageViewModel by viewModel()

    companion object {
        @JvmStatic
        fun newInstance() = MainPageFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        observeUpdateNotes()
    }

    private fun observeData() {
        observe(mainViewModel.notes) {
            when (it.status) {
                Status.LOADING -> displayProgress()
                Status.SUCCESS -> setData(it.data)
                Status.ERROR -> {
                    displayError(it.message ?: "")
                }
            }
        }
    }

    private fun observeUpdateNotes() {
        observe(viewModel.updateNote) {
            it.data?.let { isUpdate ->
                if (isUpdate) mainViewModel.updateNotes()
            }
        }
    }

    private fun setData(noteList: List<NoteEntity>?) {
        noteList?.let {
            if (it.isEmpty())
                displayTextByEmptyList()
            else {
                displayNormal()
                noteAdapter.setList(it)
            }
        }
    }
}