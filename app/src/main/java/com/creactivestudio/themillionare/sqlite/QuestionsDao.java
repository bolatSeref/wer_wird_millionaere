package com.creactivestudio.themillionare.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class QuestionsDao {
    public void addQuestion (DatabaseHelper db, String tableName, String question, String choiceA, String choiceB, String choiceC, String choiceD,
                             int questionLevel, int questionId, int wrightAnswer, String questionLanguage, String questionDocId, String rightChoice)
    {
        SQLiteDatabase database=db.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("question", question);
        contentValues.put("choiceA", choiceA);
        contentValues.put("choiceB", choiceB);
        contentValues.put("choiceC", choiceC);
        contentValues.put("choiceD", choiceD);
        contentValues.put("questionLevel", questionLevel);
        contentValues.put("questionId", questionId);
        contentValues.put("wrightAnswer", wrightAnswer);
        contentValues.put("questionLanguage", questionLanguage);
        contentValues.put("questionDocId",questionDocId);
        contentValues.put("rightChoice",rightChoice);
        database.insertOrThrow(tableName, null,contentValues);
        database.close();


    }
    public ArrayList<Questions> allQuestions (DatabaseHelper databaseHelper)
    {
        ArrayList<Questions> questionsArrayList=new ArrayList<>();
        SQLiteDatabase database=databaseHelper.getWritableDatabase();
        Cursor c=database.rawQuery("SELECT * FROM questions", null);
        while (c.moveToNext())
        {
            Questions question=new Questions(c.getString(c.getColumnIndex("question"))
                    ,c.getString(c.getColumnIndex("choiceA"))
                    ,c.getString(c.getColumnIndex("choiceB"))
                    ,c.getString(c.getColumnIndex("choiceC"))
                    ,c.getString(c.getColumnIndex("choiceD"))
                    ,c.getInt(c.getColumnIndex("questionLevel"))
                    ,c.getInt(c.getColumnIndex("wrightAnswer"))
                    ,c.getInt(c.getColumnIndex("questionId"))
                    ,c.getString(c.getColumnIndex("questionLanguage"))
            ,c.getString(c.getColumnIndex("questionDocId"))
            ,c.getString(c.getColumnIndex("rightChoice")));
            questionsArrayList.add(question);
        }
        return questionsArrayList;
    }

    public ArrayList<Questions> randomQuestion (DatabaseHelper databaseHelper, int userLevel, String tableName)
    {

        ArrayList<Questions> questionsArrayList=new ArrayList<>();
        SQLiteDatabase database=databaseHelper.getWritableDatabase();

        String selectedLanguage="tr";

        Cursor c2=database.rawQuery("SELECT COUNT(*) as count FROM "+tableName +" WHERE questionLevel="+ userLevel, null);

        while (c2.moveToNext())
        {
            Log.e("selam", c2.getString(c2.getColumnIndex("count")));
        }

       // Cursor c=database.rawQuery("SELECT * FROM "+ tableName +" WHERE questionLevel="+userLevel+" AND WHERE questionLanguage="+selectedLanguage, null);
      //  Cursor c=database.rawQuery("SELECT * FROM "+ tableName +" WHERE questionLevel="+userLevel+" AND WHERE questionLanguage="+selectedLanguage, null);
        Cursor c=database.rawQuery("SELECT * FROM "+tableName +" WHERE questionLevel="+ userLevel, null);
        while (c.moveToNext())
        {
            Questions question=new Questions(c.getString(c.getColumnIndex("question"))
                    ,c.getString(c.getColumnIndex("choiceA"))
                    ,c.getString(c.getColumnIndex("choiceB"))
                    ,c.getString(c.getColumnIndex("choiceC"))
                    ,c.getString(c.getColumnIndex("choiceD"))
                    ,c.getInt(c.getColumnIndex("questionLevel"))
                    ,c.getInt(c.getColumnIndex("wrightAnswer"))
                    ,c.getInt(c.getColumnIndex("questionId"))
                    ,c.getString(c.getColumnIndex("questionLanguage"))
                    ,c.getString(c.getColumnIndex("questionDocId"))
            ,c.getString(c.getColumnIndex("rightChoice")));
            questionsArrayList.add(question);
        }
        return questionsArrayList;
    }

    public int dataCount (DatabaseHelper databaseHelper)
    {
        int dataCount=0;
        SQLiteDatabase database=databaseHelper.getWritableDatabase();
        Cursor c=  database.rawQuery("SELECT count(*) as result FROM questions", null);
        while (c.moveToNext())
        {
            dataCount=c.getInt(c.getColumnIndex("result"));
        }
        return dataCount;
    }
    public int levelDataCount (DatabaseHelper databaseHelper,  int level)
    {

        int dataCount=0;
        SQLiteDatabase database=databaseHelper.getWritableDatabase();
        Cursor c=  database.rawQuery("SELECT count(*) as result FROM questions WHERE questionLevel="+level, null);
        while (c.moveToNext())
        {
            dataCount=c.getInt(c.getColumnIndex("result"));
        }
        return dataCount;
    }


}


