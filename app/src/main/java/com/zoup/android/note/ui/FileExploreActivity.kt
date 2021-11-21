package com.zoup.android.note.ui

import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import checkStoragePermission
import com.zoup.android.note.MyApplication
import com.zoup.android.note.R
import com.zoup.android.note.beans.MdBean
import com.zoup.android.note.utils.AppUtils.renderItem
import com.zoup.android.note.utils.AppUtils.renderParentLink
import requestStoragePermission
import java.io.File
import java.util.*

class FileExploreActivity : AppCompatActivity() {
    private lateinit var mFilesTreeListView: ListView
    private lateinit var mAdapter: ArrayAdapter<String>
    private lateinit var mFilesList: List<File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_tree)
        setupUi()
    }

    private fun setupUi() {
        val hasPermission = checkStoragePermission(this)
        if (hasPermission) {
            mFilesTreeListView = findViewById(R.id.files_tree_list)
            mAdapter =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
            mFilesTreeListView.adapter = mAdapter
            mFilesTreeListView.setOnItemClickListener { parent, view, position, id ->
                val selectedFile = mFilesList[position]
                if (selectedFile.isDirectory) {
                    openFolder(selectedFile)
                } else {
                    insertMdFile(selectedFile)
                }
            }
            openFolder(Environment.getExternalStorageDirectory())
        } else {
            requestStoragePermission(this)
        }
    }


    private fun openFolder(selectedItem: File) {
        mFilesList = getFilesList(selectedItem)
        mAdapter.clear()
        mAdapter.addAll(mFilesList.map {
            if (it.path == selectedItem.parentFile.path) {
                renderParentLink(this)
            } else {
                renderItem(this, it)
            }
        })
        mAdapter.notifyDataSetChanged()
    }

    private fun insertMdFile(mdFile: File) {
        val uri = mdFile.path
        val mdBean = MdBean(
            0,
            uri,
            null,
            null,
            mdFile.nameWithoutExtension,
            null,
            null,
            System.currentTimeMillis(),
            System.currentTimeMillis()
        )
        MyApplication.database.mdBeanDao.insertMdBean(mdBean)
        delayFinish()
    }

    private fun delayFinish() {
        class MyTimerTask : TimerTask() {
            override fun run() {
                finish()
            }
        }
        Timer().schedule(MyTimerTask(), 1000)
    }

    private fun getFilesList(selectedItem: File): List<File> {
        val rawFilesList = selectedItem.listFiles()
            ?.filter { !it.isHidden && checkMdFile(it) }
        //判断是否是根目录
        return if (selectedItem == Environment.getExternalStorageDirectory()) {
            rawFilesList?.toList() ?: listOf()
        } else {
            listOf(selectedItem.parentFile) + (rawFilesList?.toList() ?: listOf())
        }
    }

    private fun checkMdFile(file: File): Boolean {
        return file.isDirectory || (file.isFile && file.name.endsWith("md"))
    }

}
