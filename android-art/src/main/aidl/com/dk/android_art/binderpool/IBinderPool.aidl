// IBinderPool.aidl
package com.dk.android_art.binderpool;

// binder连接池，当binder连接池连接上远程服务时，会根据不同的标识即binderCodo返回不同的Binder对象，
//通过这个binder对象执行的操作全部发生在远程服务端

interface IBinderPool {
   IBinder queryBinder(int binderCode);
}
