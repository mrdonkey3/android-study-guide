package com.dk.android_art.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.dk.android_art.R
import com.dk.android_art.customview.listview.ListViewEx
import com.dk.android_art.utils.WindowUtils
import kotlinx.android.synthetic.main.activity_aidl_binder_pool.*
import kotlinx.android.synthetic.main.activity_view_event_policy.*

class ViewEventPolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event_policy)
        initView()
        initView2()
    }

    /**
     * 外部拦截法
     */
    private fun initView() {
        repeat(3) {
            val layout = layoutInflater
                .inflate(R.layout.content_layout, horizontalScrollView, false) as ViewGroup
            layout.layoutParams.width = WindowUtils.getScreenMetrics(this)?.widthPixels?:100
            layout.findViewById<TextView>(R.id.title).text = "1->page:$it"
            layout.setBackgroundColor(Color.rgb(255.div(it + 1), 255.div(it + 1), 0))
            createList(layout)
            horizontalScrollView.addView(layout)
        }
    }

    /**
     * 内部拦截法
     */
    private fun initView2(){
        repeat(3) {
            val layout = layoutInflater
                .inflate(R.layout.content_layout2, horizontalScrollViewEx2, false) as ViewGroup
            layout.layoutParams.width = WindowUtils.getScreenMetrics(this)?.widthPixels?:100
            layout.findViewById<TextView>(R.id.title).text = "2->page:$it"
            layout.setBackgroundColor(Color.rgb(255.div(it + 2), 255.div(it + 2), 0))
            createList2(layout)
            horizontalScrollViewEx2.addView(layout)
        }
    }



    private fun createList(viewGroup: ViewGroup) {
        val listView = viewGroup.findViewById<ListView>(R.id.listView)
        val data = arrayListOf<String>()
        repeat(50) {
            data.add("name:$it")
        }
        val arrayAdapter = ArrayAdapter(this, R.layout.content_list_item, R.id.name, data)
        listView.adapter = arrayAdapter
    }


    private fun createList2(viewGroup: ViewGroup) {
        val listView = viewGroup.findViewById<ListViewEx>(R.id.listViewEx)
        listView.setHorizontalScrollViewEx2(horizontalScrollViewEx2)
        val data = arrayListOf<String>()
        repeat(50) {
            data.add("name:$it")
        }
        val arrayAdapter = ArrayAdapter(this, R.layout.content_list_item, R.id.name, data)
        listView.adapter = arrayAdapter
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}