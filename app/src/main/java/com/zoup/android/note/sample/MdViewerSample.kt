package com.zoup.android.note.sample

import com.zoup.android.note.basic.BasicFragmentSample
import io.noties.markwon.Markwon

class MdViewerSample :BasicFragmentSample(){
    override fun render() {
        val content="Hello World!"
        val md=Markwon.create(context)
        md.setMarkdown(textView,content)
    }
}