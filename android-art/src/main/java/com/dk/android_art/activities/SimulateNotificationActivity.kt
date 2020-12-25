package com.dk.android_art.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.dk.android_art.Constant
import com.dk.android_art.Constant.SIMULATE_NOTIFICATION_REMOTE_ACTION
import com.dk.android_art.Constant.SIMULATE_NOTIFICATION_REMOTE_VIEW_EXTRA
import com.dk.android_art.R
import kotlinx.android.synthetic.main.activity_simulate_notification.*

/**
 * 默认跨进程的通知栏
 */
class SimulateNotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulate_notification)
        initView()
    }
    private var needUpdateViews: View? = null


    /**
     * 广播
     */
    private val mRemoteViewsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            println("SimulateNotificationActivity--->onReceive--action:${intent.action}")
            intent.getParcelableExtra<RemoteViews>(SIMULATE_NOTIFICATION_REMOTE_VIEW_EXTRA)?.apply {
                updateUI(this)
            }
        }

    }

    /**
     * 收到广播后更新ui
     */
    private fun updateUI(remoteViews: RemoteViews) {
        //加载发过来的remoteView的布局，若是不同应用的通信，则无法直接使用removeViews.apply
        //因为apply里面 的操作是加载remoteViews的layoutId到当前容器中，因为layoutId在不同应用表示的值可能不一致
        //所以应该是reapply方法，通过两个应用指定相同的布局名，进行查找对应的布局进行加载，加载完后再调用reapply方法更新remoteView
        //这里演示的是不用应用间
        val view = needUpdateViews ?: layoutInflater.inflate(
            resources.getIdentifier(
                Constant.SIMULATE_NOTIFICATION_REMOTE_VIEW_LAYOUT_ID,
                "layout",
                packageName
            ), container, false
        )
        remoteViews.reapply(this, view)
        if (needUpdateViews == null) {
            container.addView(view)
            needUpdateViews = view
        }
    }

    private fun initView() {
        //注册广播，并且设置过滤器
        registerReceiver(mRemoteViewsReceiver, IntentFilter(SIMULATE_NOTIFICATION_REMOTE_ACTION))
    }

}