package com.teligen.litedevice.ui.widget

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.dk.android_art.R
import com.dk.android_art.customview.checkedview.InputFilterMinMax
import com.dk.android_art.customview.checkedview.MultiCheckedLayout
import kotlinx.android.synthetic.main.layout_check_edit_view.view.*


/**
 *@ClassName CheckedEditTextView
 *@Description EditText与CheckBox的view
 *@Date 2020/6/2 17:09
 *@Create by linhong
 */
class CheckedEditTextView(ctx: Context, attrs: AttributeSet?) : ConstraintLayout(ctx, attrs),
    CheckedView {

    override var checkedViewState: MultiCheckedLayout.CheckedViewState =
        MultiCheckedLayout.CheckedViewState.NORMAL

    override var predicate: (arg: String) -> Boolean = { false }
    override var prompt: String = ""

    init {
        //加载布局
        LayoutInflater.from(ctx).inflate(getLayoutId(), this)
        et_number.filters = arrayOf(InputFilterMinMax(0, 1000), InputFilter.LengthFilter(4))
        et_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                println("-------afterTextChanged--($s)-------")
                s?.apply {
                    if (isNotEmpty() && predicate(this.toString())) {
                        et_number.error = prompt
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                println("-------beforeTextChanged--($s)-------")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println("-------onTextChanged--($s)-------")
            }

        })
    }


    /**
     * 获取布局id
     */
    private fun getLayoutId(): Int {
        return R.layout.layout_check_edit_view
    }

    override fun setViewId(viewId: Int) {
        id = viewId
    }

    /**
     * 拿到子view的data
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : CheckedView.Entity> viewData(): T? {
        val checked = cb_check?.isChecked == true
        et_number?.apply {
            val context = text?.toString()
            val hint = hint?.toString()
            return ViewEntity(
                if (context.isNullOrEmpty()) hint ?: "" else context,
                checked
            ) as T
        }
        return null
    }

    override fun toggle() {

    }

    override fun getViewId() = id

    /**
     * view的数据体
     */
    class ViewEntity(val content: String, val isChecked: Boolean) : CheckedView.Entity() {
        override fun toString(): String {
            return "ViewEntity(content='$content', isChecked=$isChecked)"
        }
    }
}