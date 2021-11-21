package com.zoup.android.note.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.zoup.android.note.R
import com.zoup.android.note.ui.fragment.EditFragment
import com.zoup.android.note.utils.ViewUtils.active

class MdEditorActivity : AppCompatActivity() {
    private lateinit var mPreviewLayout: LinearLayout
    private lateinit var mEditLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_md_editor)
        initToolbar()
        initBottomTabBar()
        addListeners()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Hello"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun initBottomTabBar() {
        mPreviewLayout = findViewById(R.id.preview_bar_layout)
        mEditLayout = findViewById(R.id.edit_bar_layout)
    }

    private fun addListeners() {
        mPreviewLayout.setOnClickListener {
            if (it.active) {
                it.active = false
                mEditLayout.active = true
            } else {
                it.active = true
                mEditLayout.active = false
            }
        }

        mEditLayout.setOnClickListener {
            if (it.active) {
                it.active = false
                mPreviewLayout.active = true
            } else {
                it.active = true
                mPreviewLayout.active = false
                showFragment()
            }
        }
    }

    private fun showFragment() {
        supportFragmentManager.beginTransaction().add(R.id.container, EditFragment())
            .commitAllowingStateLoss()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}