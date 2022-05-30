package com.creactivestudio.themillionare.user_inputs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.creactivestudio.themillionare.Helper;
import com.creactivestudio.themillionare.MainActivity;
import com.creactivestudio.themillionare.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportMistakeActivity extends AppCompatActivity {
    private EditText etReportFromUser, etReportMistakeUserEmail;
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private ProgressBar progressBar;
    private  String userEmail;
    private AdView adViewReportMistake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_mistake);

        init();
        loadAds();
        if(firebaseAuth.getCurrentUser()==null){
            etReportMistakeUserEmail.setVisibility(View.VISIBLE);
        }
        else
        {
            etReportMistakeUserEmail.setVisibility(View.INVISIBLE);
        }

    }

    public void loadAds(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest=new AdRequest.Builder().build();
        adViewReportMistake.loadAd(adRequest);
    }
    public void sendReport (View view){
        Helper.playVoiceEffect(this, R.raw.item_select);
       if(Helper.controlNetwork(ReportMistakeActivity.this))
       {

           if(!etReportFromUser.getText().toString().matches(""))
           {
               if(firebaseAuth.getCurrentUser()!=null)  {
                   userEmail= firebaseAuth.getCurrentUser().getEmail();
               }
               else {
                   if(Helper.isEditTextEmpty(etReportMistakeUserEmail)){
                       Snackbar snackbar=Snackbar.make(view, getText(R.string.please_write_your_email_address),5000);
                       snackbar.show();
                   }
                   else{
                       userEmail=etReportMistakeUserEmail.getText().toString();
                   }
               }
               progressBar.setVisibility(View.VISIBLE);
               String userMistakeReport=etReportFromUser.getText().toString();
               // TODO: 10.02.2021 user email giriş yapıldıysa otomatık alınacak yapılmadıysa edit text ile email alınacak
               MistakeReport mistakeReport=new MistakeReport();
               mistakeReport.setUserMessage(userMistakeReport);
               mistakeReport.setUserEmail(userEmail);


               firebaseFirestore.collection("mistakeReportsFromUsers")
                       .add(mistakeReport)
                       .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                           @Override
                           public void onSuccess(DocumentReference documentReference) {
                               Toast.makeText(ReportMistakeActivity.this, getString(R.string.your_message_has_been_sent_successfully), Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(ReportMistakeActivity.this, MainActivity.class));
                               progressBar.setVisibility(View.INVISIBLE);
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(ReportMistakeActivity.this, getString(R.string.an_error_occured), Toast.LENGTH_SHORT).show();
                               progressBar.setVisibility(View.INVISIBLE);

                           }
                       });
           }
           else
           {
               Snackbar snackbar=Snackbar.make(view, getString(R.string.please_write_your_message), 4000);
               snackbar.show();
           }
       }
       else {
           Snackbar snackbar=Snackbar.make(view, getString(R.string.please_check_your_internet_connection), 4000);
           snackbar.show();
       }
    }

    public void init(){
        adViewReportMistake=findViewById(R.id.adViewReportMistake);
        progressBar=findViewById(R.id.progressBarReport);
        etReportFromUser=findViewById(R.id.etReportFromUser);
        etReportMistakeUserEmail=findViewById(R.id.etReportMistakeUserEmail);

    }
    public void cancelClick(View view) {
        Helper.playVoiceEffect(this, R.raw.item_select);
        startActivity(new Intent(ReportMistakeActivity.this, MainActivity.class));
    }
}