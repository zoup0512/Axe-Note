package com.zoup.android.note.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zoup.android.note.R
import com.zoup.android.note.beans.MdBean
import com.zoup.android.note.ui.MdViewerActivity
import com.zoup.android.note.utils.DateUtils.mills2String

class NoteRvAdapter(var data: List<MdBean>, val activity: Activity) :
    RecyclerView.Adapter<NoteRvAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.md_title)
        val timeText: TextView = itemView.findViewById(R.id.md_create_time)
        val fromText: TextView = itemView.findViewById(R.id.md_from)
        val categoryText: TextView = itemView.findViewById(R.id.md_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_note_fragment_content, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val mdBean = data[position]
        holder.timeText.text = mdBean.updateTime.mills2String()
        holder.titleText.text = mdBean.title
        holder.fromText.text = "来源:${mdBean.from ?: "未知"}"
        holder.categoryText.text = "分类:${mdBean.category ?: "未知"}"
        holder.itemView.setOnClickListener {
            val intent = Intent()
            intent.setClass(activity, MdViewerActivity::class.java)
            intent.putExtra("FILE_PATH", mdBean.uri)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}