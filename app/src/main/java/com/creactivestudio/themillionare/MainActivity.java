package com.creactivestudio.themillionare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.creactivestudio.themillionare.audio.AudioPlayService;
import com.creactivestudio.themillionare.audio.AudioPlayServiceGameActivity;
import com.creactivestudio.themillionare.login.ConnectActivity;
import com.creactivestudio.themillionare.score_board.ScoreBoardActivity;
import com.creactivestudio.themillionare.settings.SettingsActivity;
import com.creactivestudio.themillionare.sqlite.DatabaseHelper;
import com.creactivestudio.themillionare.user_inputs.SendQuestionActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;



// SCORE BOARD DA NASIL BİR YAPI KURULMALI HAFTALIK AYLIK YILLIK ORT. İÇİN
// SATIŞ GERÇEKLEŞTİKTEN SONRA CRASH OLUYOR

// TODO: 19.02.2021 kalan işler score board yapılacak - one more joker yapılacak - game act. todo larına bakılacak
public class MainActivity extends AppCompatActivity {
    private static final String KEY_FB_QUESTION_VERSION_NUMBER = "Question_Version_Number";
    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    EditText edit_text_title, edit_text_description;
    private TextView textViewData, tvTotalPointsMain;
    public static final String TAG = "MainActivity";
    public static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button buttonUpdate;
    private ImageView imageView;
    private AdView adViewMain;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private InterstitialAd interstitialAd;
    int versionNumberSp;
    AlertDialog dialogExit;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        loadTotalPointsTextView();
        loadAds();
        if (Helper.isSoundOn(getApplicationContext())) {
            startService(new Intent(MainActivity.this, AudioPlayService.class));
        }

