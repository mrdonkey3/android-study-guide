package com.dk.android_art.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.dk.android_art.R
import com.dk.android_art.aidl.binderpool.BinderPool
import com.dk.android_art.binderpool.ICompute
import com.dk.android_art.binderpool.ISecurityCenter
import kotlinx.android.synthetic.main.activity_aidl_binder_pool.*

class AidlBinderPoolActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "AidlBinderPoolActivity"
    private var mBinderPool: BinderPool? = null
    private var mSecurityCenter: ISecurityCenter? = null
    private val mCompute: ICompute? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aidl_binder_pool)
        btn_unbind.setOnClickListener(this)
        Thread {
            doWork()
        }.start()
    }

    private fun doWork() {
        Log.e(TAG, "--->start getInstance")
        mBinderPool = BinderPool.getInstance(this)//里面会有线程异步转同步的处理，获取时并等待连接binder
        Log.e(TAG, "--->successful getInstance")
        val securityBinder = mBinderPool?.queryBinder(BinderPool.BINDER_SECURITY_CENTER)
        mSecurityCenter = ISecurityCenter.Stub.asInterface(securityBinder) as ISecurityCenter
        Log.e(TAG, "--->visit ISecurityBinder")
        val msg = "hello android"
        Log.e(TAG, "--->msg :$msg")
        kotlin.runCatching {
            val password = mSecurityCenter?.encrypt(msg)
            Log.e(TAG, "encrypt--->password :$password")
            Log.e(TAG, "decrypt--->password :${mSecurityCenter?.decrypt(password)}")
        }.onFailure { it.printStackTrace() }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_unbind -> {
                mBinderPool?.disconnectBinder()
            }
            else -> {
            }
        }
    }
}