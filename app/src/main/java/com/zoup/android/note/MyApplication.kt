package com.zoup.android.note

import android.app.Application
import com.zoup.android.note.db.MyDatabase

class MyApplication : Application() {
    companion object {
        lateinit var application: MyApplication
        lateinit var database: MyDatabase
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        database = MyDatabase.getInstance()
    }
}