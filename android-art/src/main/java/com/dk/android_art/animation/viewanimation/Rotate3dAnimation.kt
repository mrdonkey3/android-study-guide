package com.dk.android_art.animation.viewanimation

import android.graphics.Camera
import android.view.animation.Animation
import android.view.animation.Transformation

/**
 * @create on 2020/7/20 22:44
 * @description 围绕y轴旋转并且同时沿着z轴平移从而实现一种类似3d效果
 * @author mrdonkey
 */
class Rotate3dAnimation(
    val mFromDegrees: Float,//开始角度
    val mToDegrees: Float,//结束角度
    val mCenterX: Float,//开始x坐标
    val mCenterY: Float,//开始的y坐标
    val mDepthZ: Float,//深度z
    val mReverse: Boolean//是否反转
) : Animation() {
    private lateinit var mCamera: Camera//简化矩阵变换的过程


    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        mCamera = Camera()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        val fromDegrees = mFromDegrees
        val degrees = fromDegrees + (mToDegrees - fromDegrees) * interpolatedTime
        val centerX = mCenterX
        val centerY = mCenterY
        val camera = mCamera
        val matrix = t?.matrix

        camera.save()
        if (mReverse) {
            camera.translate(0f, 0f, mDepthZ * interpolatedTime)
        } else {
            camera.translate(0f, 0f, mDepthZ * (1.0f - interpolatedTime))
        }
        camera.rotateX(degrees)
        camera.getMatrix(matrix)
        camera.restore()
        matrix?.preTranslate(-centerX, -centerY)
        matrix?.postTranslate(centerX, centerY)

    }
}