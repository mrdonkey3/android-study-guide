package com.dk.android_art.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.dk.android_art.R
import com.dk.android_art.service.TCPServerService
import kotlinx.android.synthetic.main.activity_tcp_client.*
import java.io.*
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*

/**
 * 简易聊天室实现
 */
class TCPClientActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private val MESSAGE_RECEIVE_NEW_MSG = 1
        private val MESSAGE_SOCKET_CONNECTED = 2
    }

    private var mClientSocket: Socket? = null
    private var mWriter: PrintWriter? = null
    private val TAG = "SocketActivity"

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                MESSAGE_RECEIVE_NEW_MSG -> {
                    tv_msg_board.text = tv_msg_board.text.toString().plus(msg.obj)
                }
                MESSAGE_SOCKET_CONNECTED -> {
                    btn_send.isEnabled = true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tcp_client)
        setListener()
        startService(Intent(this, TCPServerService::class.java))
        Thread {
            connectedTCPServer()
        }.start()
    }

    /**
     * 链接socket
     */
    private fun connectedTCPServer() {
        var socket: Socket? = null
        Log.e(TAG, "--->start connect to tcp server ... ")
        while (socket == null) {
            kotlin.runCatching {
                socket = Socket("localhost", 8688)
                mClientSocket = socket
                Log.e(TAG, "--->${socket?.getOutputStream()} ... ")
                socket?.getOutputStream()?.apply {
                    //客户端输出流
                    mWriter = PrintWriter(BufferedWriter(OutputStreamWriter(this)), true)
                }
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED)
            }.onFailure {
                SystemClock.sleep(1000)//不会抛异常
                Log.e(TAG, "--->connect tcp server failed, try ... ")
            }
        }
        Log.e(TAG, "--->success connect to tcp server ... ")
        kotlin.runCatching {
            //接收服务端的消息
            var br: BufferedReader? = null
            socket?.getInputStream()?.apply {
                br = BufferedReader(InputStreamReader(this))
                while (!this@TCPClientActivity.isFinishing) {
                    val msg = br?.readLine()
                    Log.e(TAG, "--->receive : $msg ... ")
                    if (msg != null) {
                        val time = formatDateTime(System.currentTimeMillis())
                        val showMsg = "server $time : $msg \n"
                        mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showMsg).sendToTarget()
                    }
                }
            }
            Log.e(TAG, "--->quit ... ")
            mWriter?.close()
            br?.close()
            socket?.close()
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun setListener() {
        btn_send.setOnClickListener(this)
    }

    private fun formatDateTime(time: Long): String {
        return SimpleDateFormat.getDateTimeInstance().format(Date(time))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_send -> {
                val sendMsg = et_input_msg.text.toString()
                if (sendMsg.isNotEmpty() && mWriter != null) {
                    Thread {
                        mWriter?.println(sendMsg)
                    }.start()
                    et_input_msg.setText("")
                    val time = formatDateTime(System.currentTimeMillis())
                    val showMsg = "self $time : $sendMsg \n"
                    tv_msg_board.text = tv_msg_board.text.toString().plus(showMsg)

                }
            }
        }
    }

    override fun onDestroy() {
        kotlin.runCatching {
            if (mClientSocket != null) {
                mClientSocket?.shutdownInput()
                mClientSocket?.close()
            }
        }.onFailure {
            it.printStackTrace()
        }
        super.onDestroy()
    }
}