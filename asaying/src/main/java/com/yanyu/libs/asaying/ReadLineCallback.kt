package com.yanyu.libs.asaying

import android.content.Context
import java.io.InputStream
import java.util.LinkedList

abstract class ReadLineCallback(private val dao: SayingDao) {

    abstract val authorName: String
    private val linkedList: LinkedList<SayingBean> = LinkedList()

    abstract fun onCallback(data: String)

    fun addBean(content: String) {
        linkedList.add(SayingBean(0, authorName, content))
    }

    fun insertWithCheckMax(max: Int = 0) {
        if (linkedList.size > max) {
            dao.insertAll(linkedList)
            linkedList.clear()
        }
    }

    open fun createInputStream(context: Context): InputStream {
        throw IllegalArgumentException("请重写createInputStream方法")
    }

    /**
     * 默认读取assets目录下的文件
     */
    open fun createInputStream(context: Context, filePath: String): InputStream {
        return context.assets.open(filePath)
    }
}