package com.dk.android_art.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import kotlin.random.Random

/**
 * @create on 2020/6/22 23:08
 * @description tcp  socket 服务端
 * @author mrdonkey
 */
class TCPServerService : Service() {

    private var mIsServiceDestroy = false
    private val TAG = "TCPServerService"

    //服务端自动回复客户端的消息
    private val mDefinedMessages = arrayOf("回复1", "回复2", "回复3", "回复4", "回复5")

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        //开启tcp服务
        Thread(TcpServer()).start()
        super.onCreate()
    }

    override fun onDestroy() {
        mIsServiceDestroy = true
        super.onDestroy()
    }

    //内部类
    inner class TcpServer : Runnable {
        private val TAG = "TcpServer"

        override fun run() {
            var serviceSocket: ServerSocket? = null
            kotlin.runCatching {
                serviceSocket = ServerSocket(8688)
            }.onFailure {
                Log.e(TAG, "establish tcp server failed,port:8688")
                it.printStackTrace()
                return
            }
            while (!mIsServiceDestroy) {
                kotlin.runCatching {
                    //监听客户端连接请求，该方法会阻塞直到有链接
                    val client = serviceSocket?.accept()
                    Log.e(TAG, "client is connected ---->${client} accept")
                    //开一个线程来回复客户端
                    Thread {
                        kotlin.runCatching {
                            client?.apply { responseClient(this) }
                        }.onFailure {
                            it.printStackTrace()
                        }
                    }.start()
                }.onFailure {
                    it.printStackTrace()
                }
            }

        }

        /**
         * 回复客户端的方法
         */
        private fun responseClient(client: Socket) {
            //用于接收客户端的消息
            val cIn = BufferedReader(InputStreamReader(client.getInputStream()))
            //用于向客户端发送消息
            val cOut = PrintWriter(OutputStreamWriter(client.getOutputStream()),true)
            cOut.println("欢迎来到聊天室！")
            while (!mIsServiceDestroy) {
                val receivedMsg = cIn.readLine()
                Log.e(TAG, "--->msg from client：$receivedMsg")
                if (receivedMsg == null) {
                    //客户端断开连接了
                    break
                }
                val sendMsg = mDefinedMessages.random()
                cOut.println(sendMsg)
                Log.e(TAG, "--->send msg to  client：$sendMsg")
            }
            Log.e(TAG, "--->client quit")
            //关闭流
            cIn.close()
            cOut.close()
            client.close()
        }

    }
}