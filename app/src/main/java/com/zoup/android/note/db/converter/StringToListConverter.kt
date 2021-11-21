package com.zoup.android.note.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringToListConverter {

    @TypeConverter
    fun stringToList(string: String?): List<String>? {
        return Gson().fromJson(string, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun listToString(list: List<String>?): String? {
        return Gson().toJson(list)
    }
}