package com.creactivestudio.themillionare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.creactivestudio.themillionare.audio.AudioPlayService;
import com.creactivestudio.themillionare.audio.AudioPlayServiceGameActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class Helper {


    private static final String KEY_IS_SOUND_ON= "is_sound_on";
    private static FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private static CollectionReference collectionReference=firebaseFirestore.collection("Users");
    public static boolean result;

    public static boolean isEditTextEmpty (EditText editText){
        return editText.getText().toString().trim().length()==0;
    }

    public static void setLocale(Activity activity) {

        SharedPreferences sharedPreferences=activity.getSharedPreferences(GameActivity.SP_FILE_JOKERS, Context.MODE_PRIVATE);
        String lang=sharedPreferences.getString(GameActivity.SP_SELECTED_LANGUAGE,"en");

            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Resources resources = activity.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static boolean isUserNameUnique (String askedUserName){
        // TODO: 19.02.2021 kullanıcı adı alınmış mı bakılacak
        ArrayList<String> userNameList=new ArrayList<>();
      //  String userName=etUserName.getText().toString();

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult())
                    {
                        String userName=queryDocumentSnapshot.get("userName").toString();
                        userNameList.add(userName);
                    }

                    if(userNameList.contains(askedUserName))
                    {
                        result=false;
                    }
                    else {
                        result=true;
                    }
                }
            }
        });


        return result;
    }


    public static void disableActivity(Activity activity){
               activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE );
    }
    public static void enableActivity (Activity activity){
   activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    public static String returnSelectedLanguage (Context context) {
        // TODO: 20.06.2021  DİL / diğer diller eklendiğinde tekrar açılacak şu an sadece tr gönderiyor seçilen dili
      /*  SharedPreferences sharedPreferences=context.getSharedPreferences(GameActivity.SP_FILE_JOKERS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        String selectedLanguage=sharedPreferences.getString(GameActivity.SP_SELECTED_LANGUAGE,"");
*/
        String selectedLanguage="tr";
        return selectedLanguage;
    }

    public static boolean isSoundOn (Context context)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(GameActivity.SP_FILE_JOKERS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        boolean isSoundOn =sharedPreferences.getBoolean(KEY_IS_SOUND_ON, true);
        return isSoundOn;
    }

    public static void turnVoiceOff (Context context){
        context.stopService(new Intent(context, AudioPlayService.class));
        context.stopService(new Intent(context, AudioPlayServiceGameActivity.class));

        SharedPreferences sharedPreferences=context.getSharedPreferences(GameActivity.SP_FILE_JOKERS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(KEY_IS_SOUND_ON,false);
        editor.commit();

    }

    public static void turnVoiceOn (Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(GameActivity.SP_FILE_JOKERS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(KEY_IS_SOUND_ON,true);
        editor.commit();
      //  context.startService(new Intent(context, AudioPlayService.class));

    }

    public static void playVoiceEffect(Context context, int id)
    {
        if(isSoundOn(context)) {

            MediaPlayer mediaPlayer = MediaPlayer.create(context, id);
            mediaPlayer.start();
        }

    }
    public static void playItemSelectSound (Context context){
        if(isSoundOn(context)){
            MediaPlayer mediaPlayer=MediaPlayer.create(context,R.raw.item_select2);
            mediaPlayer.start();

        }
    }


    public static void showExitAlertDialog(final Context context){
        AlertDialog.Builder builder =new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name)
                .setIcon(R.mipmap.app_icon)
                .setMessage(R.string.do_you_want_to_exit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playVoiceEffect(context,R.raw.item_select2);
                        ((Activity)context).finishAffinity();
                        context.stopService(new Intent(context, AudioPlayService.class));
                        context.stopService(new Intent(context, AudioPlayServiceGameActivity.class));

                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playVoiceEffect(context,R.raw.item_select2);

                        dialog.cancel();
                    }
                })
                .setNeutralButton(R.string.rate_us, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     Intent intent=new Intent();
                     intent.setAction(Intent.ACTION_VIEW);
                     intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        // TODO: 26.01.2021  uygulama linki değiştirilecek
                     intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.creactivestudio.mathforkids"));
                     context.startActivity(intent);
                    }
                });
        AlertDialog alert=builder.create();
        alert.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
            }
        });
        alert.show();



    }
    public static boolean controlNetwork(Activity activity) {

        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
