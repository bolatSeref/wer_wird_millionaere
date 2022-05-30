package com.creactivestudio.themillionare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creactivestudio.themillionare.audio.AudioPlayServiceGameActivity;
import com.creactivestudio.themillionare.score_board.ScoreBoard;
import com.creactivestudio.themillionare.score_board.ScoreBoardActivity;
import com.creactivestudio.themillionare.sqlite.DatabaseHelper;
import com.creactivestudio.themillionare.sqlite.Questions;
import com.creactivestudio.themillionare.sqlite.QuestionsDao;
import com.creactivestudio.themillionare.user_inputs.ReportMistakeActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

    public class GameActivity extends AppCompatActivity {
    public static final String KEY_USER_NAME = "user_name";
    private static final String KEY_SOCIAL_SHARE_AMOUNT = "social_share_amount";
    private static final String KEY_LAST_CHANCE_POP_UP_COUNT = "last_chance_pop_up_count";
    private TextView tvTimer, tvGems, tvCurrentLevel, tvQuestionFlow, tvQuestion, tvA, tvB, tvC, tvD;
    private TextView tvChoiceA, tvChoiceB, tvChoiceC, tvChoiceD, tvPhoneJokerCorrectChoice;
    private ImageView imgChoiceA, imgChoiceB, imgChoiceC, imgChoiceD, imgFiftyFiftyJoker, imgAudienceJoker, imgPhoneJoker,
            imgDoubleJoker, imgTimerCircle, imgQuestionPanel, imgAudienceGraphA, imgAudienceGraphB, imgAudienceGraphC, imgAudienceGraphD;
    private CountDownTimer countDownTimer;
    private Thread background;
    private SharedPreferences sharedPreferences, spLevel;
    private SharedPreferences.Editor editor, editorLevel;
    private DatabaseHelper databaseHelper;
    private ArrayList<String> moneyStringList;
    private String rightAnswerTag, rightChoice;
    private AdView adViewGame;
    private int socialShareAmount = 0, shareAmount;
    Random random;
    private int doubleJokerClickAmount = 0;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    boolean isAnswerRight, isDoubleJokerSelected;
    boolean hasUserPhoneJoker, hasUserAudienceJoker, hasUserDoubleRightJoker, hasUserFiftyFiftyJoker;
    private ConstraintLayout consLayPhoneJokerView, consLayAudienceJokerView;
    public static final String KEY_LAST_CHANCE_POP_UP = "last_chance_pop_up";
    public static final String KEY_IS_QUESTIONS_UPLOADED_FROM_FIREBASE = "is_questions_uploaded_from_fb_to_device";
    public static final String KEY_TIME_UP_POP_UP = "time_up_pop_up";
    public static final String SP_PHONE_JOKER = "phone_joker";
    public static final String SP_FIFTY_FIFTY_JOKER = "fiftyFifty_joker";
    public static final String SP_AUDIENCE_JOKER = "audience_joker";
    public static final String KEY_SCORE_BOARD = "score_board";
    public static final String SP_DOUBLE_RIGHT_JOKER = "double_right_joker";
    public static final String SP_USER_LEVEL = "user_level";
    public static final String SP_FILE_JOKERS = "sp_jokers";
    public static final String FB_QUESTION_KEY = "question";
    public static final String FB_RIGHT_CHOICE_KEY = "rightChoice";
    public static final String FB_CHOICE_A_KEY = "choiceA";
    public static final String FB_CHOICE_B_KEY = "choiceB";
    public static final String FB_CHOICE_C_KEY = "choiceC";
    public static final String FB_CHOICE_D_KEY = "choiceD";
    public static final String FB_QUESTION_DOC_ID_KEY = "questionDocId";
    public static final String FB_QUESTION_LEVEL_KEY = "questionLevel";
    public static final String FB_QUESTION_LANGUAGE_KEY = "questionLanguage";
    public static final String FB_RIGHT_ANSWER_KEY = "rightAnswer";
    public static final String FB_QUESTION_ID_KEY = "questionId";
    public static final String SP_SELECTED_LANGUAGE = "selected_language";
    public static final String KEY_TIME_UP_POP_UP_COUNT = "time_up_pop_up_count";
    public static final String KEY_LANGUAGE_EN = "en";
    public static final String KEY_LANGUAGE_TR = "tr";
    public static final String KEY_CURRENT_GEM = "current_gem";
    public static final String KEY_TOTAL_POINTS = "total_points";
    private String userChoice;
    private int currentGem;
    AlertDialog dialogLastChance, dialogGameIsOver, dialogTimeUp;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReferenceQuestions = firebaseFirestore.collection("Questions");
    public int givenGemAccountForSharingApp = 20;
    public int givenGemAccountForSharingViaWhatsappStatus = 40;
    public int timeUpPopUpCount, randomQuestion;
    public int lastChancePopUpCount = 0;
    public String rightAnswer, clickedChoiceText;
    private MediaPlayer mediaPlayerTimer;
    private RewardedAd rewardedAd, mRewardedAd;
    private RewardedAdLoadCallback rewardedAdLoadListener;
    private RewardedAdCallback rewardedAdProcessListener;
    int userChoiceClickAmount = 0;
    private String popUpKey;
    private ConstraintLayout constraintLayout4;

    private View view = null;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        init();

        fillGems(tvGems);
        fillQuestionListFromSqlite();
        startNewGame();
        if (Helper.isSoundOn(getApplicationContext())) {
            startService(new Intent(GameActivity.this, AudioPlayServiceGameActivity.class));
        }
        loadAds();
        editor.putInt(KEY_LAST_CHANCE_POP_UP_COUNT,0);
        editor.commit();
        Helper.setLocale(this);
    }



    private void loadAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adViewGame.loadAd(adRequest);

       // loadRewardedAd();
        loadMyRewardedAd();
    //    rewardedAdListener();
    }

    // TODO: 15.02.2021 rewarded ad açıldığında müzik kontrol edilecek, ses açıkca reklam bitene kadar müzik durdurulacak
    public void loadRewardedAd() {
        // TODO: 15.02.2021 gerçek id ile değiştirilecek
        rewardedAd = new RewardedAd(this, getString(R.string.rewarded_ad_test_id));
        rewardedAdLoadListener = new RewardedAdLoadCallback() {

            @Override
            public void onRewardedAdLoaded() {
                Log.e("yükleme listener", "onRewardedAdLoaded çalıştı");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e("yükleme listener", "onAdFailedToLoad çalıştı");
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), rewardedAdLoadListener);



    }

    public void loadMyRewardedAd () {
       AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this,getString(R.string.rewarded_ad_test_id),
                adRequest, new RewardedAdLoadCallback() {

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                    //    Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                   //     Log.d(TAG, "Ad was loaded.");


                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                //  Log.d(TAG, "Ad was shown.");


                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                //   Log.d(TAG, "Ad failed to show.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                // Log.d(TAG, "Ad was dismissed.");
                                fillGems(tvGems);
                                startNewGame();
                                dialogLastChance.dismiss();
                                enableAvailableChoices(view, userChoice);
                                mRewardedAd = null;


                                }
                        });
                    }

                });




    }

    public void rewardedAdListener() {
        rewardedAdProcessListener = new RewardedAdCallback() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                switch (popUpKey) {
                    case KEY_LAST_CHANCE_POP_UP: {
                        startNewGame();
                        dialogLastChance.dismiss();
                        // changeSelectedChoiceToOrange(tvA, userChoice);
                        enableAvailableChoices(tvA, userChoice);
                    }
                    break;
                    case KEY_TIME_UP_POP_UP: {
                        startNewGame();
                        dialogTimeUp.dismiss();
                        enableAvailableChoices(tvA, userChoice);
                    }
                    break;
                }
            }

            @Override
            public void onRewardedAdOpened() {
                if (Helper.isSoundOn(getApplicationContext())) {
                    stopService(new Intent(GameActivity.this, AudioPlayServiceGameActivity.class));
                }
            }

            @Override
            public void onRewardedAdClosed() {
                loadRewardedAd();
                if (Helper.isSoundOn(getApplicationContext())) {
                    startService(new Intent(GameActivity.this, AudioPlayServiceGameActivity.class));
                }
            }

            @Override
            public void onRewardedAdFailedToShow(int i) {
            }
        };
    }




 //   public void updateGems ()

    public int userWins() {
        int userWins = 0;
        int userLevel = sharedPreferences.getInt(SP_USER_LEVEL, 1);
        switch (userLevel) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5: {
                userWins = 0;
            }
            break;
            case 6: {
                userWins = 5000;
            }
            break;
            case 7: {
                userWins = 7500;
            }
            break;
            case 8: {
                userWins = 15000;
            }
            break;
            case 9: {
                userWins = 30000;
            }
            break;
            case 10: {
                userWins = 60000;
            }
            break;
            case 11: {
                userWins = 125000;
            }
            break;
            case 12: {
                userWins = 250000;
            }
            break;
            case 13: {
                userWins = 500000;
            }
            break;
            case 14: {
                userWins = 1000000;
            }
            break;
        }
        return userWins;
    }

    private void fillQuestionListFromSqlite() {
        String tableName;
        switch (Helper.returnSelectedLanguage(getApplicationContext())) {
            // TODO: 20.06.2021 yeni dil eklendiğinde düzeltilecek şu an sadece tr sorular var DİL
            case GameActivity.KEY_LANGUAGE_EN: {
                tableName = "questions";
            }
            break;
            case GameActivity.KEY_LANGUAGE_TR: {
                tableName = "questionsTR";
            }
            break;
            default:
                tableName = "questionsTR";
        }

// TODO: 21.02.2021 random soru oluşturmada bir hata var bakılacak !!!! size doğru gelmiyor
        int userLevel = sharedPreferences.getInt(SP_USER_LEVEL, 1);
        ArrayList<Questions> randomQuestionList = new QuestionsDao().randomQuestion(databaseHelper, userLevel, tableName);
        int randomBound = randomQuestionList.size();

        randomQuestion = random.nextInt(randomBound);
        //   randomQuestionList.clear();
        tvQuestion.setText(randomQuestionList.get(randomQuestion).getQuestion());
        tvChoiceA.setText(randomQuestionList.get(randomQuestion).getChoiceA());
        tvChoiceB.setText(randomQuestionList.get(randomQuestion).getChoiceB());
        tvChoiceC.setText(randomQuestionList.get(randomQuestion).getChoiceC());
        tvChoiceD.setText(randomQuestionList.get(randomQuestion).getChoiceD());
        rightAnswerTag = String.valueOf(randomQuestionList.get(randomQuestion).getWrightAnswer());
        rightChoice = randomQuestionList.get(randomQuestion).getRightChoice();

    }

    public boolean controlPhoneJoker() {
        boolean hasUserPhoneJoker = sharedPreferences.getBoolean(SP_PHONE_JOKER, true);
        if (hasUserPhoneJoker) {
            return true;
        } else return false;
    }

    public boolean controlDoubleJoker() {
        boolean hasUserDoubleJoker = sharedPreferences.getBoolean(SP_DOUBLE_RIGHT_JOKER, true);
        if (hasUserDoubleJoker) {
            return true;
        } else return false;

    }

    public boolean controlAudienceJoker() {
        boolean hasUserAudienceJoker = sharedPreferences.getBoolean(SP_AUDIENCE_JOKER, true);
        if (hasUserAudienceJoker) {
            return true;
        } else return false;
    }

    public boolean controlFiftyFiftyJoker() {
        boolean hasFiftyFiftyJoker = sharedPreferences.getBoolean(SP_FIFTY_FIFTY_JOKER, true);
        if (hasFiftyFiftyJoker) {
            return true;
        } else return false;
    }

    public void startNewGame() {
        fillGems(tvGems);
        Helper.enableActivity(this);
        mediaPlayerTimer = MediaPlayer.create(GameActivity.this, R.raw.last_ten_seconds);
        countDownTimer = new CountDownTimer(31000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(millisUntilFinished / 1000 + "");
                // change timer background image for every seconds
                String timerPath = "timer_sec_" + millisUntilFinished / 1000;
                int id = getResources().getIdentifier("com.creactivestudio.themillionare:drawable/" + timerPath, null, null);
                imgTimerCircle.setImageResource(id);

                if (millisUntilFinished / 1000 < 10) {
                    if (!mediaPlayerTimer.isPlaying()) {
                        if (Helper.isSoundOn(getApplicationContext())) {
                            mediaPlayerTimer.start();
                        }
                    }
                }
            }

            @Override
            public void onFinish() {
                mediaPlayerTimer.stop();
                mediaPlayerTimer.release();

                if (returnTimeUpPopUpCount()) {
                    showCustomTimeUpDialog();
                    int count = sharedPreferences.getInt(KEY_TIME_UP_POP_UP_COUNT, 2);
                    editor.putInt(KEY_TIME_UP_POP_UP_COUNT, ++count);
                    editor.commit();
                    ++timeUpPopUpCount;

                } else {

                    showGameOverPopUpDialog();
                }
            }
        }.start();
    }


    public boolean returnTimeUpPopUpCount() {
        int count = sharedPreferences.getInt(KEY_TIME_UP_POP_UP_COUNT, 2);
        if (count % 2 == 0) {
            return true;
        } else {
            return false;
        }
    }

    public  void updateTotalPoints(int updateAmount) {
        int totalPoint = sharedPreferences.getInt(KEY_TOTAL_POINTS, 0);
        editor.putInt(KEY_TOTAL_POINTS, totalPoint + updateAmount);
        editor.commit();
        // TODO: 10.02.2021 score board için güncelleme yapılmalı

        if (Helper.controlNetwork(this)) {
            if (firebaseAuth.getCurrentUser() != null) {
                // TODO: 15.02.2021 kullanıcı ilk yüklediğinde createScore metodu bir sefer çalışmalı

                String docID = sharedPreferences.getString(ScoreBoardActivity.KEY_DOC_ID, "");
                if (docID.matches("")) {
                    createUsersFirebaseScoreBoard();

                } else {
                    updateUsersFirebaseScoreBoard();

                }
            }
        }
    }
    public void createUsersFirebaseScoreBoard() {
        if (Helper.controlNetwork(GameActivity.this)) {
            String userEmail = firebaseAuth.getCurrentUser().getEmail();
            int currentPoint = getTotalPointFromSharedPref();
            // String docId = sharedPreferences.getString(ScoreBoardActivity.KEY_DOC_ID, "");
            String userName = sharedPreferences.getString(KEY_USER_NAME, "");


            ScoreBoard scoreBoard = new ScoreBoard();
            scoreBoard.setEmail(userEmail);
            scoreBoard.setScore(currentPoint);
            scoreBoard.setUserName(userName);

            firebaseFirestore.collection(KEY_SCORE_BOARD)
                    .add(scoreBoard)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            firebaseFirestore.collection(KEY_SCORE_BOARD).document(documentReference.getId()).update("docId", documentReference.getId());
                            editor.putString(ScoreBoardActivity.KEY_DOC_ID, documentReference.getId());
                            editor.commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                        }
                    });
        } else {
            Toast.makeText(this, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUsersFirebaseScoreBoard() {
        // TODO: 21.02.2021 kontrol edilecek . ilk kayıtta ne yapılacak ???
        if (Helper.controlNetwork(GameActivity.this)) {
            String userEmail = firebaseAuth.getCurrentUser().getEmail();

            int currentPoint = getTotalPointFromSharedPref();

            ScoreBoard scoreBoard = new ScoreBoard();
            scoreBoard.setEmail(userEmail);
            scoreBoard.setScore(currentPoint);


            Map<String, Object> map = new HashMap<>();
            map.put("score", currentPoint);

            String docId = sharedPreferences.getString(ScoreBoardActivity.KEY_DOC_ID, "");
            firebaseFirestore.collection(KEY_SCORE_BOARD).document(docId)
                    .update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        } else {
            Toast.makeText(this, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_SHORT).show();
        }


    }

    public int getTotalPointFromSharedPref() {
        int totalPoint = sharedPreferences.getInt(KEY_TOTAL_POINTS, 0);
        return totalPoint;
    }

    public void showGameOverPopUpDialog() {

        shareAmount = sharedPreferences.getInt(KEY_SOCIAL_SHARE_AMOUNT, 1);
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        view = inflater.inflate(R.layout.pop_up_game_is_over, null);
        builder.setCancelable(false);
        builder.setView(view);

        ImageView imgHome = view.findViewById(R.id.imgHome);
        ImageView imgShareForGems = view.findViewById(R.id.imgShareForGems);
        ImageView imgReportMistake = view.findViewById(R.id.imgReportMistake);
        ImageView imgNewGame = view.findViewById(R.id.imgNewGamePopUp);
        ImageView imgShareViaWhatsapp = view.findViewById(R.id.imgShareViaWhatsapp);
        TextView tvCurrentPoints = view.findViewById(R.id.tvCurrentPoints);
        TextView tvPoints = view.findViewById(R.id.tvPoints);
        TextView tvSocialShare = view.findViewById(R.id.tvSocialShare);
        if (shareAmount % 2 == 0) {
            tvSocialShare.setText(R.string.share_via_instagram);
        } else {
            tvSocialShare.setText(R.string.share_via_whatsapp);
        }

        tvPoints.setText(userWins() + " " + getString(R.string.dollar));
        updateTotalPoints(userWins());

        dialogGameIsOver = builder.create();
        dialogGameIsOver.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogGameIsOver.show();

        Helper.playVoiceEffect(this, R.raw.game_over);

        String fullText = getString(R.string.you_won) + " " + userWins() + getString(R.string.dollar);
        tvCurrentPoints.setText(fullText);
        imgReportMistake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                startActivity(new Intent(GameActivity.this, ReportMistakeActivity.class));
            }
        });
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                startActivity(new Intent(GameActivity.this, MainActivity.class));
                finish();
            }
        });
        imgShareViaWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                editor.putInt(KEY_SOCIAL_SHARE_AMOUNT, ++shareAmount);
                editor.commit();
                if (shareAmount % 2 == 0) {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.i_recommend_this_fantastic_app) + "\n\n" + "https://play.google.com/store/apps/details?id=com.creactivestudio.themillionare");
                    try {
                        GameActivity.this.startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(GameActivity.this, getString(R.string.whatsapp_have_not_been_installed), Toast.LENGTH_SHORT).show();
                    }
                    addGems(givenGemAccountForSharingViaWhatsappStatus);
                } else {

                    Intent instagramIntent = new Intent(Intent.ACTION_SEND);
                    instagramIntent.setType("text/plain");
                    instagramIntent.setPackage("com.instagram");
                    instagramIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.i_recommend_this_fantastic_app) + "\n\n" + "https://play.google.com/store/apps/details?id=com.creactivestudio.themillionare");
                    try {
                        GameActivity.this.startActivity(instagramIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(GameActivity.this, getString(R.string.instagram_have_not_been_installed), Toast.LENGTH_SHORT).show();
                    }
                    addGems(givenGemAccountForSharingViaWhatsappStatus);
                }
            }
        });
        imgShareForGems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.i_recommend_this_fantastic_app) + "\n\n" + "https://play.google.com/store/apps/details?id=com.creactivestudio.themillionare";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.this_game_is_wonderfull));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
                // TODO: 10.02.2021 hergün belli sayıda paylaşım yapabilsin
                addGems(givenGemAccountForSharingApp);
                // // TODO: 8.02.2021 elmas verilecek her paylaşmada 1 adet olabilir
            }
        });
        imgNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                dialogGameIsOver.dismiss();
                startActivity(new Intent(GameActivity.this, MainActivity.class));
                finish();
            }
        });


    }


    private void showCustomLastChanceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_one_last_chance, null);
        builder.setCancelable(false);
        builder.setView(view);

        ImageView imgCloseLastChance = view.findViewById(R.id.imgCloseOneMoreJoker);
        ImageView imgGemsLastChance = view.findViewById(R.id.imgGemsOneMore);
        ImageView imgShowRewardLastChance = view.findViewById(R.id.imgShowRewardOneMore);

        dialogLastChance = builder.create();
        dialogLastChance.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogLastChance.show();

        imgCloseLastChance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                dialogLastChance.dismiss();
                showGameOverPopUpDialog();
                // TODO: 7.02.2021 oyunu bitir
            }
        });

        imgGemsLastChance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 7.02.2021 YETERLİ ELMASI VARSA KULLANDIR YOKSA mağazaya gönder

                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                if (currentGem >= 20) {
                    decreaseGem(20);
                    fillGems(tvGems);
                    startNewGame();
                    dialogLastChance.dismiss();
                    enableAvailableChoices(view, userChoice);
                } else {
                    // TODO: 21.02.2021 markete gittikten sonra geri tuşuna basarsa oyundan çıkıyor düzeltilmesi lazım
                    // TODO: 10.02.2021 oyunu durdurup mağazaya gidecek .. satın alma olursa tekrar kaldığı yerden devam etmeli
                       Intent intent = new Intent(GameActivity.this, MarketActivity.class);
                        startActivityForResult(intent, 1);
                }
            }
        });
        imgShowRewardLastChance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                /*TEST AMAÇLI KAPATILDI*/
                popUpKey = KEY_LAST_CHANCE_POP_UP;
                showMyRewardedAd();

            }
        });

    }

    public void showMyRewardedAd (){
        if (mRewardedAd != null) {
            Activity activityContext = GameActivity.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    //Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();

/*                    switch (popUpKey) {
                        case KEY_LAST_CHANCE_POP_UP: {
                            startNewGame();
                            dialogLastChance.dismiss();
                            // changeSelectedChoiceToOrange(tvA, userChoice);
                            enableAvailableChoices(tvA, userChoice);
                        }
                        break;
                        case KEY_TIME_UP_POP_UP: {
                            startNewGame();
                            dialogTimeUp.dismiss();
                            enableAvailableChoices(tvA, userChoice);
                        }
                        break;
                    }*/




                }
            });
        } else {
            Toast.makeText(GameActivity.this, getString(R.string.rewarded_ad_is_not_loaded_please_try_again_later), Toast.LENGTH_SHORT).show();

        }

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                dialogLastChance.dismiss();
                startNewGame();
                enableAvailableChoices(tvA, userChoice);
            }
            if (resultCode == RESULT_CANCELED) {

            }

        }
    }

    public int controlGems() {
        int curGem = sharedPreferences.getInt(KEY_CURRENT_GEM, 100);
        return curGem;
    }

    public int decreaseGem(int decreaseAmount) {
        int curGem = sharedPreferences.getInt(KEY_CURRENT_GEM, 100);
        int newGem = curGem - decreaseAmount;
        editor.putInt(KEY_CURRENT_GEM, newGem);
        editor.commit();
        return newGem;
    }


    public void addGems(int additionAccount) {
        int currentGem = sharedPreferences.getInt(KEY_CURRENT_GEM, 100);
        int newGem = currentGem + additionAccount;
        editor.putInt(KEY_CURRENT_GEM, newGem);
        editor.commit();
    }


    public void fillGems(TextView textView) {
        currentGem = sharedPreferences.getInt(KEY_CURRENT_GEM, 90);
        textView.setText(String.valueOf(currentGem));
    }
    public void updateGemsTv (int newGem){
        currentGem = sharedPreferences.getInt(KEY_CURRENT_GEM, 90);
        int totalGem=newGem+currentGem;
        tvGems.setText(String.valueOf(totalGem));
        SharedPreferences.Editor editor2 = sharedPreferences.edit();
        editor2.putInt(KEY_CURRENT_GEM, totalGem);
        editor2.apply();

    }


    private void showCustomTimeUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_time_is_up, null);
        builder.setCancelable(false);
        builder.setView(view);

        ImageView imgCloseTimeUpPopUp = view.findViewById(R.id.imgCloseOneMoreJoker);
        ImageView imgGemsTimeUpPopUp = view.findViewById(R.id.imgGemsOneMore);
        ImageView imgShowRewardTimeUpPopUp = view.findViewById(R.id.imgShowRewardOneMore);

        dialogTimeUp = builder.create();
        dialogTimeUp.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogTimeUp.show();

        imgCloseTimeUpPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                // TODO: 7.02.2021 oyunu bitir
                dialogTimeUp.dismiss();
                showGameOverPopUpDialog();
            }
        });

        imgGemsTimeUpPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 7.02.2021 mağazaya gönder
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                if (currentGem >= 20) {

                   // decreaseGem(20);

                    tvGems.setText(String.valueOf(decreaseGem(20)));
                    dialogTimeUp.dismiss();
                    startNewGame();
                } else {
                    // TODO: 10.02.2021 oyunu durdurup mağazaya gidecek .. satın alma olursa tekrar kaldığı yerden devam etmeli
                    startActivity(new Intent(GameActivity.this, MarketActivity.class));
                }

            }
        });
        imgShowRewardTimeUpPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                if (rewardedAd.isLoaded()) {
                    popUpKey = KEY_TIME_UP_POP_UP;
                    rewardedAd.show(GameActivity.this, rewardedAdProcessListener);
                } else {
                    Toast.makeText(GameActivity.this, getString(R.string.rewarded_ad_is_not_loaded_please_try_again_later), Toast.LENGTH_SHORT).show();
                }
                // TODO: 7.02.2021 reklam izletip ek süre ver
            }
        });

    }

    public void init() {
        timeUpPopUpCount = 0;
        constraintLayout4=findViewById(R.id.constraintLayout4);
        adViewGame = findViewById(R.id.adViewGame);
        spLevel = getSharedPreferences("sp_level", MODE_PRIVATE);
        editorLevel = spLevel.edit();
        random = new Random();
        tvTimer = findViewById(R.id.tvTimer);
        tvGems = findViewById(R.id.tvGems);
        tvCurrentLevel = findViewById(R.id.tvCurrentLevel);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvQuestionFlow = findViewById(R.id.tvQuestionFlow);
        tvA = findViewById(R.id.tvA);
        tvB = findViewById(R.id.tvB);
        tvC = findViewById(R.id.tvC);
        tvD = findViewById(R.id.tvD);
        imgAudienceGraphA = findViewById(R.id.imgAudienceGraphA);
        imgAudienceGraphB = findViewById(R.id.imgAudienceGraphB);
        imgAudienceGraphC = findViewById(R.id.imgAudienceGraphC);
        imgAudienceGraphD = findViewById(R.id.imgAudienceGraphD);
        consLayAudienceJokerView = findViewById(R.id.consLayAudienceJokerView);
        imgQuestionPanel = findViewById(R.id.imgQuestionPanel);
        tvPhoneJokerCorrectChoice = findViewById(R.id.tvPhoneJokerCorrectChoice);
        consLayPhoneJokerView = findViewById(R.id.consLayPhoneJokerView);
        tvChoiceA = findViewById(R.id.tvChoiceA);
        tvChoiceB = findViewById(R.id.tvChoiceB);
        tvChoiceC = findViewById(R.id.tvChoiceC);
        tvChoiceD = findViewById(R.id.tvChoiceD);
        imgChoiceA = findViewById(R.id.imgChoiceA);
        imgChoiceB = findViewById(R.id.imgChoiceB);
        imgChoiceC = findViewById(R.id.imgChoiceC);
        imgChoiceD = findViewById(R.id.imgChoiceD);
        imgFiftyFiftyJoker = findViewById(R.id.imgFiftyFiftyJoker);
        imgAudienceJoker = findViewById(R.id.imgAudienceJoker);
        imgPhoneJoker = findViewById(R.id.imgPhoneJoker);
        imgDoubleJoker = findViewById(R.id.imgDoubleJoker);
        imgTimerCircle = findViewById(R.id.imgTimerCircle);

        moneyStringList = new ArrayList<>();
        moneyStringList.add(getString(R.string.five_hundred_dollar));
        moneyStringList.add(getString(R.string.one_thousend_dollars));
        moneyStringList.add(getString(R.string.two_thousend_dollars));
        moneyStringList.add(getString(R.string.three_thousend_dollars));
        moneyStringList.add(getString(R.string.five_thousend_dollars));
        moneyStringList.add(getString(R.string.seven_thousend_five_hundred_dollars));
        moneyStringList.add(getString(R.string.fifteen_thousend_dollars));
        moneyStringList.add(getString(R.string.thirty_thousend_dollars));
        moneyStringList.add(getString(R.string.sixty_thousend_dollars));
        moneyStringList.add(getString(R.string.hundred_twenty_five_thousend_dollars));
        moneyStringList.add(getString(R.string.two_hundred_fifty_thousend_dollars));
        moneyStringList.add(getString(R.string.five_hundred_thousend_dollar));
        moneyStringList.add(getString(R.string.one_million_dollar));


        sharedPreferences = getSharedPreferences("sp_jokers", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        databaseHelper = new DatabaseHelper(this);

        editor.putInt(KEY_TIME_UP_POP_UP_COUNT, 2);
        editor.commit();
    }

    public void goMarket(View view) {
        Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
        // TODO: 8.02.2021 oyunu dondurup alert ile sorulacak markete gitmeden önce
    }

    @Override
    protected void onResume() {
        super.onResume();
        editor.putInt(KEY_TIME_UP_POP_UP_COUNT, 2);
        editor.commit();
        fillGems(tvGems);
        int userLevel = sharedPreferences.getInt(SP_USER_LEVEL, 1);
        tvQuestionFlow.setText(userLevel + " / 13");
        tvCurrentLevel.setText(moneyStringList.get(userLevel - 1));
        boolean hasUserFiftyFiftyJoker = sharedPreferences.getBoolean(SP_FIFTY_FIFTY_JOKER, true);
        boolean hasUserPhoneJoker = sharedPreferences.getBoolean(SP_PHONE_JOKER, true);
        boolean hasUserAudienceJoker = sharedPreferences.getBoolean(SP_AUDIENCE_JOKER, true);
        boolean hasUserDoubleRightJoker = sharedPreferences.getBoolean(SP_DOUBLE_RIGHT_JOKER, true);
        if (!hasUserFiftyFiftyJoker) {
            imgFiftyFiftyJoker.setImageResource(R.drawable.joker_fifty_fifty_passive);
        }
        if (!hasUserPhoneJoker) {
            imgPhoneJoker.setImageResource(R.drawable.joker_phone_passive);
        }
        if (!hasUserAudienceJoker) {
            imgAudienceJoker.setImageResource(R.drawable.joker_audience_passive);
        }
        if (!hasUserDoubleRightJoker) {
            imgDoubleJoker.setImageResource(R.drawable.joker_half_passive);
        }


    }

    public void jokerClick(View view) {

        hasUserPhoneJoker = sharedPreferences.getBoolean(SP_PHONE_JOKER, true);
        hasUserFiftyFiftyJoker = sharedPreferences.getBoolean(SP_FIFTY_FIFTY_JOKER, true);
        hasUserAudienceJoker = sharedPreferences.getBoolean(SP_AUDIENCE_JOKER, true);
        hasUserDoubleRightJoker = sharedPreferences.getBoolean(SP_DOUBLE_RIGHT_JOKER, true);

        if (rightChoice.equals(tvChoiceA.getText().toString())) {
            rightAnswer = "A";
        } else if (rightChoice.equals(tvChoiceB.getText().toString())) {
            rightAnswer = "B";
        } else if (rightChoice.equals(tvChoiceC.getText().toString())) {
            rightAnswer = "C";
        } else rightAnswer = "D";
        switch (view.getTag().toString()) {
            case "0":
                if (hasUserFiftyFiftyJoker) {
                    Helper.playVoiceEffect(this, R.raw.joker_selected);
                    imgFiftyFiftyJoker.setImageResource(R.drawable.joker_fifty_fifty_passive);
                    imgFiftyFiftyJoker.setEnabled(false);

                    editor.putBoolean(SP_FIFTY_FIFTY_JOKER, false);
                    editor.commit();

                    String aChoice = tvChoiceA.getText().toString();
                    String bChoice = tvChoiceB.getText().toString();
                    String cChoice = tvChoiceC.getText().toString();
                    String dChoice = tvChoiceD.getText().toString();
                    if (rightChoice.equals(aChoice)) {
                        imgChoiceB.setVisibility(View.INVISIBLE);
                        tvChoiceB.setVisibility(View.INVISIBLE);
                        tvB.setVisibility(View.INVISIBLE);
                        imgChoiceD.setVisibility(View.INVISIBLE);
                        tvChoiceD.setVisibility(View.INVISIBLE);
                        tvD.setVisibility(View.INVISIBLE);
                    } else if (rightChoice.equals(bChoice)) {
                        imgChoiceD.setVisibility(View.INVISIBLE);
                        imgChoiceA.setVisibility(View.INVISIBLE);
                        tvChoiceA.setVisibility(View.INVISIBLE);
                        tvA.setVisibility(View.INVISIBLE);
                        tvChoiceD.setVisibility(View.INVISIBLE);
                        tvD.setVisibility(View.INVISIBLE);
                    } else if (rightChoice.equals(cChoice)) {
                        imgChoiceB.setVisibility(View.INVISIBLE);
                        tvChoiceB.setVisibility(View.INVISIBLE);
                        tvB.setVisibility(View.INVISIBLE);
                        imgChoiceA.setVisibility(View.INVISIBLE);
                        tvChoiceA.setVisibility(View.INVISIBLE);
                        tvA.setVisibility(View.INVISIBLE);
                    } else {
                        imgChoiceA.setVisibility(View.INVISIBLE);
                        tvChoiceA.setVisibility(View.INVISIBLE);
                        tvA.setVisibility(View.INVISIBLE);
                        imgChoiceC.setVisibility(View.INVISIBLE);
                        tvChoiceC.setVisibility(View.INVISIBLE);
                        tvC.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case "1":
                if (hasUserPhoneJoker) {
                    Helper.playVoiceEffect(this, R.raw.joker_selected);
                    imgPhoneJoker.setImageResource(R.drawable.joker_phone_passive);
                    imgPhoneJoker.setEnabled(false);
                    editor.putBoolean(SP_PHONE_JOKER, false);
                    editor.commit();
                    // TODO: 28.01.2021 telefon jokerinde paneli daraltmaya gerek yok gibi tekrar değerlendirilecek
                   //imgQuestionPanel.setImageResource(R.drawable.panel_question_narrow);

                    if (consLayAudienceJokerView.getVisibility() == View.VISIBLE) {
                        consLayAudienceJokerView.setVisibility(View.INVISIBLE);
                    }
                    consLayPhoneJokerView.setVisibility(View.VISIBLE);

                    // TODO: 28.01.2021  doğru cevap şıkkı basılacak
                    tvPhoneJokerCorrectChoice.setText(rightAnswer);

                }
                break;
            case "2":
                if (hasUserAudienceJoker) {
                    Helper.playVoiceEffect(this, R.raw.joker_selected);

                    imgAudienceJoker.setImageResource(R.drawable.joker_audience_passive);
                    imgAudienceJoker.setEnabled(false);
                    editor.putBoolean(SP_AUDIENCE_JOKER, false);
                    editor.commit();
                    if (consLayPhoneJokerView.getVisibility() == View.VISIBLE) {
                        consLayPhoneJokerView.setVisibility(View.INVISIBLE);
                    }
                    consLayAudienceJokerView.setVisibility(View.VISIBLE);

                    switch (rightAnswer) {
                        case "A": {
                            imgAudienceGraphA.setImageResource(R.drawable.joker_audience_high);
                            imgAudienceGraphB.setImageResource(R.drawable.joker_audience_low);
                            imgAudienceGraphC.setImageResource(R.drawable.joker_audience_low);
                            imgAudienceGraphD.setImageResource(R.drawable.joker_audience_low);
                        }
                        break;
                        case "B": {
                            imgAudienceGraphA.setImageResource(R.drawable.joker_audience_low);
                            imgAudienceGraphB.setImageResource(R.drawable.joker_audience_high);
                            imgAudienceGraphC.setImageResource(R.drawable.joker_audience_low);
                            imgAudienceGraphD.setImageResource(R.drawable.joker_audience_low);
                        }
                        break;
                        case "C": {
                            imgAudienceGraphA.setImageResource(R.drawable.joker_audience_low);
                            imgAudienceGraphB.setImageResource(R.drawable.joker_audience_low);
                            imgAudienceGraphC.setImageResource(R.drawable.joker_audience_high);
                            imgAudienceGraphD.setImageResource(R.drawable.joker_audience_low);
                        }
                        break;
                        case "D": {
                            imgAudienceGraphA.setImageResource(R.drawable.joker_audience_low);
                            imgAudienceGraphB.setImageResource(R.drawable.joker_audience_low);
                            imgAudienceGraphC.setImageResource(R.drawable.joker_audience_low);
                            imgAudienceGraphD.setImageResource(R.drawable.joker_audience_high);
                        }
                        break;
                    }
                }
                break;
            case "3":
                if (hasUserDoubleRightJoker) {
                    Helper.playVoiceEffect(this, R.raw.joker_selected);

                    imgDoubleJoker.setImageResource(R.drawable.joker_half_passive);
                    imgDoubleJoker.setEnabled(false);
                    editor.putBoolean(SP_DOUBLE_RIGHT_JOKER, false);
                    editor.commit();

                    isDoubleJokerSelected = true;

                    // TODO: 28.01.2021  double joker aktifleştirilecek kod tarafında
                }
                break;
            default:
                Log.e("joker", "joker_error");
        }
    }

    @Override
    public void onBackPressed() {
        backArrowClick(tvA);
    }

    public void backArrowClick(View view) {
        // TODO: 26.01.2021  handle back arrow click

        Helper.playVoiceEffect(this, R.raw.item_select);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setIcon(R.mipmap.app_icon)
                .setMessage(R.string.do_you_want_to_exit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        countDownTimer.start();
                        countDownTimer.cancel();
                        startActivity(new Intent(GameActivity.this, MainActivity.class));
                        if (mediaPlayerTimer.isPlaying())
                        {
                            mediaPlayerTimer.stop();
                        }

                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                        dialog.cancel();
                    }
                })
                .setNeutralButton(R.string.rate_us, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        // TODO: 26.01.2021  uygulama linki değiştirilecek
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.creactivestudio.themillionare"));
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
            }
        });
        alert.show();
    }


    public void userChoiceClick(View view) {

        Helper.playVoiceEffect(this, R.raw.item_select);
        // TODO: 20.02.2021 jokerler kapatılmalı

        userChoice = view.getTag().toString();
        if (isDoubleJokerSelected) {
            ++userChoiceClickAmount;
            if (userChoiceClickAmount == 1) {

            } else {
                countDownTimer.cancel();
                Helper.disableActivity(this);
            }
        } else {

            countDownTimer.cancel();
            Helper.disableActivity(this);
        }
        changeSelectedChoiceToOrange(view);

        switch (view.getTag().toString()) {
            case "0": {
                clickedChoiceText = tvChoiceA.getText().toString();
            }
            break;
            case "1": {
                clickedChoiceText = tvChoiceB.getText().toString();
            }
            break;
            case "2": {
                clickedChoiceText = tvChoiceC.getText().toString();
            }
            break;
            case "3": {
                clickedChoiceText = tvChoiceD.getText().toString();
            }
            break;
        }

        if (isDoubleJokerSelected) {
            if (clickedChoiceText.equals(rightChoice)) {
                countDownTimer.cancel();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //view.getTag().toString().equals(rightAnswerTag)
                    if (clickedChoiceText.equals(rightChoice)) {
                        view.findViewWithTag(view.getTag().toString()).setBackgroundResource(R.drawable.answer_correct);
                        Helper.playVoiceEffect(getApplicationContext(), R.raw.correct_answer);
                        Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //  stopService(new Intent(GameActivity.this,AudioPlayServiceGameActivity.class));
                                startActivity(new Intent(GameActivity.this, CurrentLevelActivity.class));
                                finish();
                            }
                        }, 2000);

                        int currentLevel = sharedPreferences.getInt(SP_USER_LEVEL, 1);
                        editor.putInt(SP_USER_LEVEL, ++currentLevel);
                        editor.commit();
                    } else {
                        if (userChoiceClickAmount == 1) {
                            if (Helper.isSoundOn(getApplicationContext())) {
                                Helper.playVoiceEffect(getApplicationContext(), R.raw.wrong_answer);
                            }
                            view.findViewWithTag(view.getTag().toString()).setBackgroundResource(R.drawable.answer_false);

                        } else {
                            countDownTimer.cancel();
                            showGameOverPopUpDialog();
                        }
                        // TODO: 7.02.2021  last chance pop up göster


                        // TODO: 7.02.2021  test amaçlı alttaki 5 satır kapatıldı .. yanlış cevapta çalışması gereken kodlar
                   /* view.findViewWithTag(view.getTag().toString()).setBackgroundResource(R.drawable.answer_false);
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(GameActivity.this, MainActivity.class));
                        }
                    }, 2000);*/
                    }
                }
            }, 3000);

        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //view.getTag().toString().equals(rightAnswerTag)
                    if (clickedChoiceText.equals(rightChoice)) {
                        view.findViewWithTag(view.getTag().toString()).setBackgroundResource(R.drawable.answer_correct);
                        Helper.playVoiceEffect(getApplicationContext(), R.raw.correct_answer);
                        Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //  stopService(new Intent(GameActivity.this,AudioPlayServiceGameActivity.class));
                                startActivity(new Intent(GameActivity.this, CurrentLevelActivity.class));
                                finish();
                            }
                        }, 2000);

                        int currentLevel = sharedPreferences.getInt(SP_USER_LEVEL, 1);
                        editor.putInt(SP_USER_LEVEL, ++currentLevel);
                        editor.commit();
                    } else {

                        // TODO: 7.02.2021  last chance pop up göster
                        int lastChanceCount=sharedPreferences.getInt(KEY_LAST_CHANCE_POP_UP_COUNT,0);

                        if (lastChanceCount == 0) {
                            view.findViewWithTag(view.getTag().toString()).setBackgroundResource(R.drawable.answer_false);
                            showCustomLastChanceDialog();
                           // lastChancePopUpCount++;
                            editor.putInt(KEY_LAST_CHANCE_POP_UP_COUNT,1);
                            editor.commit();
                        } else {
                            view.findViewWithTag(view.getTag().toString()).setBackgroundResource(R.drawable.answer_false);
                            showGameOverPopUpDialog();
                        }

                        // TODO: 7.02.2021  test amaçlı alttaki 5 satır kapatıldı .. yanlış cevapta çalışması gereken kodlar
                   /* view.findViewWithTag(view.getTag().toString()).setBackgroundResource(R.drawable.answer_false);
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(GameActivity.this, MainActivity.class));
                        }
                    }, 2000);*/
                    }
                }
            }, 3000);

        }

    }

    private void changeSelectedChoiceToOrange(View view, String selectedChoiceTag) {
        selectedChoiceTag = userChoice;
        switch (selectedChoiceTag) {
            case "0":
                imgChoiceA.setBackgroundResource(R.drawable.answer_selected);
                imgChoiceB.setEnabled(true);
                imgChoiceC.setEnabled(true);
                imgChoiceD.setEnabled(true);
                break;
            case "1":
                imgChoiceB.setBackgroundResource(R.drawable.answer_selected);
                imgChoiceA.setEnabled(true);
                imgChoiceC.setEnabled(true);
                imgChoiceD.setEnabled(true);
                break;
            case "2":
                imgChoiceC.setBackgroundResource(R.drawable.answer_selected);
                imgChoiceA.setEnabled(true);
                imgChoiceB.setEnabled(true);
                imgChoiceD.setEnabled(true);
                break;
            case "3":
                imgChoiceD.setBackgroundResource(R.drawable.answer_selected);
                imgChoiceA.setEnabled(true);
                imgChoiceB.setEnabled(true);
                imgChoiceC.setEnabled(true);
                break;

        }
    }

    private void enableAvailableChoices(View view, String selectedChoiceTag) {
        selectedChoiceTag = userChoice;
        switch (selectedChoiceTag) {
            case "0":
                imgChoiceA.setBackgroundResource(R.drawable.answer_false);
                imgChoiceB.setEnabled(true);
                imgChoiceC.setEnabled(true);
                imgChoiceD.setEnabled(true);
                break;
            case "1":
                imgChoiceB.setBackgroundResource(R.drawable.answer_false);
                //  freezeScreen();
                imgChoiceA.setEnabled(true);
                imgChoiceC.setEnabled(true);
                imgChoiceD.setEnabled(true);
                break;
            case "2":
                imgChoiceC.setBackgroundResource(R.drawable.answer_false);
                imgChoiceA.setEnabled(true);
                imgChoiceB.setEnabled(true);
                imgChoiceD.setEnabled(true);
                //freezeScreen();
                break;
            case "3":
                imgChoiceD.setBackgroundResource(R.drawable.answer_false);
                imgChoiceA.setEnabled(true);
                imgChoiceB.setEnabled(true);
                imgChoiceC.setEnabled(true);
                //freezeScreen();
                break;

        }
    }


    private void changeSelectedChoiceToOrange(View view) {
        switch (view.getTag().toString()) {
            case "0":
                imgChoiceA.setBackgroundResource(R.drawable.answer_selected);
                if (isDoubleJokerSelected) {
                    if (userChoiceClickAmount == 1) {

                    } else {
                        imgChoiceB.setEnabled(false);
                        imgChoiceC.setEnabled(false);
                        imgChoiceD.setEnabled(false);
                    }
                } else {
                    imgChoiceB.setEnabled(false);
                    imgChoiceC.setEnabled(false);
                    imgChoiceD.setEnabled(false);
                }
                break;
            case "1":
                imgChoiceB.setBackgroundResource(R.drawable.answer_selected);
                if (isDoubleJokerSelected) {
                    if (userChoiceClickAmount == 1) {

                    } else {
                        imgChoiceA.setEnabled(false);
                        imgChoiceC.setEnabled(false);
                        imgChoiceD.setEnabled(false);
                    }
                } else {
                    imgChoiceA.setEnabled(false);
                    imgChoiceC.setEnabled(false);
                    imgChoiceD.setEnabled(false);
                }
                break;

            case "2":
                imgChoiceC.setBackgroundResource(R.drawable.answer_selected);
                if (isDoubleJokerSelected) {
                    if (userChoiceClickAmount == 1) {

                    } else {
                        imgChoiceA.setEnabled(false);
                        imgChoiceB.setEnabled(false);
                        imgChoiceD.setEnabled(false);
                    }
                } else {
                    imgChoiceA.setEnabled(false);
                    imgChoiceB.setEnabled(false);
                    imgChoiceD.setEnabled(false);
                }
                break;
            case "3":
                imgChoiceD.setBackgroundResource(R.drawable.answer_selected);
                if (isDoubleJokerSelected) {
                    if (userChoiceClickAmount == 1) {

                    } else {
                        imgChoiceA.setEnabled(false);
                        imgChoiceB.setEnabled(false);
                        imgChoiceC.setEnabled(false);
                    }
                } else {
                    imgChoiceA.setEnabled(false);
                    imgChoiceB.setEnabled(false);
                    imgChoiceC.setEnabled(false);
                }
                break;
        }
    }

    @Override
    protected void onPause() {
       super.onPause();
         countDownTimer.start();
        countDownTimer.cancel();
        if (mediaPlayerTimer.isPlaying())
        {
            mediaPlayerTimer.stop();
        }

    }
}