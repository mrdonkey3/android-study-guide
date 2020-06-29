package com.dk.android_art.aidl.binderpool

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.dk.android_art.binderpool.IBinderPool
import com.dk.android_art.service.BinderPoolService
import java.util.concurrent.CountDownLatch

/**
 * @create on 2020/6/25 13:43
 * @description binder连接池，将业务模块的binder请求统一转发到远程service中执行
 * 内部实现：binder连接池它去绑定远程服务，绑定成功后，客户端（通过该连接池拿到需对应的binder对象，进行不一样的远程业务操作）就可以通过queryBinder方法获取各自对应的binder，拿到所需的binder以后
 * 不同模块就可以进行各自的操作了。（本质就是绑定一个binder连接池（包含多个业务的binder），而不是单个业务的binder对象）
 * @author mrdonkey
 */
class BinderPool {
    companion object {
        private val TAG = "BinderPool"
        val BINDER_NONE = -1
        val BINDER_COMPUTE = 0
        val BINDER_SECURITY_CENTER = 1
        private var sInstance: BinderPool? = null


        @JvmStatic
        fun getInstance(context: Context): BinderPool {
            return sInstance ?: synchronized(this) {
                sInstance ?: BinderPool(context)
            }
        }
    }

    var mContext: Context
    private var mBinderPool: IBinderPool? = null
    lateinit var mConnectBinderPoolCountDownLatch: CountDownLatch

    private constructor(mContext: Context) {
        this.mContext = mContext
        connectBinderPoolService()
    }

    private fun connectBinderPoolService() {
        mConnectBinderPoolCountDownLatch = CountDownLatch(1)
        val service = Intent(mContext, BinderPoolService::class.java)
        mContext.bindService(service, mBinderPoolConnection, Context.BIND_AUTO_CREATE)
        kotlin.runCatching {
            Log.e(TAG,"--->connectBinderPoolService ${Thread.currentThread()}")
            mConnectBinderPoolCountDownLatch.await()//阻塞 当前线程，直至连接上，（达到异步转同步的效果）
        }
    }


    fun queryBinder(binderCode: Int): IBinder? {
        kotlin.runCatching {
            return mBinderPool?.queryBinder(binderCode)
        }.onFailure {
            it.printStackTrace()
        }
        return null
    }

    fun disconnectBinder(){
        mContext.unbindService(mBinderPoolConnection)
    }

    private val mBinderPoolConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            //ignored
            Log.e(TAG,"--->onServiceDisconnected ${Thread.currentThread()}")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e(TAG,"--->onServiceConnected ${Thread.currentThread()}")
            mBinderPool = IBinderPool.Stub.asInterface(service)
            kotlin.runCatching {
                //设置死亡代理
                mBinderPool?.asBinder()?.linkToDeath(mBinderPoolDeathRecipient, 0)
            }.onFailure {
                it.printStackTrace()
            }
            mConnectBinderPoolCountDownLatch.countDown()//减1，等到为0的时候await的线程被重新唤醒
        }

    }

    //进程异常终止才会回调
    private val mBinderPoolDeathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            //binder died
            Log.e(TAG, "--->binder die.")
            mBinderPool?.asBinder()?.unlinkToDeath(this, 0)
            mBinderPool = null
            connectBinderPoolService()//重连
        }
    }


    class BinderPoolImpl : IBinderPool.Stub() {
        override fun queryBinder(binderCode: Int): IBinder? {
            return when (binderCode) {
                BINDER_COMPUTE -> {
                    ComputeImpl()
                }
                BINDER_SECURITY_CENTER -> {
                    SecurityCenterImpl()
                }
                else -> {
                    null
                }
            }
        }
    }


}