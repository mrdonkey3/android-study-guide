package com.dk.android_art

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dk.android_art.activities.AidlBinderPoolActivity
import com.dk.android_art.activities.AidlBookClientActivity
import com.dk.android_art.activities.TCPClientActivity
import com.dk.android_art.activities.ViewEventPolicyActivity
import com.dk.android_art.frags.CheckedViewFragment
import com.dk.android_art.frags.DialogFragment
import com.dk.android_art.frags.PersonViewFragment
import com.dk.android_art.utils.NetWorkUtils
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
            R.id.btn_socket_tcp_client_activity->{
                startActivity(Intent(this, TCPClientActivity::class.java))
            }
            R.id.btn_view_event_policy->{
                startActivity(Intent(this, ViewEventPolicyActivity::class.java))
            }

            else -> {
                return
            }
        }
        transaction.commit()
    }
}
