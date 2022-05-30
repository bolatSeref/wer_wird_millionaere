package com.creactivestudio.themillionare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.creactivestudio.themillionare.settings.SelectLanguageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences sharedPreferences, sp;
    SharedPreferences.Editor editor, et;
    boolean isFirstTime;
    int userLoginTimes;
    private Thread background;
    public static final String SP_FILE_HAS_EVER_STARTED = "has_ever_started";
    public static final String SP_IS_FIRST_TIME = "is_first_time";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static final String KEY_IS_SOUND_ON = "is_sound_on";
    Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // TODO: 13.02.2021 kullanıcının son puanı title a çekilmeli .. firebase veya shared pref. den??
        // TODO: 9.02.2021 versiyon kontrolü eklenecek .. yeni sorular eklendiğinde soruların güncellenmesi lazım
        sharedPreferences = getSharedPreferences(SP_FILE_HAS_EVER_STARTED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_SOUND_ON, true);
        editor.commit();

        sp = getSharedPreferences(GameActivity.SP_FILE_JOKERS, MODE_PRIVATE);
        et = sp.edit();

        userLoginTimes = sp.getInt(SelectLanguageActivity.USER_LOGIN_TIMES, 1);

        isFirstTime = sharedPreferences.getBoolean(SP_IS_FIRST_TIME, true);
        startThread();
        Helper.setLocale(this);

    }

 /*   private void setLocale() {
        String lang = sp.getString(GameActivity.SP_SELECTED_LANGUAGE, "en");
        locale = new Locale(lang);
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration conf = resources.getConfiguration();
        conf.locale = locale;
        resources.updateConfiguration(conf, dm);

    }
*/
    private void startThread() {

        background = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    // TODO: 25.01.2021 tutorial ekranlar geldiğinde aktifleştirilecek
                    if (userLoginTimes >= 2) {
                    //    editor.putBoolean(SP_FILE_HAS_EVER_STARTED, false);
                      //  editor.commit();
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();

                    } else {
                        et.putInt(GameActivity.KEY_CURRENT_GEM, 100);
                        et.commit();
                        et.putInt(SelectLanguageActivity.USER_LOGIN_TIMES,++userLoginTimes);
                        et.commit();

                        // TODO: 20.06.2021 yeni diller eklendiğinde tekrar açılacak şu an gerekli değil çünkü sadece türkçe sorular var
                       // startActivity(new Intent(SplashScreen.this, SelectLanguageActivity.class));
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                    }
                } catch (Exception e) {
                }
            }
        };
        background.start();
    }
}