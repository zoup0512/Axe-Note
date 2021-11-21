package com.zoup.android.note.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zoup.android.note.R
import com.zoup.android.note.utils.AppUtils.readFile
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.recycler.SimpleEntry
import io.noties.markwon.recycler.table.TableEntry
import io.noties.markwon.recycler.table.TableEntryPlugin
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.node.FencedCodeBlock
import java.io.File
import java.io.FileInputStream

class MdViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_md_viewer)
        val data = intent.extras?.getString("FILE_PATH")
        data?.let {
            initRecyclerView(it)
        }
    }

    private val markwon: Markwon
        get() = Markwon.builder(this)
            .usePlugin(ImagesPlugin.create())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(TableEntryPlugin.create(this))
            .usePlugin(TaskListPlugin.create(this))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    builder.on(FencedCodeBlock::class.java) { visitor, block ->
                        // we actually won't be applying code spans here, as our custom view will
                        // draw background and apply mono typeface
                        //
                        // NB the `trim` operation on literal (as code will have a new line at the end)
                        val code = visitor.configuration()
                            .syntaxHighlight()
                            .highlight(block.info, block.literal.trim())
                        visitor.builder().append(code)
                    }
                }
            })
            .build()

    private fun initRecyclerView(path: String) {
        val adapter = MarkwonAdapter.builder(R.layout.adapter_node, R.id.adapter_text_view)
            .include(
                FencedCodeBlock::class.java,
                SimpleEntry.create(R.layout.adapter_node_code_block, R.id.text_view)
            )
            .include(TableBlock::class.java, TableEntry.create {
                it
                    .tableLayout(R.layout.adapter_node_table_block, R.id.table_layout)
                    .textLayoutIsRoot(R.layout.view_table_entry_cell)
            })
            .build()

        val recyclerView: RecyclerView = findViewById(R.id.md_viewer_rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter

        val file = File(path)
        if (!file?.exists()) return
        val inputStream = FileInputStream(file)
        inputStream.let {
            val content = inputStream.readFile()
            val node = markwon.parse(content)
            if (window != null) {
                recyclerView.post {
                    adapter.setParsedMarkdown(markwon, node)
                    adapter.notifyDataSetChanged()
                }
            }
        }

    }

}