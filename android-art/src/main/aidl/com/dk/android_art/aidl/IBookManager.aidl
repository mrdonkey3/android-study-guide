// IBookManager.aidl
package com.dk.android_art.aidl;

import com.dk.android_art.aidl.Book;
import com.dk.android_art.aidl.IOnNewBookArrivedListener;

interface IBookManager { //aidl接口只支持方法，不支持静态常量
    //从远程服务端获取图书列表
    List<Book> getBookList();
    //往图书列表中添加书
    void addBook(in Book book);//除基本数据类型外，其他的类型必须标上方向，in/out/inout in：表示输入类型

    void registerListener(IOnNewBookArrivedListener lisenter);

    void unRegisterListener(IOnNewBookArrivedListener lisenter);

}
