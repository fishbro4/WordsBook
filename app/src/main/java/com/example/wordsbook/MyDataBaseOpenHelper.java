package com.example.wordsbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDataBaseOpenHelper extends SQLiteOpenHelper {

    /*
     * @param context 上下文
     * @param name 数据库名称
     * @param factory 默认设置
     * @param version 版本
     */

    public MyDataBaseOpenHelper(Context context) {
        super(context, "words_list.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table words(id integer primary key autoincrement,word varchar(20),meaning varchar(50),sample varchar(100))");
        sqLiteDatabase.execSQL("insert into words (word,meaning,sample) values('Apple','苹果','This is an apple.')");
        sqLiteDatabase.execSQL("insert into words (word,meaning,sample) values('Orange','橘子','This orange is very nice.')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("");
        onCreate(sqLiteDatabase);
    }
}