        if (Helper.controlNetwork(this)) {
            getQuestionVersionNumberFromFirebaseToSharedPreferences();
        }
        Helper.setLocale(this);
    }

    private void init() {

        tvTotalPointsMain = findViewById(R.id.tvTotalPointsMain);
        sharedPreferences = getSharedPreferences(GameActivity.SP_FILE_JOKERS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        imageView = findViewById(R.id.imageView);
        adViewMain = findViewById(R.id.adViewMain);

    }

    private void showCustomExitDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_exit, null);
        builder.setCancelable(false);
        builder.setView(view);

        ImageView imgCloseExit = view.findViewById(R.id.imgCloseExit);
        ImageView imgRateUs = view.findViewById(R.id.imgRateUs);
        ImageView imgExitApp = view.findViewById(R.id.imgExitApp);


        dialogExit = builder.create();
        dialogExit.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogExit.show();

        imgCloseExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                // TODO: 7.02.2021 oyunu bitir
                dialogExit.dismiss();
            }
        });

        imgRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 7.02.2021 mağazaya gönder
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                // TODO: 26.01.2021  uygulama linki değiştirilecek
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.creactivestudio.themillionare"));
                startActivity(intent);

            }
        });
        imgExitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                finishAffinity();
                stopService(new Intent(MainActivity.this, AudioPlayService.class));
                stopService(new Intent(MainActivity.this, AudioPlayServiceGameActivity.class));

             }
        });

    }

    private void getQuestionVersionNumberFromFirebaseToSharedPreferences() {
        String version= sharedPreferences.getString(KEY_FB_QUESTION_VERSION_NUMBER,"1");
        versionNumberSp=  Integer.parseInt(version);


        CollectionReference collectionReference = db.collection(KEY_FB_QUESTION_VERSION_NUMBER);
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()
                        ) {

                            int versionNum = Integer.parseInt(documentSnapshot.get("questionVersion").toString());
                            if(versionNum>versionNumberSp){
                                editor.putString(KEY_FB_QUESTION_VERSION_NUMBER, String.valueOf(versionNum));
                                editor.commit();
                                // TODO: 21.02.2021 verileri güncelle
                                startService(new Intent(MainActivity.this, UpdateService.class));

                            }


                        }
                    }
                }
            }
        });
    }



    public void loadTotalPointsTextView() {
        int totalPoint = sharedPreferences.getInt(GameActivity.KEY_TOTAL_POINTS, 0);
        tvTotalPointsMain.setText(totalPoint + " " + getString(R.string.dollar));
    }

    public void loadAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adViewMain.loadAd(adRequest);

        interstitialAd = new InterstitialAd(MainActivity.this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_test_id));
        AdRequest adRequest1 = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest1);

        interstitialAdListeners();
    }

    public void interstitialAdListeners() {
        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                //reklam yüklendiğinde çalışır
            }

            @Override
            public void onAdClosed() {
                //reklam kapatıldığında çalışır
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                startActivity(new Intent(MainActivity.this, GameActivity.class));
                stopService(new Intent(MainActivity.this, AudioPlayService.class));
                cleanSharedPreferencesForUserLevel();
                cleanSharedPreferencesForUserJokers();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                //hata olduğunda çalışır
            }

            @Override
            public void onAdOpened() {
                //reklam ekranı kapladığında çalışır
            }

            @Override
            public void onAdLeftApplication() {
                //kullanıcı reklama tıklayıp uygulamadan ayrıldığında çalışır
            }
        });


    }


    public void goMarket(View view) {
        Helper.playVoiceEffect(this, R.raw.item_select);
        startActivity(new Intent(MainActivity.this, MarketActivity.class));
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public void scoreBoardClick(View view) {
        if(Helper.controlNetwork(this)){
            if (firebaseUser != null) {
                Helper.playVoiceEffect(this, R.raw.item_select);
                startActivity(new Intent(MainActivity.this, ScoreBoardActivity.class));
            } else {
                Toast.makeText(this, getString(R.string.first_you_have_to_sign_in), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, ConnectActivity.class));
            }
        }
        else   {
            Toast.makeText(this, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_SHORT).show();
        }


    }

    public boolean isQuestionsUploaded () {
       return sharedPreferences.getBoolean(GameActivity.KEY_IS_QUESTIONS_UPLOADED_FROM_FIREBASE,false);

    }

    public void newGameClick(View view) {
        if (isQuestionsUploaded()) {
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            } else {
                Helper.playVoiceEffect(this, R.raw.item_select);
                startActivity(new Intent(MainActivity.this, GameActivity.class));
                stopService(new Intent(MainActivity.this, AudioPlayService.class));
                cleanSharedPreferencesForUserLevel();
                cleanSharedPreferencesForUserJokers();
                finish();
            }
        }
        else Toast.makeText(this, getString(R.string.loading_please_wait), Toast.LENGTH_SHORT).show();

    }

    public void settingsClick(View view) {
        Helper.playVoiceEffect(this, R.raw.item_select);
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    public void sendQuestion(View view) {
        Helper.playVoiceEffect(this, R.raw.item_select);
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, getString(R.string.first_you_have_to_sign_in), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, ConnectActivity.class));
        } else {
            startActivity(new Intent(MainActivity.this, SendQuestionActivity.class));
        }
    }

    private void cleanSharedPreferencesForUserJokers() {
        editor.putBoolean(GameActivity.SP_FIFTY_FIFTY_JOKER, true);
        editor.putBoolean(GameActivity.SP_PHONE_JOKER, true);
        editor.putBoolean(GameActivity.SP_AUDIENCE_JOKER, true);
        editor.putBoolean(GameActivity.SP_DOUBLE_RIGHT_JOKER, true);
        editor.commit();
    }

    private void cleanSharedPreferencesForUserLevel() {
        editor.putInt("user_level", 1);
        editor.commit();
    }

    public void leaveAppClick(View view) {
        Helper.playVoiceEffect(this, R.raw.item_select);
       // Helper.showExitAlertDialog(this);
        showCustomExitDialog();
    }

    @Override
    public void onBackPressed() {
        Helper.playVoiceEffect(this, R.raw.item_select);
        //Helper.showExitAlertDialog(this);
        showCustomExitDialog();
    }
    // TODO: 15.02.2021 uygulamadan menü tuşuna basılıp çıkıldığında ve arama geldiğinde müziğin kapanamsı lazım
}