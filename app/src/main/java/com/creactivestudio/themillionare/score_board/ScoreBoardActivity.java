package com.creactivestudio.themillionare.score_board;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creactivestudio.themillionare.GameActivity;
import com.creactivestudio.themillionare.Helper;
import com.creactivestudio.themillionare.MainActivity;
import com.creactivestudio.themillionare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ScoreBoardActivity extends AppCompatActivity {
    TextView tvGoldName, tvGoldPoint, tvSilverName, tvSilverPoint, tvBronzeName, tvBronzePoint;
    RecyclerView rvScores;
    ArrayList<ScoreBoard> scoreBoardList;
    RecyclerAdapterScores recyclerAdapterScores;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String userDocId;
    public static final String KEY_DOC_ID="key_doc_id";
    private ImageView imgWeek, imgMonth, imgYear;
    @Override

    // TODO: 21.06.2021 aylık haftalık yıllık şimdilik kapatıldı backend ine bakılması gerekiyor açılacak daha sonra
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        init();
        getScoresFromFB();

        rvScores.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapterScores=new RecyclerAdapterScores(scoreBoardList);
        rvScores.setAdapter(recyclerAdapterScores);
        findDocId();

    }

    public void init (){
        imgMonth=findViewById(R.id.imgMonth);
        imgWeek=findViewById(R.id.imgWeek);
        imgYear=findViewById(R.id.imgYear);


        sharedPreferences=getSharedPreferences(GameActivity.SP_FILE_JOKERS, MODE_PRIVATE);
        editor=sharedPreferences.edit();


        tvGoldName=findViewById(R.id.tvGoldName);
        tvGoldPoint=findViewById(R.id.tvGoldPoint);
        tvSilverName=findViewById(R.id.tvSilverName);
        tvSilverPoint=findViewById(R.id.tvSilverPoint);
        tvBronzeName=findViewById(R.id.tvBronzeName);
        tvBronzePoint=findViewById(R.id.tvBronzePoint);
        rvScores=findViewById(R.id.rvScores);
        scoreBoardList=new ArrayList<>();

    }

    public void getScoresFromFB(){
        firebaseFirestore.collection(GameActivity.KEY_SCORE_BOARD).orderBy("score", Query.Direction.DESCENDING).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
               if(error !=null){
                   Toast.makeText(ScoreBoardActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
               }
               else {
                   if(value!=null)
                   {
                       for(DocumentSnapshot snapshot:value.getDocuments()) {
                           ScoreBoard scoreBoard=new ScoreBoard();
                           String docId=snapshot.getId();
                           scoreBoard.setDocId(docId);
                           scoreBoard.setUserName(snapshot.get("userName").toString());
                           scoreBoard.setEmail(snapshot.get("email").toString());
                           String skor=snapshot.get("score").toString();

                           int skr= Integer.valueOf(skor);
                           scoreBoard.setScore(skr);
                           scoreBoardList.add(scoreBoard);
                           recyclerAdapterScores.notifyDataSetChanged();
                       }

                       tvGoldPoint.setText(String.valueOf(scoreBoardList.get(0).getScore()));
                       tvSilverPoint.setText(String.valueOf(scoreBoardList.get(1).getScore()));
                       tvBronzePoint.setText(String.valueOf(scoreBoardList.get(2).getScore()));

                       tvGoldName.setText(String.valueOf(scoreBoardList.get(0).getUserName()));
                       tvSilverName.setText(String.valueOf(scoreBoardList.get(1).getUserName()));
                       tvBronzeName.setText(String.valueOf(scoreBoardList.get(2).getUserName()));

                   }
               }
            }
        });
    }

    public void findDocId (){
        for (ScoreBoard s :
                scoreBoardList)
        {
            if(s.getEmail().equals(firebaseAuth.getCurrentUser().getEmail()))
            {
                userDocId=s.getDocId();
                editor.putString(KEY_DOC_ID, userDocId);
                editor.commit();
            }
        }


    }
    public void imgBackArrow(View view){
        Helper.playVoiceEffect(this, R.raw.item_select);
        startActivity(new Intent(ScoreBoardActivity.this, MainActivity.class));
     }
    public void timeSelect (View view){
        Helper.playVoiceEffect(this, R.raw.item_select);
        switch (view.getId()){
            case R.id.imgWeek:
            {
                imgYear.setImageResource(R.drawable.btn_time_passive);
                imgMonth.setImageResource(R.drawable.btn_time_passive);
                imgWeek.setImageResource(R.drawable.btn_time_active);
            }
            break;
            case R.id.imgMonth:
            {
                imgYear.setImageResource(R.drawable.btn_time_passive);
                imgMonth.setImageResource(R.drawable.btn_time_active);
                imgWeek.setImageResource(R.drawable.btn_time_passive);
            }
            break;
            case R.id.imgYear:
            {
                imgYear.setImageResource(R.drawable.btn_time_active);
                imgMonth.setImageResource(R.drawable.btn_time_passive);
                imgWeek.setImageResource(R.drawable.btn_time_passive);
            }
            break;





        }
    }

}