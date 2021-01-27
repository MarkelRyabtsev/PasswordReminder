package com.markel.passwordreminder.ui.folders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.markel.passwordreminder.R

class FoldersActivity: AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, FoldersActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.folders_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FolderListFragment.newInstance())
                .commitNow()
        }
    }
}