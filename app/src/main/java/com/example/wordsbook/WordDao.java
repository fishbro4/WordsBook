package com.example.wordsbook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class WordDao {

    private MyDataBaseOpenHelper helper;
    public WordDao(Context context){
        helper = new MyDataBaseOpenHelper(context);
    }

    /**
     * 增
     */
    public void add(String word,String meaning,String sample){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into words(word,meaning,sample) values (?,?,?)",new Object[]{word,meaning,sample});
        db.close();
    }

    /**
     * 删（通过单词本身）
     */
    public void delete(String word){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from words where word=?",new Object[]{word});
        db.close();
    }

    /**
     * 改
     * @param newWord 待修改单词
     * @param newMeaning 修改后的meaning
     * @param newSample 修改后的sample
     */
    public void updata(String id,String newWord,String newMeaning,String newSample){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("update words set word=?,meaning=?,sample=? where id=?",new Object[]{newWord,newMeaning,newSample,id});
        db.close();
    }

    public List<Word> findTest(String keyword){
        List<Word> words = new ArrayList<Word>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql="select word,meaning,sample from words where word like ? order by word desc";
        Cursor cursor=db.rawQuery(sql,new String[]{"%"+keyword+"%"});
        while (cursor.moveToNext()){
            String word=cursor.getString(0);
            String meaning=cursor.getString(1);
            String sample=cursor.getString(2);
            Word w = new Word();
            w.setWord(word);
            w.setMeaning(meaning);
            w.setSample(sample);
            words.add(w);
        }
        cursor.close();
        db.close();
        return  words;
    }

    public Word findByWord(String word){
        SQLiteDatabase db = helper.getWritableDatabase();
        Word w = new Word();
        String meaning = null;
        String sample=null;
        Cursor cursor = db.rawQuery("select meaning,sample from words where word=?",new String[]{word});
        boolean result = cursor.moveToNext();
        if(result){
            meaning = cursor.getString(0);
            sample=cursor.getString(1);
            w.setMeaning(meaning);
            w.setSample(sample);;
        }
        cursor.close();
        db.close();
        return w;
    }

    public Word findfirstword(){
        SQLiteDatabase db = helper.getWritableDatabase();
        Word w = new Word();
        String meaning = null;
        String sample=null;
        String word=null;
        Cursor cursor = db.rawQuery("select word,meaning,sample from words limit 1",null);
        boolean result = cursor.moveToNext();
        if(result){
            word=cursor.getString(0);
            meaning = cursor.getString(1);
            sample=cursor.getString(2);
            w.setWord(word);
            w.setMeaning(meaning);
            w.setSample(sample);;
        }
        cursor.close();
        db.close();
        return w;
    }

    public String findId(String word){
        SQLiteDatabase db = helper.getReadableDatabase();
        String id = null;
        Cursor cursor = db.rawQuery("select id from words where word=?",new String[]{word});
        boolean result = cursor.moveToNext();
        if(result){
            id = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return id;
    }

    public List<Word> findAll(){
        List<Word> words = new ArrayList<Word>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select id,word,meaning,sample from words",null);
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String word=cursor.getString(1);
            String meaning=cursor.getString(2);
            String sample=cursor.getString(3);
            Word w = new Word();
            w.setId(id);
            w.setWord(word);
            w.setMeaning(meaning);
            w.setSample(sample);
            words.add(w);
        }
        cursor.close();
        db.close();
        return  words;
    }
}
