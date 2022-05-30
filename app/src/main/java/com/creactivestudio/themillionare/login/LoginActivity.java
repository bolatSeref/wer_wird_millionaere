package com.creactivestudio.themillionare.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.creactivestudio.themillionare.GameActivity;
import com.creactivestudio.themillionare.Helper;
import com.creactivestudio.themillionare.MainActivity;
import com.creactivestudio.themillionare.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button btnResetPassword;
    private TextView tvResetPassword, tvRegister, tvLogin, tvResetPasswordButtonText;
    private ImageView btnSignIn, btnSignUp, imgResetPassword;
    private EditText etMail, etPassword, etUserName;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = firebaseAuth.getCurrentUser();
    private String email, password;
    private AdView adViewLogin;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=firebaseFirestore.collection("Users");
    private boolean result;
    private boolean isCheckingDone;
    private SharedPreferences sharedPreferences;
    ArrayList<String> userNameList;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        loadAds();
        Helper.setLocale(this);
        Intent intent=getIntent();
        int popUpCode=intent.getIntExtra("code",0);
        if (popUpCode==3) {
            resetPassTextViewClick(tvResetPassword);
         //   resetPassword(btnResetPassword);
        }
        else if (popUpCode==2)
        {
            changeViewsForSignIn();
        }

        else if (popUpCode==1) {
            changeViewsForSignUp();
        }
    }

    public void loadAds(){

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest=new AdRequest.Builder().build();
        adViewLogin.loadAd(adRequest);
    }

    public void init() {
        userNameList=new ArrayList<>();
        sharedPreferences=getSharedPreferences(GameActivity.SP_FILE_JOKERS, MODE_PRIVATE);
        editor=sharedPreferences.edit();
        etUserName=findViewById(R.id.etUserName);
        adViewLogin=findViewById(R.id.adViewLogin);
        progressBar = findViewById(R.id.progressLogin);
        progressBar.setVisibility(View.INVISIBLE);
        btnSignIn = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnRegister);
        tvResetPassword = findViewById(R.id.tvResetPassword);
        etMail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etUserPassword);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        tvRegister = findViewById(R.id.tvRegister);
        tvLogin = findViewById(R.id.tvLogin);
        tvResetPasswordButtonText = findViewById(R.id.tvResetPassButtonText);
        imgResetPassword = findViewById(R.id.imgResetPassword);
    }



    private void addUserNameToSharedPreferences(String user_name) {
        editor.putString(GameActivity.KEY_USER_NAME, user_name);
        editor.commit();
    }

    public boolean controlEditText(EditText editText, EditText editText1) {
        if (!editText.getText().toString().matches("") && !editText1.getText().toString().matches("")) {
            return true;
        } else return false;
    }

    public boolean controlEditText(EditText editText, EditText editText1, EditText editText2) {
        if (!editText.getText().toString().matches("") && !editText1.getText().toString().matches("") && !editText2.getText().toString().matches("") ) {
            return true;
        } else return false;
    }

    public boolean isUserNameUnique (){
        // TODO: 19.02.2021 kullanıcı adı alınmış mı bakılacak
        String userName=etUserName.getText().toString();
        result=false;
        userNameList.clear();

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                           // List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();

                            for (QueryDocumentSnapshot doc:task.getResult())
                            {
                                String username= (String) doc.get("userName");
                                userNameList.add(username);
                            }
                            result = true;
                            for (String val:userNameList )
                            {
                                if (val.equals(userName))
                                {
                                    result=false;
                                    break;
                                }
                            }
                        }

                    }

                });

        return result;
    }

    public void addUserToFirebase (User user){

        collectionReference.add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    public void signIn(View view) {
        Helper.playVoiceEffect(this,R.raw.item_select2);
        String email=etMail.getText().toString();
        String password=etPassword.getText().toString();
      //  String userName=etUserName.getText().toString();
        if (Helper.controlNetwork(LoginActivity.this)) {

            if (controlEditText(etMail, etPassword)) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                              /*  Snackbar snackbar = Snackbar.make(view, getString(R.string.welcome) + user.getEmail(), 3500);
                                snackbar.show();
                         */     progressBar.setVisibility(View.INVISIBLE);

                                //
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar snackbar = Snackbar.make(view, e.getLocalizedMessage(), 3500);
                                snackbar.show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

            } else {
                Snackbar snackbar = Snackbar.make(view, getString(R.string.please_write_your_email_and_password), 3500);
                snackbar.show();
            }

        } else {
            Snackbar snackbar = Snackbar.make(view, getString(R.string.please_check_your_internet_connection), 3500);
            snackbar.show();
        }
    }
    public void signUp(View view) {
        Helper.playVoiceEffect(this,R.raw.item_select2);
        if (Helper.controlNetwork(LoginActivity.this)) {
            email = etMail.getText().toString();
            password = etPassword.getText().toString();
            String userName=etUserName.getText().toString();

            result=false;
            userNameList.clear();

            collectionReference.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                // List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();

                                for (QueryDocumentSnapshot doc:task.getResult())
                                {
                                    String username= (String) doc.get("userName");
                                    userNameList.add(username);
                                }
                                result = true;
                                for (String val:userNameList )
                                {
                                    if (val.equals(userName))
                                    {
                                        result=false;
                                        break;
                                    }
                                }

                                if (result)
                                {
                                    if (controlEditText(etMail, etPassword,etUserName) ) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                        Toast.makeText(LoginActivity.this, getString(R.string.sign_up_is_successfull), Toast.LENGTH_SHORT).show();

                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                                        User user=new User();
                                                        user.setTotalGem(100);
                                                        user.setUserEmail(email);
                                                        user.setUserName(userName);

                                                        addUserNameToSharedPreferences(userName);
                                                        addUserToFirebase(user);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Snackbar snackbar = Snackbar.make(view, e.getLocalizedMessage(), 3500);
                                                        snackbar.show();
                                                        // etMail.setText("");
                                                        etPassword.setText("");
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                });
                                    } else {
                                        Snackbar snackbar = Snackbar.make(view, getString(R.string.please_fill_all_fields), 3500);
                                        snackbar.show();
                                    }
                                }
                                else {
                                    Snackbar snackbar=Snackbar.make(view, getString(R.string.these_user_name_is_not_available),4000);
                                    snackbar.show();
                                }
                            }

                        }

                    });

