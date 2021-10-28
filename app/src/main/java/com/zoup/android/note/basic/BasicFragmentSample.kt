package com.zoup.android.note.basic

import android.content.Context
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import com.zoup.android.note.R

abstract class BasicFragmentSample : BasicSample() {

    protected lateinit var context: Context
    protected lateinit var scrollView: ScrollView
    protected lateinit var textView: TextView

    override val layoutResId: Int = R.layout.view_basic_md_fragment

    override fun onViewCreated(view: View) {
        context = view.context
        scrollView = view.findViewById(R.id.scroll_view)
        textView = view.findViewById(R.id.text_view)
        render()
    }

    abstract fun render()
}