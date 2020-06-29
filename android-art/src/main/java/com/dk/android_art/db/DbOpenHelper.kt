package com.dk.android_art.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * @create on 2020/6/21 19:43
 * @description TODO
 * @author mrdonkey
 */
class DbOpenHelper(context: Context, dbName: String = "book_provider.db", dbVersion: Int = 1) :
    SQLiteOpenHelper(context, dbName, null, dbVersion) {
    companion object {
         val BOOK_TABLE_NAME = "book"
         val USER_TABLE_NAME = "user"

        private val CREATE_BOOK_TABLE = "CREATE TABLE IF NOT EXISTS " +
                BOOK_TABLE_NAME + " (_id INTEGER PRIMARY KEY, name TEXT)"
        private val CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " +
                USER_TABLE_NAME + " (_id INTEGER PRIMARY KEY, name TEXT , sex INT)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_BOOK_TABLE)
        db?.execSQL(CREATE_USER_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //todo ingnore
    }

}