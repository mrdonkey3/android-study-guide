package com.dk.android_art.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * @create on 2020/8/22 14:10
 * @description 图片压缩类
 * @author mrdonkey
 */
class ImageResizer {

    companion object{
        var test=0

    }

    /**
     * 从resource 中 有效解析 bitmap 对象
     */
    fun decodeSampledBitmapFromResource(res: Resources, resId: Int, reqWidth: Int, reqHeight: Int):
            Bitmap {
        //first decode with inJustDecodeBounds = true to check dimensions  通过该方法获取原始图片的尺寸
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)

        //calculate inSampleSize 计算采样率
        val calculateInSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inSampleSize = calculateInSampleSize

        //decodeBitmap with inSampleSize set
        options.inJustDecodeBounds = false

        return  BitmapFactory.decodeResource(res,resId,options)

    }

    /**
     * 计算采样率 通过原始尺寸与期望尺寸比较得到最终的采样率
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        //raw width and height of image
        val rawWidth= options.outWidth
        val rawHeight = options.outHeight

        var inSampleSize = 1

        if (rawWidth > reqWidth || rawHeight > reqHeight){
            val halfWidth  = rawWidth / 2
            val halfHeight = rawHeight / 2
            //calculate the largest inSampleSize value that is a power of 2 and keeps both
            //height and width larger than the request height and width
            while( (halfWidth/inSampleSize) >= reqWidth && (halfHeight/inSampleSize) >= reqHeight){
                inSampleSize *=2
            }
        }

        return inSampleSize
    }

}