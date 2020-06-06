package com.dk.android_art.frags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dk.android_art.R
import com.dk.android_art.customview.dialogfragment.FullScreenDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_dialog.*


/**
 * 展示[FullScreenDialogFragment]的fragment
 */
class DialogFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialogFragmentTest()
    }


    private fun dialogFragmentTest() {
        fragmentManager?.apply {
            btn_show_dialog?.setOnClickListener {
                val dialog = FullScreenDialogFragment()
                if (!dialog.isAdded)
                    dialog.show(this, "dialog")
            }
        }
    }

}