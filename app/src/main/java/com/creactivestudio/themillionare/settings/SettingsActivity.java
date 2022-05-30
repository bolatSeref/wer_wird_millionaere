package com.creactivestudio.themillionare.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creactivestudio.themillionare.GameActivity;
import com.creactivestudio.themillionare.Helper;
import com.creactivestudio.themillionare.MainActivity;
import com.creactivestudio.themillionare.R;
import com.creactivestudio.themillionare.login.ConnectActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private TextView tvLogin, tvActionSound;
    boolean isLogin;
    private ImageView imgActionSound;
    private SharedPreferences sharedPreferences;
    private AdView adViewSettings;
    private SharedPreferences.Editor editor;

    // TODO: 20.06.2021 DİL // dil ayar butonu gone yapıldı yeni dil eklendiğinde düzeltilecek
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();

        loadViewsAndTexts();
        loadAds();
        Helper.setLocale(this);
    }
    public void loadAds(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest=new AdRequest.Builder().build();
        adViewSettings.loadAd(adRequest);
    }

    public void loadViewsAndTexts() {
        if (firebaseAuth.getCurrentUser() == null) {
            tvLogin.setText(getText(R.string.login));
            isLogin = false;
        } else {
            tvLogin.setText(getText(R.string.log_out));
            isLogin = true;
        }

        if (Helper.isSoundOn(getApplicationContext())) {
            imgActionSound.setImageResource(R.drawable.ic_sound_on);
            tvActionSound.setText(getText(R.string.sound_on));

        } else {
            imgActionSound.setImageResource(R.drawable.ic_sound_off);
            tvActionSound.setText(getText(R.string.sound_off));
        }
    }

    public void init() {
        tvLogin = findViewById(R.id.tvSettingsLogin);
        adViewSettings =findViewById(R.id.adViewSettings);
        sharedPreferences = getSharedPreferences(GameActivity.SP_FILE_JOKERS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        tvActionSound = findViewById(R.id.tvActionSound);
        imgActionSound = findViewById(R.id.imgActionSound);

    }

    public void settings(View view) {
        //Helper.playVoiceEffect(this, R.raw.item_select);
        Helper.playItemSelectSound(this);
        switch (view.getId()) {
            case R.id.imgSetSound: {

                if (Helper.isSoundOn(getApplicationContext())) {
                    Helper.turnVoiceOff(getApplicationContext());
                    tvActionSound.setText(getText(R.string.sound_off));
                    imgActionSound.setImageResource(R.drawable.ic_sound_off);
                } else {
                    Helper.turnVoiceOn(getApplicationContext());
                    tvActionSound.setText(getText(R.string.sound_on));
                    imgActionSound.setImageResource(R.drawable.ic_sound_on);

                }

                // TODO: 7.02.2021 ses aç kapa
            }
            break;
            case R.id.imgSetLanguage: {
                startActivity(new Intent(SettingsActivity.this, SelectLanguageActivity.class));

            }
            break;
            case R.id.imgSettingsClose: {
                startActivity(new Intent(this, MainActivity.class));
            }
            break;
            case R.id.imgShareApp: {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.i_recommend_this_fantastic_app) + "\n\n" + "https://play.google.com/store/apps/details?id=com.creactivestudio.themillionare";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.this_game_is_wonderfull));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
            }
            break;
            case R.id.imgCreactiveStudio: {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=CreActive+Studio"));
                startActivity(intent);
            }
            break;
            case R.id.imgLogin: {
                // TODO: 7.02.2021 kullanıcı girişi yapılmışsa log out olsun
                if (isLogin) {
                    firebaseAuth.signOut();
                    tvLogin.setText(getText(R.string.login));
                    Toast.makeText(this, getString(R.string.sign_out_is_successfull), Toast.LENGTH_SHORT).show();
                    isLogin = false;
                } else {
                    startActivity(new Intent(SettingsActivity.this, ConnectActivity.class));
                }
            }
            break;
            case R.id.imgRateUs: {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.creactivestudio.themillionare"));
                startActivity(intent);
            }
            break;
            default:

        }
    }

}