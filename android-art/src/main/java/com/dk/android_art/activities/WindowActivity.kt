package com.dk.android_art.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager.LayoutParams
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dk.android_art.R
import com.dk.android_art.utils.ViewUtils

class WindowActivity : AppCompatActivity() {

    private lateinit var mFloatingButton: Button
    private lateinit var mLayoutParams: LayoutParams
    private var comsumer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_window)
        checkSelfPermission()
    }

    private fun showFloatingWindow() {
        mFloatingButton = Button(this)
        mFloatingButton.text = "floating"
        mFloatingButton.setOnTouchListener(this::onBtnTouch)
        mLayoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            0,
            0,
            PixelFormat.TRANSPARENT
        )
        mLayoutParams.flags =
            LayoutParams.FLAG_NOT_FOCUSABLE
                .or(LayoutParams.FLAG_NOT_TOUCH_MODAL)
//                .or(LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        mLayoutParams.gravity = Gravity.START.or(Gravity.TOP)
        mLayoutParams.x = 100
        mLayoutParams.y = 300
        mLayoutParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY
        windowManager.addView(mFloatingButton, mLayoutParams)
    }

    private fun checkSelfPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                showFloatingWindow()
            } else {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                startActivityForResult(intent, 1001)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1001 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        //弹出悬浮框
                        showFloatingWindow()
                    } else {
                        Toast.makeText(this, "not granted permission!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else -> {
                //ignore
            }
        }
    }

    private fun onBtnTouch(view: View, event: MotionEvent): Boolean {
        val rawX = event.rawX
        val rawY = event.rawY
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                comsumer = ViewUtils.isTouchPointInView(mFloatingButton, rawX, rawY)
            }
            MotionEvent.ACTION_MOVE -> {
                if (comsumer) {
                    mLayoutParams.x = rawX.toInt()
                    mLayoutParams.y = rawY.toInt()
                    windowManager.updateViewLayout(mFloatingButton, mLayoutParams)
                    return true
                }
            }
            else -> {
                return false
            }

        }
        return false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
//        if (this::mFloatingButton.isInitialized)
//            windowManager.removeView(mFloatingButton)
    }
}