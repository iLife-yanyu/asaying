package com.yanyu.demoapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yanyu.demoapp.databinding.ActivityBootBinding
import com.yanyu.libs.asaying.SayingBean
import com.yanyu.libs.asaying.SayingDatabase
import java.util.concurrent.TimeUnit

class BootActivity : AppCompatActivity() {

    private var loading: Boolean = false
    private val binding by lazy(LazyThreadSafetyMode.NONE) { ActivityBootBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadDataOnThread()
        binding.tvTip.setOnClickListener { showNextSaying() }
        binding.tvAuthor.setOnClickListener { showNextSaying() }
    }

    private fun showNextSaying() {
        if (loading) {
            showToast(R.string.loading_now_please_wait_a_moment)
            return
        }
        val bean = SayingDatabase.getNextShow(this)
        updateViews(bean)
        loading = true
        Thread {
            sleepOnSafe()
            SayingDatabase.initAssets(this)
            runOnUiThread {
                showToast(R.string.load_success)
                loading = false
            }
        }.start()
    }

    private fun loadDataOnThread() {
        loading = true
        Thread {
            sleepOnSafe()
            val bean = printOnFirst()
            // 加载默认一言数据
            SayingDatabase.initAssets(this)
            printDatabaseItem()
            printOnUiThread(bean)
        }.start()
    }

    /**
     * 数据输出到UI中
     */
    private fun printOnUiThread(paramBean: SayingBean) {
        val bean = if (paramBean.id == -1L) {
            SayingDatabase.getNextShow(this)
        }
        else {
            paramBean
        }
        runOnUiThread {
            showToast(R.string.load_success)
            updateViews(bean)
            loading = false
        }
    }

    private fun showToast(tip: Int) {
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show()
    }

    private fun updateViews(bean: SayingBean) {
        binding.tvTip.text = bean.content
        binding.tvAuthor.text = bean.wrapAuthor()
    }

    /**
     * 首次调用的时候会用默认的数据
     */
    private fun printOnFirst(): SayingBean {
        val sayingBean = SayingDatabase.getNextShow(this)
        Log.e("BootActivity", "sayingBean = ${sayingBean.convert2json()}")
        return sayingBean
    }

    private fun sleepOnSafe() {
        try {
            // 睡眠三秒是为了非首次启动加载数据过快反应不过来问题
            TimeUnit.SECONDS.sleep(3)
        }
        catch (_: Exception) {

        }
    }

    /**
     * 遍历打印每一个数据
     */
    private fun printDatabaseItem() {
        val database = SayingDatabase.getDatabase(this)
        val all = database.dao().getAll()
        all.forEach {
            Log.e("BootActivity", "item = ${it.convert2json()}")
        }
    }
}