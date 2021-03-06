package com.markel.passwordreminder.ui.page_fragment

import android.os.Bundle
import android.view.View
import com.markel.passwordreminder.base.fragment.BasePageFragment
import com.markel.passwordreminder.base.vo.Status
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.observe
import com.markel.passwordreminder.ui.page_fragment.view_model.AdditionalPageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AdditionalPageFragment : BasePageFragment() {

    private val pageViewModel: AdditionalPageViewModel by viewModel {
        parametersOf(getGroupId())
    }

    companion object {

        private const val GROUP_ID = "group_id"

        @JvmStatic
        fun newInstance(groupId: Int): AdditionalPageFragment {
            return AdditionalPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(GROUP_ID, groupId)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        observeUpdateNotes()
    }

    private fun observeData() {
        observe(pageViewModel.notes) {
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
                if (isUpdate) pageViewModel.updateNotes()
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

    private fun getGroupId() = arguments?.getInt(GROUP_ID) ?: 1
}