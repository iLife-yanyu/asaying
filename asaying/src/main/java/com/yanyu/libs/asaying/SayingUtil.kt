package com.yanyu.libs.asaying

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.InputStream
import java.util.Scanner

internal object SayingUtil {

    private const val K_SP_NAME = "lib_a_saying"
    private const val K_SAYING_CACHE = "kSayingCache"
    const val TAG = "LibFamousSaying"

    @Throws(Exception::class)
    fun readOnCallback(stream: InputStream, iCallback: ReadLineCallback) {
        val scanner = Scanner(stream)
        scanner.use {
            while (it.hasNextLine()) {
                iCallback.onCallback(it.nextLine())
            }
            iCallback.insertWithCheckMax()
        }
    }


    fun putBean(context: Context, sayingBean: SayingBean) {
        val preferences = context.getSharedPreferences(K_SP_NAME, Context.MODE_PRIVATE)
        val data = sayingBean.convert2json()
        // Log.i(TAG, "convert2json: $data")
        preferences.edit().putString(K_SAYING_CACHE, data).apply()
    }

    fun getBean(context: Context): SayingBean {
        val preferences = context.getSharedPreferences(K_SP_NAME, Context.MODE_PRIVATE)
        val data = preferences.getString(K_SAYING_CACHE, null)
        // Log.i(TAG, "cacheJson: $data")
        return if (data != null) {
            parse(data)
        }
        else {
            defaultBean()
        }
    }

    private fun defaultBean(): SayingBean {
        return SayingBean(
                id = -1L,
                author = "孔子",
                content = "子曰：“学而时习之，不亦说乎？有朋自远方来，不亦乐乎？人不知而不愠，不亦君子乎？”",
                showed = false,
        )
    }

    private fun parse(string: String): SayingBean {
        try {
            val jsonObject = JSONObject(string)
            return SayingBean(
                    id = jsonObject.optLong(Constant.ID),
                    author = jsonObject.optString(Constant.AUTHOR),
                    content = jsonObject.optString(Constant.CONTENT),
                    showed = jsonObject.optBoolean(Constant.SHOWED),
            )
        }
        catch (throwable: Throwable) {
            Log.e(TAG, "parse error: $throwable")
            return defaultBean()
        }
    }
}