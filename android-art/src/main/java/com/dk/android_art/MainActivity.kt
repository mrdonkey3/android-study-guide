package com.dk.android_art

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dk.android_art.customview.dialogfragment.FullScreenDialogFragment
import com.dk.android_art.frags.CheckedViewFragment
import com.dk.android_art.frags.DialogFragment
import com.dk.android_art.frags.PersonViewFragment
import com.teligen.litedevice.ui.widget.CheckedEditTextView
import com.teligen.litedevice.ui.widget.CheckedView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListener()
    }

    private fun setListener(){
        btn_person_view?.setOnClickListener(this)
        btn_check_view?.setOnClickListener(this)
        btn_dialog?.setOnClickListener(this)
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
            else -> {
                return
            }
        }
        transaction.commit()
    }
}