/*            if(isUserNameUnique()){
                if (controlEditText(etMail, etPassword,etUserName) ) {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.sign_up_is_successfull), Toast.LENGTH_SHORT).show();

                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    User user=new User();
                                    user.setTotalGem(100);
                                    user.setUserEmail(email);
                                    user.setUserName(userName);

                                    addUserNameToSharedPreferences(userName);
                                    addUserToFirebase(user);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar snackbar = Snackbar.make(view, e.getLocalizedMessage(), 3500);
                                    snackbar.show();
                                   // etMail.setText("");
                                    etPassword.setText("");
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                } else {
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.please_fill_all_fields), 3500);
                    snackbar.show();
                }

            }
            else  {
                Snackbar snackbar=Snackbar.make(view, getString(R.string.these_user_name_is_not_available),4000);
                snackbar.show();
            } */


        } else {
            Snackbar snackbar = Snackbar.make(view, getString(R.string.please_check_your_internet_connection), 3500);
            snackbar.show();
        }


    }

    public void resetPassword(View view) {
        Helper.playVoiceEffect(this,R.raw.item_select2);

        if (Helper.controlNetwork(LoginActivity.this)) {
            if (!etMail.getText().toString().matches("")) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(etMail.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar snackbar = Snackbar.make(view, getString(R.string.please_check_your_email_box), 4000);
                                snackbar.show();
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
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
                Snackbar snackbar = Snackbar.make(view, getString(R.string.please_write_your_email_address), 4000);
                snackbar.show();
            }

        } else {
            Snackbar snackbar = Snackbar.make(view, getString(R.string.please_check_your_internet_connection), 4000);
            snackbar.show();
        }


    }

    public void cancelClick(View view) {
        Helper.playVoiceEffect(this,R.raw.item_select2);

        startActivity(new Intent(LoginActivity.this, ConnectActivity.class));
        finish();
    }

    public void resetPassTextViewClick(View view) {
        Helper.playVoiceEffect(this,R.raw.item_select2);
        etUserName.setVisibility(View.INVISIBLE);
        etPassword.setVisibility(View.INVISIBLE);
        tvResetPassword.setVisibility(View.INVISIBLE);
        btnSignUp.setVisibility(View.INVISIBLE);
        btnSignIn.setVisibility(View.INVISIBLE);
        imgResetPassword.setVisibility(View.VISIBLE);
        tvResetPasswordButtonText.setVisibility(View.VISIBLE);
        tvRegister.setVisibility(View.INVISIBLE);
        tvLogin.setVisibility(View.INVISIBLE);
    }

    public void changeViewsForSignIn () {
        etUserName.setVisibility(View.INVISIBLE);
        btnSignUp.setVisibility(View.INVISIBLE);
        tvResetPasswordButtonText.setVisibility(View.INVISIBLE);
        tvRegister.setVisibility(View.INVISIBLE);
        tvResetPassword.setVisibility(View.INVISIBLE);
    }

    public void changeViewsForSignUp () {
        tvResetPassword.setVisibility(View.INVISIBLE);
        btnSignIn.setVisibility(View.INVISIBLE);
        imgResetPassword.setVisibility(View.INVISIBLE);
        tvResetPasswordButtonText.setVisibility(View.INVISIBLE);
        tvLogin.setVisibility(View.INVISIBLE);
    }
}











