package com.dk.android_art.customview.dialogfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dk.android_art.R
import com.dk.android_art.adapter.MultiEditTextAdapter
import kotlinx.android.synthetic.main.layout_full_screen_dialog.*

/**
 * @create on 2020/6/4 21:16
 * @description 全屏的dialog fragment
 * @author mrdonkey
 */
class FullScreenDialogFragment : DialogFragment() {

    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = super.onCreateView(inflater, container, savedInstanceState)
        if (rootView == null)
            rootView = inflater.inflate(R.layout.layout_full_screen_dialog, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        rootView?.findViewById<EditText>(R.id.et_0)?.apply {
            clearFocus()
            requestFocus()
        }
        rootView?.findViewById<RecyclerView>(R.id.rv_multi_edit_text)?.apply {
            layoutManager = LinearLayoutManager(context)
            val multiEditTextAdapter = MultiEditTextAdapter()
            adapter = multiEditTextAdapter
            multiEditTextAdapter.submitList(arrayListOf("1", "2", "3", "4", "5", "6"))

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.apply {
            setGravity(Gravity.CENTER)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}