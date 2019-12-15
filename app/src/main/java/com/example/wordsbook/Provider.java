package com.example.wordsbook;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class Provider extends ContentProvider {

    private MyDataBaseOpenHelper myDataBaseOpenHelper;
    public Provider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        SQLiteDatabase db=myDataBaseOpenHelper.getWritableDatabase();
        db.execSQL("delete from words where word=?",selectionArgs);
        db.close();
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        SQLiteDatabase db=myDataBaseOpenHelper.getWritableDatabase();
        String word=values.getAsString("word");
        String meaning=values.getAsString("meaning");
        String sample=values.getAsString("sample");
        db.execSQL("insert into words(word,meaning,sample) values (?,?,?)",new Object[]{word,meaning,sample});
        db.close();
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        myDataBaseOpenHelper = new MyDataBaseOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteDatabase db=myDataBaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select id,word,meaning,sample from words",null);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        SQLiteDatabase db=myDataBaseOpenHelper.getReadableDatabase();
        String newWord=values.getAsString("newWord");
        String oldWord=values.getAsString("oldWord");
        db.execSQL("update words set word=? where word=?",new Object[]{newWord,oldWord});
        db.close();
        return 0;
    }
}
