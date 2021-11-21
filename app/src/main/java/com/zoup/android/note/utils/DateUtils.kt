package com.zoup.android.note.utils

import java.text.SimpleDateFormat
import java.util.*


object DateUtils {

    fun Long.mills2String(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return simpleDateFormat.format(Date(this))
    }
}