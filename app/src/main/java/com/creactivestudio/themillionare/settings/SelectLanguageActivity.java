package com.creactivestudio.themillionare.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.creactivestudio.themillionare.GameActivity;
import com.creactivestudio.themillionare.Helper;
import com.creactivestudio.themillionare.MainActivity;
import com.creactivestudio.themillionare.R;
import com.creactivestudio.themillionare.SplashScreen;
import com.creactivestudio.themillionare.UpdateService;
import com.creactivestudio.themillionare.login.ConnectActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class SelectLanguageActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences, sharedPreferences1;
    SharedPreferences.Editor editor, editor1;
    private ImageView imgSelectEnglish, imgSelectTurkish, imgClosePopUp;
    private TextView tvSelectEnglish, tvSelectTurkish, tvCloseText;
    public static final String USER_LOGIN_TIMES = "user_login_times";
    private AdView adViewSelectLanguage;
    int userLoginTimes;
    Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        init();
        userLoginTimes = sharedPreferences.getInt(SelectLanguageActivity.USER_LOGIN_TIMES, 1);
        editor.putInt(USER_LOGIN_TIMES, ++userLoginTimes);
        editor.commit();
        loadAds();


    }

    public void loadAds(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        AdRequest adRequest=new AdRequest.Builder().build();
        adViewSelectLanguage.loadAd(adRequest);
    }

    public void init() {
        tvCloseText=findViewById(R.id.tvCloseText);
        sharedPreferences1 = getSharedPreferences(SplashScreen.SP_FILE_HAS_EVER_STARTED, MODE_PRIVATE);
        editor1 = sharedPreferences1.edit();
        adViewSelectLanguage=findViewById(R.id.adViewSelectLanguage);
        sharedPreferences = getSharedPreferences(GameActivity.SP_FILE_JOKERS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        imgSelectEnglish = findViewById(R.id.imgSelectEnglish);
        imgSelectTurkish = findViewById(R.id.imgSelectTurkish);
        imgClosePopUp = findViewById(R.id.imgClosePopUp);
        tvSelectEnglish = findViewById(R.id.tvSelectEnglish);
        tvSelectTurkish = findViewById(R.id.tvSelectTurkish);


    }

    public void languageSelected(View view) {

        Helper.playVoiceEffect(this, R.raw.item_select);
        tvCloseText.setVisibility(View.INVISIBLE);
        imgClosePopUp.setVisibility(View.INVISIBLE);
        switch (view.getId()) {
            case R.id.tvSelectEnglish:
            case R.id.imgSelectEnglish: {
                editor.putString(GameActivity.SP_SELECTED_LANGUAGE, GameActivity.KEY_LANGUAGE_EN);
                editor.commit();
                setLocale("en");


                // String lan=sharedPreferences.getString(GameActivity.SP_SELECTED_LANGUAGE,"en");
                Snackbar snackbar = Snackbar.make(view, getString(R.string.you_have_selected_english_and_you_can_change_your_language_anytime_in_settings), 4000);
                snackbar.show();
                startService(new Intent(SelectLanguageActivity.this, UpdateService.class));
                waitAndGo();

            }
            break;
            case R.id.tvSelectTurkish:
            case R.id.imgSelectTurkish: {
                editor.putString(GameActivity.SP_SELECTED_LANGUAGE, GameActivity.KEY_LANGUAGE_TR);
                editor.commit();
                Snackbar snackbar = Snackbar.make(view, getString(R.string.you_have_selected_turkish_and_you_can_change_your_language_anytime_in_settings), 4000);
                snackbar.show();
                setLocale("tr");

                startService(new Intent(SelectLanguageActivity.this, UpdateService.class));
                waitAndGo();

            }
            break;
            default:
        }
    }

    public void setLocale(String localeName){
        locale=new Locale(localeName);
        Resources resources=getResources();
        DisplayMetrics dm=resources.getDisplayMetrics();
        Configuration conf=resources.getConfiguration();
        conf.locale=locale;
        resources.updateConfiguration(conf,dm);
    }

    private void waitAndGo() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isFirstTime = sharedPreferences1.getBoolean(SplashScreen.SP_FILE_HAS_EVER_STARTED, true);
                if (!isFirstTime) {
                    startActivity(new Intent(SelectLanguageActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SelectLanguageActivity.this, ConnectActivity.class));
                }
            }
        }, 2000);
    }

    public void closeClick(View view) {
        Helper.playVoiceEffect(this, R.raw.item_select);
        editor.putString(GameActivity.SP_SELECTED_LANGUAGE, GameActivity.KEY_LANGUAGE_EN);
        editor.commit();
        Snackbar snackbar = Snackbar.make(view, getString(R.string.you_have_selected_english_and_you_can_change_your_language_anytime_in_settings), 4000);
        snackbar.show();
        waitAndGo();

    }
}