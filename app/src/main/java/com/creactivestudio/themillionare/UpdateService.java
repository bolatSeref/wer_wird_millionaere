package com.creactivestudio.themillionare;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.creactivestudio.themillionare.sqlite.DatabaseHelper;
import com.creactivestudio.themillionare.sqlite.QuestionsDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.creactivestudio.themillionare.GameActivity.FB_CHOICE_A_KEY;
import static com.creactivestudio.themillionare.GameActivity.FB_CHOICE_B_KEY;
import static com.creactivestudio.themillionare.GameActivity.FB_CHOICE_C_KEY;
import static com.creactivestudio.themillionare.GameActivity.FB_CHOICE_D_KEY;
import static com.creactivestudio.themillionare.GameActivity.FB_QUESTION_KEY;
import static com.creactivestudio.themillionare.GameActivity.FB_QUESTION_LEVEL_KEY;

public class UpdateService  extends Service {
    private static final String KEY_FB_QUESTION_VERSION_NUMBER = "Question_Version_Number";
    String selectedQuestionCollection;
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
     //   fillEnQuestionTableFromFirebaseToSqlite();
        fillTrQuestionTableFromFirebaseToSqlite();
        sharedPreferences=getSharedPreferences("sp_jokers", MODE_PRIVATE);
        editor=sharedPreferences.edit();


    }

    private void fillTrQuestionTableFromFirebaseToSqlite() {
        DatabaseHelper databaseHelper;
        databaseHelper=new DatabaseHelper(this);
        String tableNameTR="questionsTR";
        //QuestionsTR eski db
        CollectionReference collectionReferenceQuestions=firebaseFirestore.collection("test_questions_02tem");
        collectionReferenceQuestions.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
// TODO: 31.01.2021  kullanıcının interneti kontrol edilecek ,,, güncel sürüm var mı kontrol edilecek ,, splash screen de çalıştırılacak
                        if(task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document :
                                    task.getResult()) {
                                String question=document.get(FB_QUESTION_KEY).toString();
                                String choiceA=document.get(FB_CHOICE_A_KEY).toString();
                                String choiceB=document.get(FB_CHOICE_B_KEY).toString();
                                String choiceC=document.get(FB_CHOICE_C_KEY).toString();
                                String choiceD=document.get(FB_CHOICE_D_KEY).toString();
                                int questionLevel=Integer.parseInt(document.get(FB_QUESTION_LEVEL_KEY).toString());
                                int questionRightAnswer=1;
                                String rightChoice= String.valueOf(document.get("rightChoice"));
                                //  int questionRightAnswer=Integer.parseInt(document.get(FB_RIGHT_ANSWER_KEY).toString());
                                String questionLanguage=document.get(GameActivity.FB_QUESTION_LANGUAGE_KEY).toString();

                                //String questionDocId=document.get(GameActivity.FB_QUESTION_DOC_ID_KEY).toString();
                                String questionDocId=document.getId();
                                //  int questionId=Integer.parseInt(document.get(GameActivity.FB_QUESTION_ID_KEY).toString());
                                int questionId=1;
                                // TODO: 19.02.2021 question id test amaçlı 1 yapıldı ihtiyaç yoksa kaldırılacak
                                new QuestionsDao().addQuestion(databaseHelper,tableNameTR,question,choiceA,choiceB,choiceC,choiceD,questionLevel,questionId,questionRightAnswer,
                                        questionLanguage,questionDocId,rightChoice);

                                editor.putBoolean(GameActivity.KEY_IS_QUESTIONS_UPLOADED_FROM_FIREBASE, true);
                                editor.commit();

                            }
                        }
                        else{
                            Log.e("db_update_servis", "db_update_servis_error");
                        }
                    }
                });
    }

    private void fillEnQuestionTableFromFirebaseToSqlite() {
        DatabaseHelper databaseHelper;
        databaseHelper=new DatabaseHelper(this);

        String tableName="questions";
        String tableNameTR="questionsTR";
        CollectionReference collectionReferenceQuestions=firebaseFirestore.collection("Questions");
        collectionReferenceQuestions.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
// TODO: 31.01.2021  kullanıcının interneti kontrol edilecek ,,, güncel sürüm var mı kontrol edilecek ,, splash screen de çalıştırılacak
                        if(task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document :
                                    task.getResult()) {
                                String question=document.get(FB_QUESTION_KEY).toString();
                                String choiceA=document.get(FB_CHOICE_A_KEY).toString();
                                String choiceB=document.get(FB_CHOICE_B_KEY).toString();
                                String choiceC=document.get(FB_CHOICE_C_KEY).toString();
                                String choiceD=document.get(FB_CHOICE_D_KEY).toString();
                                int questionLevel=Integer.parseInt(document.get(FB_QUESTION_LEVEL_KEY).toString());
                                int questionRightAnswer=1;
                                String rightChoice= String.valueOf(document.get("rightChoice"));
                                //  int questionRightAnswer=Integer.parseInt(document.get(FB_RIGHT_ANSWER_KEY).toString());
                                String questionLanguage=document.get(GameActivity.FB_QUESTION_LANGUAGE_KEY).toString();

                                //String questionDocId=document.get(GameActivity.FB_QUESTION_DOC_ID_KEY).toString();
                                String questionDocId=document.getId();
                                //  int questionId=Integer.parseInt(document.get(GameActivity.FB_QUESTION_ID_KEY).toString());
                                int questionId=1;
                                // TODO: 19.02.2021 question id test amaçlı 1 yapıldı ihtiyaç yoksa kaldırılacak
                                new QuestionsDao().addQuestion(databaseHelper,tableName,question,choiceA,choiceB,choiceC,choiceD,questionLevel,questionId,questionRightAnswer,
                                        questionLanguage,questionDocId,rightChoice);
                            }

                        }
                        else{
                            Log.e("db_update_servis", "db_update_servis_error");
                        }
                    }
                });
    }


    @Override
    public void onDestroy() {

    }
}
