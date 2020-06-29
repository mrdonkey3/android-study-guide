package com.dk.android_art.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log
import com.dk.android_art.db.DbOpenHelper

/**
 * @create on 2020/6/21 18:52
 * @description 实现ipc的方式之contentProvider 底层也binder
 * @author mrdonkey
 */
class BookContentProvider : ContentProvider() {

    private val TAG = "BookContentProvider"
    private val AUTHORITY = "com.dk.android_art.book.provider"
    private val BOOK_CONTENT_URI = Uri.parse("content://$AUTHORITY/book")
    private val USER_CONTENT_URI = Uri.parse("content://$AUTHORITY/user")
    private val BOOK_URI_CODE = 0
    private val USER_URI_CODE = 1
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)//匹配uri获取对应的code

    private var ctx: Context? = null
    private var db: SQLiteDatabase? = null

    init {
        uriMatcher.addURI(AUTHORITY, "book", BOOK_URI_CODE)
        uriMatcher.addURI(AUTHORITY, "user", USER_URI_CODE)
    }


    override fun onCreate(): Boolean {
        Log.e(TAG, "--->onCreate:${Thread.currentThread()}")
        ctx = context
        //不推荐在主线程中进行耗时的数据库操作，仅为了方便
        initProviderData()
        return false
    }

    private fun initProviderData() {
        ctx?.apply {
            db = DbOpenHelper(this).writableDatabase.apply {
                execSQL("delete from " + DbOpenHelper.BOOK_TABLE_NAME)
                execSQL("delete from " + DbOpenHelper.USER_TABLE_NAME)
                execSQL("insert into book values(3,'android') ")
                execSQL("insert into book values(4,'ios') ")
                execSQL("insert into book values(5,'html') ")
                execSQL("insert into user values(1,'jake',1) ")
                execSQL("insert into user values(2,'jame',0) ")
            }
        }
    }

    override fun getType(uri: Uri): String? {
        Log.e(TAG, "--->getType:${Thread.currentThread()}")
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.e(TAG, "--->insert:${Thread.currentThread()}")
        val tableName = getTableName(uri) ?: throw IllegalArgumentException("UnSupport URI:$uri")
        db?.insert(tableName, null, values)
        ctx?.contentResolver?.notifyChange(uri, null)
        return uri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        Log.e(TAG, "--->delete:${Thread.currentThread()}")
        val tableName = getTableName(uri) ?: throw IllegalArgumentException("UnSupport URI:$uri")
        val count = db?.delete(tableName, selection, selectionArgs) ?: 0
        if (count > 0)
            ctx?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        Log.e(TAG, "--->query:${Thread.currentThread()}")
        val tableName = getTableName(uri) ?: throw IllegalArgumentException("UnSupport URI:$uri")
        return db?.query(
            tableName,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder,
            null
        )
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        Log.e(TAG, "--->update:${Thread.currentThread()}")
        val tableName = getTableName(uri) ?: throw IllegalArgumentException("UnSupport URI:$uri")
        val row = db?.update(tableName, values, selection, selectionArgs) ?: 0
        if (row > 0)
            ctx?.contentResolver?.notifyChange(uri, null)
        return row
    }


    /**
     * 传入uri 通过uriMatcher进行匹配得到对应的code来决定表名
     */
    private fun getTableName(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            BOOK_URI_CODE -> DbOpenHelper.BOOK_TABLE_NAME
            USER_URI_CODE -> DbOpenHelper.USER_TABLE_NAME
            else -> null
        }
    }


}