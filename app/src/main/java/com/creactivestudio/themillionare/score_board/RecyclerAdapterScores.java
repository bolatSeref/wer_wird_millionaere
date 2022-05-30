package com.creactivestudio.themillionare.score_board;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creactivestudio.themillionare.R;

import java.util.ArrayList;

public class RecyclerAdapterScores extends RecyclerView.Adapter<RecyclerAdapterScores.ScoreHolder> {

    private ArrayList<ScoreBoard> scoreBoardList;

    public RecyclerAdapterScores(ArrayList<ScoreBoard> scoreBoardList) {
        this.scoreBoardList = scoreBoardList;
    }

    @NonNull
    @Override
    public ScoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.score_board_item, parent, false);

        return new ScoreHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ScoreHolder holder, int position) {
        holder.tvScoreItemName.setText(scoreBoardList.get(position).getUserName());
        holder.tvScoreItemPoints.setText(String.valueOf(scoreBoardList.get(position).getScore()));
    }

    @Override
    public int getItemCount() {
        return scoreBoardList.size();
    }

    class ScoreHolder extends RecyclerView.ViewHolder {

        TextView tvScoreItemName, tvScoreItemPoints;

        public ScoreHolder(@NonNull View itemView) {
            super(itemView);

            tvScoreItemName = itemView.findViewById(R.id.tvScoreItemName);
            tvScoreItemPoints = itemView.findViewById(R.id.tvScoreItemPoints);

        }
    }
}
