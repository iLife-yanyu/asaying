package com.yanyu.libs.asaying

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = SayingDao.TABLE_NAME)
data class SayingBean(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = Constant.ID)
        val id: Long = 0, // 自增主键
        @ColumnInfo(name = Constant.AUTHOR)
        var author: String,
        @ColumnInfo(name = Constant.CONTENT)
        val content: String,
        @ColumnInfo(name = Constant.SHOWED)
        var showed: Boolean = false,
) {

    fun convert2json(): String {
        return "{\"${Constant.ID}\":$id,\"${Constant.AUTHOR}\":\"$author\",\"${Constant.CONTENT}\":\"$content\",\"${Constant.SHOWED}\":$showed}"
    }

    fun wrapAuthor(): String {
        return "——$author"
    }
}