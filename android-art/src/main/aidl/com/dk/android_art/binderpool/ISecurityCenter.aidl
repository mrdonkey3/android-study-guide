// ISecurityCenter.aidl
package com.dk.android_art.binderpool;

// 负责提供加密解密功能

interface ISecurityCenter {
  String encrypt(String content);
  String decrypt(String content);
}
