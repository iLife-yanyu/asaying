package com.yanyu.libs.asaying

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SayingDao {

    companion object {

        const val TABLE_NAME = "a_saying_table"
    }

    @Insert
    fun insert(bean: SayingBean): Long

    @Update
    fun update(bean: SayingBean): Int

    @Insert
    fun insertAll(list: List<SayingBean>)

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): List<SayingBean>

    @Query("SELECT COUNT(*) FROM $TABLE_NAME")
    fun getCount(): Int

    @Query("UPDATE $TABLE_NAME SET showed=0")
    fun convertAll()

    @Query("SELECT * FROM $TABLE_NAME WHERE showed=0 LIMIT 1")
    fun getNotShowed(): SayingBean?
}
