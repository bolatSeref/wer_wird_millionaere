package com.creactivestudio.themillionare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;

import static com.creactivestudio.themillionare.GameActivity.KEY_CURRENT_GEM;

public class MarketActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    private ImageView imgGem1, imgGem2, imgGem3, imgGem4;
    private TextView tvCurrentGem;
    private BillingClient mBillingClient;
    private List<SkuDetails> skuINAPPDetayListesi = new ArrayList<>();
    private int currentGem;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private AdView adViewMarket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        // TODO: 8.02.2021  mevcut elmas textview basılacak açılışta
        init();
        loadAds();

        sharedPreferences = getSharedPreferences(GameActivity.SP_FILE_JOKERS, MODE_PRIVATE);
        editor=sharedPreferences.edit();
        int currentGem = sharedPreferences.getInt(KEY_CURRENT_GEM, 100);
        tvCurrentGem.setText(String.valueOf(currentGem));
        mBillingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases().setListener(this::onPurchasesUpdated).build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    enablePurchaseViews(true);
                    List<String> skuListINAPP = new ArrayList<>();
                    skuListINAPP.add("gem100");
                    skuListINAPP.add("gem175");
                    skuListINAPP.add("gem250");
                    skuListINAPP.add("gem500");

                    SkuDetailsParams.Builder paramsINAPP = SkuDetailsParams.newBuilder();
                    paramsINAPP.setSkusList(skuListINAPP).setType(BillingClient.SkuType.INAPP);
                    mBillingClient.querySkuDetailsAsync(paramsINAPP.build(), new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                            skuINAPPDetayListesi = list;
                        }
                    });

                } else {
                    enablePurchaseViews(false);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                enablePurchaseViews(false);
            }
        });

        imgGem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuINAPPDetayListesi.get(0))
                        .build();
                mBillingClient.launchBillingFlow(MarketActivity.this, flowParams);

            }
        });

        imgGem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuINAPPDetayListesi.get(1))
                        .build();
                mBillingClient.launchBillingFlow(MarketActivity.this, flowParams);

            }
        });

        imgGem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuINAPPDetayListesi.get(2))
                        .build();
                mBillingClient.launchBillingFlow(MarketActivity.this, flowParams);
            }
        });
        imgGem4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.playVoiceEffect(getApplicationContext(), R.raw.item_select);

                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuINAPPDetayListesi.get(3))
                        .build();
                mBillingClient.launchBillingFlow(MarketActivity.this, flowParams);
            }
        });


    }

    void handlePurchase(Purchase purchase) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                 }
            }
        };

        //Ürün tüketme
        mBillingClient.consumeAsync(consumeParams, listener);
    }

    public void init() {
        adViewMarket = findViewById(R.id.adViewMarket);
        tvCurrentGem = findViewById(R.id.tvCurrentGem);
        imgGem1 = findViewById(R.id.imgGem1);
        imgGem2 = findViewById(R.id.imgGem2);
        imgGem3 = findViewById(R.id.imgGem3);
        imgGem4 = findViewById(R.id.imgGem4);

    }

    public void loadAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adViewMarket.loadAd(adRequest);
    }

    public void enablePurchaseViews(boolean status) {
        imgGem1.setEnabled(status);
        imgGem2.setEnabled(status);
        imgGem3.setEnabled(status);
        imgGem4.setEnabled(status);
    }

    public void backImage(View view) {
        Helper.playVoiceEffect(this, R.raw.item_select);
       startActivity(new Intent(MarketActivity.this, MainActivity.class));

    }
    public void addGems(int additionAccount) {
        int currentGem = sharedPreferences.getInt(KEY_CURRENT_GEM, 100);
        int newGem = currentGem + additionAccount;
        editor.putInt(KEY_CURRENT_GEM, newGem);
        editor.commit();
    }
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        GameActivity gameActivity=new GameActivity();
        //satışların takibi bu metod ile yapılıyor ** vetitabanına kaydedilecek
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            for (Purchase purchase : list) {
                //  handlePurchase(purchase);
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                }
                if (purchase.getSku().equals("gem100")) {
                    currentGem += 100;

                    addGems(100);
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();

                }
                if (purchase.getSku().equals("gem175")) {
                    currentGem += 175;
                    addGems(175);
                    Intent intent = getIntent();
                    int rand = intent.getIntExtra("purchase", 0);
                    int result = rand;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result", result);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }

                if (purchase.getSku().equals("gem250")) {
                    currentGem += 250;
                    addGems(250);
                    Intent intent = getIntent();
                    int rand = intent.getIntExtra("purchase", 0);

                    int result = rand;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result", result);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }

                if (purchase.getSku().equals("gem500")) {
                    currentGem += 500;
                    addGems(500);
                    Intent intent = getIntent();
                    int rand = intent.getIntExtra("purchase", 0);
                    int result = rand;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result", result);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }

                tvCurrentGem.setText(String.valueOf(currentGem));
            }
        }
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show();

        }
    }
}