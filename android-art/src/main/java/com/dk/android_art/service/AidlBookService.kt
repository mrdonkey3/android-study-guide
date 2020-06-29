package com.dk.android_art.service

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import com.dk.android_art.aidl.Book
import com.dk.android_art.aidl.IBookManager
import com.dk.android_art.aidl.IOnNewBookArrivedListener
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @create on 2020/6/21 11:40
 * @description book 服务端
 * aidl 的创建流程
 * 1.建立一个service和aidl接口
 * 2.创建一个类继承自aidl接口的Stub类并实现它的抽象方法
 * 3.在service的onBind方法返回上面创建的类对象，然后客户端在ServiceConnect就能拿到对应的binder对象，进行操作服务端的方法（aidl接口定义的方法）
 * @author mrdonkey
 */
class AidlBookService : Service() {
    private val TAG = "BookService"
    private var mIsServiceDestroy = AtomicBoolean(false);
    var mBookList = CopyOnWriteArrayList<Book>()//CopyOnWriteArrayList 自动线程同步，支持并非读写
    var mListenerList =
        RemoteCallbackList<IOnNewBookArrivedListener>()//RemoteCallbackList 用于删除跨进程的listener接口，因为Binder底层通过反序列会生成一个新的listener对象
    //对象是无法通过跨进程直接传输的，RemoteCallbackList利用他们都共有一个binder对象的前提，当客户端解注册时，会删除具有同一个binder对象的listener


    private val mMessenger = Messenger(MessengerHandler())//通Messenger进行ipc，本质也是aidl

    companion object {
        @JvmField
        var isBinder = false
        val MSG_FROM_CLIENT = 1001
        val MSG_FROM_SERVICE = 1002
        val MSG_FROM_SERVICE_NEW_BOOK_ARRIVED = 1003

    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "--->onCreate")
        object : Thread() {
            override fun run() {
                super.run()
                try {
                    repeat(100) {
                        if (mIsServiceDestroy.get()) {//让service中断
                            interrupt()
                        }
                        Log.e(TAG, "run--->$it")
                        sleep(1000)
                        //模拟耗时，每个一秒添加1本新书
                        onNewBookArrived(Book(it, "name".plus(it)))
                    }
                    interrupted()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }


    override fun onBind(intent: Intent?): IBinder? {
        //aidl中进行权限验证
        val check =
            checkCallingOrSelfPermission("com.dk.android_art.ACCESS_BOOK_SERVICE")
        if (check == PackageManager.PERMISSION_DENIED) {//如果没有该权限，则返回null'，使应用无法绑定这个服务
            return null
        }
        Log.e(TAG, "--->onBind")
        isBinder = true
        return BookManagerImpl()
//        return mMessenger.binder
    }

    inner class BookManagerImpl : IBookManager.Stub() {

        override fun getBookList(): MutableList<Book> {
            Log.e(TAG, "--->getBookList")
            //耗时5s
            Thread.sleep(5000)
            Log.e(TAG, "--->sleep 5s")
            return mBookList
        }

        override fun addBook(book: Book?) {
            Log.e(TAG, "--->addBook")
            book?.apply {
                mBookList.add(this)
            }
        }

        override fun registerListener(lisenter: IOnNewBookArrivedListener?) {
            mListenerList.register(lisenter)
        }

        override fun unRegisterListener(lisenter: IOnNewBookArrivedListener?) {
            mListenerList.unregister(lisenter)
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBinder = false
        return super.onUnbind(intent)
    }

    class MessengerHandler : Handler() {
        private val TAG = "MessengerHandler"

        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                MSG_FROM_CLIENT -> {
                    Log.e(TAG, "--->receiver msg from client： ${msg.data.getString("msg")}")
                    //假设客户端传来回复信使
//                    val replyTo = msg.replyTo
//                    val msg = Message.obtain(null, MSG_FROM_SERVICE)
//                    msg.data=Bundle().apply { putString("reply","收到client的消息啦") }
//                    replyTo.send(msg)//发送给客户端
                }
                else -> {
                    super.handleMessage(msg)
                }
            }
        }
    }

    private fun onNewBookArrived(book: Book) {
        Log.e(TAG, "--->onNewBookArrived")
        mBookList.add(book)
        val number = mListenerList.beginBroadcast()
        for (i in 0 until number) {
            val l = mListenerList.getBroadcastItem(i)
            //主线程中调用客户端binder的方法，主线也会被挂起，避免在做过于耗时的在l?.onNewBookArrived(中，否则使用工作线程来执行该方法
            l?.onNewBookArrived(book)
            Log.e(TAG, "--->onNewBookArrived--->notify:$l")
        }
        mListenerList.finishBroadcast()//配对使用
    }

    override fun onDestroy() {
        Log.e(TAG, "--->onDestroy")
        mIsServiceDestroy.set(true)
        super.onDestroy()
    }
}