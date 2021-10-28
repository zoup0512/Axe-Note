package com.zoup.android.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zoup.android.note.sample.MdViewerSample

class MdViewerFragment : Fragment() {
    private val mdViewSample:MdViewerSample= MdViewerSample()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mdViewSample.createView(inflater,container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mdViewSample.onViewCreated(view)
    }

}