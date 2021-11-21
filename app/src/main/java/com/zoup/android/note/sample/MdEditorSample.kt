package com.zoup.android.note.sample

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.zoup.android.note.R
import com.zoup.android.note.basic.BasicSample
import com.zoup.android.note.sample.handler.BlockQuoteEditHandler
import com.zoup.android.note.sample.handler.CodeEditHandler
import com.zoup.android.note.sample.handler.LinkEditHandler
import com.zoup.android.note.sample.handler.StrikethroughEditHandler
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.core.spans.EmphasisSpan
import io.noties.markwon.core.spans.StrongEmphasisSpan
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import io.noties.markwon.editor.handler.EmphasisEditHandler
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.inlineparser.BangInlineProcessor
import io.noties.markwon.inlineparser.EntityInlineProcessor
import io.noties.markwon.inlineparser.HtmlInlineProcessor
import io.noties.markwon.inlineparser.MarkwonInlineParser
import io.noties.markwon.linkify.LinkifyPlugin
import org.commonmark.parser.Parser
import java.util.ArrayList
import java.util.concurrent.Executors

class MdEditorSample : BasicSample() {

    protected lateinit var context: Context
    protected lateinit var editText: EditText

    override val layoutResId: Int
        get() = R.layout.view_md_editor_sample

    override fun onViewCreated(view: View) {
        context = view.context
        editText = view.findViewById(R.id.edit_text)
        initBottomBar(view)
        render()
    }

    open fun render() {
        // for links to be clickable


        // for links to be clickable
        editText.movementMethod = LinkMovementMethod.getInstance()

        val inlineParserFactory =
            MarkwonInlineParser.factoryBuilder() // no inline images will be parsed
                .excludeInlineProcessor(BangInlineProcessor::class.java) // no html tags will be parsed
                .excludeInlineProcessor(HtmlInlineProcessor::class.java) // no entities will be parsed (aka `&amp;` etc)
                .excludeInlineProcessor(EntityInlineProcessor::class.java)
                .build()

        val markwon = Markwon.builder(context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureParser(builder: Parser.Builder) {

                    // disable all commonmark-java blocks, only inlines will be parsed
//          builder.enabledBlockTypes(Collections.emptySet());
                    builder.inlineParserFactory(inlineParserFactory)
                }
            })
            .usePlugin(SoftBreakAddsNewLinePlugin.create())
            .build()

        val onClick: LinkEditHandler.OnClick =
            LinkEditHandler.OnClick { widget, link ->
                markwon.configuration().linkResolver().resolve(widget, link)
            }

        val editor = MarkwonEditor.builder(markwon)
            .useEditHandler(EmphasisEditHandler())
            .useEditHandler(StrongEmphasisEditHandler())
            .useEditHandler(StrikethroughEditHandler())
            .useEditHandler(CodeEditHandler())
            .useEditHandler(BlockQuoteEditHandler())
            .useEditHandler(LinkEditHandler(onClick))
            .build()

        editText.addTextChangedListener(
            MarkwonEditorTextWatcher.withPreRender(
                editor, Executors.newSingleThreadExecutor(), editText
            )
        )
    }

    private fun initBottomBar(view: View) {
        // all except block-quote wraps if have selection, or inserts at current cursor position
        val bold: Button = view.findViewById(R.id.bold)
        val italic: Button = view.findViewById(R.id.italic)
        val strike: Button = view.findViewById(R.id.strike)
        val quote: Button = view.findViewById(R.id.quote)
        val code: Button = view.findViewById(R.id.code)

        addSpan(bold, StrongEmphasisSpan())
        addSpan(italic, EmphasisSpan())
        addSpan(strike, StrikethroughSpan())

        bold.setOnClickListener(InsertOrWrapClickListener(editText, "**"))
        italic.setOnClickListener(InsertOrWrapClickListener(editText, "_"))
        strike.setOnClickListener(InsertOrWrapClickListener(editText, "~~"))
        code.setOnClickListener(InsertOrWrapClickListener(editText, "`"))
        quote.setOnClickListener {
            val start = editText.selectionStart
            val end = editText.selectionEnd
            if (start < 0) {
                return@setOnClickListener
            }
            if (start == end) {
                editText.text.insert(start, "> ")
            } else {
                // wrap the whole selected area in a quote
                val newLines: MutableList<Int> = ArrayList(3)
                newLines.add(start)
                val text = editText.text.subSequence(start, end).toString()
                var index = text.indexOf('\n')
                while (index != -1) {
                    newLines.add(start + index + 1)
                    index = text.indexOf('\n', index + 1)
                }
                for (i in newLines.indices.reversed()) {
                    editText.text.insert(newLines[i], "> ")
                }
            }
        }
    }

    private fun addSpan(textView: TextView, vararg spans: Any) {
        val builder = SpannableStringBuilder(textView.text)
        val end = builder.length
        for (span in spans) {
            builder.setSpan(span, 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        textView.text = builder
    }

    private class InsertOrWrapClickListener(
        private val editText: EditText,
        private val text: String
    ) : View.OnClickListener {
        override fun onClick(v: View) {
            val start = editText.selectionStart
            val end = editText.selectionEnd
            if (start < 0) {
                return
            }
            if (start == end) {
                // insert at current position
                editText.text.insert(start, text)
            } else {
                editText.text.insert(end, text)
                editText.text.insert(start, text)
            }
        }

    }
}