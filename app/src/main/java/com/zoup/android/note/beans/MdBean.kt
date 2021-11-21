package com.zoup.android.note.beans

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.zoup.android.note.db.converter.StringToListConverter

@Entity(tableName = "md_bean")
@TypeConverters(StringToListConverter::class)
data class MdBean(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uri: String?,
    val category: String?,
    @ColumnInfo(name = "category_id")
    val categoryId: Int?,
    val title: String,
    val from: String?,
    val tags: List<String>?,
    @ColumnInfo(name = "create_time")
    var createTime: Long?,
    @ColumnInfo(name = "update_time")
    var updateTime: Long = System.currentTimeMillis()
)