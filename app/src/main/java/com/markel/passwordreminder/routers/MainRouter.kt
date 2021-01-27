package com.markel.passwordreminder.routers

import android.app.Activity
import com.markel.passwordreminder.R
import com.markel.passwordreminder.ui.folders.FoldersActivity
import com.markel.passwordreminder.ui.main.MainActivity
import com.markel.passwordreminder.ui.settings.SettingsActivity

class MainRouter {

    fun openMain(context: Activity?) {
        context?.startActivity(MainActivity.getStartIntent(context))
        context?.overridePendingTransition(R.anim.slide_in_down, R.anim.fade_out)
    }

    fun openSettings(context: Activity?) {
        context?.startActivity(SettingsActivity.getStartIntent(context))
        context?.overridePendingTransition(R.anim.slide_in_down, R.anim.fade_out)
    }

    fun openFolders(context: Activity?) {
        context?.startActivity(FoldersActivity.newIntent(context))
        context?.overridePendingTransition(R.anim.slide_in_down, R.anim.fade_out)
    }
}