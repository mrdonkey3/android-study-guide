package com.teligen.litedevice.ui.widget

import android.annotation.IdRes
import com.dk.android_art.customview.checkedview.MultiCheckedLayout

/**
 *@ClassName CheckedView
 *@Description 抽象CheckedView 提供公共的方法，使用MultiCheckedLayout来填充的基本view都需要实现该view [T] 是希望从该view获取什么样类型的数据
 *@Date 2020/6/2 15:19
 *@Create by linhong
 */
interface CheckedView {


    /**
     * 当前view的状态
     */
    var checkedViewState: MultiCheckedLayout.CheckedViewState

    /**
     * 预测方法
     */
    var predicate: (arg: String) -> Boolean

    /**
     * 提示内容
     */
    var prompt: String


    /**
     * 切换动作，具体实现由实现类来操作
     */
    fun toggle()

    /**
     * 设置默认显示文本 具体实现类可以选择性 重写
     */
    fun setDefaultText(text: String) {

    }

    /**
     * 设置通代码设置id
     */
    fun setViewId(viewId: Int)

    /**
     * 获取checkView的id
     */
    fun getViewId(): Int

    /**
     * 获取子view的data，具体由实现类选择性重写
     */
    fun <T : Entity> viewData(): T? {
        return null
    }

    abstract class Entity


}