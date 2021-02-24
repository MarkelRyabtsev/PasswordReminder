package com.markel.passwordreminder.ui.folders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.markel.passwordreminder.R
import kotlinx.android.synthetic.main.activity_folders.*

class FoldersActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    companion object {
        fun newIntent(context: Context) = Intent(context, FoldersActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folders)

        val host: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.folders_nav_host_fragment) as NavHostFragment?
                ?: return
        navController = host.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setSupportActionBar(toolbar)

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return false
    }
}