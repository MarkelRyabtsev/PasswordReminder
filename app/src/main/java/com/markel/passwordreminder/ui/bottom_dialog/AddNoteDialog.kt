package com.markel.passwordreminder.ui.bottom_dialog

import android.content.Context
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.bottom_sheet_dialog.BaseBottomSheetDialog
import com.markel.passwordreminder.ext.setSafeOnClickListener
import kotlinx.android.synthetic.main.dialog_add_note.*
import org.koin.core.component.KoinComponent

class AddNoteDialog constructor(
    context: Context,
    defStyleAttr: Int = R.style.AppBottomSheetDialogTheme
) : BaseBottomSheetDialog(context, defStyleAttr), KoinComponent {


    init {
        val layoutBottomSheet = layoutInflater.inflate(R.layout.dialog_add_note, null)
        setContentView(layoutBottomSheet)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setClicks()
    }


    private fun setClicks() {
        button_create.setSafeOnClickListener {
            dismiss()
        }
    }
}