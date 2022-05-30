package com.creactivestudio.themillionare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creactivestudio.themillionare.audio.AudioPlayServiceGameActivity;
import com.creactivestudio.themillionare.score_board.ScoreBoard;
import com.creactivestudio.themillionare.score_board.ScoreBoardActivity;
import com.creactivestudio.themillionare.user_inputs.ReportMistakeActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.creactivestudio.themillionare.GameActivity.KEY_TOTAL_POINTS;

public class CurrentLevelActivity extends AppCompatActivity {

    ImageView imgLevel1, imgLevel2, imgLevel3, imgLevel4, imgLevel5, imgLevel6, imgLevel7,
            imgLevel8, imgLevel9, imgLevel10, imgLevel11, imgLevel12, imgLevel13, imgLeaveGame;
    private SharedPreferences sharedPreferences, sp;
    List<ImageView> imageViewList;
    AlertDialog dialogOneMoreJoker, dialogOneMillion, dialogLeaveWithMoney,dialogExit;
    private SharedPreferences.Editor editor;
    private TextView tvCurrentGemsLevelAct, tvTapToContinue;
    private AdView adViewCurrentLevel;
    private RewardedAd rewardedAd;
    private RewardedAdLoadCallback rewardedAdLoadListener;
    private RewardedAdCallback rewardedAdProcessListener;
    private String selectedJokerKey;
    private boolean isTapped=false;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_level);

        init();
        loadViews();
        loadAds();
        Helper.setLocale(this);
    }

    private void showCustomExitDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(CurrentLevelActivity.this);
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
                startActivity(new Intent(CurrentLevelActivity.this, MainActivity.class));
            }
        });

    }

    public void leaveGame(View view){
        // todo 5.000 geçti ise puanı artırılacak

        int currentLevel = sharedPreferences.getInt(GameActivity.SP_USER_LEVEL, 1);
        sp = getSharedPreferences(GameActivity.SP_FILE_JOKERS, MODE_PRIVATE);
        if(currentLevel<6){
            showCustomExitDialog();
        }
        else {
            leaveGameWithMoney();
        }
    }
    public void leaveGameWithMoney() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CurrentLevelActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_leave_with_money, null);
        builder.setCancelable(false);
        builder.setView(view);

        ImageView imgCloseLeaveWithMoney = view.findViewById(R.id.imgCloseLeaveWithMoney);
        ImageView imgNo = view.findViewById(R.id.imgNo);
        ImageView imgYes = view.findViewById(R.id.imgYes);

        dialogLeaveWithMoney = builder.create();
        dialogLeaveWithMoney.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogLeaveWithMoney.show();

        imgCloseLeaveWithMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                dialogLeaveWithMoney.dismiss();
            }
        });

        imgNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 7.02.2021 mağazaya gönder
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                dialogLeaveWithMoney.dismiss();

            }
        });
        imgYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 1.03.2021 para ekleyip bitirilecek
                startActivity(new Intent(CurrentLevelActivity.this, MainActivity.class));
            }
        });

    }

    public void loadAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adViewCurrentLevel.loadAd(adRequest);

        loadRewardedAd();
        rewardedAdListener();

    }
    public void loadRewardedAd() {
        // TODO: 15.02.2021 gerçek id ile değiştirilecek
        rewardedAd = new RewardedAd(this, getString(R.string.rewarded_ad_test_id));
        rewardedAdLoadListener = new RewardedAdLoadCallback() {

            @Override
            public void onRewardedAdLoaded() {
             }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
             }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), rewardedAdLoadListener);
    }

    public void rewardedAdListener() {
        rewardedAdProcessListener = new RewardedAdCallback() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                dialogOneMoreJoker.dismiss();
                makeSelectedJokerAvailable(selectedJokerKey);
                startActivity(new Intent(CurrentLevelActivity.this,GameActivity.class));
                finish();
            }

            @Override
            public void onRewardedAdOpened() {
                if(Helper.isSoundOn(getApplicationContext())) {
                    stopService(new Intent(CurrentLevelActivity.this, AudioPlayServiceGameActivity.class));
                }
            }

            @Override
            public void onRewardedAdClosed() {
                loadRewardedAd();
                if(Helper.isSoundOn(getApplicationContext())) {
                    startService(new Intent(CurrentLevelActivity.this, AudioPlayServiceGameActivity.class));
                }
            }
            @Override
            public void onRewardedAdFailedToShow(int i) {
            }
        };
    }
    public void fillGems(TextView textView) {

        int currentGem = sharedPreferences.getInt(GameActivity.KEY_CURRENT_GEM, 90);
        textView.setText(String.valueOf(currentGem));
    }

    public void loadViews() {
        Helper.disableActivity(this);
        int currentLevel = sharedPreferences.getInt(GameActivity.SP_USER_LEVEL, 1);
        sp = getSharedPreferences(GameActivity.SP_FILE_JOKERS, MODE_PRIVATE);

        if(currentLevel<6){
            imgLeaveGame.setImageResource(R.drawable.ic_arrow_back);
        }
        else {
            imgLeaveGame.setImageResource(R.drawable.ic_leave_with_pay);
        }

        if (currentLevel == 13) {
            showOneMillionPopUp();
        } else {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    imageViewList.get(currentLevel - 1).setBackgroundResource(R.drawable.btn_level_active);
                    if(!isTapped){
                        Helper.playVoiceEffect(getApplicationContext(), R.raw.level_up);
                    }
                    Helper.enableActivity(CurrentLevelActivity.this);
                    tvTapToContinue.setVisibility(View.VISIBLE);
                }
            }, 3000);

            fillGems(tvCurrentGemsLevelAct);

            for (int i = 0; i < currentLevel - 1; i++) {
                imageViewList.get(i).setBackgroundResource(R.drawable.btn_level_completed);
            }

        }
    }

    public int getTotalPointFromSharedPref() {
        int totalPoint = sharedPreferences.getInt(KEY_TOTAL_POINTS, 0);
        return totalPoint;
    }

    public void createUsersFirebaseScoreBoard() {
        if (Helper.controlNetwork(CurrentLevelActivity.this)) {
            String userEmail = firebaseAuth.getCurrentUser().getEmail();
            int currentPoint = getTotalPointFromSharedPref();
            // String docId = sharedPreferences.getString(ScoreBoardActivity.KEY_DOC_ID, "");
            String userName = sharedPreferences.getString(GameActivity.KEY_USER_NAME, "");


            ScoreBoard scoreBoard = new ScoreBoard();
            scoreBoard.setEmail(userEmail);
            scoreBoard.setScore(currentPoint);
            scoreBoard.setUserName(userName);

            firebaseFirestore.collection(GameActivity.KEY_SCORE_BOARD)
                    .add(scoreBoard)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            firebaseFirestore.collection(GameActivity.KEY_SCORE_BOARD).document(documentReference.getId()).update("docId", documentReference.getId());
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
        if (Helper.controlNetwork(CurrentLevelActivity.this)) {
            String userEmail = firebaseAuth.getCurrentUser().getEmail();

            int currentPoint = getTotalPointFromSharedPref();

            ScoreBoard scoreBoard = new ScoreBoard();
            scoreBoard.setEmail(userEmail);
            scoreBoard.setScore(currentPoint);


            Map<String, Object> map = new HashMap<>();
            map.put("score", currentPoint);

            String docId = sharedPreferences.getString(ScoreBoardActivity.KEY_DOC_ID, "");
            firebaseFirestore.collection(GameActivity.KEY_SCORE_BOARD).document(docId)
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

    public void updateTotalPoints(int updateAmount) {
        GameActivity gameActivity=new GameActivity();
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

    public void showOneMillionPopUp() {
        // TODO: 6.09.2021 burada hata var .. updateTotalPoints kaldırınca çalışııyor .. önemli ____
        GameActivity gameActivity = new GameActivity();
       // gameActivity.updateTotalPoints(1000000);


        updateTotalPoints(1000000);

        // TODO: 15.02.2021 kullanıcı puanı güncellenmeli
        AlertDialog.Builder builder = new AlertDialog.Builder(CurrentLevelActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_one_million, null);
        builder.setCancelable(false);
        builder.setView(view);

        ImageView imgHome = view.findViewById(R.id.imgHomeOneMillion);
        ImageView imgShareForGems = view.findViewById(R.id.imgShareOneMillion);
        ImageView imgReportMistake = view.findViewById(R.id.imgReportMistakeOneMillion);
        ImageView imgNewGame = view.findViewById(R.id.imgNewGameOneMillion);


        dialogOneMillion = builder.create();
        dialogOneMillion.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogOneMillion.show();

        Helper.playVoiceEffect(this, R.raw.millionaire);

        imgNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                dialogOneMillion.dismiss();
                startActivity(new Intent(CurrentLevelActivity.this, MainActivity.class));
                finish();
            }
        });

        imgShareForGems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.i_recommend_this_fantastic_app) + "\n" + "https://play.google.com/store/apps/details?id=com.creactivestudio.themillionare";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.this_game_is_wonderfull));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));

                gameActivity.addGems(2);
            }
        });
        imgReportMistake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                startActivity(new Intent(CurrentLevelActivity.this, ReportMistakeActivity.class));
            }
        });
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                startActivity(new Intent(CurrentLevelActivity.this, MainActivity.class));
                finish();
            }
        });
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                startActivity(new Intent(CurrentLevelActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    public void init() {
        tvTapToContinue=findViewById(R.id.tvTapToContinue);
        imgLeaveGame=findViewById(R.id.imgLeaveGame);
        adViewCurrentLevel = findViewById(R.id.adViewCurrentLevel);
        imageViewList = new ArrayList<>();
        tvCurrentGemsLevelAct = findViewById(R.id.tvCurrentGemLevelAct);
        sharedPreferences = getSharedPreferences(GameActivity.SP_FILE_JOKERS, MODE_PRIVATE);
        editor=sharedPreferences.edit();
        imageViewList.add(imgLevel1 = findViewById(R.id.imgLevel1));
        imageViewList.add(imgLevel2 = findViewById(R.id.imgLevel2));
        imageViewList.add(imgLevel3 = findViewById(R.id.imgLevel3));
        imageViewList.add(imgLevel4 = findViewById(R.id.imgLevel4));
        imageViewList.add(imgLevel5 = findViewById(R.id.imgLevel5));
        imageViewList.add(imgLevel6 = findViewById(R.id.imgLevel6));
        imageViewList.add(imgLevel7 = findViewById(R.id.imgLevel7));
        imageViewList.add(imgLevel8 = findViewById(R.id.imgLevel8));
        imageViewList.add(imgLevel9 = findViewById(R.id.imgLevel9));
        imageViewList.add(imgLevel10 = findViewById(R.id.imgLevel10));
        imageViewList.add(imgLevel11 = findViewById(R.id.imgLevel11));
        imageViewList.add(imgLevel12 = findViewById(R.id.imgLevel12));
        imageViewList.add(imgLevel13 = findViewById(R.id.imgLevel13));


    }

    public void tapToContinue(View view) {
        // TODO: 25.01.2021 resume game when user click
        isTapped=true;
        boolean hasUserAudienceJoker = sp.getBoolean(GameActivity.SP_AUDIENCE_JOKER, true);
        boolean hasFiftyFiftyJoker = sharedPreferences.getBoolean(GameActivity.SP_FIFTY_FIFTY_JOKER, true);
        boolean hasUserDoubleJoker = sharedPreferences.getBoolean(GameActivity.SP_DOUBLE_RIGHT_JOKER, true);
        boolean hasUserPhoneJoker = sharedPreferences.getBoolean(GameActivity.SP_PHONE_JOKER, true);

        if (!hasUserAudienceJoker && !hasFiftyFiftyJoker && !hasUserDoubleJoker && !hasUserPhoneJoker) {
            showOneMoreJokerDialog();
        } else {
            startActivity(new Intent(CurrentLevelActivity.this, GameActivity.class));
            finish();
        }

    }

    public void makeSelectedJokerAvailable (String sp_key){
        editor.putBoolean(sp_key,true);
        editor.commit();
    }

    public void showOneMoreJokerDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(CurrentLevelActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_one_more_joker, null);
        builder.setCancelable(false);
        builder.setView(view);

        ImageView imgFiftyFiftyJoker=view.findViewById(R.id.imgFiftyFiftyCurrentLevel);
        ImageView imgPhoneJoker =view.findViewById(R.id.imgPhoneJokerCurrentLevel);
        ImageView imgAudienceJoker =view.findViewById(R.id.imgAudienceJokerCurrentLevel);
        ImageView imgDoubleJoker=view.findViewById(R.id.imgDoubleJokerCurrentLevel);

        ImageView imgClose = view.findViewById(R.id.imgCloseOneMoreJoker);
        ImageView imgGems = view.findViewById(R.id.imgGemsOneMore);
        ImageView imgShowReward = view.findViewById(R.id.imgShowRewardOneMore);

        dialogOneMoreJoker = builder.create();
        dialogOneMoreJoker.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogOneMoreJoker.show();
        selectedJokerKey=GameActivity.SP_AUDIENCE_JOKER;
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOneMoreJoker.dismiss();
                // TODO: 7.02.2021 oyunu bitir
                startActivity(new Intent(CurrentLevelActivity.this, GameActivity.class));
            }
        });
        imgGems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 7.02.2021 YETERLİ ELMASI VARSA KULLANDIR YOKSA mağazaya gönder
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                int currentGem = sharedPreferences.getInt(GameActivity.KEY_CURRENT_GEM, 90);
                if (currentGem >= 20) {
                    Toast.makeText(CurrentLevelActivity.this, getString(R.string.you_have_used_20_gems), Toast.LENGTH_SHORT).show();
                    decreaseGem(20);
                    fillGems(tvCurrentGemsLevelAct);
                    dialogOneMoreJoker.dismiss();
                    editor.putBoolean(selectedJokerKey,true);
                    editor.commit();
                    startActivity(new Intent(CurrentLevelActivity.this, GameActivity.class));
                    finish();

                } else {
                    // TODO: 10.02.2021 oyunu durdurup mağazaya gidecek .. satın alma olursa tekrar kaldığı yerden devam etmeli
                       Intent intent = new Intent(CurrentLevelActivity.this, MarketActivity.class);
                       startActivityForResult(intent, 1);
                }
            }
        });
        imgShowReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);
                if (rewardedAd.isLoaded()) {
                    rewardedAd.show(CurrentLevelActivity.this, rewardedAdProcessListener);

                } else {
                    Toast.makeText(CurrentLevelActivity.this, getString(R.string.rewarded_ad_is_not_loaded_please_try_again_later), Toast.LENGTH_SHORT).show();
                }

            }
        });

        imgFiftyFiftyJoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFiftyFiftyJoker.setImageResource(R.drawable.extra_joker_fifty_fifty_active);
                imgPhoneJoker.setImageResource(R.drawable.extra_joker_phone_passive);
                imgAudienceJoker.setImageResource(R.drawable.extra_joker_audience_passive);
                imgDoubleJoker.setImageResource(R.drawable.extra_joker_half_passive);
                selectedJokerKey=GameActivity.SP_FIFTY_FIFTY_JOKER;
            }
        });

        imgPhoneJoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFiftyFiftyJoker.setImageResource(R.drawable.extra_joker_fifty_fifty_passive);
                imgPhoneJoker.setImageResource(R.drawable.extra_joker_phone_active);
                imgAudienceJoker.setImageResource(R.drawable.extra_joker_audience_passive);
                imgDoubleJoker.setImageResource(R.drawable.extra_joker_half_passive);
                selectedJokerKey=GameActivity.SP_PHONE_JOKER;

            }
        });
        imgAudienceJoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFiftyFiftyJoker.setImageResource(R.drawable.extra_joker_fifty_fifty_passive);
                imgPhoneJoker.setImageResource(R.drawable.extra_joker_phone_passive);
                imgAudienceJoker.setImageResource(R.drawable.joker_audience_active);
                imgDoubleJoker.setImageResource(R.drawable.extra_joker_half_passive);
                selectedJokerKey=GameActivity.SP_AUDIENCE_JOKER;

            }
        });
        imgDoubleJoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFiftyFiftyJoker.setImageResource(R.drawable.extra_joker_fifty_fifty_passive);
                imgPhoneJoker.setImageResource(R.drawable.extra_joker_phone_passive);
                imgAudienceJoker.setImageResource(R.drawable.extra_joker_audience_passive);
                imgDoubleJoker.setImageResource(R.drawable.extra_joker_half_active);
                selectedJokerKey=GameActivity.SP_DOUBLE_RIGHT_JOKER;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // TODO: 20.02.2021 bu kısım kontrol edilecek . satın alma yapıp geri dönebilmesi lazım

                dialogOneMoreJoker.dismiss();
                makeSelectedJokerAvailable(selectedJokerKey);
                startActivity(new Intent(CurrentLevelActivity.this, GameActivity.class));

            }
            if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    public void decreaseGem (int decreaseAmount) {
        int curGem = sharedPreferences.getInt(GameActivity.KEY_CURRENT_GEM, 100);
        int newGem = curGem - decreaseAmount;
        editor.putInt(GameActivity.KEY_CURRENT_GEM, newGem);
        editor.commit();
        fillGems(tvCurrentGemsLevelAct);

    }

}