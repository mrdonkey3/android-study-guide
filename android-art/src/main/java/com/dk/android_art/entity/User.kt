package com.dk.android_art.entity

/**
 * @create on 2020/6/22 00:01
 * @description 用户类
 * @author mrdonkey
 */
class User constructor(var _id: Int, var name: String, var sex: Int) {
    override fun toString(): String {
        return "User(_id=$_id, name='$name', sex=$sex)"
    }
}