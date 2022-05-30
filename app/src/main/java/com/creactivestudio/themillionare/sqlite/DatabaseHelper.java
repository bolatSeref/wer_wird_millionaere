package com.creactivestudio.themillionare.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, "Millionare", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE questions (question TEXT,choiceA TEXT,choiceB TEXT,choiceC TEXT,choiceD TEXT,questionLevel INTEGER,questionId INTEGER,questionLanguage TEXT,wrightAnswer INTEGER,questionDocId TEXT,rightChoice TEXT);");
        db.execSQL("CREATE TABLE questionsTR (question TEXT,choiceA TEXT,choiceB TEXT,choiceC TEXT,choiceD TEXT,questionLevel INTEGER,questionId INTEGER,questionLanguage TEXT,wrightAnswer INTEGER,questionDocId TEXT,rightChoice TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS questions");
        db.execSQL("DROP TABLE IF EXISTS questionsTR");
         onCreate(db);
    }
}
