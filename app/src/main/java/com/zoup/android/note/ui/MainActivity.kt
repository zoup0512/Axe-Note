package com.zoup.android.note.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import checkStoragePermission
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zoup.android.note.MyApplication
import com.zoup.android.note.R
import com.zoup.android.note.beans.MdBean
import requestStoragePermission

class MainActivity : AppCompatActivity() {

    private lateinit var mToolbar: Toolbar
    private lateinit var mNavigationView: BottomNavigationView

    private var hasPermission = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolBar()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun checkPermission() {
        hasPermission = checkStoragePermission(this)
        if (!hasPermission) {
            requestStoragePermission(this)
        }
    }


    private fun setupNavigation() {
        mNavigationView = findViewById(R.id.bottom_nav_view)
        val navigationController = findNavController(R.id.navigation_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.note_navigation, R.id.diary_navigation, R.id.mine_navigation)
        )
        setupActionBarWithNavController(navigationController, appBarConfiguration)
        mNavigationView.setupWithNavController(navigationController)
    }

    private fun initToolBar() {
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mToolbar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                if (item != null) {
                    when (item.itemId) {
                        R.id.menu_add -> {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    FileExploreActivity::class.java
                                )
                            )
                        }
                    }
                }
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.context_menu, menu)
        return true
    }

}