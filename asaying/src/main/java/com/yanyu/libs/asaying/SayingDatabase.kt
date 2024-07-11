package com.yanyu.libs.asaying

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.InputStream

@Database(
        // 实体类
        entities = [SayingBean::class],
        // 自动更新实现类
        autoMigrations = [/* AutoMigration(from = 1, to = 2) */],
        // 版本号
        version = SayingDatabase.DB_VERSION,
)
abstract class SayingDatabase : RoomDatabase() {

    abstract fun dao(): SayingDao

    companion object {

        internal const val DB_VERSION = 1
        private const val DB_NAME = "a_saying_database"

        @Volatile
        private var INSTANCE: SayingDatabase? = null

        private fun save2nextShow(context: Context, sayingBean: SayingBean) {
            SayingUtil.putBean(context, sayingBean)
        }

        fun getNextShow(context: Context): SayingBean {
            return SayingUtil.getBean(context)
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun getDatabase(context: Context): SayingDatabase {
            return INSTANCE ?: synchronized(this) {
                val applicationContext = context.applicationContext
                val klass = SayingDatabase::class.java
                val instance = Room.databaseBuilder(applicationContext, klass, DB_NAME)
                    // .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun dao(context: Context): SayingDao {
            return getDatabase(context).dao()
        }

        /**
         * 每次调动 [initAssets] 方法的时候，会判断是否已经有数据了，有的话就跳过读取写入数据库步骤，最后会缓存下一次还没有显示的Bean
         */
        fun initAssets(context: Context) {
            val dao = dao(context)
            val count = dao.getCount()
            if (count > 0) {
                val nextShow = SayingUtil.getBean(context)
                if (nextShow.id != -1L) {
                    nextShow.showed = true
                    dao.update(nextShow)
                }
                val bean = dao.getNotShowed()
                if (bean == null) {
                    dao.convertAll()
                    dao.getNotShowed()?.let {
                        save2nextShow(context, it)
                    }
                }
                else {
                    save2nextShow(context, bean)
                }
            }
            else {
                loadAssets(context)
            }
        }

        @Suppress("SameParameterValue")
        private fun loadAssets(context: Context, iCallback: ReadLineCallback = SampleCallbackImpl(dao(context))) {
            val stream: InputStream = iCallback.createInputStream(context)
            try {
                SayingUtil.readOnCallback(stream, iCallback)
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.e(SayingUtil.TAG, "read exception ${e.message}")
            }
            finally {
                stream.close()
            }
        }
    }
}