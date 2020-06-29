// IOnNewBookArrivedListener.aidl
package com.dk.android_art.aidl;
import com.dk.android_art.aidl.Book;

//观察者模式 添加新书的回调

interface IOnNewBookArrivedListener {
   void onNewBookArrived(in Book book);
}
