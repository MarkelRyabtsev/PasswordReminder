package com.markel.passwordreminder.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.vo.Status
import com.markel.passwordreminder.ext.observe
import com.markel.passwordreminder.ext.setSafeOnClickListener
import com.markel.passwordreminder.ui.folders.folder_list.FolderListFragment
import kotlinx.android.synthetic.main.dialog_folders_list_item_actions.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FolderActionsDialog : DialogFragment() {

    private val viewModel: FolderActionsViewModel by viewModel {
        parametersOf(getFolderId())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_folders_list_item_actions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

        observeCurrentFolder()
        observeDeleteFolder()
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setFolderName() {
        val folder = viewModel.currentFolder
        folder?.let {
            tv_folder_name.text = it.name
        }
    }

    private fun initListeners() {
        ll_edit_folder.setSafeOnClickListener {
            viewModel.currentFolder?.let {
                findNavController().navigate(
                    FolderActionsDialogDirections.actionFolderActionsDialogToEditFolderFragment(
                        it.id,
                        it.name
                    )
                )
            }
        }
        ll_delete_folder.setSafeOnClickListener {
            viewModel.deleteFolder()
        }
    }

    private fun observeCurrentFolder() {
        observe(viewModel.currentFolderLiveData) {
            when (it.status) {
                Status.SUCCESS -> setFolderName()
            }
        }
    }

    private fun observeDeleteFolder() {
        observe(viewModel.deleteFolderLiveData) {
            when (it.status) {
                Status.LOADING -> {
                    //displayProgress()
                }
                Status.SUCCESS -> {
                    findNavController().apply {
                        previousBackStackEntry?.savedStateHandle?.set(FolderListFragment.NEED_UPDATE_FOLDERS, it.data)
                        navigateUp()
                    }
                }
                Status.ERROR -> {
                    //displayError(it.message ?: "")
                }
            }
        }
    }

    private fun getFolderId(): Int {
        val args: FolderActionsDialogArgs by navArgs()
        return args.folderId
    }
}