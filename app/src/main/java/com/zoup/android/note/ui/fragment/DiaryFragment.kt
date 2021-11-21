package com.zoup.android.note.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zoup.android.note.R
import com.zoup.android.note.ui.MdEditorActivity

class DiaryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diary, container, false)
        requireActivity().startActivity(Intent(requireContext(), MdEditorActivity::class.java))
        return view
    }

}