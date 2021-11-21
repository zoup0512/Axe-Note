package com.zoup.android.note.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zoup.android.note.MyApplication
import com.zoup.android.note.R
import com.zoup.android.note.beans.MdBean
import com.zoup.android.note.ui.adapter.NoteRvAdapter

class NoteFragment : Fragment() {

    private val mMdBeanList = mutableListOf<MdBean>()
    private lateinit var mNoteRvAdapter: NoteRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_note, container, false)
        val noteMdRv: RecyclerView = view.findViewById(R.id.note_md_rv)
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        noteMdRv.layoutManager = layoutManager
        mNoteRvAdapter = NoteRvAdapter(mMdBeanList, requireActivity())
        noteMdRv.adapter = mNoteRvAdapter
        return view
    }

    private fun getData() {
        val db = MyApplication.database
        val data = db.mdBeanDao.findAll()
        data?.let {
            mMdBeanList.clear()
            mMdBeanList.addAll(it)
            mNoteRvAdapter.notifyDataSetChanged()
        }
    }


    override fun onResume() {
        super.onResume()
        getData()
    }
}