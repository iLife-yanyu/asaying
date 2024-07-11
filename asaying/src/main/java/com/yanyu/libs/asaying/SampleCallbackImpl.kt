package com.yanyu.libs.asaying

import android.content.Context
import java.io.InputStream

internal class SampleCallbackImpl(dao: SayingDao) : ReadLineCallback(dao) {

    override val authorName: String = "孔子"

    override fun onCallback(data: String) {
        val trimData = data.trimStart().trimEnd()
        if (trimData.isEmpty()) {
            return
        }
        val missMatchCount = trimData.count { it == '“' } - trimData.count { it == '”' }
        if (missMatchCount > 0) {
            val builder = StringBuilder(trimData)
            for (i in 0..<missMatchCount) {
                builder.append("”")
            }
            addBean(builder.toString())
        }
        else {
            addBean(trimData)
        }
        insertWithCheckMax(50)
    }

    override fun createInputStream(context: Context): InputStream {
        return createInputStream(context, "KongZi.txt")
    }
}