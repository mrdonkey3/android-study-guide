package com.dk.android_art.aidl.binderpool

import com.dk.android_art.binderpool.ICompute

/**
 * @create on 2020/6/25 13:42
 * @description Binder计算的实现类
 * @author mrdonkey
 */
class ComputeImpl : ICompute.Stub() {
    override fun plus(a: Int, b: Int): Int {
        return a.plus(b)
    }
}