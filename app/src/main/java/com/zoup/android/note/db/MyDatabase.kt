package com.zoup.android.note.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zoup.android.note.MyApplication
import com.zoup.android.note.beans.MdBean

@Database(entities = [MdBean::class], version = 1,exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract val mdBeanDao: MdBeanDao

    companion object {
        @Volatile
        private var instance: MyDatabase? = null
        fun getInstance() = instance ?: Room.databaseBuilder(
            MyApplication.application,
            MyDatabase::class.java,
            "MdNote.db"
        )
            .allowMainThreadQueries()
            .build()
    }
}