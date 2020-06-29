package com.dk.android_art.aidl.binderpool

import com.dk.android_art.binderpool.ISecurityCenter
import kotlin.experimental.xor

/**
 * @create on 2020/6/25 13:42
 * @description 加解密的binder
 * @author mrdonkey
 */
 open class SecurityCenterImpl : ISecurityCenter.Stub() {
    private val SECRET_CODE = '^'
    override fun encrypt(content: String?): String {
        content?.apply {
            val chars =toCharArray()
            chars.forEachIndexed { index, c ->
                chars[index]=c.toByte().xor(SECRET_CODE.toByte()).toChar()
            }
            return String(chars)
        }
        return ""
    }

    override fun decrypt(content: String?): String {
        return encrypt(content)
    }

}