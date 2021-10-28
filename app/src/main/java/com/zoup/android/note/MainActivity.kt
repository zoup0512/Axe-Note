package com.zoup.android.note

import android.os.Bundle
import android.view.Window
import androidx.fragment.app.FragmentActivity
import com.zoup.android.note.ui.MdViewerFragment

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .add(Window.ID_ANDROID_CONTENT,MdViewerFragment())
            .commit()
    }
}