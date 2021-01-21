package com.markel.passwordreminder.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.vo.Status
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.*
import com.markel.passwordreminder.routers.MainRouter
import com.markel.passwordreminder.ui.dialog.AddNoteDialog
import com.markel.passwordreminder.ui.page_fragment.PageViewModel
import com.markel.passwordreminder.ui.page_fragment.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val groupViewModel: GroupViewModel by viewModel()
    private val pageViewModel: PageViewModel by viewModel()
    private val router by inject<MainRouter>()
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var addNoteDialog: AddNoteDialog
    lateinit var editNoteDialog: AddNoteDialog

    companion object {
        fun getStartIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        initAdapter()
        initDialog()
        initListeners()
        observeGroups()
        observeAddNote()
        observeUpdateNote()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        val drawerToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initAdapter() {
        sectionsPagerAdapter = SectionsPagerAdapter(this)

        view_pager.apply {
            adapter = sectionsPagerAdapter
            offscreenPageLimit = 10
        }

        TabLayoutMediator(tabs, view_pager) { currentTab, currentPosition ->
            currentTab.text = when (currentPosition) {
                0 -> "All"
                else -> sectionsPagerAdapter.getPageName(currentPosition)
            }
        }.attach()
    }

    private fun initDialog() {
        addNoteDialog = AddNoteDialog.newInstance(false)
        addNoteDialog.setEventListener(object : AddNoteDialog.EventListener {
            override fun onClick(newNote: NoteEntity) {
                pageViewModel.addNote(newNote)
            }
        })
        editNoteDialog = AddNoteDialog.newInstance(true)
        editNoteDialog.setEventListener(object : AddNoteDialog.EventListener {
            override fun onClick(editedNote: NoteEntity) {
                pageViewModel.editNote(editedNote)
            }
        })
    }

    private fun initListeners() {
        nav_view.setNavigationItemSelectedListener(this)
        fab.setOnClickListener { showAddDialog() }
    }

    private fun observeGroups() {
        observe(groupViewModel.groupListLiveData) {
            when (it.status) {
                //Status.LOADING -> displayProgress()
                Status.SUCCESS -> {
                    it.data?.let { groups -> setGroups(groups) }
                    //if (it.data.isNullOrEmpty()) displayTextByEmptyList()
                    //displayNormal()
                }
                Status.ERROR -> {
                    //displayError(it.message ?: "")
                }
            }
        }
    }

    private fun observeAddNote() {
        observe(pageViewModel.addNote) {
            when (it.status) {
                Status.LOADING -> addNoteDialog.displayLoading()
                Status.SUCCESS -> {
                    pageViewModel.updateNotes()
                    addNoteDialog.clearFields()
                    addNoteDialog.dismiss()
                }
                Status.ERROR -> addNoteDialog.displayError()
            }
        }
    }

    private fun observeUpdateNote() {
        observe(pageViewModel.updateNote) {
            when (it.status) {
                Status.LOADING -> editNoteDialog.displayLoading()
                Status.SUCCESS -> {
                    pageViewModel.updateNotes()
                    editNoteDialog.dismiss()
                }
                Status.ERROR -> editNoteDialog.displayError()
            }
        }
    }

    private fun setGroups(groupList: List<GroupEntity>) {

        if (groupList.isNotEmpty()) {
            sectionsPagerAdapter.setGroups(groupList)
            tabs.show()
        }
    }

    private fun showAddDialog() {
        addNoteDialog.show(this.supportFragmentManager, AddNoteDialog.TAG)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> router.openSettings(this)
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) drawer_layout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }
}