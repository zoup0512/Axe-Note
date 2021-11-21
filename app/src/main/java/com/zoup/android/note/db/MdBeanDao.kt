package com.zoup.android.note.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zoup.android.note.beans.MdBean

@Dao
interface MdBeanDao {

    @Query("SELECT * FROM md_bean")
    fun findAll(): List<MdBean>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMdBean(vararg mdBean: MdBean?)
}