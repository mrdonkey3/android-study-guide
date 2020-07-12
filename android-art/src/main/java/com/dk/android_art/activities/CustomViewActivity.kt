package com.dk.android_art.activities

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dk.android_art.R
import com.dk.android_art.adapter.PinnedExpandableListAdapter
import com.dk.android_art.customview.custom.PinnedHeaderExpandableListView
import com.dk.android_art.customview.custom.StickyLayout
import com.dk.android_art.entity.Group
import com.dk.android_art.entity.People
import java.util.*

class CustomViewActivity : AppCompatActivity(), ExpandableListView.OnChildClickListener,
    ExpandableListView.OnGroupClickListener,
    PinnedHeaderExpandableListView.OnGroupHeaderUpdateListener,
    StickyLayout.OnChildGiveUpTouchEventListener {

    private lateinit var expandableListView: PinnedHeaderExpandableListView
    private lateinit var stickyLayout: StickyLayout
    private lateinit var adapter: PinnedExpandableListAdapter

    private var groupList = arrayListOf<Group>()
    private var childList = arrayListOf<List<People>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        expandableListView = findViewById(R.id.expandablelist)
        stickyLayout = findViewById(R.id.sticky_layout)
        initData()
        adapter = PinnedExpandableListAdapter(this, groupList, childList)
        expandableListView.setAdapter(adapter)
        //默认展开所有group
        for (i in groupList.indices) {
            expandableListView.expandGroup(i)
        }
        expandableListView.setOnGroupHeaderUpdateListener(this)
        expandableListView.setOnChildClickListener(this)
        expandableListView.setOnGroupClickListener(this)
        stickyLayout.setOnChildGiveUpTouchEventListener(this)
    }


    /***
     * InitData
     */
    fun initData() {
        var group: Group? = null
        for (i in 0..2) {
            group = Group("group-$i")
            groupList.add(group)
        }
        for (i in groupList.indices) {
            var childTemp: ArrayList<People>
            if (i == 0) {
                childTemp = ArrayList()
                for (j in 0..12) {
                    val people = People("yy-$j", 30, "sh-$j")
                    childTemp.add(people)
                }
            } else if (i == 1) {
                childTemp = ArrayList()
                for (j in 0..7) {
                    val people = People("yy-$j", 40, "sh-$j")
                    childTemp.add(people)
                }
            } else {
                childTemp = ArrayList()
                for (j in 0..22) {
                    val people = People("yy-$j", 20, "sh-$j")
                    childTemp.add(people)
                }
            }
            childList.add(childTemp)
        }
    }

    override fun onChildClick(
        parent: ExpandableListView?,
        v: View?,
        groupPosition: Int,
        childPosition: Int,
        id: Long
    ) = false

    override fun onGroupClick(
        parent: ExpandableListView?,
        v: View?,
        groupPosition: Int,
        id: Long
    ) = false

    override fun getPinnedHeader(): View {
        val headerView: View = layoutInflater.inflate(R.layout.group, null) as ViewGroup
        headerView.layoutParams = AbsListView.LayoutParams(
            AbsListView.LayoutParams.MATCH_PARENT,
            AbsListView.LayoutParams.WRAP_CONTENT
        )
        return headerView //最终的高度是parentSize
    }

    override fun updatePinnedHeader(mHeader: View?, firstVisibleGroupPos: Int) {
        mHeader?.findViewById<TextView>(R.id.group)?.text =
            adapter.getGroup(firstVisibleGroupPos).title
    }

    override fun giveUpTouchEvent(event: MotionEvent): Boolean {
        //所以只有在content达到顶部时，向下滑动时进行拦截，让（stickyLayout拦截）header显示出来
        if (expandableListView.firstVisiblePosition == 0) {
            val view = expandableListView.getChildAt(0)
            if (view != null && view.top >= 0) {
                return true
            }
        }
        return false
    }
}