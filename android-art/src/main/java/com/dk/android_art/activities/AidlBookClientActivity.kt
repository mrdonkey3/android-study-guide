package com.dk.android_art.activities

import android.content.*
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewRootImpl
import com.dk.android_art.R
import com.dk.android_art.aidl.Book
import com.dk.android_art.aidl.IBookManager
import com.dk.android_art.aidl.IOnNewBookArrivedListener
import com.dk.android_art.entity.User
import com.dk.android_art.service.AidlBookService
import kotlinx.android.synthetic.main.activity_aidl_book_client.*

class AidlBookClientActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "AidlClientActivity"
    private var mBookManager: IBookManager? = null
    private val conn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            //主线程
            Log.e(TAG, "--->onServiceDisconnected")
        }

        //当客户端正常连接着服务时，执行服务的绑定操作会被调用
        //此时传来的IBinder对象是onbinder的返回的对象
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            //主线程
            Log.e(TAG, "onServiceConnected")
            try {
                //客户端发起远程请求(如果是耗时，尽量不要写在主线程)，当前进程会被挂起，直至服务端进程返回数据（被调用的方法运行在服务端binder的线程池）
                Log.e(TAG, "--->request")
                service?.linkToDeath(deathRecipient, 0)//绑定成功后设置死亡代理
                //用于将服务端的binder对象转化成客户端所需要的的AIDL接口类型的对象，若是c/s同一进程，则返回的服务端的stub对象本身
                //若是跨进程则返回系统封装的Stub.Proxy对象
                mBookManager = IBookManager.Stub.asInterface(service)
                val bookList = mBookManager?.bookList
                Log.e(TAG, "--->reply")
                Log.e(TAG, "--->book size ${bookList?.size}")
                //注册事件监听(调用服务端的binder，被调用的方法运行在服务端binder的线程池，如果是耗时，尽量不要写在主线程)
                mBookManager?.registerListener(mIOnNewBookArrivedListener)
                //使用messenger的请求
                val messenger = Messenger(service)
                val msg = Message.obtain(null, AidlBookService.MSG_FROM_CLIENT)
                msg.data = Bundle().apply { putString("msg", "hello this is client") }
//                msg.replyTo=  //可以给消息带一个新信使，用来给服务端回复消息给客户端
                messenger.send(msg)//通过信使发消息给服务端
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

    }

    //当服务端进程异常终止时，bind也死亡，设置死亡代理，就可以在bind死亡收到通知，需要做一些事情 比如重新绑定远程service
    val deathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            //这个是在客户端的binder线程池被调用
            Log.e(TAG, "--->binderDied")
            //去掉死亡代理
            mBookManager?.asBinder()?.unlinkToDeath(this, 0)
            mBookManager = null
            //todo 重新绑定远程service
        }
    }

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                AidlBookService.MSG_FROM_SERVICE_NEW_BOOK_ARRIVED -> {
                    //切换到主线程
                    Log.e(TAG, "--->receive new book:" + msg.obj)
                }
                else -> {
                }
            }
            super.handleMessage(msg)
        }
    }

    /**
     * 客户端的binder
     */
    val mIOnNewBookArrivedListener = object : IOnNewBookArrivedListener.Stub() {
        override fun onNewBookArrived(book: Book?) {
            //此时是非ui线程 通过handler切换到ui线程
            mHandler.obtainMessage(AidlBookService.MSG_FROM_SERVICE_NEW_BOOK_ARRIVED, book)
                .sendToTarget()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aidl_book_client)
        setListener()
    }

    private fun setListener() {
        btn_start.setOnClickListener(this)
        btn_stop.setOnClickListener(this)
        btn_bind.setOnClickListener(this)
        btn_unbind.setOnClickListener(this)
        btn_provider.setOnClickListener(this)
    }

    /**
     * 跨进程访问
     */
    override fun onClick(v: View?) {
        val intent = Intent(this, AidlBookService::class.java)
        when (v?.id) {
            R.id.btn_start -> {
                intent.action = "com.dk.android_art.service"
                intent.setPackage("com.dk.android_art")
                startService(intent)
            }
            R.id.btn_stop -> {
                //启动服务:创建-->启动-->销毁
                //如果服务已经创建了，后续重复启动，操作的都是同一个服务，不会再重新创建了，除非你先销毁它
                intent.action = "com.dk.android_art.service"
                intent.setPackage("com.dk.android_art")
                stopService(intent)
            }
            R.id.btn_bind -> {
                //绑定服务：最大的 作用是用来实现对Service执行的任务进行进度监控
                //如果服务不存在： onCreate-->onBind-->onUnbind-->onDestroy
                // （此时服务没有再后台运行，并且它会随着Activity的摧毁而解绑并销毁）
                //服务已经存在：那么bindService方法只能使onBind方法被调用，而unbindService方法只能使onUnbind被调用
                intent.action = "com.dk.android_art.service"
                intent.setPackage("com.dk.android_art")
                bindService(intent, conn, Context.BIND_AUTO_CREATE)
            }
            R.id.btn_unbind -> {
                Log.e(TAG, "---->unbinder ${mBookManager?.asBinder()?.isBinderAlive}")
                if (mBookManager?.asBinder()?.isBinderAlive == true) {
                    unbindService(conn)
                }
            }
            R.id.btn_provider -> {
                kotlin.runCatching {
                    contentProvider()
                }.onFailure {
                    it.printStackTrace()
                }
//                val uri = Uri.parse("content://com.dk.android_art.book.provider")
//                contentResolver.query(uri, null, null, null)
//                contentResolver.query(uri, null, null, null)
//                contentResolver.query(uri, null, null, null)
            }
            else -> {
            }
        }
    }

    private fun contentProvider() {
        val bookUri = Uri.parse("content://com.dk.android_art.book.provider/book")
        val value = ContentValues()
        value.put("_id", 6)
        value.put("name", "kotlin")
        contentResolver.insert(bookUri, value)
        val bookCursor = contentResolver.query(bookUri, arrayOf("_id", "name"), null, null, null)
        while (bookCursor?.moveToNext() == true) {
            val book = Book(bookCursor.getInt(0), bookCursor.getString(1))
            Log.e(TAG, "--->query:book:${book.toString()}")
        }
        bookCursor?.close()

        val userUri = Uri.parse("content://com.dk.android_art.book.provider/user")
        val userCursor =
            contentResolver.query(userUri, arrayOf("_id", "name", "sex"), null, null, null)
        while (userCursor?.moveToNext() == true) {
            val user = User(userCursor.getInt(0), userCursor.getString(1), userCursor.getInt(2))
            Log.e(TAG, "--->query:user:${user.toString()}")
        }
        userCursor?.close()

    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(conn)
    }

}