package com.dk.android_art

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import com.dk.android_art.activities.*
import com.dk.android_art.frags.CheckedViewFragment
import com.dk.android_art.frags.DialogFragment
import com.dk.android_art.frags.PersonViewFragment
import com.dk.android_art.utils.NetWorkUtils
import com.dk.android_art.utils.SystemUtils
import com.dk.android_art.utils.WindowUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListener()
    }

    private fun setListener() {
        btn_person_view?.setOnClickListener(this)
        btn_check_view?.setOnClickListener(this)
        btn_dialog?.setOnClickListener(this)
        btn_second_activity?.setOnClickListener(this)
        btn_aidl_client_activity?.setOnClickListener(this)
        btn_aidl_binder_pool_activity?.setOnClickListener(this)
        btn_socket_tcp_client_activity?.setOnClickListener(this)
        btn_view_event_policy?.setOnClickListener(this)
        btn_custom_view?.setOnClickListener(this)
        btn_notify?.setOnClickListener(this)
        btn_simulate_notification?.setOnClickListener(this)
        btn_alipay?.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        NetWorkUtils.getConnectionDeviceIp(this)
    }

    override fun onClick(v: View?) {
        val transaction = supportFragmentManager.beginTransaction()
        when (v?.id) {
            R.id.btn_person_view -> {
                transaction.replace(R.id.containerView, PersonViewFragment())
            }
            R.id.btn_check_view -> {
                transaction.replace(R.id.containerView, CheckedViewFragment())
            }
            R.id.btn_dialog -> {
                transaction.replace(R.id.containerView, DialogFragment())
            }
            R.id.btn_second_activity -> {
                //隐式启动activity 不指定包名和类名
                val intent = Intent()
                intent.action = "com.dk.android_art.action1"
                intent.addCategory("com.dk.android_art.category")
                intent.setDataAndType(Uri.parse("content://xxx"), "text/plain")
                //最佳匹配activity，匹配不到则返回null
                val resolveActivity =
                    packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                //返回所有成功匹配的activity
                val queryIntentActivities =
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                if (resolveActivity != null && queryIntentActivities != null) {
                    startActivity(intent)
                }
            }
            R.id.btn_aidl_client_activity -> {//跨进程访问service
                startActivity(Intent(this, AidlBookClientActivity::class.java))
            }
            R.id.btn_aidl_binder_pool_activity -> {
                startActivity(Intent(this, AidlBinderPoolActivity::class.java))
            }
            R.id.btn_socket_tcp_client_activity -> {
                startActivity(Intent(this, TCPClientActivity::class.java))
            }
            R.id.btn_view_event_policy -> {
                startActivity(Intent(this, ViewEventPolicyActivity::class.java))
            }
            R.id.btn_custom_view -> {
                startActivity(Intent(this, CustomViewActivity::class.java))
            }
            R.id.btn_notify -> {
                val random = arrayOf(0, 1).random()
                println("btn_notify--->$random")
                if (random == 0)
                    WindowUtils.createSystemNotification(this, this::class.java)
                else
                    WindowUtils.createCustomNotification(this, this::class.java)

            }
            R.id.btn_simulate_notification -> {
                btn_simulate_notification?.postDelayed({
                    Thread {
                        repeat(100) {
                            sendRemoteViews(it)
                            SystemClock.sleep(100)
                        }
                    }.start()
                    //延时两秒，为了让进入到第二个进程的activity能接从0开始接受广播
                }, 1000)
                startActivity(Intent(this, SimulateNotificationActivity::class.java))
            }
            R.id.btn_alipay -> {
                SystemUtils.startAliPay(this)
            }
            else -> {
                return
            }
        }
        transaction.commit()
    }

    private fun sendRemoteViews(progress: Int) {
        val remoteViews = RemoteViews(packageName, R.layout.layout_simulate_notification)
        remoteViews.setTextViewText(R.id.tv_title, "msg from process:${Process.myPid()}")
        remoteViews.setTextViewText(R.id.tv_content, "progress:$progress")
//        remoteViews.setImageViewResource(R.id.ic_pic, R.drawable.ic_launcher)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, this::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val open2ActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, SimulateNotificationActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        remoteViews.setOnClickPendingIntent(R.id.tv_content, pendingIntent)
        val intent = Intent(Constant.SIMULATE_NOTIFICATION_REMOTE_ACTION).apply {
            putExtra(Constant.SIMULATE_NOTIFICATION_REMOTE_VIEW_EXTRA, remoteViews)
        }
        sendBroadcast(intent)
    }

}
