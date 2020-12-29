package com.dk.android_art.frags

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dk.android_art.R
import com.dk.android_art.customview.displayView.DisplayView
import com.teligen.litedevice.ui.widget.CheckedEditTextView
import com.teligen.litedevice.ui.widget.CheckedView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_checked_view.*

/**
 * 展示[MultiCheckedLayout]的fragment
 */
class CheckedViewFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_checked_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkViewTest()
        var isps =
            arrayOf(0, 1, 11, -1).toList().shuffled().take(arrayOf(1, 2, 3).random()).toTypedArray()
        var status = arrayOf(DisplayView.STATUS.UNREADY, DisplayView.STATUS.READY, DisplayView.STATUS.PA).random().status
        if (isps.contains(-1)) {
            isps = arrayOf(-1)
            status = DisplayView.STATUS.UNREADY.status
        }
        display.reloadLayout(isps,status)
    }


    private fun checkViewTest() {
        btn_check_view?.setOnClickListener {
            val textData = multi_cl_text?.getChildViewData<CheckedView.Entity>()
            val editData = multi_cl_edit?.getChildViewData<CheckedEditTextView.ViewEntity>()
            multi_cl_edit?.setPredicate {
                !(it.toInt() in 101 downTo 9)//10-100
            }?.setPrompt(getString(R.string.number_over_range))
            tv_scan?.text = editData?.joinToString(separator = "\n")
        }
    }

}