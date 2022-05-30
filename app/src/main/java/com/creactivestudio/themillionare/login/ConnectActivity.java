package com.creactivestudio.themillionare.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ConnectActivity extends AppCompatActivity {

    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG="ConnectActivity";
    private FirebaseAuth mAuth;
    private String user_name;
    private int RC_SIGN_IN=1;
    private ProgressBar progressBar;
    private AdView adViewConnectAct;
    private AlertDialog dialogSelectUserName, dialogSignInOrSignUp;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceForUsers=firebaseFirestore.collection("Users");
    private boolean result;
    ArrayList<String> userNameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        init();
        loadAds();

        signInButton=findViewById(R.id.signInButton);
        mAuth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }
    public void loadAds () {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest=new AdRequest.Builder().build();
        adViewConnectAct.loadAd(adRequest);
    }
    public void init(){
        adViewConnectAct=findViewById(R.id.adViewConnectAct);
        progressBar=findViewById(R.id.progressBarConnect);
        progressBar.setVisibility(View.INVISIBLE);
        userNameList=new ArrayList<>();
    }
    private void signIn(){
        Intent signInIntent =mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    // TODO: 21.02.2021 google hesapları seçmek için açıldığında eğer boş bir yere tıklanırsa uygulama çöküyor !!! önemli bakılacak
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount acc=completedTask.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e)
        {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }

    }
    private void FirebaseGoogleAuth (GoogleSignInAccount acct){
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential authCredential= GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    FirebaseUser user=mAuth.getCurrentUser();
                    updateUI(user);
                }
                else
                {
                    progressBar.setVisibility(View.INVISIBLE);

                    Toast.makeText(ConnectActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI (FirebaseUser fUser){
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account!=null){

            String personName=account.getDisplayName();
            String personGivenName=account.getGivenName();
            String personFamilyName=account.getFamilyName();
            String personEmail=account.getEmail();
            Toast.makeText(this,  getString(R.string.welcome)+" "+personName, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ConnectActivity.this,MainActivity.class));

            saveUserToFb(personEmail);
        }
    }
    private void saveUserToFb (String userEmail){

        // TODO KULLANICININ MEVCUT SCORE VARSA SP DEN ALINIP KAYDEDİLECEK

        User user=new User();
        user.setUserName(user_name);
        user.setUserEmail(userEmail);

        collectionReferenceForUsers.add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                //        Toast.makeText(ConnectActivity.this, "kullancı kaydedildi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ConnectActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showSelectUserNamePopUp (){
        AlertDialog.Builder builder=new AlertDialog.Builder(ConnectActivity.this,R.style.custom_alert);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.pop_up_select_a_user_name,null);
        builder.setCancelable(false);
        builder.setView(view);

        ImageView imgControlUserName = view.findViewById(R.id.imgControlUserName);
        EditText etUserName=view.findViewById(R.id.etUserNamePopUp);
        ProgressBar progressBar=view.findViewById(R.id.progressBarPopUpUserName);
        ImageView imgCloseUserNamePopUp =view.findViewById(R.id.imgCloseUserNamePopUp);

        dialogSelectUserName=builder.create();
        dialogSelectUserName.show();

        imgCloseUserNamePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectUserName.dismiss();
            }
        });

        imgControlUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Helper.isEditTextEmpty(etUserName))
                {
                    progressBar.setVisibility(View.VISIBLE);

                    result=false;
                    userNameList.clear();
                    String userName=etUserName.getText().toString();
                    collectionReferenceForUsers.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if (task.isSuccessful())
                                    {
                                        for(QueryDocumentSnapshot doc:task.getResult())
                                        {
                                            String username=(String) doc.get("userName");
                                            userNameList.add(username);
                                        }

                                        result=true;
                                        for(String val:userNameList)
                                        {
                                            if(val.equals(userName))
                                            {
                                                result=false;
                                                break;
                                            }
                                        }
                                        if (result)
                                        {

                                            user_name= etUserName.getText().toString();
                                            connectWithGoogle();
                                            progressBar.setVisibility(View.INVISIBLE);



                                        }
                                        else
                                        {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(ConnectActivity.this, getString(R.string.these_user_name_is_not_available), Toast.LENGTH_SHORT).show();

                                        }
                                    }





                                }
                            });





             /*       if(Helper.isUserNameUnique(user_name))
                    {
                        // TODO: 21.06.2021 galiba ters olmuş kontrol et
                        user_name= etUserName.getText().toString();
                        connectWithGoogle();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(ConnectActivity.this, getString(R.string.these_user_name_is_not_available), Toast.LENGTH_SHORT).show();
                    }*/
                }
                else {
                    Snackbar snackbar =Snackbar.make(view,getString(R.string.please_select_a_user_name),4000);
                    snackbar.show();
                }

            }
        });

    }
    public void connect (View view){
        Helper.playVoiceEffect(this,R.raw.item_select);
        switch (view.getId()) {
            case R.id.imgConnectWithEmail: {
          //       startActivity(new Intent(ConnectActivity.this, LoginActivity.class));
           // mGoogleSignInClient.signOut();
                showSignInOrSignUpPopUp();
            }
            break;
            case R.id.imgConnectWithGoogle:
            {
                showSelectUserNamePopUp();

                /*    mGoogleSignInClient.signOut();
                Toast.makeText(this, "sign out", Toast.LENGTH_SHORT).show();*/
            }
            break;
            case R.id.imgPlayAsGuest:
            {
                startActivity(new Intent(ConnectActivity.this, MainActivity.class));
            }
            break;

        }
    }

    private void showSignInOrSignUpPopUp() {
        AlertDialog.Builder builder=new AlertDialog.Builder(ConnectActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.pop_up_sign_in_or_sign_up,null);
        builder.setCancelable(true);
        builder.setView(view);

        ImageView imgSignIn=view.findViewById(R.id.imgSignIn);
        ImageView imgSignUp=view.findViewById(R.id.imgSignUp);
        ImageView imgResetPass=view.findViewById(R.id.imgResetPass);

        dialogSignInOrSignUp=builder.create();
        dialogSignInOrSignUp.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogSignInOrSignUp.show();
        //LoginActivity loginActivity=new LoginActivity();
        imgResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(ConnectActivity.this, LoginActivity.class ));
                Helper.playVoiceEffect(ConnectActivity.this,R.raw.item_select2);
                Intent intent =new Intent(ConnectActivity.this, LoginActivity.class);
                intent.putExtra("code",3);
                startActivity(intent);

            }
        });

        imgSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(ConnectActivity.this,R.raw.item_select2);
                Intent intent =new Intent(ConnectActivity.this, LoginActivity.class);
                intent.putExtra("code",2);
                startActivity(intent);
            }
        });

        imgSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(ConnectActivity.this,R.raw.item_select2);
                Intent intent =new Intent(ConnectActivity.this, LoginActivity.class);
                intent.putExtra("code",1);
                startActivity(intent);

            }
        });




    }

    public void connectWithGoogle (){
        signIn();
    }
}