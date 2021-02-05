package com.markel.passwordreminder.base.fragment

import android.os.Bundle
import android.view.ActionMode
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.markel.passwordreminder.R
import com.markel.passwordreminder.custom.CustomSnackbar
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.hideGroupViews
import com.markel.passwordreminder.ext.show
import com.markel.passwordreminder.ext.toast
import com.markel.passwordreminder.ui.dialog.AddNoteDialog
import com.markel.passwordreminder.ui.main.MainActivity
import com.markel.passwordreminder.ui.page_fragment.ActionModeController
import com.markel.passwordreminder.ui.page_fragment.note.*
import com.markel.passwordreminder.ui.page_fragment.view_model.EditNoteViewModel
import kotlinx.android.synthetic.main.fragment_page.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

abstract class BasePageFragment : Fragment(R.layout.fragment_page),
    SwipeRefreshLayout.OnRefreshListener {

    protected val viewModel by lazy { requireActivity().getViewModel<EditNoteViewModel>() }

    protected lateinit var noteAdapter: NoteAdapter
    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initAdapter()
    }

    private fun initListeners() {
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun initAdapter() {
        noteAdapter = NoteAdapter(this.requireContext(), ::onItemClick, ::onItemActionClick)
        rvNotes.adapter = noteAdapter
        val tracker = SelectionTracker.Builder<NoteEntity>(
            // индетифицируем трекер в констексе
            "someId",
            rvNotes,
            // для Long ItemKeyProvider реализован в виде StableIdKeyProvider
            MyItemKeyProvider(noteAdapter),
            MyItemLookup(rvNotes),
            // существуют аналогичные реализации для Long и String
            StorageStrategy.createParcelableStorage(NoteEntity::class.java)
        )
            .build()
        noteAdapter.tracker = tracker

        tracker.addObserver(object : SelectionTracker.SelectionObserver<NoteEntity>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                if (tracker.hasSelection() && actionMode == null) {
                    actionMode = requireActivity().startActionMode(ActionModeController(tracker))
                    setSelectedTitle(tracker.selection.size())
                } else if (!tracker.hasSelection()) {
                    actionMode?.finish()
                    actionMode = null
                } else {
                    setSelectedTitle(tracker.selection.size())
                }
            }
        })
    }

    private fun setSelectedTitle(selected: Int) {
        actionMode?.title = "$selected"
    }

    override fun onRefresh() {
        viewModel.updateNotes()
    }

    fun displayProgress() {
        progressBar.show()
        hideGroupViews(rvNotes, tvError)
    }

    fun displayNormal() {
        rvNotes.show()
        hideGroupViews(progressBar, tvError)
        swipeRefreshLayout.isRefreshing = false
    }

    private fun onItemClick(clickedItem: NoteEntity?) {
        viewModel.editableNote = clickedItem
    }

    private fun onItemActionClick(clickedItem: NoteItemClick) {
        when (clickedItem.operationType) {
            OperationType.EDIT -> showEditDialog(clickedItem.note)
            OperationType.SHARE -> context?.toast("Share ${clickedItem.note.id}")
            OperationType.DELETE -> deleteItem(
                clickedItem.note.id,
                clickedItem.noteIndex,
                clickedItem.note.description
            )
        }
    }

    private fun showEditDialog(note: NoteEntity) {
        (requireActivity() as? MainActivity)?.editNoteDialog?.show(
            requireActivity().supportFragmentManager,
            AddNoteDialog.TAG
        )
    }

    private fun deleteItem(id: Int, index: Int, description: String?) {
        CustomSnackbar.make(
            constraintLayout,
            description,
            2500,
            View.OnClickListener {
                this.context?.toast("undo")
            }
        ) {
            viewModel.deleteNote(id)
        }?.show()
    }

    protected fun displayTextByEmptyList() {
        hideGroupViews(progressBar, rvNotes)
        tvError.show()

        if (swipeRefreshLayout.isRefreshing) swipeRefreshLayout.isRefreshing = false
    }

    protected fun displayError(text: String) {
        tvError.show()
        hideGroupViews(progressBar, rvNotes)
        swipeRefreshLayout.isRefreshing = false
    }
}