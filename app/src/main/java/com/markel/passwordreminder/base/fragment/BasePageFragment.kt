package com.markel.passwordreminder.base.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.vo.Status
import com.markel.passwordreminder.custom.CustomSnackbar
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.hideGroupViews
import com.markel.passwordreminder.ext.observe
import com.markel.passwordreminder.ext.show
import com.markel.passwordreminder.ext.toast
import com.markel.passwordreminder.ui.dialog.AddNoteDialog
import com.markel.passwordreminder.ui.main.MainActivity
import com.markel.passwordreminder.ui.page_fragment.PageViewModel
import com.markel.passwordreminder.ui.page_fragment.note.NoteAdapter
import com.markel.passwordreminder.ui.page_fragment.note.NoteItemClick
import com.markel.passwordreminder.ui.page_fragment.note.OperationType
import kotlinx.android.synthetic.main.fragment_page.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

abstract class BasePageFragment : Fragment(R.layout.fragment_page),
    SwipeRefreshLayout.OnRefreshListener {

    protected val viewModel by lazy { requireActivity().getViewModel<PageViewModel>() }

    protected lateinit var noteAdapter: NoteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initAdapter()
        observeData()
    }

    private fun initListeners() {
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun initAdapter() {
        noteAdapter = NoteAdapter(this.requireContext(), ::onItemClick, ::onItemActionClick)
        rvNotes.adapter = noteAdapter
    }

    private fun observeData() {
        observe(viewModel.isDataLoaded) {
            when (it.status) {
                Status.LOADING -> displayProgress()
                Status.SUCCESS -> {
                    it.data?.let { isLoaded ->
                        if (isLoaded) setData()
                        else displayTextByEmptyList()
                    }
                }
                Status.ERROR -> {
                    displayError(it.message ?: "")
                }
            }
        }
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

    private fun deleteItem(id: Int, index: Int, description: String) {
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

    private fun displayError(text: String) {
        tvError.show()
        hideGroupViews(progressBar, rvNotes)
        swipeRefreshLayout.isRefreshing = false
    }

    open fun setData() {
        viewModel.getAllNotes().also {
            if (it.isEmpty())
                displayTextByEmptyList()
            else {
                displayNormal()
                noteAdapter.setList(it)
            }
        }
    }
}