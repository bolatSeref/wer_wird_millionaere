package com.creactivestudio.themillionare.user_inputs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.creactivestudio.themillionare.Helper;
import com.creactivestudio.themillionare.MainActivity;
import com.creactivestudio.themillionare.R;
import com.creactivestudio.themillionare.admin.QuestionFromAdmin;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SendQuestionActivity extends AppCompatActivity {

    private EditText etUserQuestion, etUserChoiceA, etUserChoiceB, etUserChoiceC, etUserChoiceD, etQuestionLevel;
    private RadioGroup radioGroupChoices, radioGroupLanguage;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String userQuestion, userChoiceA, userChoiceB, userChoiceC, userChoiceD;
    private String question, choiceA, choiceB, choiceC, choiceD, email, rightChoice, questionLanguage, selectedLanguage;
    private Button btnSendQuestionAsAdmin;
    private ImageView imgSaveQuestion, imgDiscard;
    private int questionLevel;
    private TextView tvDescription;
    private String collectionPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_question);

        init();

        if (firebaseUser.getEmail().matches("admin3@gmail.com")) {
            btnSendQuestionAsAdmin.setVisibility(View.VISIBLE);
            etQuestionLevel.setVisibility(View.VISIBLE);
            tvDescription.setVisibility(View.INVISIBLE);
            radioGroupLanguage.setVisibility(View.VISIBLE);
            imgSaveQuestion.setVisibility(View.INVISIBLE);
        }
    }
    public void init() {
        radioGroupLanguage=findViewById(R.id.radioGroupLanguage);
        tvDescription=findViewById(R.id.tvDescription);
        progressBar = findViewById(R.id.progressBar);
        imgDiscard = findViewById(R.id.imgDiscard);
        imgSaveQuestion = findViewById(R.id.imgSaveQuestion);
        progressBar.setVisibility(View.INVISIBLE);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        etUserQuestion = findViewById(R.id.etQuestionFromUser);
        etUserChoiceA = findViewById(R.id.etChoiceAfromUser);
        etUserChoiceB = findViewById(R.id.etChoiceBfromUser);
        etUserChoiceC = findViewById(R.id.etChoiceCfromUser);
        etUserChoiceD = findViewById(R.id.etChoiceDfromUser);
        radioGroupChoices = findViewById(R.id.radioGroupChoice);
        etQuestionLevel = findViewById(R.id.etQuestionLevel);
        btnSendQuestionAsAdmin = findViewById(R.id.btnSendQuestionAsAdmin);
        Helper.setLocale(this);
    }

    public void cancelClick(View view) {
        Helper.playVoiceEffect(this, R.raw.item_select);
        startActivity(new Intent(SendQuestionActivity.this, MainActivity.class));

    }

    public void sendQuestionAsAdmin(View view) {

        if (!etUserQuestion.getText().toString().matches("") && !etUserChoiceA.getText().toString().matches("")
                && !etUserChoiceB.getText().toString().matches("") && !etUserChoiceC.getText().toString().matches("")
                && !etUserChoiceD.getText().toString().matches("") && !etQuestionLevel.getText().toString().matches("") && radioGroupChoices.getCheckedRadioButtonId() != -1
        &&radioGroupLanguage.getCheckedRadioButtonId()!=-1
        ) {
            if (Helper.controlNetwork(SendQuestionActivity.this)) {
                progressBar.setVisibility(View.VISIBLE);
                String rightAnswer;

                switch (radioGroupChoices.getCheckedRadioButtonId()) {
                    case R.id.rbA:
                        rightAnswer = etUserChoiceA.getText().toString();
                        break;
                    case R.id.rbB:
                        rightAnswer = etUserChoiceB.getText().toString();
                        break;
                    case R.id.rbC:
                        rightAnswer = etUserChoiceC.getText().toString();
                        break;
                    case R.id.rbD:
                        rightAnswer = etUserChoiceD.getText().toString();
                        break;
                    default:
                        rightAnswer ="";
                }
                switch (radioGroupLanguage.getCheckedRadioButtonId())
                {
                    case R.id.radioEnglish:{
                        selectedLanguage="en";
                        collectionPath="Questions";
                    }
                    break;
                    case R.id.radioTurkish: {
                        selectedLanguage="tr";
                        collectionPath="QuestionsTR";
                    }
                    break;
                }

                question = etUserQuestion.getText().toString();
                choiceA = etUserChoiceA.getText().toString();
                choiceB = etUserChoiceB.getText().toString();
                choiceC = etUserChoiceC.getText().toString();
                choiceD = etUserChoiceD.getText().toString();
                questionLevel = Integer.parseInt(etQuestionLevel.getText().toString());

                QuestionFromAdmin questionFromAdmin=new QuestionFromAdmin();
                questionFromAdmin.setChoiceA(choiceA);
                questionFromAdmin.setChoiceB(choiceB);
                questionFromAdmin.setChoiceC(choiceC);
                questionFromAdmin.setChoiceD(choiceD);
                questionFromAdmin.setQuestion(question);
                questionFromAdmin.setQuestionLanguage(selectedLanguage);
                questionFromAdmin.setQuestionLevel(questionLevel);
                questionFromAdmin.setAdminEmail(firebaseUser.getEmail());
                questionFromAdmin.setRightChoice(rightAnswer);


                firebaseFirestore.collection(collectionPath).add(questionFromAdmin)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(SendQuestionActivity.this, getString(R.string.we_have_save_your_question_and_we_will_inform_you_soon), Toast.LENGTH_SHORT).show();

                                Snackbar snackbar = Snackbar.make(view, getString(R.string.we_have_save_your_question_and_we_will_inform_you_soon), 4000);
                                snackbar.show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar snackbar = Snackbar.make(view, e.getLocalizedMessage(), 4000);
                                snackbar.show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
            } else {
                Snackbar snackbar = Snackbar.make(view, getString(R.string.please_check_your_internet_connection), 4000);
                snackbar.show();
            }
        } else {
            Snackbar snackbar = Snackbar.make(view, getString(R.string.please_fill_all_fields), 4000);
            snackbar.show();
        }
    }

    public void sendQuestionClick(View view) {
        Helper.playVoiceEffect(this, R.raw.item_select);
        // save user question to firebase and inform admins
        if (!etUserQuestion.getText().toString().matches("") && !etUserChoiceA.getText().toString().matches("")
                && !etUserChoiceB.getText().toString().matches("") && !etUserChoiceC.getText().toString().matches("")
                && !etUserChoiceD.getText().toString().matches("") && radioGroupChoices.getCheckedRadioButtonId() != -1) {
            if (Helper.controlNetwork(SendQuestionActivity.this)) {
                progressBar.setVisibility(View.VISIBLE);
                int rightAnswer;

                switch (radioGroupChoices.getCheckedRadioButtonId()) {
                    case R.id.rbA:
                        rightAnswer = 0;
                        break;
                    case R.id.rbB:
                        rightAnswer = 1;
                        break;
                    case R.id.rbC:
                        rightAnswer = 2;
                        break;
                    case R.id.rbD:
                        rightAnswer = 3;
                        break;
                    default:
                        rightAnswer = 1;
                }

                userQuestion = etUserQuestion.getText().toString();
                userChoiceA = etUserChoiceA.getText().toString();
                userChoiceB = etUserChoiceB.getText().toString();
                userChoiceC = etUserChoiceC.getText().toString();
                userChoiceD = etUserChoiceD.getText().toString();


                QuestionFromUser questionFromUser = new QuestionFromUser();
                questionFromUser.setQuestion(userQuestion);
                questionFromUser.setChoiceA(userChoiceA);
                questionFromUser.setChoiceB(userChoiceB);
                questionFromUser.setChoiceC(userChoiceC);
                questionFromUser.setChoiceD(userChoiceD);
                questionFromUser.setUserEmail(firebaseUser.getEmail());
                questionFromUser.setRightAnswer(rightAnswer);
                questionFromUser.setQuestionLevel(0);

// TODO: 15.02.2021  internet bağlı olmadığında uygulamanın çalışması kontrol edilecek
                firebaseFirestore.collection("questionFromUsers").add(questionFromUser)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(SendQuestionActivity.this, getString(R.string.we_have_save_your_question_and_we_will_inform_you_soon), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(SendQuestionActivity.this, MainActivity.class));
                                finish();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar snackbar = Snackbar.make(view, e.getLocalizedMessage(), 4000);
                                snackbar.show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
            } else {
                Snackbar snackbar = Snackbar.make(view, getString(R.string.please_check_your_internet_connection), 4000);
                snackbar.show();
            }
        } else {
            Snackbar snackbar = Snackbar.make(view, getString(R.string.please_fill_all_fields), 4000);
            snackbar.show();
        }
    }
}