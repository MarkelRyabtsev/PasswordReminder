package com.markel.passwordreminder.ui.folders

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.vo.Status
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.observe
import com.markel.passwordreminder.ext.setSafeOnClickListener
import com.markel.passwordreminder.ext.toast
import com.markel.passwordreminder.ui.folders.IncludeNotesFragment.Companion.NEW_CHECKED_NOTES
import com.markel.passwordreminder.ui.folders.adapter.SelectedNotesAdapter
import com.markel.passwordreminder.ui.folders.view_model.FoldersViewModel
import kotlinx.android.synthetic.main.folders_fragment_new.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class NewFolderFragment : Fragment() {

    private val viewModel by lazy { requireActivity().getViewModel<FoldersViewModel>() }

    private lateinit var adapter: SelectedNotesAdapter
    private lateinit var buttonSave: MenuItem

    companion object {
        const val CURRENT_FOLDER_ID = "current_folder_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.folders_fragment_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        observeIncludedNotes()
        observeCurrentNotes()
        observeCreatingFolder()

        initAdapter()
        initListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.activity_folders_menu, menu)
        buttonSave = menu.findItem(R.id.action_save)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                viewModel.saveFolder(
                    et_folder_name.text.toString(),
                    getIncludedNotes()
                )
                return true
            }
        }

        return false
    }

    private fun showButtonSave() {
        buttonSave.isVisible = true
    }

    private fun hideButtonSave() {
        buttonSave.isVisible = false
    }

    private fun observeCurrentNotes() {
        val observableData = findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Int>(CURRENT_FOLDER_ID)
        observableData?.let {
            observe(it) { folderId ->
                viewModel.getNotesByFolderId(folderId)
            }
        }
    }

    private fun observeIncludedNotes() {
        val observableData = findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<List<Int>>(NEW_CHECKED_NOTES)
        observableData?.let {
            observe(it) { noteIds ->
                setData(viewModel.getNotesById(noteIds))
            }
        }
    }

    private fun observeData() {
        observe(viewModel.notesInFolderLiveData) {
            when (it.status) {
                Status.LOADING -> {
                    //displayProgress()
                }
                Status.SUCCESS -> setData(it.data)
                Status.ERROR -> {
                    //displayError(it.message ?: "")
                }
            }
        }
    }

    private fun observeCreatingFolder() {
        observe(viewModel.newFolderLiveData) {
            when (it.status) {
                Status.SUCCESS -> viewModel.updateGroupList()
                Status.ERROR -> requireActivity().toast(it.message)
            }

            findNavController().popBackStack()
        }
    }

    private fun initAdapter() {
        adapter = SelectedNotesAdapter(requireActivity())
        rv_included_notes.adapter = adapter
    }

    private fun initListeners() {
        ll_add_notes.setSafeOnClickListener {
            findNavController().navigate(
                R.id.action_newFolderFragment_to_addNotesFragment,
                bundleOf(IncludeNotesFragment.PASSED_CHECKED_NOTES to adapter.adapterList.map { it.id })
            )
        }
        et_folder_name.doAfterTextChanged {
            if (it.isNullOrEmpty()) hideButtonSave()
            else showButtonSave()
        }
    }

    private fun setData(noteList: List<NoteEntity>?) {
        noteList?.let {
            adapter.setList(it)
        }
    }

    private fun getIncludedNotes() : List<NoteEntity> {
        return adapter.adapterList
    }
}