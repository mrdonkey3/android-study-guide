package com.dk.android_art.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dk.android_art.R
import com.dk.android_art.utils.WindowUtils

/**
 * @create on 2020/7/3 20:36
 * @description TODO
 * @author mrdonkey
 */
class MultiEditTextAdapter : ListAdapter<String, MultiEditTextAdapter.ViewHolder>
    (object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return false
    }

}) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_multi_edit_text, parent,false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("onBindViewHolder--->$position")
        holder.setIsRecyclable(false)
        //Custom view `EditText` has setOnTouchListener called on it but does not override performClick
        //在onTouch中返回ture,同时又添加了onClick监听,这时onClick就不会执行了,事件被onTouch消化掉了.
        // 来查看一下执行顺序就知道了,onTouchEvent=>performClick=>onClick,所以在onTouch返回ture时,同时又添加了onClick监听,
        // 正确的处理方法应该是在onTouch中适当的地方执行performClick方法,来触发onClick.
        //自动控制edittext的touch事件,控制弹出软键盘，并获得焦点
        holder.et1.setOnTouchListener { v, event ->
            if (event.action.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                WindowUtils.showSoftInputFromWindow(v)
                true
            } else
                false
        }
        holder.et1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                println("afterTextChanged--->$s")
                if (!s.isNullOrEmpty()) {
                    val number = s.toString().toInt()
                    holder.iv.visibility = if (number > 100) View.VISIBLE else View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                println("beforeTextChanged--->")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println("onTextChanged--->")
            }

        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val et1: EditText = itemView.findViewById(R.id.editTextNumber)
        val et2: EditText = itemView.findViewById(R.id.editTextNumber2)
        val iv: ImageView = itemView.findViewById(R.id.iv)
    }
}