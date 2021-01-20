package com.markel.passwordreminder.ui.bottom_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.markel.passwordreminder.R
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.setSafeOnClickListener
import com.markel.passwordreminder.ext.toast
import com.markel.passwordreminder.ui.page_fragment.PageViewModel
import kotlinx.android.synthetic.main.dialog_add_note.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddNoteDialog : DialogFragment() {

    private val viewModel: PageViewModel by sharedViewModel()

    private var eventListener: EventListener? = null

    companion object {
        const val TAG = "AddNoteDialogFragment"

        private const val IS_EDITING = "isEditing"

        fun newInstance(
            isEditing: Boolean
        ) = AddNoteDialog().apply {
            arguments = Bundle().apply {
                putBoolean(IS_EDITING, isEditing)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_add_note, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    override fun onResume() {
        super.onResume()
        dialog?.let {
            if (getIsEditing()) {
                button_confirm.text = getString(R.string.action_edit)
                et_description.setText(viewModel.editableNote?.description)
                et_password.setText(viewModel.editableNote?.password)
                switch_hide_password.isChecked = viewModel.editableNote?.isProtected ?: false
            }
        }
    }

    fun setEventListener(eventListener: EventListener) {
        this.eventListener = eventListener
    }

    private fun initListeners() {
        button_confirm.setSafeOnClickListener {
            if (isValid()) {
                if (getIsEditing()) {
                    viewModel.editableNote?.let {
                        eventListener?.onClick(
                            NoteEntity(
                                id = it.id,
                                groupId = it.groupId,
                                description = et_description.text.toString(),
                                password = et_password.text.toString(),
                                isProtected = switch_hide_password.isChecked
                            )
                        )
                    }
                }
                else {
                    eventListener?.onClick(
                        NoteEntity(
                            description = et_description.text.toString(),
                            password = et_password.text.toString(),
                            isProtected = switch_hide_password.isChecked
                        )
                    )
                }
            }
        }
        button_cancel.setSafeOnClickListener { dismiss() }
    }

    private fun isValid(): Boolean {
        if (et_description.text.isBlank() && et_password.text.isBlank()) {
            toast("Поля не могут быть пустыми")
            return false
        } else if (et_description.text.isBlank() || et_password.text.isBlank()) {
            toast("Поле не может быть пустым")
            return false
        }

        return true
    }

    private fun getIsEditing() = arguments?.getBoolean(IS_EDITING, false) ?: false

    interface EventListener {
        fun onClick(editedNote: NoteEntity)
    }
}